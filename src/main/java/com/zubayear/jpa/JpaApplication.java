package com.zubayear.jpa;

import com.zubayear.jpa.entity.Order;
import com.zubayear.jpa.entity.Person;
import com.zubayear.jpa.entity.PersonType;
import com.zubayear.jpa.entity.Status;
import com.zubayear.jpa.repository.OrderRepository;
import com.zubayear.jpa.repository.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpaApplication {

	private final OrderRepository orderRepository;
	private final PersonRepository personRepository;

	public JpaApplication(OrderRepository orderRepository, PersonRepository personRepository) {
		this.orderRepository = orderRepository;
		this.personRepository = personRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(JpaApplication.class, args);
	}

	@Bean
	public CommandLineRunner lookUP() {
		return args -> {
//			orderRepository.save(new Order("MacBook Pro", Status.COMPLETED));
//			orderRepository.save(new Order("iPhone", Status.IN_PROGRESS));
			personRepository.save(new Person("Syed Ibna Zubayear", PersonType.DEVELOPER));
			personRepository.save(new Person("Elon Musk", PersonType.TEACHER));
		};
	}
}
