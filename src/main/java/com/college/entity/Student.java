package com.college.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "Participants")
public class Student {
    @Id
    @Column(name = "participant_id")
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "roll_no", nullable = false)
    private String rollNo;
    @Column(name = "department")
    private String department;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "role")
    private String role = "PARTICIPANT";

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
