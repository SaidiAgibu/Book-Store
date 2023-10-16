package com.saidi.book_store.controller;

import com.saidi.book_store.dto.BookDto;
import com.saidi.book_store.dto.OrderDto;
import com.saidi.book_store.dto.UserDto;
import com.saidi.book_store.exceptions.NotFoundException;
import com.saidi.book_store.models.Book;
import com.saidi.book_store.models.Order;
import com.saidi.book_store.service.AdminService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Data
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/add-book")
    public ResponseEntity<String> addBook(@RequestBody BookDto bookDto) throws IOException {
        return adminService.addBook(bookDto);
    }

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() throws NotFoundException {
        return  adminService.getAllBooks();
    }

    @DeleteMapping("/book/delete/{id}")
    public ResponseEntity<String>  deleteBook(@PathVariable("id") Long id) throws NotFoundException {
        return adminService.deleteBook(id);
    }

    @PutMapping("/book/update/{id}")
    public ResponseEntity<String>  updateBook(@PathVariable("id") Long id, @RequestBody BookDto bookDto) throws NotFoundException {
        return adminService.updateBook(id, bookDto);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() throws NotFoundException {
        return  adminService.getAllUsers();
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getOrders() throws NotFoundException {
        return  adminService.getAllOrders();
    }






}
