package com.nsteuerberg.Bot.de.NSteuerberg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.nsteuerberg")
public class BotDeNSteuerbergApplication {

	public static void main(String[] args) {
		SpringApplication.run(BotDeNSteuerbergApplication.class, args);
	}

}
