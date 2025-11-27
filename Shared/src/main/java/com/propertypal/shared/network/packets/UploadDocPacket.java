package com.propertypal.shared.network.packets;

public class UploadDocPacket extends BasePacket
{
    public String mime_type;
    public Long file_size;
    public String doc_data;
    public boolean allow_unauth;
}
