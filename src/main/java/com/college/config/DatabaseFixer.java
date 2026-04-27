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
        
        // 1. Fix Certificates table ID generation
        try {
            jdbcTemplate.execute("ALTER TABLE certificates MODIFY COLUMN certificate_id BIGINT AUTO_INCREMENT");
            System.out.println(">>> Database Fix: 'certificates' table primary key set to AUTO_INCREMENT.");
        } catch (Exception e) {}

        // 2. Fix Payments table ID generation
        try {
            jdbcTemplate.execute("ALTER TABLE payments MODIFY COLUMN payment_id BIGINT AUTO_INCREMENT");
            System.out.println(">>> Database Fix: 'payments' table primary key set to AUTO_INCREMENT.");
        } catch (Exception e) {}

        // 3. Fix Foreign Key Constraint for coordinator_events to allow CASCADE DELETE
        // This solves the ERROR 1451 in MySQL Workbench
        try {
            // First, try to drop the existing constraint (common default name is coordinator_events_ibfk_2)
            try { jdbcTemplate.execute("ALTER TABLE coordinator_events DROP FOREIGN KEY coordinator_events_ibfk_2"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TABLE coordinator_events DROP FOREIGN KEY fk_coordinator_events_event"); } catch (Exception e) {}
            
            // Add the constraint back with ON DELETE CASCADE
            jdbcTemplate.execute("ALTER TABLE coordinator_events ADD CONSTRAINT fk_coordinator_events_event " +
                               "FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE");
            System.out.println(">>> Database Fix: 'coordinator_events' now supports CASCADE DELETE.");
        } catch (Exception e) {
            System.out.println(">>> Note: Could not update coordinator_events constraint (might already be fixed).");
        }
    }
}
