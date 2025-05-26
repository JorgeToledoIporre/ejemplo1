package com.EcoMarket.producto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {
		Dotenv.load();
		SpringApplication.run(ProjectApplication.class, args);
	}

}
