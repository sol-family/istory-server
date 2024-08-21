package com.solfamily.istory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.solfamily.istory.global")
public class IstoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(IstoryApplication.class, args);
	}

}
