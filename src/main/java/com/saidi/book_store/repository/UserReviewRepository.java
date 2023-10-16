package com.saidi.book_store.repository;

import com.saidi.book_store.models.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, Long> {

    @Query(
            "select s from UserReview  s where s.book.bookId = ?1"
    )
    List<UserReview> findByBookId(Long bookId);
}
