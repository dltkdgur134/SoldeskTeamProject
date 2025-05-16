package com.soldesk6F.ondal.useract.order.entity;

public enum OrderStatus {
    PENDING("주문 요청 중"), 
    CONFIRMED("주문 확인 완료"), 
    IN_DELIVERY("배달 중"), 
    COMPLETED("주문 및 결제 완료"), 
    CANCELED("주문 취소");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}