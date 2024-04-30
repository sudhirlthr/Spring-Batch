package spring.web.webApp;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import spring.web.webApp.pojo.Employee;
import spring.web.webApp.repo.CustomRepository;

@SpringBootApplication
@EnableBatchProcessing
public class WebAppApplication implements CommandLineRunner {
	@Autowired
	CustomRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(WebAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Employee employee1 = new Employee(101L, "SK1", "KK1");
		Employee employee2 = new Employee(102L, "SK2", "KK2");
		Employee employee3 = new Employee(103L, "SK3", "KK3");
		Employee employee4 = new Employee(104L, "SK4", "KK4");
		Employee employee5 = new Employee(105L, "SK5", "KK5");
		Employee employee6 = new Employee(106L, "SK6", "KK6");
		Employee employee7 = new Employee(107L, "SK7", "KK7");
		Employee employee8 = new Employee(108L, "SK8", "KK8");
		repository.save(employee1);
		repository.save(employee2);
		repository.save(employee3);
		repository.save(employee4);
		repository.save(employee5);
		repository.save(employee6);
		repository.save(employee7);
		repository.save(employee8);

		Employee employee9 = new Employee(109L, "SK9", "KK9");
		repository.save(employee9);
	}
}
