package com.example.euserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer

public class EuServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EuServerApplication.class, args);
	}

}
