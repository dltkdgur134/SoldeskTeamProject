package com.soldesk6F.ondal.useract.cart.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_options")
@Getter
@Setter
@NoArgsConstructor
public class CartItemOption {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cart_item_id")
    private CartItems cartItem;

    private String groupName;
    private String optionName;
    private int optionPrice;
//    private Integer groupIndex;

    @Builder
    public CartItemOption(CartItems cartItem, String groupName, String optionName, int optionPrice) {
        this.cartItem = cartItem;
        this.groupName = groupName;
        this.optionName = optionName;
        this.optionPrice = optionPrice;
    }
}



