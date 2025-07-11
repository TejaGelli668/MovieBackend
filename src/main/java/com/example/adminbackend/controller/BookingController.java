//package com.example.adminbackend.controller;
//
//import com.example.adminbackend.dto.ApiResponse;
//import com.example.adminbackend.dto.BookingRequest;
//import com.example.adminbackend.dto.BookingResponse;
//import com.example.adminbackend.entity.Booking;
//import com.example.adminbackend.entity.BookingStatus;
//import com.example.adminbackend.entity.User;
//import com.example.adminbackend.service.BookingService;
//import com.example.adminbackend.service.UserService;
//import com.example.adminbackend.security.JwtUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.servlet.http.HttpServletRequest;
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/bookings")
//@CrossOrigin(origins = "http://localhost:3000")
//public class BookingController {
//
//    @Autowired
//    private BookingService bookingService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private JwtUtils jwtUtils;
//
//    // ENHANCED CREATE BOOKING WITH FOOD ITEMS
//    @PostMapping
//    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@RequestBody BookingRequest request) {
//        try {
//            // Validate food items if present
//            if (request.getFoodItems() != null && !request.getFoodItems().isEmpty()) {
//                bookingService.validateFoodItems(request.getFoodItems());
//            }
//
//            BookingResponse booking = bookingService.createBookingWithFood(request);
//            return ResponseEntity.ok(new ApiResponse<>(true, "Booking created successfully", booking));
//        } catch (Exception e) {
//            return ResponseEntity.status(500)
//                    .body(new ApiResponse<>(false, "Failed to create booking: " + e.getMessage(), null));
//        }
//    }
//
//    // GET BOOKING WITH FOOD ITEMS
//    @GetMapping("/details/{id}")
//    public ResponseEntity<ApiResponse<BookingResponse>> getBookingDetails(@PathVariable Long id) {
//        try {
//            BookingResponse booking = bookingService.getBookingWithFoodItems(id);
//            return ResponseEntity.ok(new ApiResponse<>(true, "Booking details retrieved successfully", booking));
//        } catch (Exception e) {
//            return ResponseEntity.status(404)
//                    .body(new ApiResponse<>(false, "Booking not found: " + e.getMessage(), null));
//        }
//    }
//
//    // Get all bookings for the current user
//    @GetMapping("/user")
//    public ResponseEntity<?> getUserBookings(HttpServletRequest request) {
//        try {
//            // Extract token from Authorization header
//            String token = extractTokenFromRequest(request);
//            if (token == null) {
//                return ResponseEntity.status(401).body("No token provided");
//            }
//
//            // Validate token first
//            if (!jwtUtils.validateToken(token)) {
//                return ResponseEntity.status(401).body("Invalid or expired token");
//            }
//
//            // Get user email from token
//            String email = jwtUtils.extractUsername(token);
//
//            // Find user by email (your UserService.findByUsername actually uses email)
//            User user = findUserByEmailOrUsername(email);
//
//            if (user == null) {
//                return ResponseEntity.status(404).body("User not found");
//            }
//
//            // Get user's bookings
//            List<Booking> userBookings = bookingService.getBookingsByUserId(user.getId());
//
//            return ResponseEntity.ok(userBookings);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error fetching user bookings: " + e.getMessage());
//        }
//    }
//
//    // Get specific booking by ID (only if it belongs to the current user)
//    @GetMapping("/{bookingId}")
//    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId, HttpServletRequest request) {
//        try {
//            String token = extractTokenFromRequest(request);
//            if (token == null) {
//                return ResponseEntity.status(401).body("No token provided");
//            }
//
//            if (!jwtUtils.validateToken(token)) {
//                return ResponseEntity.status(401).body("Invalid or expired token");
//            }
//
//            String email = jwtUtils.extractUsername(token);
//            User user = findUserByEmailOrUsername(email);
//
//            if (user == null) {
//                return ResponseEntity.status(404).body("User not found");
//            }
//
//            Optional<Booking> booking = bookingService.getBookingById(bookingId);
//
//            if (booking.isPresent()) {
//                // Check if booking belongs to the current user
//                if (booking.get().getUser().getId().equals(user.getId())) {
//                    return ResponseEntity.ok(booking.get());
//                } else {
//                    return ResponseEntity.status(403).body("Access denied to this booking");
//                }
//            } else {
//                return ResponseEntity.status(404).body("Booking not found");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error fetching booking: " + e.getMessage());
//        }
//    }
//
//    // Cancel a booking (only if it belongs to the current user)
//    @PutMapping("/{bookingId}/cancel")
//    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId, HttpServletRequest request) {
//        try {
//            String token = extractTokenFromRequest(request);
//            if (token == null) {
//                return ResponseEntity.status(401).body("No token provided");
//            }
//
//            if (!jwtUtils.validateToken(token)) {
//                return ResponseEntity.status(401).body("Invalid or expired token");
//            }
//
//            String email = jwtUtils.extractUsername(token);
//            User user = findUserByEmailOrUsername(email);
//
//            if (user == null) {
//                return ResponseEntity.status(404).body("User not found");
//            }
//
//            Optional<Booking> booking = bookingService.getBookingById(bookingId);
//
//            if (booking.isPresent()) {
//                // Check if booking belongs to the current user
//                if (booking.get().getUser().getId().equals(user.getId())) {
//                    // Check if booking can be cancelled (not already cancelled)
//                    if (BookingStatus.CANCELLED.equals(booking.get().getStatus())) {
//                        return ResponseEntity.badRequest().body("Booking is already cancelled");
//                    }
//
//                    // Cancel the booking
//                    Booking cancelledBooking = bookingService.cancelBooking(bookingId);
//                    return ResponseEntity.ok(cancelledBooking);
//                } else {
//                    return ResponseEntity.status(403).body("Access denied to this booking");
//                }
//            } else {
//                return ResponseEntity.status(404).body("Booking not found");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error cancelling booking: " + e.getMessage());
//        }
//    }
//
//    // Helper method to extract token from Authorization header
//    private String extractTokenFromRequest(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//
//    // Helper method to find user by email or username
//    private User findUserByEmailOrUsername(String identifier) {
//        try {
//            // Use the simple UserService method
//            return userService.findByUsername(identifier);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}
package com.example.adminbackend.controller;

