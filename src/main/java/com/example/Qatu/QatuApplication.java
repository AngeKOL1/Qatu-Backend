package com.example.Qatu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QatuApplication {

	public static void main(String[] args) {
		SpringApplication.run(QatuApplication.class, args);
	}

}
