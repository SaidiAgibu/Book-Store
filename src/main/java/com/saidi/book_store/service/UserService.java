package com.saidi.book_store.service;

import com.saidi.book_store.dto.LoginDto;
import com.saidi.book_store.dto.UserReviewsDto;
import com.saidi.book_store.exceptions.NotAuthorizedException;
import com.saidi.book_store.exceptions.NotFoundException;
import com.saidi.book_store.models.Book;
import com.saidi.book_store.models.User;
import com.saidi.book_store.requests.AuthResponse;
import com.saidi.book_store.requests.ChangePasswordRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public interface UserService {
    public ResponseEntity<String> registerUser(User user);
    public ResponseEntity<String> verifyAccount(String token);
    public ResponseEntity<AuthResponse> loginUser(LoginDto loginDto);

    public ResponseEntity<User> viewProfile(Long userId) throws NotFoundException;

    public ResponseEntity<?> updateProfile(Long userId, User updatedUser) throws NotFoundException;


    public ResponseEntity<String> changeUserPassword(Long id, ChangePasswordRequest request) throws NotFoundException;

    public ResponseEntity<List<Book>> viewBookCatalog() throws NotFoundException;

    public ResponseEntity<Book> viewBookDetails(Long bookId) throws NotFoundException;

    public List<Book> searchBooks(String title, String author, String category);

    public ResponseEntity<String> addReview(UserReviewsDto userReview) throws NotAuthorizedException, NotFoundException;

    public double getAverageRatingForBook(Long bookId);
    public Book findById(Long bookId) throws NotFoundException;
}
