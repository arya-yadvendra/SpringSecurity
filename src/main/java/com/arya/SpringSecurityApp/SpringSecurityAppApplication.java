package com.arya.SpringSecurityApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.arya.SpringSecurityApp.entity"})
public class SpringSecurityAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityAppApplication.class, args);
		System.out.println("\nApplication Running...............");
	}

}
