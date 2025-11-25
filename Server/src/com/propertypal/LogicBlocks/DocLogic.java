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
        String parentObj = packet.parent_obj;
        String mimeType = packet.mime_type;
        long fileBytes = packet.file_bytes;
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
        if (fileBytes > 0 && fileContent.length != fileBytes)
        {
            System.out.println("WARNING: Size mismatch! expected " + fileBytes +
                    " but got " + fileContent.length);
        }

        //TODO query
        Long docID = null;


        //Build response and send
        UploadDocResponse resp = new UploadDocResponse();
        resp.STATUS = BaseResponseEnum.SUCCESS;
        resp.DOC_ID = docID;
        req.setResponse(resp);
        filter.sendResponse(req);





    }
}
