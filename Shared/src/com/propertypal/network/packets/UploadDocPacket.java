package com.propertypal.network.packets;

public class UploadDocPacket extends BasePacket
{
    public String parent_obj;
    public String mime_type;
    public long file_bytes;
    public String doc_data;
}
