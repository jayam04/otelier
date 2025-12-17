package space.jayampatel.otelier.service;

import space.jayampatel.otelier.model.Booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailNotificationService {

    private static final Logger logger =
        LoggerFactory.getLogger(EmailNotificationService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${notification.email.support}")
    private String supportEmail;

    public void sendBookingCreatedEmail(Booking booking) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(supportEmail);
            message.setSubject("New Hotel Booking Created");
            message.setText(buildEmailBody(booking));

            mailSender.send(message);
            logger.info("Booking email sent for booking {}", booking.getId());
        } catch (Exception e) {
            logger.error("Failed to send booking email", e);
        }
    }

    private String buildEmailBody(Booking booking) {
        return """
            A new booking has been created.

            Hotel ID: %s
            Guest: %s (%s)
            Room: %s
            Check-in: %s
            Check-out: %s
            Status: %s
            Created By: %s
            """
            .formatted(
                booking.getHotelId(),
                booking.getGuestName(),
                booking.getGuestEmail(),
                booking.getRoomNumber(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getStatus(),
                booking.getCreatedBy()
            );
    }
}
