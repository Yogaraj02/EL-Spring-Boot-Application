package com.college.config;

import com.college.entity.Student;
import com.college.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create Admin if not exists
        if (studentRepository.findByEmail("admin@gmail.com").isEmpty()) {
            Student admin = new Student();
            admin.setId(1);
            admin.setName("System Admin");
            admin.setRollNo("ADMIN001");
            admin.setDepartment("Management");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            studentRepository.save(admin);
            System.out.println("Admin user created: admin@gmail.com / admin123");
        }
    }
}
