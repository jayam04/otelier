package space.jayampatel.otelier.controller;

import space.jayampatel.otelier.dto.BookingResponse;
import space.jayampatel.otelier.dto.CreateBookingRequest;
import space.jayampatel.otelier.model.Booking;
import space.jayampatel.otelier.service.BookingService;
import space.jayampatel.otelier.service.AuthorizationService;
import space.jayampatel.otelier.security.AuthenticationContext;

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
    private AuthorizationService authorizationService;
    
    @Autowired
    private AuthenticationContext authContext;
    
    /**
     * GET /api/hotels/{hotelId}/bookings
     * List bookings for hotels user has access to
     */
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getBookings(
            @PathVariable String hotelId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("GET /api/hotels/{}/bookings", hotelId);
        
        // Check hotel access
        authorizationService.checkHotelAccess(hotelId);
        
        List<Booking> bookings = bookingService.getBookings(hotelId, startDate, endDate);
        
        List<BookingResponse> response = bookings.stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());
        
        logger.info("Returning {} bookings", response.size());
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/hotels/{hotelId}/bookings
     * Create booking (requires staff or reception role for the hotel)
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @PathVariable String hotelId,
            @Valid @RequestBody CreateBookingRequest request) {
        
        logger.info("POST /api/hotels/{}/bookings", hotelId);
        
        // Check user has staff/reception role for this hotel
        authorizationService.checkHotelRole(hotelId, "staff", "reception");
        
        String userId = authContext.getCurrentUserId();
        Booking booking = bookingService.createBooking(hotelId, request, userId);
        
        logger.info("Booking created: {}", booking.getId());
        
        BookingResponse response = new BookingResponse(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
