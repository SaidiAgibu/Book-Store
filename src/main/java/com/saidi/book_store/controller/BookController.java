package com.saidi.book_store.controller;

import com.saidi.book_store.dto.UserReviewsDto;
import com.saidi.book_store.exceptions.NotFoundException;
import com.saidi.book_store.models.Book;
import com.saidi.book_store.models.UserReview;
import com.saidi.book_store.service.UserService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Setter
@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private UserService userService;

    @Autowired
    public BookController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Book>> viewBookCatalog() throws NotFoundException {
        return userService.viewBookCatalog();
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Book> viewBookDetails(@PathVariable("bookId") Long bookId) throws NotFoundException {
        return userService.viewBookDetails(bookId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "category", required = false) String category) {
        List<Book> filteredBooks = userService.searchBooks(title, author, category);
        return ResponseEntity.ok(filteredBooks);
    }





}
