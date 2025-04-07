package com.soldesk6F.ondal.useract.cart.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.menu.entity.Menu;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cart_items")
public class CartItems {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "cart_items_id", nullable = false, unique = true)
    private UUID cartItemsId;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @CreationTimestamp
    @Column(name = "added_time", nullable = false)
    private LocalDateTime addedTime;

    @Lob
    @Column(name = "options")
    private String options;

    @Column(name = "option_total_price")
    private int optionTotalPrice;

    public int getItemTotalPrice() {
        return (menu.getPrice() + optionTotalPrice) * quantity;
    }

    @Builder
    public CartItems(Cart cart, Menu menu, int quantity, List<String> selectedOptions) {
        this.cart = cart;
        this.menu = menu;
        this.quantity = quantity;
        this.setOptions(selectedOptions);
    }

    public void setOptions(List<String> selectedOptions) {
        List<String> safeOptions = selectedOptions.stream()
                .map(opt -> opt.replace("온달", ""))
                .collect(Collectors.toList());
        this.options = String.join("온달", safeOptions);
        calculateOptionTotalPrice(safeOptions);
    }

    public List<String> getOptionsAsList() {
        if (this.options == null || this.options.isBlank()) return new ArrayList<>();
        return Arrays.stream(this.options.split("온달"))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public void calculateOptionTotalPrice(List<String> selectedOptions) {
        List<String> allOptionNames = new ArrayList<>();
        List<Integer> allOptionPrices = new ArrayList<>();

        if (menu.getMenuOptions1() != null) {
            allOptionNames.addAll(Arrays.asList(menu.getMenuOptions1().split("온달")));
            allOptionPrices.addAll(menu.getMenuOptions1PriceList());
        }

        if (menu.getMenuOptions2() != null) {
            allOptionNames.addAll(Arrays.asList(menu.getMenuOptions2().split("온달")));
            allOptionPrices.addAll(menu.getMenuOptions2PriceList());
        }

        if (menu.getMenuOptions3() != null) {
            allOptionNames.addAll(Arrays.asList(menu.getMenuOptions3().split("온달")));
            allOptionPrices.addAll(menu.getMenuOptions3PriceList());
        }

        int total = 0;
        for (String selected : selectedOptions) {
            int index = allOptionNames.indexOf(selected);
            if (index >= 0 && index < allOptionPrices.size()) {
                total += allOptionPrices.get(index);
            }
        }

        this.optionTotalPrice = total;
    }
}
