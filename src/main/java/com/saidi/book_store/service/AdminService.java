package com.saidi.book_store.service;

import com.saidi.book_store.dto.BookDto;
import com.saidi.book_store.dto.OrderDto;
import com.saidi.book_store.dto.UserDto;
import com.saidi.book_store.exceptions.NotFoundException;
import com.saidi.book_store.models.Book;
import com.saidi.book_store.models.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface AdminService {
    public ResponseEntity<String> addBook(BookDto bookDto) throws IOException;

    public ResponseEntity<List<Book>> getAllBooks() throws NotFoundException;

    public ResponseEntity<String> deleteBook(Long id) throws NotFoundException;

    public ResponseEntity<String> updateBook(Long id, BookDto bookDto) throws NotFoundException;

    public ResponseEntity<List<UserDto>> getAllUsers() throws NotFoundException;

    public ResponseEntity<List<OrderDto>> getAllOrders() throws NotFoundException;
}
