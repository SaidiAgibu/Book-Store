package com.saidi.book_store.dto;

import com.saidi.book_store.models.Book;
import com.saidi.book_store.models.Order;
import com.saidi.book_store.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private UUID orderId;
    private User user;
    private Book book;
    private int quantity;
    private double totalAmount;
    @Enumerated(EnumType.STRING)
    private Order.Status status;
}