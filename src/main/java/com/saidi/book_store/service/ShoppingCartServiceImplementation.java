package com.saidi.book_store.service;

import com.codevirtus.Pesepay;
import com.codevirtus.payments.Transaction;
import com.codevirtus.response.Response;
import com.saidi.book_store.dto.AddToCartDto;
import com.saidi.book_store.dto.CartItemsDto;
import com.saidi.book_store.dto.ShoppingCartDto;
import com.saidi.book_store.exceptions.NotAuthorizedException;
import com.saidi.book_store.exceptions.NotFoundException;
import com.saidi.book_store.models.*;
import com.saidi.book_store.repository.*;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Data
@Service
@Transactional
public class ShoppingCartServiceImplementation implements ShoppingCartService{

    @Value("${PESEPAY_INTEGRATION_KEY}")
    private String INTEGRATION_KEY;
    @Value("${PESEPAY_ENCRIPTION_KEY}")
    private String ENCRIPTION_KEY;
    @Value("${spring.mail.username}")
    private String fromEmail;
    private static final String CURRENCY_USD = "USD";


    private UserRepository userRepository;
    private BooksRepository booksRepository;
    private ShoppingCartRepository shoppingCartRepository;
    private UserService userService;
    private OrderRepository orderRepository;
    private JavaMailSender javaMailSender;

