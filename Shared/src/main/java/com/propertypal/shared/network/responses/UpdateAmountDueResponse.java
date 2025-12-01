package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

public class UpdateAmountDueResponse extends BaseResponse
{
    //No additional fields needed past the base

    public static class UpdateAmountDueStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_LEASE = 1;
        public static final int ERR_BAD_AMOUNT = 2;
        public static final int ERR_BAD_DUE_DAY = 4;
        public static final int ERR_BAD_PAYPAL_LINK = 8;
    }
}
