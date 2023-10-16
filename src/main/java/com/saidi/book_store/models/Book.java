package com.saidi.book_store.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "books")
public class Book {
    @Id
    @SequenceGenerator(
            name = "books_sequence",
            sequenceName = "books_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "books_sequence")
    private Long bookId;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    @Column(name = "file_content",columnDefinition = "BLOB")
    private byte[] fileContent;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String category;
    @Column(nullable = false)
    private Double price;
    private Integer quantityAvailable;
    @OneToMany(mappedBy = "book")
    private List<UserReview> userReviews;



    public double calculateAverageRating() {
        if (userReviews == null || userReviews.isEmpty()) {
            return 0;
        }

        int totalRatings = 0;
        for (UserReview review : userReviews) {
            totalRatings += review.getRating();
        }

        return (double) totalRatings / userReviews.size();
    }

}
