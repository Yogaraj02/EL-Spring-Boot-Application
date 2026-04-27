package com.college.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Coordinators")
public class Coordinator {

    @Id
    @Column(name = "coordinator_id")
    private Integer coordinatorId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "department", length = 50)
    private String department;

    @Column(name = "phone", length = 20)
    private String phone;

    // Getters and Setters
    public Integer getCoordinatorId() { return coordinatorId; }
    public void setCoordinatorId(Integer coordinatorId) { this.coordinatorId = coordinatorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
