package com.mjdku.dkunotifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DkuNotifierApplication {

    public static void main(String[] args) {
        SpringApplication.run(DkuNotifierApplication.class, args);
    }

}
