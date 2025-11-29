package com.propertypal.shared.network.packets;

import java.util.List;

public class EditTicketPacket extends BasePacket
{
    public Long ticket_id;
    public Integer ticket_type; //Use TicketEnums.Type to set
    public Integer ticket_state; //Use TicketEnums.State to set
    public String description;
    public List<Long> attachment_ids;
    public List<Long> remove_attachment_ids;
}
