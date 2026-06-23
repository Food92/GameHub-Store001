package com.gamehubstore.order_mscv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrderMscvApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderMscvApplication.class, args);
	}

}
