package com.tcs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AudioFileServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AudioFileServiceApplication.class, args);
	}

}
