package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

public class AcceptInviteResponse extends BaseResponse
{
    public static class InviteStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_INVITE = 1;
    }
}
