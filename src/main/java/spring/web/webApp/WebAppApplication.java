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
		Employee employee = new Employee(10L, "SK", "KK");
		repository.save(employee);
	}
}
