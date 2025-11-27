package com.propertypal.filters;

import com.propertypal.ClientRequest;
import com.propertypal.shared.network.responses.*;
import com.propertypal.shared.network.packets.*;
import com.propertypal.shared.network.enums.*;

public class PaymentFilters extends BaseFilters
{
    public void filterRequestRentPacket(ClientRequest req)
    {
        if (!(req.packet instanceof RequestRentPacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterRequestRentPacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        //TODO - make sure user is landlord and owns this lease

        //All checks passed, let it through
        logic.handleRequestRent(req);
    }

    public void filterUpdAmountDuePacket(ClientRequest req)
    {
        if (!(req.packet instanceof UpdateAmountDuePacket))
        {
            //Endpoint registered to wrong handler
            System.out.println("ERROR: filterUpdAmountDuePacket is registered to the wrong endpoint");
            BaseResponse resp = new BaseResponse();
            resp.STATUS = BaseResponseEnum.ERR_UNKNOWN;
            req.setResponse(resp);
            filter.sendResponse(req);
            return;
        }

        //Validate user is logged in
        int authSuccess = filter.enforceLoggedIn(req);
        if (authSuccess != BaseResponseEnum.SUCCESS) return;

        //TODO - make sure user is landlord and owns this lease

        //All checks passed, let it through
        logic.handleUpdAmountDue(req);
    }

    public void filterPayRentPacket(ClientRequest req) { ; }//TODO
}
