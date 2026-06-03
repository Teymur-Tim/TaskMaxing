package com.example.taskmaxing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Köhnə təsdiqlənməmiş taskların avtomatik təmizlənməsi üçün
public class TaskmaxingApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskmaxingApplication.class, args);
	}

}
