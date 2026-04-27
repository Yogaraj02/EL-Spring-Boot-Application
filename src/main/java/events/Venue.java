package events;

import util.BaseEntity;

public class Venue extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String location;
    private int capacity;
    private String facilities;
    private boolean isAvailable;

    public Venue(int venueId, String venueName, String location, int capacity, String facilities) {
        super(venueId, venueName);
        this.location = location;
        this.capacity = capacity;
        this.facilities = facilities;
        this.isAvailable = true;
    }

    public int getVenueId() {
        return id;
    }

    public String getVenueName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getFacilities() {
        return facilities;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public void displayInfo() {
        System.out.println("================================");
        System.out.println("Venue ID   : " + id);
        System.out.println("Name       : " + name);
        System.out.println("Location   : " + location);
        System.out.println("Capacity   : " + capacity + " people");
        System.out.println("Facilities : " + facilities);
        System.out.println("Status     : " + (isAvailable ? "Available" : "Booked"));
    }

    @Override
    public String toDataString() {
        return "VENUE|ID:" + id + "|Name:" + name + "|Location:" + location + "|Capacity:" + capacity
                + "|Facilities:" + facilities + "|Available:" + isAvailable;
    }
}
