package space.jayampatel.otelier.service;

import space.jayampatel.otelier.model.HotelAssignment;
import space.jayampatel.otelier.repository.HotelAssignmentRepository;
import space.jayampatel.otelier.security.AuthenticationContext;
import space.jayampatel.otelier.exception.UnauthorizedException;
import space.jayampatel.otelier.repository.HotelRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class AuthorizationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelAssignmentRepository assignmentRepository;

    @Autowired
    private AuthenticationContext authContext;

    /**
     * Check if user has access to hotel
     */
    public void checkHotelAccess(String hotelId) {
        String userId = authContext.getCurrentUserId();

        if (!assignmentRepository.existsByUserIdAndHotelId(userId, hotelId)) {
            logger.warn("User {} attempted to access hotel {} without assignment", userId, hotelId);
            throw new UnauthorizedException("You don't have access to this hotel");
        }
    }

    /**
     * Check if user has specific role for hotel
     */
    public void checkHotelRole(String hotelId, String... allowedRoles) {
        String userId = authContext.getCurrentUserId();

        HotelAssignment assignment = assignmentRepository.findByUserIdAndHotelId(userId, hotelId)
                .orElseThrow(() -> new UnauthorizedException("You don't have access to this hotel"));

        for (String role : allowedRoles) {
            if (role.equalsIgnoreCase(assignment.getRole())) {
                return;
            }
        }

        throw new UnauthorizedException(
                "Insufficient permissions. Required: " + String.join(" or ", allowedRoles) +
                        ". Your role: " + assignment.getRole());
    }

    /**
     * Get user's assigned hotels
     */
    public List<HotelAssignment> getUserHotels(String userId) {
        return assignmentRepository.findByUserId(userId);
    }

    /**
     * Assign user to hotel
     */
    public HotelAssignment assignUserToHotel(String userId, String hotelId, String role) {
        if (assignmentRepository.existsByUserIdAndHotelId(userId, hotelId)) {
            throw new IllegalArgumentException("User already assigned to this hotel");
        }

        if (!hotelRepository.existsById(hotelId)) {
            throw new IllegalArgumentException("Hotel does not exist");
        }

        HotelAssignment assignment = new HotelAssignment();
        assignment.setUserId(userId);
        assignment.setHotelId(hotelId);
        assignment.setRole(role);
        assignment.setAssignedBy(authContext.getCurrentUserId());

        return assignmentRepository.save(assignment);
    }
}
