package com.propertypal.shared.network.enums;

public class DocTypeEnum
{
    public static class Type
    {
        public static final int png = 1;
        public static final int jpeg = 2;
        public static final int pdf = 3;
        public static final int txt = 4;

        public static String toString(int n)
        {
            switch(n) {
                case 1:
                    return "image/png";
                case 2:
                    return "image/jpeg";
                case 3:
                    return "application/pdf";
                case 4:
                    return "text/plain";
            }
            throw new IllegalArgumentException("Invalid mime type");

        }

        public static int toInteger(String str)
        {
            switch(str){
                case "image/png":
                    return 1;
                case "image/jpeg":
                    return 2;
                case "application/pdf":
                    return 3;
                case "text/plain":
                    return 4;
            }
            throw new IllegalArgumentException("Invalid mimetype");
        }

    }


}
