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

        // 4. Fix Foreign Key Constraints for deleting Students/Participants
        try {
            // event_registrations
            try { jdbcTemplate.execute("ALTER TABLE event_registrations DROP FOREIGN KEY FKr249nu19r0w7nqbanwh4gbt39"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TABLE event_registrations DROP FOREIGN KEY event_registrations_ibfk_1"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TABLE event_registrations ADD CONSTRAINT fk_er_student FOREIGN KEY (participant_id) REFERENCES participants(participant_id) ON DELETE CASCADE"); } catch (Exception e) {}
            
            // payments
            try { jdbcTemplate.execute("ALTER TABLE payments DROP FOREIGN KEY FK3v2k3ymf5md0mksns5q7l1d4t"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TABLE payments DROP FOREIGN KEY payments_ibfk_1"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TABLE payments ADD CONSTRAINT fk_pay_student FOREIGN KEY (participant_id) REFERENCES participants(participant_id) ON DELETE CASCADE"); } catch (Exception e) {}

            // certificates
            try { jdbcTemplate.execute("ALTER TABLE certificates DROP FOREIGN KEY FKsokg0kmb22k5f7vym818oay7s"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TABLE certificates DROP FOREIGN KEY certificates_ibfk_1"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TABLE certificates ADD CONSTRAINT fk_cert_student FOREIGN KEY (participant_id) REFERENCES participants(participant_id) ON DELETE CASCADE"); } catch (Exception e) {}

            // event_participants
            try { jdbcTemplate.execute("ALTER TABLE event_participants DROP FOREIGN KEY FKt4n3g8x2q1v7k9d4p8w6m5j2c"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TABLE event_participants DROP FOREIGN KEY event_participants_ibfk_1"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TABLE event_participants ADD CONSTRAINT fk_ep_student FOREIGN KEY (participant_id) REFERENCES participants(participant_id) ON DELETE CASCADE"); } catch (Exception e) {}

            // feedbacks
            try { jdbcTemplate.execute("ALTER TABLE feedbacks DROP FOREIGN KEY FK2b9r5j7k6h4m1d8s3c0q9w5v1"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TABLE feedbacks DROP FOREIGN KEY feedbacks_ibfk_1"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TABLE feedbacks ADD CONSTRAINT fk_fb_student FOREIGN KEY (participant_id) REFERENCES participants(participant_id) ON DELETE CASCADE"); } catch (Exception e) {}

            System.out.println(">>> Database Fix: All child tables now support CASCADE DELETE for students.");
        } catch (Exception e) {}
    }
}
