package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

public class GetInviteInfoResponse extends BaseResponse
{
    public String FNAME;
    public String LNAME;
    public String EMAIL;

    public static class GetInviteInfoStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_INVITE = 1;
    }
}
