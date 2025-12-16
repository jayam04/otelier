package space.jayampatel.otelier.controller;

import space.jayampatel.otelier.model.HotelAssignment;
import space.jayampatel.otelier.service.AuthorizationService;
import space.jayampatel.otelier.security.AuthenticationContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotel-assignments")
public class HotelAssignmentController {
    
    @Autowired
    private AuthorizationService authorizationService;
    
    @Autowired
    private AuthenticationContext authContext;
    
    /**
     * GET /api/hotel-assignments/my-hotels
     * Get hotels assigned to current user
     */
    @GetMapping("/my-hotels")
    public ResponseEntity<List<HotelAssignment>> getMyHotels() {
        String userId = authContext.getCurrentUserId();
        List<HotelAssignment> hotels = authorizationService.getUserHotels(userId);
        return ResponseEntity.ok(hotels);
    }
    
    /**
     * POST /api/hotel-assignments
     * Assign user to hotel (admin only - for now, any authenticated user can do this for testing)
     */
    @PostMapping
    public ResponseEntity<HotelAssignment> assignUser(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String hotelId = request.get("hotelId");
        String role = request.get("role");
        
        HotelAssignment assignment = authorizationService.assignUserToHotel(userId, hotelId, role);
        return ResponseEntity.ok(assignment);
    }
}
