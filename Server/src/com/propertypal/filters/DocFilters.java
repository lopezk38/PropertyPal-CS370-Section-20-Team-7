package com.propertypal.filters;

import com.propertypal.ClientRequest;
import com.propertypal.CoreLogic;
import com.propertypal.DbWrapper;
import com.propertypal.SecurityFilter;
import com.propertypal.LogicBlocks.AccountLogic;
import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.enums.*;

public class DocFilters extends BaseFilters
{
    public void filterUploadDocPacket(ClientRequest req)
    {
        if (!(req.packet instanceof UploadDocPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterUploadDocPacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        logic.handleUploadDoc(req);
    }

    public void filterDeleteDocPacket(ClientRequest req)
    {
        if (!(req.packet instanceof DeleteDocPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterDeleteDocPacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        logic.handleDeleteDoc(req);
    }

    public void filterViewDocPacket(ClientRequest req)
    {
        if (!(req.packet instanceof ViewDocPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterViewDocPacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        logic.handleViewDoc(req);
    }
    public void filterGetDocInfoPacket(ClientRequest req)
    {
        if (!(req.packet instanceof GetDocInfoPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterGetDocInfoPacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        logic.handleGetDocInfo(req);
    }

}
