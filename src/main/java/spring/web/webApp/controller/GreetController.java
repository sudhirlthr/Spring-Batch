package spring.web.webApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.web.webApp.pojo.Employee;
import spring.web.webApp.repo.CustomRepository;

import java.util.List;

@RestController
public class GreetController {

    @Autowired
    CustomRepository repository;

    @GetMapping("/hello")
    public String greet(){
        return "greet";
    }

    @GetMapping("/emp")
    public List<Employee> getEmployees(){
        return repository.findAll();
    }
}
