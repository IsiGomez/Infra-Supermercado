package cl.supermercado.puntos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class PuntosApplication {

	public static void main(String[] args) {
		SpringApplication.run(PuntosApplication.class, args);
	}

}
