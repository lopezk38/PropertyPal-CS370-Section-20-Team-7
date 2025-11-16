package com.propertypal.network.responses;

public class AcceptInviteResponse extends BaseResponse
{
    public static class InviteStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_INVITE = 1;
    }
}
