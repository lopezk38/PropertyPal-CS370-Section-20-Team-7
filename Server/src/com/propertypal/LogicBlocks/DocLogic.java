package com.propertypal.LogicBlocks;

import com.propertypal.ClientRequest;
import com.propertypal.DbWrapper;
import com.propertypal.SecurityFilter;
import com.propertypal.network.packets.*;
import com.propertypal.network.responses.BaseResponse;
import com.propertypal.network.responses.BaseResponseEnum;

public class DocLogic
{
    public void handleUploadDoc(ClientRequest req)
    {
        SecurityFilter filter = SecurityFilter.getInstance();
        DbWrapper db = DbWrapper.getInstance();

        if (!(req.packet instanceof UploadDocPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: handleUploadDoc is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //TODO
    }
}
