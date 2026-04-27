package users;

import util.Role;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import events.Event;

public class Admin extends User {
    private static final long serialVersionUID = 1L;

    private String password;

    private Set<Event> createdEvents = ConcurrentHashMap.newKeySet();

    public Admin(int adminId, String name, String email, String password) {
        super(adminId, name, email, Role.ADMIN);
        this.password = password;
    }

    public boolean verifyPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public String getPassword() {
        return password;
    }

    public void addCreatedEvent(Event e) {
        createdEvents.add(e);
    }

    public Set<Event> getCreatedEvents() {
        return createdEvents;
    }

    @Override
    public void displayInfo() {
        System.out.println("================================");
        System.out.println("Admin ID   : " + id);
        System.out.println("Name       : " + name);
        System.out.println("Email      : " + email);
        System.out.println("Role       : " + role);
        System.out.println("Events Created: " + createdEvents.size());
    }

    @Override
    public String toDataString() {
        return "ADMIN|ID:" + id + "|Name:" + name + "|Email:" + email + "|Password:" + password;
    }
}
