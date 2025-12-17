package space.jayampatel.otelier.service;

import space.jayampatel.otelier.model.Booking;
import space.jayampatel.otelier.repository.BookingRepository;
import space.jayampatel.otelier.dto.CreateBookingRequest;
import space.jayampatel.otelier.exception.BookingConflictException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailNotificationService emailNotificationService;

    /**
     * Get all bookings for a hotel, optionally filtered by date range
     */
    public List<Booking> getBookings(String hotelId, LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching bookings for hotel: {}, startDate: {}, endDate: {}",
                hotelId, startDate, endDate);

        if (startDate != null && endDate != null) {
            return bookingRepository.findByHotelIdAndCheckInDateGreaterThanEqualAndCheckOutDateLessThanEqual(
                    hotelId, startDate, endDate);
        }

        return bookingRepository.findByHotelId(hotelId);
    }

    /**
     * Create a new booking with conflict detection
     */
    public Booking createBooking(String hotelId, CreateBookingRequest request, String userId) {
        logger.info("Creating booking for hotel: {}, room: {}, user: {}",
                hotelId, request.getRoomNumber(), userId);

        // Validation: Check-out must be after check-in
        if (request.getCheckOutDate().isBefore(request.getCheckInDate()) ||
                request.getCheckOutDate().isEqual(request.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // Validation: Check-in must be in the future or today
        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        // Conflict detection: Check if room is already booked
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                hotelId,
                request.getRoomNumber(),
                request.getCheckInDate(),
                request.getCheckOutDate());

        if (!conflicts.isEmpty()) {
            logger.warn("Booking conflict detected for hotel: {}, room: {}",
                    hotelId, request.getRoomNumber());
            throw new BookingConflictException(
                    String.format("Room %s is already booked for the selected dates",
                            request.getRoomNumber()));
        }

        // Create booking
        Booking booking = new Booking();
        booking.setHotelId(hotelId);
        booking.setUserId(userId);
        booking.setGuestName(request.getGuestName());
        booking.setGuestEmail(request.getGuestEmail());
        booking.setRoomNumber(request.getRoomNumber());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setCreatedBy(userId);

        // Save to database
        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Booking created successfully: {}", savedBooking.getId());

        // Send notification (async, won't block)
        notificationService.notifyBookingCreated(savedBooking);
        emailNotificationService.sendBookingCreatedEmail(savedBooking);

        return savedBooking;
    }
}
