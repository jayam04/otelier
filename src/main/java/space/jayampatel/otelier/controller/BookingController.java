package space.jayampatel.otelier.controller;

import space.jayampatel.otelier.dto.BookingResponse;
import space.jayampatel.otelier.dto.CreateBookingRequest;
import space.jayampatel.otelier.model.Booking;
import space.jayampatel.otelier.service.BookingService;
import space.jayampatel.otelier.security.AuthenticationContext;
import space.jayampatel.otelier.exception.UnauthorizedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hotels/{hotelId}/bookings")
public class BookingController {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private AuthenticationContext authContext;
    
    /**
     * GET /api/hotels/{hotelId}/bookings
     * List all bookings for a hotel, optionally filtered by date range
     */
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getBookings(
            @PathVariable String hotelId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("GET /api/hotels/{}/bookings - startDate: {}, endDate: {}", 
                    hotelId, startDate, endDate);
        
        List<Booking> bookings = bookingService.getBookings(hotelId, startDate, endDate);
        
        // Convert to response DTOs
        List<BookingResponse> response = bookings.stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());
        
        logger.info("Returning {} bookings", response.size());
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/hotels/{hotelId}/bookings
     * Create a new booking (requires staff or reception role)
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @PathVariable String hotelId,
            @Valid @RequestBody CreateBookingRequest request) {
        
        logger.info("POST /api/hotels/{}/bookings - room: {}, checkIn: {}, checkOut: {}", 
                    hotelId, request.getRoomNumber(), request.getCheckInDate(), request.getCheckOutDate());
        
        // Get current authenticated user
        String userId = authContext.getCurrentUserId();
        String userRole = authContext.getCurrentUserRole();
        
        logger.info("User: {}, Role: {}", userId, userRole);
        
        // Check if user has required role (staff or reception)
        if (!authContext.hasAnyRole("staff", "reception")) {
            logger.warn("Unauthorized booking attempt by user: {} with role: {}", userId, userRole);
            throw new UnauthorizedException(
                "Only staff or reception personnel can create bookings. Your role: " + userRole
            );
        }
        
        // Create booking
        Booking booking = bookingService.createBooking(hotelId, request, userId);
        
        logger.info("Booking created successfully: {}", booking.getId());
        
        // Return response
        BookingResponse response = new BookingResponse(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
