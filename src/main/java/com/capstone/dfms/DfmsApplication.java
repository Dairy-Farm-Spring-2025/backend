package com.capstone.dfms;

import com.capstone.dfms.components.configurations.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(AppProperties.class)

public class DfmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DfmsApplication.class, args);
	}

}
