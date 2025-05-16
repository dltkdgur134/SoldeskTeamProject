package com.soldesk6F.ondal.useract.payment.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CartItemsDTO {
    private String menuName;
    private int quantity;
    private int menuPrice;
    private List<String> optionNames;
    private int optionTotalPrice;
    private int totalPrice;
    private String menuImg;
}