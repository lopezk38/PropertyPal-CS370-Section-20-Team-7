package com.propertypal.network.responses;

import com.propertypal.network.enums.BaseResponseEnum;

public class UploadDocResponse extends BaseResponse
{
    public String DOC_ID;
    public static class UploadStatus extends BaseResponseEnum
    {
        public static final int ERR_BAD_PARENT = 1;
        public static final int ERR_BAD_MIME = 2;
        public static final int ERR_ILLEGAL_SIZE = 4;
        public static final int ERR_CORRUPT_PAYLOAD = 8;
    }
}
