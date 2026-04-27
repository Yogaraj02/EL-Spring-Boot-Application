package events;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import users.Participant;

import util.BaseEntity;

public class Event extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String eventType;
    private String date;
    private String time;
    private String venue;
    private int fee;

    private Set<Participant> registeredParticipants = ConcurrentHashMap.newKeySet();

    public Event(int eventId, String eventName, String eventType,
                 String date, String time, String venue, int fee) {
        super(eventId, eventName);
        this.eventType = eventType;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.fee = fee;
    }

    public int getEventId() {
        return id;
    }

    public String getEventName() {
        return name;
    }

    public String getEventType() {
        return eventType;
    }

    public int getFee() {
        return fee;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getVenue() {
        return venue;
    }

    public void addParticipant(Participant p) {
        registeredParticipants.add(p);
    }

    public Set<Participant> getRegisteredParticipants() {
        return registeredParticipants;
    }

    @Override
    public void displayInfo() {
        System.out.println("--------------------------------");
        System.out.println("Event ID   : " + id);
        System.out.println("Name       : " + name);
        System.out.println("Type       : " + eventType);
        System.out.println("Date       : " + date);
        System.out.println("Time       : " + time);
        System.out.println("Venue      : " + venue);
        System.out.println("Fee        : Rs." + fee);
    }

    @Override
    public String toDataString() {
        return "EVENT|Event ID:" + id + "|Name:" + name + "|Type:" + eventType + "|Date:" + date + "|Time:"
                + time + "|Venue:" + venue + "|Fee:" + fee;
    }
}
