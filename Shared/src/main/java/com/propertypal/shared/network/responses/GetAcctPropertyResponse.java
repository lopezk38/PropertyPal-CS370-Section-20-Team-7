package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

public class GetAcctPropertyResponse extends BaseResponse
{
    public Long PROP_ID;

    public static class GetAcctPropertyStatus extends BaseResponseEnum
    {
        public static final int ERR_ACCT_NOT_SETUP = 1;
        public static final int ERR_NOT_LANDLORD = 2;
    }
}
