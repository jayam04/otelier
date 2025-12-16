package space.jayampatel.otelier.repository;

import space.jayampatel.otelier.model.HotelAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelAssignmentRepository extends MongoRepository<HotelAssignment, String> {
    
    List<HotelAssignment> findByUserId(String userId);
    
    List<HotelAssignment> findByHotelId(String hotelId);
    
    Optional<HotelAssignment> findByUserIdAndHotelId(String userId, String hotelId);
    
    boolean existsByUserIdAndHotelId(String userId, String hotelId);
}
