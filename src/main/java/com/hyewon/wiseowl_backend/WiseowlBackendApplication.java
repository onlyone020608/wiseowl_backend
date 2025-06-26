package com.hyewon.wiseowl_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class WiseowlBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WiseowlBackendApplication.class, args);
	}

}
