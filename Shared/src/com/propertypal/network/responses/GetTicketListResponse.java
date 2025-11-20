package com.propertypal.network.responses;

import com.propertypal.network.enums.BaseResponseEnum;

import java.util.List;

public class GetTicketListResponse extends BaseResponse
{
    public List<Long> TICKETS = null;


    public static class GetTicketListStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_LEASE = 1;
    }
}
