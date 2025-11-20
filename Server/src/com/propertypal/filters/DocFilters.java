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

        logic.handleUploadDoc(req);
    }
}
