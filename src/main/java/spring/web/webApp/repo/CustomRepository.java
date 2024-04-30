package spring.web.webApp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.web.webApp.pojo.Employee;

public interface CustomRepository extends JpaRepository<Employee, Long> {
}
