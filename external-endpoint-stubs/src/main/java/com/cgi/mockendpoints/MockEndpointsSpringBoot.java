package com.cgi.mockendpoints;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Simple Spring Boot project to simulate endpoints for hn secure testing without connectivity to the actual endpoints.
 * 
 * @author dave.p.barrett
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = { "com.cgi.mockendpoints.rest.api" })
public class MockEndpointsSpringBoot {

    public static void main(String[] args) {
        SpringApplication.run(MockEndpointsSpringBoot.class, args);
    }

}
