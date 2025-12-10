package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

import java.util.ArrayList;
import java.util.List;

public class GetTicketListResponse extends BaseResponse
{
    public ArrayList<Long> TICKETS = null;


    public static class GetTicketListStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_LEASE = 1;
    }
}
