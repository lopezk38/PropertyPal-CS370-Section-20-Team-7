package com.propertypal.shared.network.packets;

public class CreateInvitePacket extends BasePacket
{
    public long leaseId;
    public String targetUser;
}
