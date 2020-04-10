package com.alexbelke.springbootdemo;

import com.alexbelke.springbootdemo.model.Employee;
import com.alexbelke.springbootdemo.model.Order;
import com.alexbelke.springbootdemo.model.Status;
import com.alexbelke.springbootdemo.repository.EmployeeRepository;
import com.alexbelke.springbootdemo.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class LoadDatabase {

	@Bean
	CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository) {
		return args -> {
			employeeRepository.save(new Employee("Bilbo", "Baggins", "burglar"));
			employeeRepository.save(new Employee("Frodo", "Baggins", "thief"));

			employeeRepository.findAll().forEach(employee -> {
				log.info("Preloaded " + employee);
			});

			// tag::order[]
			orderRepository.save(new Order("MacBook Pro", Status.COMPLETED));
			orderRepository.save(new Order("iPhone", Status.IN_PROGRESS));

			orderRepository.findAll().forEach(order -> {
				log.info("Preloaded " + order);
			});
			// end::order[]
		};
	}
}
