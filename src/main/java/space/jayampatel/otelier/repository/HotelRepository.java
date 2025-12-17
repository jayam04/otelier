package space.jayampatel.otelier.repository;

import space.jayampatel.otelier.model.Hotel;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends MongoRepository<Hotel, String> {
    boolean existsById(String id);
}
