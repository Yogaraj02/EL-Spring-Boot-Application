package users;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import events.Event;
import util.Role;

public class Participant extends User {
    private static final long serialVersionUID = 1L;

    private String rollNo;
    private String department;
    private String password;

    private Set<Event> registeredEvents = ConcurrentHashMap.newKeySet();

    public Participant(int id, String name, String rollNo, String department, String email, String password, Role role) {
        super(id, name, email, role);
        this.rollNo = rollNo;
        this.department = department;
        this.password = password;
    }

    public boolean verifyPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    public void addEvent(Event e) {
        registeredEvents.add(e);
    }

    public Set<Event> getRegisteredEvents() {
        return registeredEvents;
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getDepartment() {
        return department;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public void displayInfo() {
        System.out.println("================================");
        System.out.println("Participant ID : " + id);
        System.out.println("Name           : " + name);
        System.out.println("Roll No        : " + rollNo);
        System.out.println("Department     : " + department);
        System.out.println("Email          : " + email);
        System.out.println("Role           : " + role);
        System.out.println("Events Reg     : " + registeredEvents.size());
    }

    @Override
    public String toDataString() {
        return "PARTICIPANT|ID:" + id + "|Name:" + name + "|RollNo:" + rollNo + "|Department:" + department + "|Email:"
                + email + "|Password:" + password + "|Role:" + role;
    }
}
