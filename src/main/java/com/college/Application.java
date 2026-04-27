package com.college;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.println("  College Event Management System - Running   ");
        System.out.println("  Port: 8000 | Health: Green                ");
        System.out.println("----------------------------------------------");
        SpringApplication.run(Application.class, args);
    }
}
