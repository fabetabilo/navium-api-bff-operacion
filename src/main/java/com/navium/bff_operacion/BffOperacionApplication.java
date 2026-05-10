package com.navium.bff_operacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.navium")
public class BffOperacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(BffOperacionApplication.class, args);
	}

}
