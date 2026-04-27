package com.college.service;
import com.college.entity.Student;
import com.college.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    public Student getStudentById(Integer id) {
        return studentRepository.findById(id).orElse(null);
    }
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }
    
    @Transactional
    public void deleteStudent(Integer id) {
        // Drop dependent records safely to bypass Foreign Key constraint exceptions
        // Using lowercase snake_case table names as per the actual database schema
        jdbcTemplate.update("DELETE FROM event_registrations WHERE participant_id = ?", id);
        jdbcTemplate.update("DELETE FROM payments WHERE participant_id = ?", id);
        jdbcTemplate.update("DELETE FROM certificates WHERE participant_id = ?", id);
        jdbcTemplate.update("DELETE FROM feedbacks WHERE participant_id = ?", id);
        jdbcTemplate.update("DELETE FROM event_participants WHERE participant_id = ?", id);
        
        studentRepository.deleteById(id);
    }
}
