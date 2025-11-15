package com.propertypal.network.responses;

public class CreateInviteResponse
{
    public String INVITE_ID;

    public static class InviteStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_TARGET_USER = 1;
        public static final int ERR_BAD_LEASE = 2;
    }
}
