package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

import java.time.LocalDateTime;

public class GetDocInfoResponse extends BaseResponse
{
    public Integer MIME_TYPE;
    public String NAME;
    public String DESCRIPTION;
    public LocalDateTime MOD_DATE;

    public static class GetInfoStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_DOC = 1;

    }
}
