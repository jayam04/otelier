package space.jayampatel.otelier.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import space.jayampatel.otelier.model.Booking;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    @Value("${notification.slack.webhook-url:}")
    private String slackWebhookUrl;
    
    private final WebClient webClient;
    
    public NotificationService() {
        this.webClient = WebClient.builder().build();
    }
    
    public void notifyBookingCreated(Booking booking) {
        if (slackWebhookUrl == null || slackWebhookUrl.isEmpty()) {
            logger.warn("Slack webhook URL not configured. Skipping notification.");
            return;
        }
        
        try {
            String message = String.format(
                "🏨 *New Booking Created*\n" +
                "Hotel ID: %s\n" +
                "Guest: %s (%s)\n" +
                "Room: %s\n" +
                "Check-in: %s\n" +
                "Check-out: %s\n" +
                "Status: %s",
                booking.getHotelId(),
                booking.getGuestName(),
                booking.getGuestEmail(),
                booking.getRoomNumber(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus()
            );
            
            Map<String, String> payload = new HashMap<>();
            payload.put("text", message);
            
            webClient.post()
                .uri(slackWebhookUrl)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                    response -> logger.info("Slack notification sent successfully"),
                    error -> logger.error("Failed to send Slack notification: {}", error.getMessage())
                );
                
            logger.info("Notification sent for booking: {}", booking.getId());
            
        } catch (Exception e) {
            logger.error("Error sending notification: {}", e.getMessage());
        }
    }
}