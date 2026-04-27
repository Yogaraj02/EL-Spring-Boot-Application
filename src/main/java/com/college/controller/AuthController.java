package com.college.controller;

import com.college.entity.Student;
import com.college.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.ThreadLocalRandom;

@Controller
public class AuthController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(
            @RequestParam("name") String name,
            @RequestParam("rollNo") String rollNo,
            @RequestParam("department") String department,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model) {
        
        if (studentRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }

        Student s = new Student();
        s.setId(ThreadLocalRandom.current().nextInt(100000, 999999));
        s.setName(name);
        s.setRollNo(rollNo);
        s.setDepartment(department);
        s.setEmail(email);
        s.setPassword(passwordEncoder.encode(password));
        s.setRole("PARTICIPANT");
        
        studentRepository.save(s);
        return "redirect:/login?registered";
    }
}
