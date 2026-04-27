package com.college.repository;

import com.college.entity.EventRegistration;
import com.college.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByStudent(Student student);
    Optional<EventRegistration> findByStudentAndEvent_EventId(Student student, Integer eventId);
}
