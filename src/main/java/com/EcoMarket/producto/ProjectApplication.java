package com.EcoMarket.producto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Clase principal del microservicio EcoMarket - Productos
 * 
 * @SpringBootApplication: Anotación que combina:
 * - @Configuration: Indica que la clase contiene configuración de Spring
 * - @EnableAutoConfiguration: Habilita la configuración automática de Spring Boot
 * - @ComponentScan: Escanea automáticamente los componentes en el paquete y subpaquetes
 */
@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {
		Dotenv.load();
		SpringApplication.run(ProjectApplication.class, args);
	}

}
