package com.saidi.book_store.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserReview {
    @Id
    @SequenceGenerator(
            name = "reviews_sequence",
            sequenceName = "reviews_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reviews_sequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String review;

    @Override
    public String toString() {
        return "UserReview{" +
                "id=" + id +
                ", rating=" + rating +
                ", review='" + review + '\'' +
                '}';
    }

}

