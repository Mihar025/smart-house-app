package com.misha.sh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class ShApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShApplication.class, args);
    }

}
