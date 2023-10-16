package com.saidi.book_store.service;

import com.saidi.book_store.dto.BookDto;
import com.saidi.book_store.dto.OrderDto;
import com.saidi.book_store.dto.UserDto;
import com.saidi.book_store.exceptions.NotFoundException;
import com.saidi.book_store.models.Book;
import com.saidi.book_store.models.Order;
import com.saidi.book_store.models.User;
import com.saidi.book_store.repository.BooksRepository;
import com.saidi.book_store.repository.OrderRepository;
import com.saidi.book_store.repository.UserRepository;
import com.saidi.book_store.repository.UserReviewRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@Service
public class AdminServiceImplementation implements AdminService{

    private UserRepository userRepository;
    private BooksRepository booksRepository;
    private UserReviewRepository userReviewRepository;
    private OrderRepository orderRepository;

    @Autowired
    public AdminServiceImplementation(UserRepository userRepository, BooksRepository booksRepository, UserReviewRepository userReviewRepository,OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.booksRepository = booksRepository;
        this.userReviewRepository = userReviewRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public ResponseEntity<String> addBook(BookDto bookDto) throws IOException {

        Book book = Book.builder()
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .category(bookDto.getCategory())
                .description(bookDto.getDescription())
                .price(bookDto.getPrice())
                .quantityAvailable(bookDto.getQuantityAvailable())
                .build();


        booksRepository.save(book);
        return ResponseEntity.ok().body("Book added successfully");
    }

    @Override
    public ResponseEntity<List<Book>> getAllBooks() throws NotFoundException {
        List<Book> books = booksRepository.findAll();
        if(books.isEmpty()) {
            throw new NotFoundException("No books found");
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> deleteBook(Long id) throws NotFoundException {
        Optional<Book> book = booksRepository.findById(id);
        if(book.isEmpty()) {
            throw new NotFoundException("No books found");
        }
        booksRepository.deleteById(id);
        return ResponseEntity.ok().body("Book deleted successfully");
    }

    @Override
    public ResponseEntity<String> updateBook(Long id, BookDto bookDto) throws NotFoundException {
        Optional<Book> book = booksRepository.findById(id);
        if(book.isPresent()) {
            Book existingBook = book.get();
            existingBook.setTitle(bookDto.getTitle());
            existingBook.setAuthor(bookDto.getAuthor());
            existingBook.setCategory(bookDto.getCategory());
            existingBook.setDescription(bookDto.getDescription());
            existingBook.setPrice(bookDto.getPrice());
            existingBook.setQuantityAvailable(bookDto.getQuantityAvailable());

            booksRepository.save(existingBook);
            return ResponseEntity.ok().body("Book updated successfully");
        }
        throw new NotFoundException("No books found");
    }

    @Override
    public ResponseEntity<List<UserDto>> getAllUsers() throws NotFoundException {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) {
            throw new NotFoundException("No books found");
        }
        return new ResponseEntity<>(users.stream()
                .map(this::mapToUserDto)
                .toList(), HttpStatus.OK);


    }

    @Override
    public ResponseEntity<List<OrderDto>> getAllOrders() throws NotFoundException {
        List<Order> orders = orderRepository.findAll();
        if(orders.isEmpty()) {
            throw new NotFoundException("Orders not found");
        }
        return new ResponseEntity<>(orders.stream()
                .map(this::mapToOrderDto)
                .toList(),
                HttpStatus.OK
        );
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .roles(user.getRoles())
                .build();
    }

    private OrderDto mapToOrderDto(Order order) {
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus())
                .user(order.getUser())
                .build();
    }
}
