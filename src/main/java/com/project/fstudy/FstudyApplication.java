package com.project.fstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableMethodSecurity
public class FstudyApplication {
	public static void main(String[] args) {
		SpringApplication.run(FstudyApplication.class, args);
	}

}
