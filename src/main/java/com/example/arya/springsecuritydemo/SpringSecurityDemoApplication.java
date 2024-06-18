package com.example.arya.springsecuritydemo;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringSecurityDemoApplication {

	public static void main(String[] args) {

		SpringApplication.run(SpringSecurityDemoApplication.class, args);
		System.out.println("Application running");
	}
}
