package space.jayampatel.otelier.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import space.jayampatel.otelier.model.Employee;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
}
