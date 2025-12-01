package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

public class CreateTicketResponse extends BaseResponse
{
    public Long TICKET_ID;

    public static class CreateTicketStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_LEASE = 1;
        public static final int ERR_BAD_TICKET_TYPE = 2;
        public static final int ERR_BAD_TICKET_STATE = 4;
        public static final int ERR_BAD_DESCRIPTION = 8;
        public static final int ERR_BAD_ATTACHMENT = 16;
        public static final int ERR_BAD_TITLE = 32;
    }
}
