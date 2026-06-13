package com.samreact.skooLLy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SkooLLyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkooLLyApplication.class, args);
	}

}
