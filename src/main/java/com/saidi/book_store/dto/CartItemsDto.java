package com.saidi.book_store.dto;

import com.saidi.book_store.models.Book;
import com.saidi.book_store.models.ShoppingCart;
import lombok.Getter;

@Getter
public class CartItemsDto {

    private Long id;
    private Integer quantity;
    private Book book;

    public CartItemsDto() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public CartItemsDto(ShoppingCart cart) {
        this.id = cart.getId();
        this.quantity = cart.getQuantity();
        this.setBook(cart.getBook());
    }
}
