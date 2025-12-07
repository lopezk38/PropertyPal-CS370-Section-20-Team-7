package com.propertypal.LogicBlocks;

import com.propertypal.ClientRequest;
import com.propertypal.DbWrapper;
import com.propertypal.SecurityFilter;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.enums.*;
import com.propertypal.shared.network.helpers.*;

import java.sql.*;
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
        Long leaseID = packet.lease_id;
        String mimeType = packet.mime_type;
        Long fileSize = packet.file_size;
        String docData = packet.doc_data;
        String docName = packet.name;
        String docDesc = packet.description;
        boolean UnauthViewAllow = packet.allow_unauth;

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

        Timestamp createTime = Timestamp.valueOf(LocalDateTime.now());
        try
        {
            //create new document upload
            updocQ = db.compileQuery("""
                    INSERT INTO Documents (
                        docType,
                        dateCreated,
                        dateModified,
                        allowUnauthView,
                        name,
                        description,
                        data,
                        fileSize,
                        parentLease
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""", Statement.RETURN_GENERATED_KEYS);

            updocQ.setInt(1, mimeTypeInt); //docType
            updocQ.setTimestamp(2, createTime); //dateCreated
            updocQ.setTimestamp(3, createTime); //dateModified
            updocQ.setBoolean(4, UnauthViewAllow); //allowUnauthView
            updocQ.setString(5, docName); //name
            updocQ.setString(6, docDesc); //description
            updocQ.setBytes(7, compressed); //data
            updocQ.setLong(8, fileSize); //fileSize
            updocQ.setLong(9, leaseID);
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

    public void handleViewDoc(ClientRequest req)
    {
        ViewDocPacket packet = (ViewDocPacket) req.packet;
        Long docID = packet.doc_id;

        //Get userID
        long userID = userIDFromToken(packet.token);
        if (userID == -1)
        {
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            filter.sendResponse(req);
        }

        //values to retrieve
        Long doc_owner = null;
        int fileType = 0;
        Timestamp date_uploaded = null;
        boolean allow_unauth = false;
        String doc_name = null;
        String desc = null;
        byte[] doc_data = null;
        Long file_size = null;

        //initialize encoded raw data for response
        String encodedData = null;

        PreparedStatement viewdocQ = null;
        ResultSet res = null;

        try
        {
            viewdocQ = db.compileQuery("""
            SELECT owner,
                   docType,
                   dateCreated,
                   allowUnauthView,
                   name,
                   description,
                   data,
                   fileSize
            FROM Documents
            WHERE docID = ?
            """);

            viewdocQ.setLong(1, docID);
            res = viewdocQ.executeQuery();

            if (!res.next())
            {
                System.out.println("docID not found");
                req.setUnknownErrResponse();
                filter.sendResponse(req);
                return;
            }

            doc_owner = res.getLong("owner");
            fileType = res.getInt("docType");
            date_uploaded = res.getTimestamp("dateCreated");
            allow_unauth = res.getBoolean("allowUnauthView");
            doc_name = res.getString("name");
            desc = res.getString("description");
            doc_data = res.getBytes("data");
            file_size = res.getLong("fileSize");

            //encode data for response
            encodedData = Base64.getEncoder().encodeToString(doc_data);

        }
        catch(SQLException e)
        {
            System.out.println("ERROR: SQLException during ViewDoc: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            db.closeConnection(viewdocQ);
            return;
        }
        finally
        {
            db.closeConnection(viewdocQ);
        }

        ViewDocResponse resp = new ViewDocResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;

        resp.OWNER = doc_owner;
        resp.MIME_TYPE = fileType;
        resp.DATE_CREATED = date_uploaded;
        resp.ALLOW_UNAUTH = allow_unauth;
        resp.NAME = doc_name;
        resp.DESCRIPTION = desc;
        resp.DOC_DATA = encodedData;
        resp.FILE_SIZE = file_size;
        req.setResponse(resp);
        filter.sendResponse(req);

    }

    public void handleDeleteDoc(ClientRequest req)
    {
        DeleteDocPacket packet = (DeleteDocPacket) req.packet;
        Long docID = packet.doc_id;

        //Get userID
        long userID = userIDFromToken(packet.token);
        if (userID == -1)
        {
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            filter.sendResponse(req);
        }

        PreparedStatement deldocQ = null;
        try
        {
            deldocQ = db.compileQuery("""
                    DELETE FROM Documents
                    WHERE docID = ?
                    """);
            deldocQ.setLong(1, docID);

            int rows = deldocQ.executeUpdate();

            if (rows == 0)
            {
                throw new SQLException("No document found with docID = " + docID);
            }
        }
        catch (SQLException e)
        {
            System.out.println("ERROR: SQLException during DeleteUploadDoc DB insert: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);

            return;
        }
        finally
        {
            db.closeConnection(deldocQ);
        }

            //Build response and send
            DeleteDocResponse resp = new DeleteDocResponse();
            resp.STATUS = BaseResponseEnum.SUCCESS;
            req.setResponse(resp);
            filter.sendResponse(req);
    }

    public void handleGetDocInfo(ClientRequest req)
    {
        GetDocInfoPacket packet = (GetDocInfoPacket) req.packet;
        Long doc_id = packet.docID;

        //Get userID
        long userID = userIDFromToken(packet.token);
        if (userID == -1)
        {
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            filter.sendResponse(req);
        }

        //values to retrieve
        int fileType = 0;
        String docName = null;
        String docDesc = null;
        LocalDateTime docDateMod = null;

        PreparedStatement infoQ = null;
        ResultSet res = null;

        try
        {
            infoQ = db.compileQuery("""
            SELECT docType,
                   name,
                   description,
                   dateModified
            FROM Documents
            WHERE docID = ?
            """);

            infoQ.setLong(1, doc_id);
            res = infoQ.executeQuery();

            if (!res.next())
            {
                System.out.println("docID not found");
                req.setUnknownErrResponse();
                filter.sendResponse(req);
                return;
            }

            fileType = res.getInt("docType");
            docName = res.getString("name");
            docDesc = res.getString("description");
            docDateMod = res.getTimestamp("dateModified").toLocalDateTime();

        }
        catch(SQLException e)
        {
            System.out.println("ERROR: SQLException during  GetDocInfo: " + e.toString());

            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }
        finally
        {
            db.closeConnection(infoQ);
        }

        GetDocInfoResponse resp = new GetDocInfoResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        resp.MIME_TYPE = fileType;
        resp.NAME = docName;
        resp.DESCRIPTION = docDesc;
        resp.MOD_DATE = docDateMod;
        req.setResponse(resp);
        filter.sendResponse(req);
    }

    public void handleGetDocList(ClientRequest req)
    {
        GetDocListPacket packet = (GetDocListPacket) req.packet;
        Long lease_id = packet.leaseID;

        //Get userID
        long userID = userIDFromToken(packet.token);
        if (userID == -1)
        {
            req.setBaseErrResponse(BaseResponseEnum.ERR_BAD_TOKEN);
            filter.sendResponse(req);
        }

        //values to retrieve
        ArrayList<Long> docs = new ArrayList<Long>();

        PreparedStatement listQ = null;

        try
        {
            listQ = db.compileQuery("""
               SELECT docID
               FROM Documents
               WHERE parentLease = ?
               """);
            listQ.setLong(1, lease_id);
        }
        catch (SQLException e)
        {
            System.out.println("Error: SQLException during GetDocList compilation: " + e);
            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }

        try
        {
            ResultSet res = listQ.executeQuery();

            while(res.next())
            {
                docs.add(res.getLong("docID"));
            }

        }
        catch (SQLException e)
        {
            System.out.println("Error: SQLException during execution");
            req.setUnknownErrResponse();
            filter.sendResponse(req);
            return;
        }
        finally
        {
            db.closeConnection(listQ);
        }

        //Build and send response
        GetDocListResponse resp = new GetDocListResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        resp.DOCS = docs;
        req.setResponse(resp);
        filter.sendResponse(req);
    }
}

