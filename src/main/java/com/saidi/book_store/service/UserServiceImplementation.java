package com.saidi.book_store.service;

import com.codevirtus.Pesepay;
import com.saidi.book_store.config.JWTGenerator;
import com.saidi.book_store.dto.LoginDto;
import com.saidi.book_store.dto.UserReviewsDto;
import com.saidi.book_store.exceptions.InvalidInputException;
import com.saidi.book_store.exceptions.NotAuthorizedException;
import com.saidi.book_store.exceptions.NotFoundException;
import com.saidi.book_store.models.*;
import com.saidi.book_store.repository.*;
import com.saidi.book_store.requests.AuthResponse;
import com.saidi.book_store.requests.ChangePasswordRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Data
@Service
public class UserServiceImplementation implements UserService{
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private ConfirmationRepository confirmationRepository;
    private BooksRepository booksRepository;
    private UserReviewRepository userReviewRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;
    private AuthenticationManager authenticationManager;
    private JWTGenerator jwtGenerator;


    @Autowired
    public UserServiceImplementation(UserRepository userRepository, RoleRepository roleRepository, ConfirmationRepository confirmationRepository,PasswordEncoder passwordEncoder,EmailService emailService,AuthenticationManager authenticationManager, JWTGenerator jwtGenerator,BooksRepository booksRepository,UserReviewRepository userReviewRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.confirmationRepository = confirmationRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
        this.booksRepository = booksRepository;
        this.userReviewRepository = userReviewRepository;
    }

    @Transactional
    @Override
    public ResponseEntity<String> registerUser(User user) {
        Optional<User> user1 = userRepository.findByEmail(user.getEmail());
        if(user1.isPresent()) {
            return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);
        }
        User newUser = new User();
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEnabled(false);

        Role role = roleRepository.findByName("ROLE_USER");
        if(role == null) {
            role = checkRoleExists();
        }
        newUser.setRoles(Arrays.asList(role));
        userRepository.save(newUser);

        Confirmation confirmation = new Confirmation(newUser);
        confirmationRepository.save(confirmation);

        /* Sending email to the client */
        emailService.sendEmailToUser(newUser.getFirstName(), newUser.getEmail(), confirmation.getToken());
        return new ResponseEntity<>("Please verify your email to complete registration", HttpStatus.OK);
    }

    @Transactional
    @Override
    public ResponseEntity<String> verifyAccount(String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token);
        User user = userRepository.findByEmail(confirmation.getUser().getEmail()).get();
        user.setEnabled(true);
        userRepository.save(user);
        return new ResponseEntity<>("Account verified successfully", HttpStatus.OK);
    }

    @Transactional
    @Override
    public ResponseEntity<AuthResponse> loginUser(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);
        return new ResponseEntity<>(new AuthResponse(token), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<User> viewProfile(Long userId) throws NotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    @Transactional
    @Override
    public ResponseEntity<?> updateProfile(Long userId, User updatedUser) throws NotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User existingUser = user.get();
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());

        userRepository.save(existingUser);

        return ResponseEntity.ok().body("Profile updated successfully");
    }

    @Transactional
    @Override
    public ResponseEntity<String> changeUserPassword(Long id, ChangePasswordRequest request) {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            if(passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);
                return ResponseEntity.ok("Password changed successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect old password.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @Override
    public ResponseEntity<List<Book>> viewBookCatalog() throws NotFoundException {
        List<Book> books = booksRepository.findAll();
        if(books.isEmpty()) {
            throw new NotFoundException("No books found");
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Book> viewBookDetails(Long bookId) throws NotFoundException {
        Optional<Book> book = booksRepository.findById(bookId);
        if(book.isEmpty()) {
            throw new NotFoundException("Book not found");
        }
        return new ResponseEntity<>(book.get(), HttpStatus.OK);
    }

    @Override
    public List<Book> searchBooks(String title, String author, String category) {
        List<Book> books;

        if (title != null && author != null && category != null) {
            // Search by title, author, and category
            books = booksRepository.findByTitleIgnoreCaseAndAuthorIgnoreCaseAndCategoryIgnoreCase(title, author, category);
        } else if (title != null && author != null) {
            // Search by title and author
            books = booksRepository.findByTitleIgnoreCaseAndAuthorIgnoreCase(title, author);
        } else if (title != null && category != null) {
            // Search by title and genre
            books = booksRepository.findByTitleIgnoreCaseAndCategoryIgnoreCase(title, category);
        } else if (author != null && category != null) {
            // Search by author and genre
            books = booksRepository.findByAuthorIgnoreCaseAndCategoryIgnoreCase(author, category);
        } else if (title != null) {
            // Search by title
            books = booksRepository.findByTitleIgnoreCase(title);
        } else if (author != null) {
            // Search by author
            books = booksRepository.findByAuthorIgnoreCase(author);
        } else if (category != null) {
            // Search by genre
            books = booksRepository.findByCategoryIgnoreCase(category);
        } else {
            // If no criteria provided, return all books
            books = booksRepository.findAll();
        }

        return books;
    }

    @Transactional
    @Override
    public ResponseEntity<String> addReview(UserReviewsDto userReview) throws NotAuthorizedException, NotFoundException {
        // Validate input
        if (userReview.getReview() == null || userReview.getRating() < 1 || userReview.getRating() > 5) {
            return new ResponseEntity<>("Invalid review input", HttpStatus.BAD_REQUEST);
        }

        UserReview review = new UserReview();
        review.setReview(userReview.getReview());
        review.setRating(userReview.getRating());

        // Getting the currently authenticated user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            User loggedInUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                    () -> new NotAuthorizedException("User not found")
            );
            review.setUser(loggedInUser);

            // Find the book by ID
            Book book = booksRepository.findById(userReview.getBook().getBookId()).orElseThrow(
                    () -> new NotFoundException("Book not found")
            );
            review.setBook(book);

            // Save the review
            userReviewRepository.save(review);

            return ResponseEntity.ok().body("Review added successfully");
        } else {
            throw new NotAuthorizedException("Please login to rate this book");
        }
    }

    @Override
    public Book findById(Long bookId) throws NotFoundException {
        Optional<Book> optionalProduct = booksRepository.findById(bookId);
        if (optionalProduct.isEmpty()) {
            throw new NotFoundException("product id is invalid: " + bookId);
        }
        return optionalProduct.get();
    }


    @Override
    public double getAverageRatingForBook(Long bookId) {
        List<UserReview> reviews = userReviewRepository.findByBookId(bookId);
        if (reviews.isEmpty()) {
            return 0;
        }

        int totalRatings = 0;
        for (UserReview review : reviews) {
            totalRatings += review.getRating();
            System.out.println(totalRatings);
        }

        return (double) totalRatings / reviews.size();
    }


    private Role checkRoleExists() {
        Role role = new Role();
        role.setName("ROLE_USER");
        return  roleRepository.save(role);

    }
}
