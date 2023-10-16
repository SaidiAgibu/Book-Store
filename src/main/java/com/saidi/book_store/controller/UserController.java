package com.saidi.book_store.controller;

import com.saidi.book_store.dto.UserReviewsDto;
import com.saidi.book_store.exceptions.NotAuthorizedException;
import com.saidi.book_store.exceptions.NotFoundException;
import com.saidi.book_store.models.User;
import com.saidi.book_store.requests.ChangePasswordRequest;
import com.saidi.book_store.service.UserService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Setter
@RestController
@RequestMapping("/api/v1/user")
@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> viewProfile(@PathVariable("userId") Long userId) throws NotFoundException {
        return userService.viewProfile(userId);
    }
    @PutMapping("/profile/update/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable("userId") Long userId, @RequestBody User updatedUser) throws NotFoundException {
        return userService.updateProfile(userId, updatedUser);
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changeUserPassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) throws NotFoundException {
        return userService.changeUserPassword(id, request);
    }

    @PostMapping("/reviews")
    public ResponseEntity<String> addReview(@RequestBody UserReviewsDto userReview) throws NotAuthorizedException, NotFoundException {
        return userService.addReview(userReview);
    }

    @GetMapping("/average-rating/{bookId}")
    public double getAverageRating(@PathVariable Long bookId) {
        return userService.getAverageRatingForBook(bookId);
    }


}
