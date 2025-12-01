package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

import java.math.BigDecimal;

public class GetPayLinkResponse extends BaseResponse
{
    public String PAYLINK;
    public Integer DUE_DAY;
    public BigDecimal AMOUNT;

    public static class GetPayLinkStatus extends BaseResponseEnum
    {
        public static int ERR_BAD_LEASE = 1;
        public static int ERR_NO_RENT_DUE = 2;
        public static int ERR_NOT_SETUP = 4;
    }
}
