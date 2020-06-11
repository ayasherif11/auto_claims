package com.unilever.autoclaims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class AutoClaimsJarApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoClaimsJarApplication.class, args);
    }

}
