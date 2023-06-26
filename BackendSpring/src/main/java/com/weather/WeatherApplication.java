package com.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
@SpringBootApplication
public class WeatherApplication {

    // The main method of the application. It is responsible for running the Spring Boot application.
    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }

    //  A bean method annotated with @Bean that creates and returns a new instance of RestTemplate.
    //  RestTemplate is a Spring class used for making HTTP requests.
    //  This method ensures that a RestTemplate bean is available for dependency injection in other components.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}