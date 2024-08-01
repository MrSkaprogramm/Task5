package com.sfia.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BusTicket {

    private String ticketClass;

    private String ticketType;

    private String startDate;

    private String price;
}
