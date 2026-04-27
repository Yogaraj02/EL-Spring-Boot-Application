package com.college.service;

import com.college.entity.Coordinator;
import com.college.repository.CoordinatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class CoordinatorService {
    @Autowired
    private CoordinatorRepository coordinatorRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Coordinator> getAllCoordinators() {
        return coordinatorRepository.findAll();
    }

    public Coordinator getCoordinatorById(Integer id) {
        return coordinatorRepository.findById(id).orElse(null);
    }

    public Coordinator saveCoordinator(Coordinator coordinator) {
        return coordinatorRepository.save(coordinator);
    }
    
    @Transactional
    public void deleteCoordinator(Integer id) {
        System.out.println(">>> ATTEMPTING TO DELETE COORDINATOR ID: " + id);
        
        // 1. Manually clear link tables using JDBC to be 100% sure
        try { 
            // Using lowercase snake_case table names as per the actual database schema
            int rows = jdbcTemplate.update("DELETE FROM coordinator_events WHERE coordinator_id = ?", id);
            System.out.println(">>> Cleared " + rows + " assignment records.");
        } catch (Exception e) {
            System.err.println(">>> Error clearing assignments: " + e.getMessage());
        }

        // 2. Perform the actual deletion
        try {
            coordinatorRepository.deleteById(id);
            coordinatorRepository.flush(); // Force sync with Database
            System.out.println(">>> SUCCESS: Coordinator " + id + " deleted from Database.");
        } catch (Exception e) {
            System.err.println(">>> CRITICAL ERROR: Could not delete coordinator: " + e.getMessage());
            throw e; // Rollback transaction if this fails
        }
    }
    
    @Transactional
    public void assignCoordinatorToEvent(Integer coordId, Integer eventId) {
        jdbcTemplate.update("INSERT IGNORE INTO coordinator_events (coordinator_id, event_id) VALUES (?, ?)", coordId, eventId);
    }

    public List<Map<String, Object>> getCoordinatorAssignments() {
        String sql = "SELECT c.name as coordinatorName, e.name as eventName " +
                     "FROM coordinator_events ce " +
                     "JOIN coordinators c ON ce.coordinator_id = c.coordinator_id " +
                     "JOIN events e ON ce.event_id = e.event_id";
        return jdbcTemplate.queryForList(sql);
    }
}
