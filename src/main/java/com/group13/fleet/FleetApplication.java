package com.group13.fleet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.group13.fleet.repository") // <-- Add this line
public class FleetApplication {

	public static void main(String[] args) {
		SpringApplication.run(FleetApplication.class, args);
	}

}
