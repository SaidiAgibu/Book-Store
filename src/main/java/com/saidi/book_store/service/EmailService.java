package com.saidi.book_store.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    public void sendEmailToUser(String name, String to, String token);


}
