package com.github.youssefwadie.readwithmedataloader;

import com.github.youssefwadie.readwithmedataloader.connection.DataStaxAstraProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({DataStaxAstraProperties.class})
public class ReadwithmeDataLoaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadwithmeDataLoaderApplication.class, args);
	}

}
