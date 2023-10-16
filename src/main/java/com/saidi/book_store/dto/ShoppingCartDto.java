package com.saidi.book_store.dto;

import java.util.List;

public class ShoppingCartDto {
    List<CartItemsDto> cartItemsList;
    private double totalCost;

    public ShoppingCartDto() {
    }

    public List<CartItemsDto> getCartItemsList() {
        return cartItemsList;
    }

    public void setCartItemsList(List<CartItemsDto> cartItemsList) {
        this.cartItemsList = cartItemsList;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
