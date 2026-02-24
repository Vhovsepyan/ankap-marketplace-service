package com.ankap.platform.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AnkapMarketplaceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnkapMarketplaceServiceApplication.class, args);
	}

}
