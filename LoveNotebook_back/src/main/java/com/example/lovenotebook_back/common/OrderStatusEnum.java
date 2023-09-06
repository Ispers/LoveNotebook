package com.example.lovenotebook_back.common;

public enum OrderStatusEnum {
    ORDER_ERROR(-9, "ERROR"),
    ORDER_SUCCESS(1, "已下单");

    private int orderStatus;

    private String value;

    OrderStatusEnum(int orderStatus, String value) {
        this.orderStatus = orderStatus;
        this.value = value;
    }

    public static OrderStatusEnum getOrderStatusEnumByStatus(int orderStatus) {
        for (OrderStatusEnum orderStatusEnum : OrderStatusEnum.values()) {
            if (orderStatusEnum.getOrderStatus() == orderStatus) {
                return orderStatusEnum;
            }
        }
        return ORDER_ERROR;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
