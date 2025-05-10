package com.soldesk6F.ondal.owner.order.dto;

import java.util.List;

import com.soldesk6F.ondal.useract.order.entity.Order.OrderToRider;

import lombok.Data;

@Data
public class OrderLiveDto {
    private String orderId;
    private OrderToRider orderStatus;
    private List<StatusTimeline> timeline;
    private double Lat;
    private double Lng;
    // + getters / setters
}
