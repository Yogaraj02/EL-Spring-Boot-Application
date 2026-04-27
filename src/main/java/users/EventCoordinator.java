package users;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import events.Event;
import util.Role;

public class EventCoordinator extends User {
    private static final long serialVersionUID = 1L;

    private String department;
    private String phone;

    private Set<Event> assignedEvents = ConcurrentHashMap.newKeySet();

    public EventCoordinator(int coordinatorId, String name, String email, String department, String phone) {
        super(coordinatorId, name, email, Role.COORDINATOR);
        this.department = department;
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public String getPhone() {
        return phone;
    }

    public void assignEvent(Event event) {
        assignedEvents.add(event);
    }

    public void removeEvent(Event event) {
        assignedEvents.remove(event);
    }

    public Set<Event> getAssignedEvents() {
        return assignedEvents;
    }

    @Override
    public void displayInfo() {
        System.out.println("================================");
        System.out.println("Coordinator ID : " + id);
        System.out.println("Name           : " + name);
        System.out.println("Email          : " + email);
        System.out.println("Department     : " + department);
        System.out.println("Phone          : " + phone);
        System.out.println("Assigned Events: " + assignedEvents.size());
    }

    public void displayAssignedEvents() {
        if (assignedEvents.isEmpty()) {
            System.out.println("No events assigned yet.");
            return;
        }

        System.out.println("\n--- Assigned Events ---");
        for (Event e : assignedEvents) {
            System.out.println("- " + e.getEventName() + " (" + e.getEventType() + ")");
        }
    }

    @Override
    public String toDataString() {
        return "COORDINATOR|ID:" + id + "|Name:" + name + "|Email:" + email + "|Department:" + department
                + "|Phone:" + phone;
    }
}
