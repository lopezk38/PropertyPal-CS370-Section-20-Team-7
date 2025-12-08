package com.propertypal.client;

import org.apache.http.*;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.config.RequestConfig;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.enums.*;

public class APIHandler
{
    private static APIHandler instance = null;

    private HttpClient http = null;
    private String activeToken = null;
    private String baseURI = null;

    private APIHandler()
    {
        int timeout = 5000;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout) //Timeout until a connection is established
                .setConnectionRequestTimeout(timeout) //Timeout to get a connection from the pool
                .setSocketTimeout(timeout) //Maximum interval between two data packets
                .build();

        baseURI = "http://localhost:678";
        http = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    private static void init()
    {
        if (instance != null) { return; }

        instance = new APIHandler();
    }

    public static APIHandler getInstance()
    {
        if (instance == null) { init(); }

        return instance;
    }

    public <T extends BaseResponse> T sendRequest(String endpoint, BasePacket packet, Class<T> respType) throws IllegalArgumentException, IOException
    {
        if (endpoint == null || endpoint.length() < 1) { throw new IllegalArgumentException("Invalid endpoint given"); }
        if (packet == null) { throw new IllegalArgumentException("Packet must not be null"); }

        //Inject login token
        if (activeToken != null) { packet.token = activeToken; }

        String serialized = null;
        try
        {
            serialized = packet.toJson();
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Malformed packet given, could not be serialized");
        }

        //Build request
        HttpPost post = new HttpPost(baseURI + endpoint);
        StringEntity reqEnt = new StringEntity(serialized, "UTF-8");
        reqEnt.setContentType("application/json");
        post.setEntity(reqEnt);
        System.out.println("Sending request to endpoint " + endpoint + " with payload " + serialized);

        //Send request
        HttpResponse postResp = null;
        try
        {
            postResp = http.execute(post);
        }
        catch (Exception e)
        {
            System.out.println("Caught exception while sending request: " + e.toString());
            throw new IOException("Failed to connect to server: " + e.getMessage(), e);
        }

        if (postResp == null)
        {
            throw new IOException("No response received from server (null HttpResponse)");
        }

        String rawResp = EntityUtils.toString(postResp.getEntity(), StandardCharsets.UTF_8);
        System.out.println("Got response code " + postResp.getStatusLine().getStatusCode() + " with response " + rawResp);
        //TODO check for code 200, throw if not

        //Deserialize
        T resp = BaseResponse.fromJson(rawResp, respType);

        //Extract login packet if response contains it
        if (resp.STATUS == BaseResponseEnum.SUCCESS)
        {
            String recievedToken = null;
            if (resp instanceof LoginResponse lResp)
            {
                recievedToken = lResp.TOKEN;
            }
            else if (resp instanceof CreateAcctResponse cResp)
            {
                recievedToken = cResp.TOKEN;
            }

            if (recievedToken != null && recievedToken.length() > 0)
            {
                activeToken = recievedToken;
            }
            else
            {
                System.out.println("WARNING: Got invalid token from server. Ignoring...");
            }
        }

        return resp;
    }

    public boolean openConnection()
    {
        return true; //TODO
    }

    public boolean closeConnection()
    {
        return true; //TODO
    }

    public void addAuthToken(String token)
    {
        activeToken = token;
    }
}