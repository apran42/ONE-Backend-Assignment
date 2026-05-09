package com.one.yjh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class OneBackendAssignmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(OneBackendAssignmentApplication.class, args);
    }

}
