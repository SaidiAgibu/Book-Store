package com.saidi.book_store.service;

import com.saidi.book_store.dto.AddToCartDto;
import com.saidi.book_store.dto.ShoppingCartDto;
import com.saidi.book_store.exceptions.NotAuthorizedException;
import com.saidi.book_store.exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ShoppingCartService {
    public ResponseEntity<String> addToCart(AddToCartDto addToCartDto) throws NotAuthorizedException, NotFoundException;

    public ResponseEntity<ShoppingCartDto> getCartItems() throws NotAuthorizedException;

    public ResponseEntity<String> removeFromCart(Long cartItemId) throws NotFoundException, NotAuthorizedException;
    public ResponseEntity<String> createOrder() throws NotAuthorizedException, NotFoundException;
}
