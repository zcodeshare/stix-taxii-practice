package com.in2it.cats.stixtaxiipractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class StixTaxiiPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(StixTaxiiPracticeApplication.class, args);
	}

}
