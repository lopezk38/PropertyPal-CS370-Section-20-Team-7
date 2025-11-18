package com.propertypal.network.responses;

public class CreateTicketResponse extends BaseResponse
{
    public long TICKET_ID;

    public static class CreateTicketStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_LEASE = 1;
        public static final int ERR_BAD_TICKET_TYPE = 2;
        public static final int ERR_BAD_TICKET_STATE = 4;
        public static final int ERR_BAD_DESCRIPTION = 8;
        public static final int ERR_BAD_ATTACHMENT = 16;
    }
}
