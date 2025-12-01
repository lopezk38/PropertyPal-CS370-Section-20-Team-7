package com.propertypal.shared.network.packets;

public class UpdateAmountDuePacket extends BasePacket
{
    public Long lease_id;
    public String amount;
    public Integer dueDay;
    public String paypalLink;
}
