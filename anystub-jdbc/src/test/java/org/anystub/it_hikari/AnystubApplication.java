package org.anystub.it_hikari;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Logger;

@SpringBootApplication
public class AnystubApplication {

	public static void main(String[] args) {
		Logger log = Logger.getLogger("xxxx");
		log.info("**************************");
		SpringApplication.run(AnystubApplication.class, args);
	}
}
