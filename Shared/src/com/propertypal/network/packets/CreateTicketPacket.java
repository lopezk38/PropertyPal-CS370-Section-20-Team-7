package com.propertypal.network.packets;

import java.util.List;

public class CreateTicketPacket extends BasePacket
{
    public long lease_id;
    public int ticket_type; //Use TicketEnums.Type to set
    public int ticket_state; //Use TicketEnums.State to set
    public String description;
    public List<Long> attachment_ids;
}
