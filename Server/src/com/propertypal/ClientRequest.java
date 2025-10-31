package com.propertypal;

public class ClientRequest
{
    public BasePacket packet;
    private Endpoint source;

    public ClientRequest(Endpoint source, BasePacket packet)
    {
        this.source = source;
        this.packet = packet;
    }
}
