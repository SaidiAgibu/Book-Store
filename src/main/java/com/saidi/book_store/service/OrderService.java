package com.saidi.book_store.service;


import com.saidi.book_store.exceptions.NotAuthorizedException;
import com.saidi.book_store.models.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface OrderService {
    public ResponseEntity<?> checkOut(Order order) throws NotAuthorizedException;
    
}
