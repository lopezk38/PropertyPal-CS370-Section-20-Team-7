package com.propertypal.shared.network.packets;

public class UploadDocPacket extends BasePacket
{
    public Long lease_id;
    public String mime_type;
    public Long file_size;
    public String doc_data;
    public Boolean allow_unauth;
    public String name;
    public String description;
}
