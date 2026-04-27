package com.college.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Events")
public class Event {

    @Id
    @Column(name = "event_id")
    private Integer eventId;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "date", nullable = false, length = 20)
    private String date;

    @Column(name = "time", nullable = false, length = 20)
    private String time;

    @Column(name = "venue", nullable = false, length = 100)
    private String venue;

    @Column(name = "fee", nullable = false)
    private Integer fee;

    // Getters and Setters
    public Integer getEventId() { return eventId; }
    public void setEventId(Integer eventId) { this.eventId = eventId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public Integer getFee() { return fee; }
    public void setFee(Integer fee) { this.fee = fee; }
}
