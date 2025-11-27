package com.propertypal.LogicBlocks;

import com.propertypal.ClientRequest;
import com.propertypal.DbWrapper;
import com.propertypal.SecurityFilter;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.enums.*;
import com.propertypal.shared.network.helpers.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.io.*;

public class DocLogic extends BaseLogic
{
    public void handleUploadDoc(ClientRequest req)
    {
        UploadDocPacket packet = (UploadDocPacket) req.packet;
        String mimeType = packet.mime_type;
        Long fileSize = packet.file_size;
        String docData = packet.doc_data;

        // Decode Base64
        byte[] compressed;
        try
        {
            compressed = Base64.getDecoder().decode(docData);
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("Bad base64 in doc_data");
            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }

        // Decompress gzip
        byte[] fileContent = null;
        try
        {
            fileContent = CompressionUtil.ungzip(compressed);
        }
        catch (IOException e)
        {
            System.out.println("Failed to ungzip doc_data");
            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }

        // Compare size with file_bytes
        if (fileSize > 0 && fileContent.length != fileSize)
        {
            System.out.println("WARNING: Size mismatch! expected " + fileSize +
                    " but got " + fileContent.length);
        }

        int mimeTypeInt = DocTypeEnum.Type.toInteger(mimeType);

        //Create document entry in DB, keep ID around
        PreparedStatement updocQ = null;
        Long docID = null;
        boolean UnauthViewAllow = true; //TODO currently unauthorized view always allowed - fix later
        String createTime = LocalDateTime.now().toString();

        try
        {
            //TODO insert document name once added to table
            //create new document upload
            updocQ = db.compileQuery("""
                    INSERT INTO Documents (
                        docType,
                        dateCreated,
                        allowUnauthView,
                        data,
                        fileSize,
                    )
                    VALUES (?, ?, ?, ?, ?)""", Statement.RETURN_GENERATED_KEYS);

            updocQ.setInt(1, mimeTypeInt); //docType
            updocQ.setString(2, createTime); //dataCreated
            updocQ.setBoolean(3, UnauthViewAllow); //allowUnauthView
            updocQ.setBytes(5, fileContent); //data
            updocQ.setLong(4, fileSize); //fileSize
            updocQ.executeUpdate();

            //Get the newly made primary key
            ResultSet res = updocQ.getGeneratedKeys();
            if (res.next())
            {
                docID = res.getLong(1);
            }
            else throw new SQLException("Failed to generate document primary key");
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during handleUploadDoc DB insert: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(updocQ);
        }

        //Build response and send
        UploadDocResponse resp = new UploadDocResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        resp.DOC_ID = docID;
        req.setResponse(resp);
        filter.sendResponse(req);
    }
}
