package space.jayampatel.otelier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
		info = @Info(
				title = "Otelier Hotel Booking API",
				version = "1.0",
				description = "API documentation for managing hotel bookings"
		)
)
@SpringBootApplication
public class OtelierApplication {

	public static void main(String[] args) {
		SpringApplication.run(OtelierApplication.class, args);
	}

}
