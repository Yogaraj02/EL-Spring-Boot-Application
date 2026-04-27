package com.college.service;
import com.college.entity.Event;
import com.college.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    public Event getEventById(Integer id) {
        return eventRepository.findById(id).orElse(null);
    }
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }
    
    @Transactional
    public void deleteEvent(Integer id) {
        // Drop dependent records safely to bypass Foreign Key constraints
        // Using lowercase snake_case table names as per the actual database schema
        jdbcTemplate.update("DELETE FROM event_registrations WHERE event_id = ?", id);
        jdbcTemplate.update("DELETE FROM payments WHERE event_id = ?", id);
        jdbcTemplate.update("DELETE FROM certificates WHERE event_id = ?", id);
        jdbcTemplate.update("DELETE FROM feedbacks WHERE event_id = ?", id);
        jdbcTemplate.update("DELETE FROM event_participants WHERE event_id = ?", id);
        jdbcTemplate.update("DELETE FROM coordinator_events WHERE event_id = ?", id);
        jdbcTemplate.update("DELETE FROM admin_events WHERE event_id = ?", id);
        
        eventRepository.deleteById(id);
    }
}
