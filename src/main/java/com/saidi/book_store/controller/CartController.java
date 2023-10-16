package com.saidi.book_store.controller;

import com.saidi.book_store.dto.AddToCartDto;
import com.saidi.book_store.dto.ShoppingCartDto;
import com.saidi.book_store.exceptions.NotAuthorizedException;
import com.saidi.book_store.exceptions.NotFoundException;
import com.saidi.book_store.service.ShoppingCartService;
import com.saidi.book_store.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Data
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private ShoppingCartService shoppingCartService;

    @Autowired
    public CartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    //add to cart
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody AddToCartDto addToCartDto) throws NotFoundException, NotAuthorizedException {
        return shoppingCartService.addToCart(addToCartDto);
    }
    //get cart items
    @GetMapping
    public ResponseEntity<ShoppingCartDto> getCartItems() throws NotAuthorizedException {
        return shoppingCartService.getCartItems();
    }

    //remove from cart
    @DeleteMapping
    public ResponseEntity<String> removeFromCart(@RequestParam("cartItemId") Long cartItemId) throws NotFoundException, NotAuthorizedException {
        return shoppingCartService.removeFromCart(cartItemId);
    }
}
