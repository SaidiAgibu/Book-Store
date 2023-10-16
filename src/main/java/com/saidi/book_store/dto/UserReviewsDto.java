package com.saidi.book_store.dto;

import com.saidi.book_store.models.Book;
import com.saidi.book_store.models.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class UserReviewsDto {
    private Book book;
    private User user;
    private int rating;
    private String review;
}
