package com.facie.java_task_facie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class JavaTaskFacieApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaTaskFacieApplication.class, args);
	}

}
