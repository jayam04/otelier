package space.jayampatel.otelier.repository;

import space.jayampatel.otelier.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    
    // Find all bookings for a specific hotel
    List<Booking> findByHotelId(String hotelId);
    
    // Find bookings by hotel and date range
    List<Booking> findByHotelIdAndCheckInDateGreaterThanEqualAndCheckOutDateLessThanEqual(
        String hotelId, LocalDate startDate, LocalDate endDate
    );
    
    // Check for conflicting bookings (same room, overlapping dates)
    @Query("{ 'hotelId': ?0, 'roomNumber': ?1, 'status': 'CONFIRMED', " +
           "$or: [ " +
           "  { 'checkInDate': { $lte: ?3 }, 'checkOutDate': { $gte: ?2 } }, " +
           "  { 'checkInDate': { $gte: ?2, $lte: ?3 } }, " +
           "  { 'checkOutDate': { $gte: ?2, $lte: ?3 } } " +
           "] }")
    List<Booking> findConflictingBookings(String hotelId, String roomNumber, 
                                          LocalDate checkInDate, LocalDate checkOutDate);
}
