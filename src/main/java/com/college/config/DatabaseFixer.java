package com.college.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseFixer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> Checking Database Schema Consistency...");
        try {
            // Fix Certificates table ID generation if it's missing AUTO_INCREMENT
            jdbcTemplate.execute("ALTER TABLE certificates MODIFY COLUMN certificate_id BIGINT AUTO_INCREMENT");
            System.out.println(">>> Database Fix: 'certificates' table primary key set to AUTO_INCREMENT.");
        } catch (Exception e) {
            // Ignore if already correct or table doesn't exist
        }

        try {
            // Fix Payments table ID generation if it's missing AUTO_INCREMENT
            jdbcTemplate.execute("ALTER TABLE payments MODIFY COLUMN payment_id BIGINT AUTO_INCREMENT");
            System.out.println(">>> Database Fix: 'payments' table primary key set to AUTO_INCREMENT.");
        } catch (Exception e) {
            // Ignore
        }
    }
}