    @Autowired
    public ShoppingCartServiceImplementation(UserRepository userRepository, BooksRepository booksRepository,ShoppingCartRepository shoppingCartRepository, UserService userService,OrderRepository orderRepository,JavaMailSender javaMailSender) {
        this.userRepository = userRepository;
        this.booksRepository = booksRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.userService =userService;
        this.orderRepository =orderRepository;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public ResponseEntity<String> addToCart(AddToCartDto addToCartDto) throws NotAuthorizedException, NotFoundException {

        Book book = userService.findById(addToCartDto.getBookId());

        ShoppingCart cart = new ShoppingCart();
        cart.setBook(book);
        cart.setQuantity(addToCartDto.getQuantity());
        cart.setCreatedAt(LocalDateTime.now());

        // Getting the currently authenticated user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            User loggedInUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                    () -> new NotAuthorizedException("User not found")
            );
            cart.setUser(loggedInUser);
            shoppingCartRepository.save(cart);
            return ResponseEntity.ok().body("Book added to cart successfully");
        } else {
            throw new NotAuthorizedException("Please login to continue");
        }


    }

    @Override
    public ResponseEntity<ShoppingCartDto> getCartItems() throws NotAuthorizedException {
        // Getting the currently authenticated user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            User loggedInUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                    () -> new NotAuthorizedException("User not found")
            );

            List<ShoppingCart> cartList = shoppingCartRepository.findAllByUserOrderByCreatedAtDesc(loggedInUser);

            List<CartItemsDto> cartItems = new ArrayList<>();
            double totalCost = 0;
            for (ShoppingCart cart: cartList) {
                CartItemsDto cartItemDto = new CartItemsDto(cart);
                totalCost += cartItemDto.getQuantity() * cart.getBook().getPrice();
                cartItems.add(cartItemDto);
            }

            ShoppingCartDto cartDto = new ShoppingCartDto();
            cartDto.setTotalCost(totalCost);
            cartDto.setCartItemsList(cartItems);


            return ResponseEntity.ok().body(cartDto);
        } else {
            throw new NotAuthorizedException("Please login to continue");
        }

    }

    @Override
    public ResponseEntity<String> removeFromCart(Long cartItemId) throws NotFoundException, NotAuthorizedException {
        Optional<ShoppingCart> cart = shoppingCartRepository.findById(cartItemId);
        if(cart.isEmpty()) {
            throw  new NotFoundException("cart item id is invalid: " + cartItemId);
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            User loggedInUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                    () -> new NotAuthorizedException("User not found")
            );
            ShoppingCart shoppingCart = cart.get();
            if(shoppingCart.getUser() != loggedInUser) {
                throw new NotAuthorizedException("User not found");
            }
            shoppingCartRepository.delete(shoppingCart);
            return ResponseEntity.ok().body("cart removed successfully");
        }  else {
            throw new NotAuthorizedException("Please login to continue");
        }


    }


    @Override
    public ResponseEntity<String> createOrder() throws NotAuthorizedException, NotFoundException {

        try {
            // Getting the currently authenticated user
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                User loggedInUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                        () -> new NotAuthorizedException("User not found")
                );

                List<ShoppingCart> cartList = shoppingCartRepository.findAllByUserOrderByCreatedAtDesc(loggedInUser);
                double total = 0;
                String paymentReason = "";
                for (ShoppingCart cart : cartList) {
                    total += cart.getQuantity() * cart.getBook().getPrice();
                    if (paymentReason.isEmpty()) {
                        paymentReason = cart.getBook().getTitle();
                    } else {
                        paymentReason += ",\n" + cart.getBook().getTitle();
                    }
                }

                Pesepay pesepay = new Pesepay(INTEGRATION_KEY, ENCRIPTION_KEY);
                pesepay.setResultUrl("http://localhost:5000");
                pesepay.setReturnUrl("http://localhost:5000");

                Transaction transaction = pesepay.createTransaction(total, CURRENCY_USD, paymentReason);
                Response response = pesepay.initiateTransaction(transaction);
                if (response.isSuccess()) {
                    String referenceNumber = response.getReferenceNumber();
                    String pollUrl = response.getPollUrl();
                    String redirectUrl = response.getRedirectUrl();

                    log.info("Redirect URL: {}", redirectUrl);

                    Response checkPaymentStatus = pesepay.checkPayment(referenceNumber);
                    if(checkPaymentStatus.isSuccess()) {
                        if(checkPaymentStatus.isPaid()) {
                            Order newOrder = new Order();
                            newOrder.setOrderId(UUID.randomUUID());
                            newOrder.setUser(loggedInUser);
                            newOrder.setOrderDate(LocalDateTime.now());

                            List<OrderItems> orderItems = new ArrayList<>();
                            double totalCost = 0;
                            for (ShoppingCart cart : cartList) {
                                OrderItems orderItem = new OrderItems();
                                orderItem.setBook(cart.getBook());
                                orderItem.setQuantity(cart.getQuantity());
                                orderItem.setPrice(cart.getBook().getPrice());
                                totalCost += cart.getQuantity() * cart.getBook().getPrice();
                                orderItems.add(orderItem);
                            }

                            newOrder.setStatus(Order.Status.COMPLETED);
                            newOrder.setOrderItems(orderItems);
                            newOrder.setTotalCost(totalCost);

                            // Save the order in the database and remove items from the cart
                            orderRepository.save(newOrder);
                            shoppingCartRepository.deleteAll(cartList);

                            //sending the book to the user's email
                            try {
                                MimeMessage message = getMimeMessage();
                                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                                helper.setPriority(1);
                                helper.setSubject("Your order " + newOrder.getOrderId() + " was successful");
                                helper.setFrom(fromEmail);
                                helper.setText("Thank you for buying from us we will continue providing you latest books\n your book is attached below");
                                helper.setTo(loggedInUser.getEmail());
                                //helper.addAttachment();

                                javaMailSender.send(message);

                            } catch (Exception e) {
                                throw  new RuntimeException(e.getMessage());
                            }
                            return ResponseEntity.ok().body("Order created successfully");
                        } else {
                            return ResponseEntity.ok().body("Failed to create order");
                        }
                    }

                } else {
                    // Handle payment failure gracefully
                    log.error("Payment failed. Error message: {}", response.getMessage());
                    // Redirect the user to an error page or return an error response
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed. Please try again.");
                }
            } else {
                throw new NotAuthorizedException("Please login to continue");
            }
        } catch (NotAuthorizedException ex) {
            log.error("User not authorized: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authorized. Please log in.");
        } catch (Exception ex) {
            log.error("Internal server error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }

        return null;
    }

    private MimeMessage getMimeMessage() {
        return javaMailSender.createMimeMessage();
    }



}





