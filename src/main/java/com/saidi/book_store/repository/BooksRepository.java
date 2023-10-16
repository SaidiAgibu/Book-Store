package com.saidi.book_store.repository;

import com.saidi.book_store.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BooksRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(String title, String author, String category);
    List<Book> findByPriceLessThanEqual(Double price);
    List<Book> findByTitleIgnoreCaseAndAuthorIgnoreCaseAndCategoryIgnoreCase(String title, String author, String category);

    List<Book> findByTitleIgnoreCaseAndAuthorIgnoreCase(String title, String author);

    List<Book> findByTitleIgnoreCaseAndCategoryIgnoreCase(String title, String category);

    List<Book> findByAuthorIgnoreCaseAndCategoryIgnoreCase(String author, String category);

    List<Book> findByTitleIgnoreCase(String title);

    List<Book> findByAuthorIgnoreCase(String author);

    List<Book> findByCategoryIgnoreCase(String category);
}
