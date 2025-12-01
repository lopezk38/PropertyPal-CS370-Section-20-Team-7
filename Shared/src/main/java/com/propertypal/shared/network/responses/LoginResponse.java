package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

public class LoginResponse extends BaseResponse
{
    public String TOKEN;
    public Boolean IS_LANDLORD;

    public static class LoginStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_EMAIL = 1;
        public static final int ERR_BAD_PASSWORD = 2;
    }
}
