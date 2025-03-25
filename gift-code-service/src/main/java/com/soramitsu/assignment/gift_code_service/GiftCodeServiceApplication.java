package com.soramitsu.assignment.gift_code_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GiftCodeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GiftCodeServiceApplication.class, args);
	}

}
