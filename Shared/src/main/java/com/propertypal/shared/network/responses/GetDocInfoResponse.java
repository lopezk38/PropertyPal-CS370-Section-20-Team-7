package com.propertypal.shared.network.responses;

import com.propertypal.shared.network.enums.BaseResponseEnum;

public class GetDocInfoResponse extends BaseResponse
{
    public Integer MIME_TYPE;
    public String NAME;
    public String DESCRIPTION;

    public static class GetInfoStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_DOC = 1;

    }
}
