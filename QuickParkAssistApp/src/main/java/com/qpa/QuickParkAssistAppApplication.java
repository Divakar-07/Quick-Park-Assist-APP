package com.qpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.qpa.*")
@EnableJpaRepositories("com.qpa.repository")
@EntityScan("com.qpa.entity")
public class QuickParkAssistAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuickParkAssistAppApplication.class, args);
		

	}

}
