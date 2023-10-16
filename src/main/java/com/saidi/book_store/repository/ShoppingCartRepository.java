package com.saidi.book_store.repository;

import com.saidi.book_store.models.ShoppingCart;
import com.saidi.book_store.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    List<ShoppingCart> findAllByUserOrderByCreatedAtDesc(User user);
}