import com.example.adminbackend.dto.ApiResponse;
import com.example.adminbackend.dto.BookingRequest;
import com.example.adminbackend.dto.BookingResponse;
import com.example.adminbackend.entity.Booking;
import com.example.adminbackend.entity.BookingStatus;
import com.example.adminbackend.entity.User;
import com.example.adminbackend.service.BookingService;
import com.example.adminbackend.service.UserService;
import com.example.adminbackend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:3000")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @RequestBody BookingRequest request,
            HttpServletRequest httpRequest) {
        try {
            if (request.getFoodItems() != null && !request.getFoodItems().isEmpty()) {
                bookingService.validateFoodItems(request.getFoodItems());
            }

            BookingResponse booking;

            if ("stripe".equals(request.getPaymentMethod()) || request.getPaymentIntentId() != null) {
                User currentUser = getCurrentUserFromRequest(httpRequest);
                booking = bookingService.createBookingWithFood(request, currentUser);
            } else {
                booking = bookingService.createBookingWithFood(request);
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Booking created successfully", booking));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>(false, "Failed to create booking: " + e.getMessage(), null));
        }
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingDetails(@PathVariable Long id) {
        try {
            BookingResponse booking = bookingService.getBookingWithFoodItems(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Booking details retrieved successfully", booking));
        } catch (Exception e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(false, "Booking not found: " + e.getMessage(), null));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserBookings(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null) {
                return ResponseEntity.status(401).body("No token provided");
            }

            if (!jwtUtils.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            String email = jwtUtils.extractUsername(token);
            User user = findUserByEmailOrUsername(email);

            if (user == null) {
                return ResponseEntity.status(404).body("User not found");
            }

            List<Booking> userBookings = bookingService.getBookingsByUserId(user.getId());
            return ResponseEntity.ok(userBookings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error fetching user bookings: " + e.getMessage());
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null) {
                return ResponseEntity.status(401).body("No token provided");
            }

            if (!jwtUtils.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            String email = jwtUtils.extractUsername(token);
            User user = findUserByEmailOrUsername(email);

            if (user == null) {
                return ResponseEntity.status(404).body("User not found");
            }

            Optional<Booking> booking = bookingService.getBookingById(bookingId);

            if (booking.isPresent()) {
                if (booking.get().getUser() != null && booking.get().getUser().getId().equals(user.getId())) {
                    return ResponseEntity.ok(booking.get());
                } else {
                    return ResponseEntity.status(403).body("Access denied to this booking");
                }
            } else {
                return ResponseEntity.status(404).body("Booking not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error fetching booking: " + e.getMessage());
        }
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId, HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null) {
                return ResponseEntity.status(401).body("No token provided");
            }

            if (!jwtUtils.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }

            String email = jwtUtils.extractUsername(token);
            User user = findUserByEmailOrUsername(email);

            if (user == null) {
                return ResponseEntity.status(404).body("User not found");
            }

            Optional<Booking> booking = bookingService.getBookingById(bookingId);

            if (booking.isPresent()) {
                if (booking.get().getUser() != null && booking.get().getUser().getId().equals(user.getId())) {
                    if (BookingStatus.CANCELLED.equals(booking.get().getStatus())) {
                        return ResponseEntity.badRequest().body("Booking is already cancelled");
                    }

                    Booking cancelledBooking = bookingService.cancelBooking(bookingId);
                    return ResponseEntity.ok(cancelledBooking);
                } else {
                    return ResponseEntity.status(403).body("Access denied to this booking");
                }
            } else {
                return ResponseEntity.status(404).body("Booking not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error cancelling booking: " + e.getMessage());
        }
    }

    private User getCurrentUserFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            throw new RuntimeException("Invalid or missing authentication token");
        }

        String email = jwtUtils.extractUsername(token);
        User user = findUserByEmailOrUsername(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return user;
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private User findUserByEmailOrUsername(String identifier) {
        try {
            return userService.findByUsername(identifier);
        } catch (Exception e) {
            return null;
        }
    }
}