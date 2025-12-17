package space.jayampatel.otelier.controller;

import space.jayampatel.otelier.model.Hotel;
import space.jayampatel.otelier.repository.HotelRepository;
import space.jayampatel.otelier.security.AuthenticationContext;
import space.jayampatel.otelier.exception.UnauthorizedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private AuthenticationContext authContext;

    /**
     * ADMIN ONLY
     * POST /api/hotels
     */
    @PostMapping
    public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel) {

        // Optional extra safety (in addition to SecurityConfig)
        if (!authContext.hasRole("admin")) {
            throw new UnauthorizedException("Admin access required");
        }

        Hotel savedHotel = hotelRepository.save(hotel);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHotel);
    }
}
