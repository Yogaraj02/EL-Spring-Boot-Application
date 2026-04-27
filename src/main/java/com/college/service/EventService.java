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
        try { jdbcTemplate.update("DELETE FROM event_registrations WHERE event_id = ?", id); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM Payments WHERE event_id = ?", id); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM Certificates WHERE event_id = ?", id); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM Feedbacks WHERE event_id = ?", id); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM Event_Participants WHERE event_id = ?", id); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM Coordinator_Events WHERE event_id = ?", id); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM Admin_Events WHERE event_id = ?", id); } catch (Exception e) {}
        
        eventRepository.deleteById(id);
    }
}
