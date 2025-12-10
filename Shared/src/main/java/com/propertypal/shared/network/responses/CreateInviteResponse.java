package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

public class CreateInviteResponse extends BaseResponse
{
    public Long INVITE_ID;

    public static class InviteStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_TARGET_USER = 1;
        public static final int ERR_BAD_LEASE = 2;
        public static final int ERR_BAD_PROPERTY = 4;
        public static final int ERR_TARGET_CANNOT_BE_LANDLORD = 8;
        public static final int ERR_TARGET_ALREADY_IN_LEASE = 16;
        public static final int ERR_INVITE_ALREADY_EXISTS = 32;
    }
}
