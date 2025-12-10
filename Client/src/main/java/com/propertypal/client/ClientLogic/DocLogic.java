package com.propertypal.client.ClientLogic;

import com.propertypal.client.APIHandler;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.helpers.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.nio.file.Files;

public class DocLogic
{
    private static DocLogic instance = null;
    private APIHandler handler = null;

    private DocLogic()
    {
        handler = APIHandler.getInstance(); //get shared APIHandler instance for sending network requests
    }

    public static DocLogic getInstance()
    {
        if (instance == null)
        {
            instance = new DocLogic();
        }

        return instance;
    }

    public UploadDocResponse uploadDocument(Path path, Long leaseID, String name, String description, Boolean allowUnauth) throws IOException
    {
        //Validate inputs
        if (path == null) { throw new IOException("Invalid file path"); }
        if (leaseID == null || leaseID < 1) { throw new IOException("Invalid lease ID"); }
        if (name == null || name.isBlank()) { throw new IOException("Invalid name"); }
        if (description == null || description.isBlank()) { throw new IOException("Invalid description"); }

        if (allowUnauth == null) { allowUnauth = false; }

        //Load file
        byte[] blob = null;
        String mime = null;
        try
        {
            blob = Files.readAllBytes(path);
            mime = Files.probeContentType(path);
        }
        catch (IOException e)
        {
            System.out.printf("WARNING: Failed to read document at path %s due to %s%n", path.toAbsolutePath(), e.getMessage());
        }

        if (blob == null || blob.length < 1) { throw new IOException("Invalid or empty file given"); }


        //Build packet
        UploadDocPacket packet = new UploadDocPacket();
        packet.lease_id = leaseID;
        packet.mime_type = mime;
        packet.file_size = new Long(blob.length);
        packet.doc_data =  Base64.getEncoder().encodeToString(CompressionUtil.gzip(blob));
        packet.allow_unauth = allowUnauth;
        packet.name = name;
        packet.description = description;

        //Send to server
        UploadDocResponse resp = handler.sendRequest("/doc/upload", packet, UploadDocResponse.class);
        if (resp.STATUS != 0)
        {
            throw new IOException("ERROR: Got error response from server while uploading doc with status: " + resp.STATUS);
        }

        return resp;
    }

    public ArrayList<Long> getDocumentList(Long leaseID) throws IOException
    {
        GetDocListPacket packet = new GetDocListPacket();
        packet.leaseID = leaseID;

        GetDocListResponse resp = handler.sendRequest("/doc/list", packet, GetDocListResponse.class);

        if (resp.STATUS != 0)
        {
            throw new IOException("ERROR: Got error response from server while requesting doc list with status: " + resp.STATUS);
        }

        return resp.DOCS;
    }

    public GetDocInfoResponse getDocumentInfo(Long docID) throws IOException
    {
        GetDocInfoPacket packet = new GetDocInfoPacket();
        packet.docID = docID;

        GetDocInfoResponse resp = handler.sendRequest("/doc/info", packet, GetDocInfoResponse.class);

        if (resp.STATUS != 0)
        {
            throw new IOException("ERROR: Got error response from server while requesting doc info with status: " + resp.STATUS);
        }

        return resp;
    }

    public ViewDocResponse viewDocument(Long docID) throws IOException
    {
        ViewDocPacket packet = new ViewDocPacket();
        packet.doc_id = docID;

        ViewDocResponse resp = handler.sendRequest("/doc/view", packet, ViewDocResponse.class);

        if (resp.STATUS != 0)
        {
            throw new IOException("ERROR: Got error response from server while requesting to view doc with status: " + resp.STATUS);
        }

        return resp;
    }
}
