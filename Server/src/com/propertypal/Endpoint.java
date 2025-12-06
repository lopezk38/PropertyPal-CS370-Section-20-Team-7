package com.propertypal;

import com.sun.net.httpserver.*;

import java.net.InetAddress;
import java.net.URI;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;

import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.enums.*;
import com.propertypal.shared.network.GsonAdapters.LocalDateTimeAdapter;

public class Endpoint<T extends BasePacket> implements HttpHandler
{
    private HttpExchange exchange = null;

    private String requestURI = null;
    private String requestQuery = null;
    private String requestData = null;

    private final Class<T> packetType;
    private T packet = null;

    private forwarder target = null;

    private Gson gson = null;

    @FunctionalInterface
    interface forwarder<packetType>
    {
        public void forward(ClientRequest request);
    }

    public Endpoint(Class<T> packetType, forwarder forwardUsing)
    {
        if (forwardUsing == null) throw new IllegalArgumentException("Forwarder must be supplied");
        this.target = forwardUsing;
        this.packetType = packetType;

        //Setup Gson/Json parser
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe());
        this.gson = gsonBuilder.create();

    }

    public void handle(HttpExchange exchange) throws IOException //Event triggered on endpoint activation
    {
        //Save for response
        this.exchange = exchange;

        URI reqURI = null;
        requestURI = null;
        requestQuery = null;
        requestData = null;

        try { reqURI = exchange.getRequestURI(); }
        catch (Exception e) { rejectUnknownErr(); }

        try { requestURI = new String(reqURI.getPath()); }
        catch (Exception e) { rejectUnknownErr(); }

        try { requestQuery = new String(reqURI.getQuery()); }
        catch (Exception e) { } //Ok for this field to be null

        try
        {
            InputStream inData = exchange.getRequestBody();
            requestData = new String(inData.readAllBytes());
        }
        catch (Exception e) { rejectBadJson(); }

        System.out.println("Got request path: " + requestURI + " with query: " + requestQuery);
        System.out.println("Got request data: " + requestData);

        deserializeRequest();
        if (this.packet == null) throw new IllegalArgumentException("ERROR: Failed to deserialize packet!");
        //TODO: Need to deal with null fields that must not be null

        target.forward(new ClientRequest(this, this.packet));
    }

    public void sendResp(String resp)
    {
        if (exchange == null)
        {
            System.out.println("WARNING: Attempted to respond to null exhange. Discarding...");
            return;
        }

        //Respond
        try
        {
            exchange.sendResponseHeaders(200, resp.length());
            OutputStream outData = exchange.getResponseBody();
            outData.write(resp.getBytes());
            outData.close();
        }
        catch (IOException e)
        {
            //Nothing we can really do. Just drop the request (AKA do nothing)
        }

        System.out.println("Responded with: " + resp);
    }

    public InetAddress getRemoteIP() { return exchange.getRemoteAddress().getAddress(); }

    private final void rejectForErr(int errCode) throws IOException
    {
        //Build response
        BaseResponse resp = new BaseResponse();
        resp.TIMESTAMP = LocalDateTime.now();
        resp.STATUS = errCode;

        //Serialize and send
        String respJson = "{}";
        try
        {
            respJson = gson.toJson(resp);
        }
        catch (Exception e)
        {
            System.out.println("ERROR: JSON failed construction: " + e.toString());
        }
        sendResp(respJson);
    }

    private final void rejectBadJson() throws IOException
    {
        rejectForErr(BaseResponseEnum.ERR_BAD_JSON);
    }

    private final void rejectBadTimestamp() throws IOException
    {
        rejectForErr(BaseResponseEnum.ERR_BAD_TIMESTAMP);
    }

    private final void rejectUnknownErr() throws IOException
    {
        rejectForErr(BaseResponseEnum.ERR_UNKNOWN);
    }

    private void deserializeRequest()
    {
        if (this.requestData == null) return; //Main handler is responsible for handling null condition, do nothing

        try
        {
            this.packet = BasePacket.fromJson(this.requestData, this.packetType);
        }
        catch (Exception e)
        {
            return; //Let main handler deal with it (packet stays null)
        }
    }

    private void forwardRequest()
    {
        System.out.println("TODO: Forward login packet");
    }
}