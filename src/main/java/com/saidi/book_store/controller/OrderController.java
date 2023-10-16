package com.saidi.book_store.controller;


import com.saidi.book_store.dto.PayPalResponse;
import com.saidi.book_store.exceptions.NotAuthorizedException;
import com.saidi.book_store.exceptions.NotFoundException;
import com.saidi.book_store.models.Order;
import com.saidi.book_store.service.OrderService;
import com.saidi.book_store.service.ShoppingCartService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.paypal.orders.OrdersGetRequest;
import java.io.IOException;
import java.util.List;

@Data
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private ShoppingCartService shoppingCartService;

    @Autowired
    public OrderController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createOrder() throws NotFoundException, NotAuthorizedException {
        return shoppingCartService.createOrder();
    }
}
