package main;

import java.util.concurrent.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

import events.Event;
import events.Venue;
import events.Certificate;
import events.Feedback;
import users.Participant;
import users.Admin;
import users.EventCoordinator;
import util.Role;
import util.Payment;
import util.PaymentType;
import exceptions.*;
import util.InputHandler;
import util.DatabaseConnection;
import java.sql.*;

public class EventManagementSystem {

    public static Map<Integer, Event> events = new ConcurrentHashMap<>();
    public static Map<Integer, Venue> venues = new ConcurrentHashMap<>();
    public static List<Payment> payments = new CopyOnWriteArrayList<>();
    public static List<Certificate> certificates = new CopyOnWriteArrayList<>();
    public static List<Feedback> feedbacks = new CopyOnWriteArrayList<>();
    public static Map<Integer, Admin> admins = new ConcurrentHashMap<>();
    public static Map<Integer, EventCoordinator> coordinators = new ConcurrentHashMap<>();
    public static Map<Integer, Participant> participants = new ConcurrentHashMap<>();
    public static Map<String, Participant> participantEmailMap = new ConcurrentHashMap<>();
    static Scanner sc = new Scanner(System.in);

    // ---------------- MULTITHREADING EXECUTORS ----------------
    // Executor for handling individual background saves
    private static final ExecutorService saveExecutor = Executors.newSingleThreadExecutor();
    // Executor for handling periodic auto-saves
    private static final ScheduledExecutorService autoSaveExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "AutoSaveDaemon");
        t.setDaemon(true);
        return t;
    });

    static int paymentCounter = 1;
    static int certificateCounter = 1;
    static int feedbackCounter = 1;
    static int participantCounter = 1;

    public static void main(String[] args) {
        loadData();

        // Start Auto-Save Daemon (Every 30 seconds)
        autoSaveExecutor.scheduleAtFixedRate(EventManagementSystem::saveData, 30, 30, TimeUnit.SECONDS);

        while (true) {
            System.out.println("\n==== COLLEGE EVENT MANAGEMENT SYSTEM ====");
            System.out.println("1. Admin");
            System.out.println("2. Participant");
            System.out.println("3. Exit");

            int choice = InputHandler.readInt(sc, "\nEnter choice: ");

            if (choice == 1)
                adminLogin();
            else if (choice == 2)
                participantMenu();
            else if (choice == 3) {
                System.out.println("\nSaving data and shutting down...");
                saveData(); // Final synchronous save
                autoSaveExecutor.shutdown();
                saveExecutor.shutdown();
                DatabaseConnection.closeConnection();
                System.out.println("Data saved successfully!");
                System.out.println("Thank You!");
                System.exit(0);
            } else {
                System.out.println("Invalid Choice");
            }
        }
    }

    // ---------------- ADMIN ----------------
    static void adminLogin() {
        String pass = InputHandler.readString(sc, "Enter Admin Password: ");

        try {
            if (!pass.equals("admin123")) {
                throw new AuthenticationException("Wrong Admin Password!");
            }
        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
            return;
        }

        loadData(); // Load fresh data once before entering the loop
        while (true) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Add Event");
            System.out.println("2. View Events");
            System.out.println("3. View Registered Participants");
            System.out.println("4. Remove Event");
            System.out.println("5. Manage Venues");
            System.out.println("6. View All Payments");
            System.out.println("7. Issue Certificates");
            System.out.println("8. View Event Feedback");
            System.out.println("9. Manage Coordinators");
            System.out.println("10. Logout");

            int ch = InputHandler.readInt(sc, "\nEnter Choice: ");

            if (ch == 1) addEvent();
            else if (ch == 2) viewEvents();
            else if (ch == 3) viewRegisteredParticipants();
            else if (ch == 4) removeEvent();
            else if (ch == 5) manageVenues();
            else if (ch == 6) viewAllPayments();
            else if (ch == 7) issueCertificates();
            else if (ch == 8) viewEventFeedback();
            else if (ch == 9) manageCoordinators();
            else if (ch == 10) break;
            else System.out.println("Invalid Choice");
        }
    }

    static void addEvent() {
        int id;
        while (true) {
            id = InputHandler.readInt(sc, "Event ID: ");
            if (events.containsKey(id)) {
                System.out.println("Event ID already exists! Please use a different ID.");
            } else {
                break;
            }
        }
        String name = InputHandler.readString(sc, "Event Name: ");
        String type = InputHandler.readString(sc, "Event Type (Technical/Cultural): ");
        String date = InputHandler.readDatePattern(sc, "Date (DD-MM-YYYY): ");
        String time = InputHandler.readString(sc, "Time (e.g., 10:00 AM): ");
        String venue = InputHandler.readString(sc, "Venue: ");
        int fee = InputHandler.readInt(sc, "Fee: ");

        Event newEvent = new Event(id, name, type, date, time, venue, fee);
        events.put(id, newEvent);
        saveData(); // Synchronous save to ensure data is persisted before re-loading or other operations
        System.out.println("Event Added Successfully!");
    }

    static void viewEvents() {
        if (events.isEmpty()) {
            System.out.println("No Events Available");
            return;
        }
        for (Event e : events.values()) {
            e.displayInfo();
        }
    }

    static void viewRegisteredParticipants() {
        for (Event e : events.values()) {
            System.out.println("\nEvent: " + e.getEventName());
            if (e.getRegisteredParticipants().isEmpty()) {
                System.out.println("No Participants Registered");
            } else {
                for (Participant p : e.getRegisteredParticipants()) {
                    System.out.println("- " + p.getName());
                }
            }
        }
    }

    static void removeEvent() {
        int id = InputHandler.readInt(sc, "Enter Event ID to Remove: ");

        boolean found = false;
        for (Event e : events.values()) {
            if (e.getEventId() == id) {
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Event not found!");
            return;
        }

        // Perform deletion asynchronously to avoid freezing UI
        saveExecutor.submit(() -> {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                try {
                    String[] relatedTables = {
                        "Event_Participants", "Coordinator_Events", "Admin_Events",
                        "Payments", "Certificates", "Feedbacks"
                    };
                    for (String table : relatedTables) {
                        try (PreparedStatement ps = conn.prepareStatement(
                                "DELETE FROM " + table + " WHERE event_id = ?")) {
                            ps.setInt(1, id);
                            ps.executeUpdate();
                        }
                    }
                    try (PreparedStatement ps = conn.prepareStatement(
                            "DELETE FROM Events WHERE event_id = ?")) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                    }
                } catch (SQLException ex) {
                    System.err.println("Database error during deletion: " + ex.getMessage());
                }
            }
        });

        events.remove(id);
        payments.removeIf(p -> p.getEventId() == id);
        certificates.removeIf(c -> c.getEvent().getEventId() == id);
        feedbacks.removeIf(f -> f.getEvent().getEventId() == id);
        for (Participant p : participants.values()) p.getRegisteredEvents().removeIf(e -> e.getEventId() == id);
        for (EventCoordinator c : coordinators.values()) c.getAssignedEvents().removeIf(e -> e.getEventId() == id);
        for (Admin a : admins.values()) a.getCreatedEvents().removeIf(e -> e.getEventId() == id);

        System.out.println("Event Removed Successfully!");
    }

    // ---------------- PARTICIPANT ----------------
    static void participantMenu() {
        Participant user = null;

        while (user == null) {
            System.out.println("\n--- PARTICIPANT ACCESS ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Back");
            int option = InputHandler.readInt(sc, "Choice: ");

            if (option == 1) {
                System.out.println("\nEmail Entry for Login:");
                String email = getAutoFilledEmail();

                user = participantEmailMap.get(email.toLowerCase());

                try {
                    if (user != null) {
                        while (true) {
                            String pass = InputHandler.readString(sc, "Enter Password: ");
                            if (user.verifyPassword(pass)) {
                                System.out.println("\nWelcome back, " + user.getName() + "!");
                                break;
                            } else {
                                throw new AuthenticationException("Wrong Password! Try again.");
                            }
                        }
                    } else {
                        throw new EntityNotFoundException("Email not found. Please Register first!");
                    }
                } catch (AuthenticationException | EntityNotFoundException e) {
                    System.out.println(e.getMessage());
                    user = null;
                }
            } else if (option == 2) {
                System.out.println("\nEmail Entry for Registration:");
                String email = getAutoFilledEmail();

                boolean exists = participantEmailMap.containsKey(email.toLowerCase());

                if (exists) {
                    System.out.println("This email is already registered! Please Login.");
                } else {
                    String name = InputHandler.readString(sc, "Enter Your Name: ");
                    String rollNo = InputHandler.readString(sc, "Enter Your Roll No: ");

                    String department = "";
                    String[] validDepts = { "CSE", "IT", "MECH", "ECE", "AIDS", "EEE", "CIVIL" };

                    while (true) {
                        department = InputHandler.readString(sc, "Enter Your Department (CSE/IT/MECH/ECE/AIDS/EEE/CIVIL): ").toUpperCase();
                        boolean isValid = false;
                        for (String dept : validDepts) {
                            if (department.equals(dept)) { isValid = true; break; }
                        }
                        if (isValid) break;
                        else System.out.println("Invalid Department! Please enter one of: CSE, IT, MECH, ECE, AIDS, EEE, CIVIL");
                    }

                    System.out.print("Set Your Password: ");
                    String password = readPassword();

                    user = new Participant(participantCounter++, name, rollNo, department, email, password, Role.PARTICIPANT);
                    participants.put(user.getId(), user);
                    participantEmailMap.put(user.getEmail().toLowerCase(), user);
                    asyncSaveData();
                    System.out.println("\nRegistration Successful!");
                    System.out.println("Welcome, " + user.getName() + "!");
                }
            } else if (option == 3) {
                return;
            } else {
                System.out.println("Invalid choice!");
            }
        }

        loadData(); // Load fresh data once before entering menu
        while (true) {
            System.out.println("\n--- PARTICIPANT MENU ---");
            System.out.println("1. View Events");
            System.out.println("2. Register Event");
            System.out.println("3. View My Registered Events");
            System.out.println("4. View My Certificates");
            System.out.println("5. Submit Feedback");
            System.out.println("6. Back");

            int ch = InputHandler.readInt(sc, "\nEnter Choice: ");

            if (ch == 1) viewEvents();
            else if (ch == 2) registerEvent(user);
            else if (ch == 3) viewMyEvents(user);
            else if (ch == 4) viewMyCertificates(user);
            else if (ch == 5) submitFeedback(user);
            else if (ch == 6) break;
            else System.out.println("Invalid Choice");
        }
    }

    static void registerEvent(Participant user) {
        if (events.isEmpty()) {
            System.out.println("No Events Available");
            return;
        }

        viewEvents();
        int id = InputHandler.readInt(sc, "Enter Event ID: ");

        for (Event e : events.values()) {
            if (e.getEventId() == id) {
                System.out.println("Event Fee: Rs." + e.getFee());
                System.out.println("Payment Method");
                System.out.println("1. Cash");
                System.out.println("2. UPI");
                System.out.println("3. Card");

                int p = InputHandler.readInt(sc, "Choice: ");

                PaymentType payType = null;

                if (p == 1) {
                    payType = PaymentType.CASH;
                    System.out.println("Payment Mode: CASH");
                } else if (p == 2) {
                    payType = PaymentType.UPI;
                    InputHandler.readString(sc, "Enter UPI ID: ");
                    System.out.println("Payment Successful via UPI");
                } else if (p == 3) {
                    payType = PaymentType.CARD;
                    InputHandler.readString(sc, "Card Number: ");
                    InputHandler.readString(sc, "Expiry Date (MM/YY): ");
                    System.out.println("Payment Successful via Card");
                } else {
                    System.out.println("Invalid Payment");
                    return;
                }

                Payment payment = new Payment(paymentCounter++, user.getId(), e.getEventId(), e.getFee(), payType);
                payment.markAsSuccessful();
                payments.add(payment);

                e.addParticipant(user);
                user.addEvent(e);
                asyncSaveData();

                System.out.println("Payment Status : PAID");
                System.out.println("Registered Successfully!");
                return;
            }
        }
        System.out.println("Event Not Found");
    }

    static void viewMyEvents(Participant user) {
        if (user.getRegisteredEvents().isEmpty()) {
            System.out.println("You have not registered for any events");
            return;
        }
        System.out.println("\nYour Registered Events:");
        for (Event e : user.getRegisteredEvents()) {
            System.out.println("- " + e.getEventName() + " (" + e.getEventType() + ")");
        }
    }

    // ---------------- VENUES ----------------
    static void manageVenues() {
        System.out.println("\n=== VENUE MANAGEMENT ===");
        System.out.println("1. Add Venue");
        System.out.println("2. View All Venues");
        System.out.println("3. Update Venue Availability");

        int choice = InputHandler.readInt(sc, "Choice: ");

        if (choice == 1) {
            int id = InputHandler.readInt(sc, "Venue ID: ");
            String name = InputHandler.readString(sc, "Venue Name: ");
            String location = InputHandler.readString(sc, "Location: ");
            int capacity = InputHandler.readInt(sc, "Capacity: ");
            String facilities = InputHandler.readString(sc, "Facilities: ");

            Venue v_new = new Venue(id, name, location, capacity, facilities);
            venues.put(v_new.getVenueId(), v_new);
            asyncSaveData();
            System.out.println("Venue Added Successfully!");
        } else if (choice == 2) {
            if (venues.isEmpty()) { System.out.println("No Venues Available"); return; }
            for (Venue v : venues.values()) v.displayInfo();
        } else if (choice == 3) {
            int id = InputHandler.readInt(sc, "Venue ID: ");
            for (Venue v : venues.values()) {
                if (v.getVenueId() == id) {
                    v.setAvailable(!v.isAvailable());
                    asyncSaveData();
                    System.out.println("Venue availability updated to: " + (v.isAvailable() ? "Available" : "Booked"));
                    return;
                }
            }
            System.out.println("Venue not found");
        }
    }

    // ---------------- PAYMENTS ----------------
    static void viewAllPayments() {
        if (payments.isEmpty()) {
            System.out.println("\nNo Payments Recorded");
            return;
        }
        System.out.println("\n=== ALL PAYMENT TRANSACTIONS ===");
        for (Payment payment : payments) payment.displayRecord();

        int total = 0;
        for (Payment p : payments) if (p.isSuccessful()) total += p.getAmount();
        System.out.println("\n================================");
        System.out.println("Total Revenue: Rs." + total);
    }

    // ---------------- CERTIFICATES ----------------
    static void issueCertificates() {
        System.out.println("\n=== ISSUE CERTIFICATE ===");

        if (events.isEmpty()) { System.out.println("No events available"); return; }

        viewEvents();
        int eventId = InputHandler.readInt(sc, "Enter Event ID: ");

        Event selectedEvent = null;
        for (Event e : events.values()) {
            if (e.getEventId() == eventId) { selectedEvent = e; break; }
        }

        try {
            if (selectedEvent == null) throw new EntityNotFoundException("Event not found");
        } catch (EntityNotFoundException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        if (selectedEvent.getRegisteredParticipants().isEmpty()) {
            System.out.println("No participants registered for this event");
            return;
        }

        System.out.println("\nParticipants:");
        int index = 1;
        for (Participant p : selectedEvent.getRegisteredParticipants()) {
            System.out.println(index++ + ". " + p.getName());
        }

        int pIndex = InputHandler.readInt(sc, "Select Participant (number): ");
        if (pIndex < 1 || pIndex > selectedEvent.getRegisteredParticipants().size()) {
            System.out.println("Invalid selection");
            return;
        }

        List<Participant> tempList = new ArrayList<>(selectedEvent.getRegisteredParticipants());
        Participant selectedParticipant = tempList.get(pIndex - 1);
        String achievement = InputHandler.readString(sc, "Achievement (PARTICIPATION/WINNER/RUNNER UP): ");

        Certificate cert = new Certificate(certificateCounter++, selectedParticipant, selectedEvent, achievement);
        cert.issueCertificate();
        certificates.add(cert);
        asyncSaveData();

        cert.displayRecord();
        System.out.println("Certificate Issued Successfully!");
    }

    // ---------------- FEEDBACK ----------------
    static void viewEventFeedback() {
        if (feedbacks.isEmpty()) { System.out.println("\nNo Feedback Submitted Yet"); return; }

        System.out.println("\n=== EVENT FEEDBACK ===");
        for (Feedback fb : feedbacks) fb.displayRecord();

        double avgRating = 0;
        for (Feedback fb : feedbacks) avgRating += fb.getRating();
        avgRating = avgRating / feedbacks.size();
        System.out.println("\n================================");
        System.out.printf("Average Rating: %.2f/5.0\n", avgRating);
    }

    // ---------------- COORDINATORS ----------------
    static void manageCoordinators() {
        System.out.println("\n=== COORDINATOR MANAGEMENT ===");
        System.out.println("1. Add Coordinator");
        System.out.println("2. View All Coordinators");
        System.out.println("3. Assign Event to Coordinator");

        int choice = InputHandler.readInt(sc, "Choice: ");

        if (choice == 1) {
            int id = InputHandler.readInt(sc, "Coordinator ID: ");
            String name = InputHandler.readString(sc, "Name: ");
            System.out.println("Email Entry:");
            String email = getAutoFilledEmail();
            String dept = InputHandler.readString(sc, "Department: ");
            String phone = InputHandler.readString(sc, "Phone: ");

            EventCoordinator ec_new = new EventCoordinator(id, name, email, dept, phone);
            coordinators.put(ec_new.getId(), ec_new);
            asyncSaveData();
            System.out.println("Coordinator Added Successfully!");
        } else if (choice == 2) {
            if (coordinators.isEmpty()) { System.out.println("No Coordinators Added"); return; }
            for (EventCoordinator coord : coordinators.values()) {
                coord.displayInfo();
                coord.displayAssignedEvents();
            }
        } else if (choice == 3) {
            if (coordinators.isEmpty() || events.isEmpty()) {
                System.out.println("Need both coordinators and events");
                return;
            }
            int coordId = InputHandler.readInt(sc, "Coordinator ID: ");
            int eventId = InputHandler.readInt(sc, "Event ID: ");

            EventCoordinator selectedCoord = null;
            Event selectedEvent = null;
            for (EventCoordinator c : coordinators.values()) if (c.getId() == coordId) { selectedCoord = c; break; }
            for (Event e : events.values()) if (e.getEventId() == eventId) { selectedEvent = e; break; }

            if (selectedCoord != null && selectedEvent != null) {
                selectedCoord.assignEvent(selectedEvent);
                asyncSaveData();
                System.out.println("Event assigned successfully!");
            } else {
                System.out.println("Invalid Coordinator or Event ID");
            }
        }
    }

    // ---------------- PARTICIPANT FEATURES ----------------
    static void viewMyCertificates(Participant user) {
        boolean found = false;
        System.out.println("\n=== YOUR CERTIFICATES ===");
        for (Certificate cert : certificates) {
            if (cert.getParticipant().getId() == user.getId()) {
                cert.displayRecord();
                found = true;
            }
        }
        if (!found) System.out.println("No certificates issued yet");
    }

    static void submitFeedback(Participant user) {
        if (user.getRegisteredEvents().isEmpty()) {
            System.out.println("\nYou haven't registered for any events yet");
            return;
        }
        System.out.println("\n=== SUBMIT FEEDBACK ===");
        System.out.println("Your Events:");
        int index = 1;
        for (Event e : user.getRegisteredEvents()) System.out.println(index++ + ". " + e.getEventName());

        int choice = InputHandler.readInt(sc, "Select Event (number): ");
        if (choice < 1 || choice > user.getRegisteredEvents().size()) {
            System.out.println("Invalid selection");
            return;
        }

        List<Event> tempList = new ArrayList<>(user.getRegisteredEvents());
        Event selectedEvent = tempList.get(choice - 1);
        int rating = InputHandler.readInt(sc, "Rating (1-5): ");
        String category = InputHandler.readString(sc, "Category (Venue/Organization/Content): ");
        String comments = InputHandler.readString(sc, "Comments: ");

        Feedback feedback = new Feedback(feedbackCounter++, user, selectedEvent, rating, comments, category);
        feedbacks.add(feedback);
        asyncSaveData();

        System.out.println("\nThank you for your feedback!");
        feedback.displayRecord();
    }

    // ---------------- HELPERS ----------------
    static String getAutoFilledEmail() {
        String username = InputHandler.readString(sc, "Enter Username: ");
        System.out.println("Select Domain:");
        System.out.println("1. @gmail.com");
        System.out.println("2. @nec.edu.in");
        System.out.println("3. Manual Enter");
        int choice = InputHandler.readInt(sc, "Choice: ");
        switch (choice) {
            case 1: return username + "@gmail.com";
            case 2: return username + "@nec.edu.in";
            case 3: return InputHandler.readString(sc, "Enter full email: ");
            default: return username;
        }
    }

    static String readPassword() {
        if (System.console() != null) {
            char[] passChars = System.console().readPassword();
            return new String(passChars);
        } else {
            return InputHandler.readString(sc, "");
        }
    }

    // ---------------- ASYNC HELPERS ----------------
    public static void asyncSaveData() {
        saveExecutor.submit(EventManagementSystem::saveData);
    }

    // ---------------- DATA PERSISTENCE (JDBC) ----------------
    public static synchronized void saveData() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) { System.err.println("Cannot save: no database connection."); return; }
        try {
            String sqlCounters = "INSERT INTO Counters (id, paymentCounter, certificateCounter, feedbackCounter, participantCounter) "
                    + "VALUES (1, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
                    + "paymentCounter=VALUES(paymentCounter), certificateCounter=VALUES(certificateCounter), "
                    + "feedbackCounter=VALUES(feedbackCounter), participantCounter=VALUES(participantCounter)";
            try (PreparedStatement ps = conn.prepareStatement(sqlCounters)) {
                ps.setInt(1, paymentCounter); ps.setInt(2, certificateCounter);
                ps.setInt(3, feedbackCounter); ps.setInt(4, participantCounter);
                ps.executeUpdate();
            }

            String sqlEvent = "INSERT INTO Events (event_id, name, type, date, time, venue, fee) VALUES (?,?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE name=VALUES(name), type=VALUES(type), date=VALUES(date), "
                    + "time=VALUES(time), venue=VALUES(venue), fee=VALUES(fee)";
            for (Event e : events.values()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlEvent)) {
                    ps.setInt(1, e.getEventId()); ps.setString(2, e.getEventName());
                    ps.setString(3, e.getEventType()); ps.setString(4, e.getDate());
                    ps.setString(5, e.getTime()); ps.setString(6, e.getVenue());
                    ps.setInt(7, e.getFee()); ps.executeUpdate();
                }
            }

            String sqlVenue = "INSERT INTO Venues (venue_id, name, location, capacity, facilities, is_available) VALUES (?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE name=VALUES(name), location=VALUES(location), "
                    + "capacity=VALUES(capacity), facilities=VALUES(facilities), is_available=VALUES(is_available)";
            for (Venue v : venues.values()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlVenue)) {
                    ps.setInt(1, v.getVenueId()); ps.setString(2, v.getName());
                    ps.setString(3, v.getLocation()); ps.setInt(4, v.getCapacity());
                    ps.setString(5, v.getFacilities()); ps.setBoolean(6, v.isAvailable());
                    ps.executeUpdate();
                }
            }

            String sqlPart = "INSERT INTO Participants (participant_id, name, roll_no, department, email, password, role) VALUES (?,?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE name=VALUES(name), roll_no=VALUES(roll_no), "
                    + "department=VALUES(department), email=VALUES(email), password=VALUES(password), role=VALUES(role)";
            for (Participant p : participants.values()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlPart)) {
                    ps.setInt(1, p.getId()); ps.setString(2, p.getName());
                    ps.setString(3, p.getRollNo()); ps.setString(4, p.getDepartment());
                    ps.setString(5, p.getEmail()); ps.setString(6, p.getPassword());
                    ps.setString(7, p.getRole().name()); ps.executeUpdate();
                }
            }

            String sqlAdmin = "INSERT INTO Admins (admin_id, name, email, password) VALUES (?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE name=VALUES(name), email=VALUES(email), password=VALUES(password)";
            for (Admin a : admins.values()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlAdmin)) {
                    ps.setInt(1, a.getId()); ps.setString(2, a.getName());
                    ps.setString(3, a.getEmail()); ps.setString(4, a.getPassword());
                    ps.executeUpdate();
                }
            }

            String sqlCoord = "INSERT INTO Coordinators (coordinator_id, name, email, department, phone) VALUES (?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE name=VALUES(name), email=VALUES(email), "
                    + "department=VALUES(department), phone=VALUES(phone)";
            for (EventCoordinator c : coordinators.values()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlCoord)) {
                    ps.setInt(1, c.getId()); ps.setString(2, c.getName());
                    ps.setString(3, c.getEmail()); ps.setString(4, c.getDepartment());
                    ps.setString(5, c.getPhone()); ps.executeUpdate();
                }
            }

            String sqlPay = "INSERT INTO Payments (payment_id, participant_id, event_id, amount, payment_type, is_successful) VALUES (?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE amount=VALUES(amount), payment_type=VALUES(payment_type), is_successful=VALUES(is_successful)";
            for (Payment p : payments) {
                try (PreparedStatement ps = conn.prepareStatement(sqlPay)) {
                    ps.setInt(1, p.getPaymentId()); ps.setInt(2, p.getParticipantId());
                    ps.setInt(3, p.getEventId()); ps.setInt(4, p.getAmount());
                    ps.setString(5, p.getPaymentType().name()); ps.setBoolean(6, p.isSuccessful());
                    ps.executeUpdate();
                }
            }

            String sqlCert = "INSERT INTO Certificates (certificate_id, participant_id, event_id, achievement, is_issued) VALUES (?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE achievement=VALUES(achievement), is_issued=VALUES(is_issued)";
            for (Certificate c : certificates) {
                try (PreparedStatement ps = conn.prepareStatement(sqlCert)) {
                    ps.setInt(1, c.getCertificateId()); ps.setInt(2, c.getParticipant().getId());
                    ps.setInt(3, c.getEvent().getEventId()); ps.setString(4, c.getAchievement());
                    ps.setBoolean(5, c.isIssued()); ps.executeUpdate();
                }
            }

            String sqlFb = "INSERT INTO Feedbacks (feedback_id, participant_id, event_id, rating, comments, category) VALUES (?,?,?,?,?,?) "
                    + "ON DUPLICATE KEY UPDATE rating=VALUES(rating), comments=VALUES(comments), category=VALUES(category)";
            for (Feedback f : feedbacks) {
                try (PreparedStatement ps = conn.prepareStatement(sqlFb)) {
                    ps.setInt(1, f.getFeedbackId()); ps.setInt(2, f.getParticipant().getId());
                    ps.setInt(3, f.getEvent().getEventId()); ps.setInt(4, f.getRating());
                    ps.setString(5, f.getComments()); ps.setString(6, f.getCategory());
                    ps.executeUpdate();
                }
            }

            String sqlEP = "INSERT IGNORE INTO Event_Participants (participant_id, event_id) VALUES (?,?)";
            for (Event e : events.values()) {
                for (Participant p : e.getRegisteredParticipants()) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlEP)) {
                        ps.setInt(1, p.getId()); ps.setInt(2, e.getEventId()); ps.executeUpdate();
                    }
                }
            }

            String sqlCE = "INSERT IGNORE INTO Coordinator_Events (coordinator_id, event_id) VALUES (?,?)";
            for (EventCoordinator c : coordinators.values()) {
                for (Event e : c.getAssignedEvents()) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlCE)) {
                        ps.setInt(1, c.getId()); ps.setInt(2, e.getEventId()); ps.executeUpdate();
                    }
                }
            }

            String sqlAE = "INSERT IGNORE INTO Admin_Events (admin_id, event_id) VALUES (?,?)";
            for (Admin a : admins.values()) {
                for (Event e : a.getCreatedEvents()) {
                    try (PreparedStatement ps = conn.prepareStatement(sqlAE)) {
                        ps.setInt(1, a.getId()); ps.setInt(2, e.getEventId()); ps.executeUpdate();
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error while saving: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized void loadData() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("Cannot load: no database connection. Starting fresh.");
            Admin a_def = new Admin(1, "Admin", "admin@college.edu", "admin123");
            admins.put(1, a_def);
            return;
        }
        try {
            events.clear(); participantEmailMap.clear(); venues.clear(); admins.clear();
            participants.clear(); coordinators.clear();
            payments.clear(); certificates.clear(); feedbacks.clear();

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM Counters WHERE id=1")) {
                if (rs.next()) {
                    paymentCounter = rs.getInt("paymentCounter");
                    certificateCounter = rs.getInt("certificateCounter");
                    feedbackCounter = rs.getInt("feedbackCounter");
                    participantCounter = rs.getInt("participantCounter");
                }
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM Events")) {
                while (rs.next()) {
                    Event e_ld = new Event(rs.getInt("event_id"), rs.getString("name"),
                            rs.getString("type"), rs.getString("date"),
                            rs.getString("time"), rs.getString("venue"), rs.getInt("fee"));
                    events.put(e_ld.getEventId(), e_ld);
                }
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM Venues")) {
                while (rs.next()) {
                    Venue v = new Venue(rs.getInt("venue_id"), rs.getString("name"),
                            rs.getString("location"), rs.getInt("capacity"), rs.getString("facilities"));
                    v.setAvailable(rs.getBoolean("is_available"));
                    venues.put(v.getVenueId(), v);
                }
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM Admins")) {
                while (rs.next()) {
                    Admin a_ld = new Admin(rs.getInt("admin_id"), rs.getString("name"),
                            rs.getString("email"), rs.getString("password"));
                    admins.put(a_ld.getId(), a_ld);
                }
            }
            if (admins.isEmpty()) {
                Admin a_def2 = new Admin(1, "Admin", "admin@college.edu", "admin123");
                admins.put(1, a_def2);
                asyncSaveData();
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM Participants")) {
                while (rs.next()) {
                    Participant p_ld = new Participant(rs.getInt("participant_id"), rs.getString("name"),
                            rs.getString("roll_no"), rs.getString("department"),
                            rs.getString("email"), rs.getString("password"),
                            Role.valueOf(rs.getString("role")));
                    participants.put(p_ld.getId(), p_ld);
                    participantEmailMap.put(p_ld.getEmail().toLowerCase(), p_ld);
                }
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM Coordinators")) {
                while (rs.next()) {
                    EventCoordinator c_ld = new EventCoordinator(rs.getInt("coordinator_id"), rs.getString("name"),
                            rs.getString("email"), rs.getString("department"), rs.getString("phone"));
                    coordinators.put(c_ld.getId(), c_ld);
                }
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM Payments")) {
                while (rs.next()) {
                    Payment pay = new Payment(rs.getInt("payment_id"), rs.getInt("participant_id"),
                            rs.getInt("event_id"), rs.getInt("amount"),
                            PaymentType.valueOf(rs.getString("payment_type")));
                    if (rs.getBoolean("is_successful")) pay.markAsSuccessful();
                    payments.add(pay);
                }
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM Certificates")) {
                while (rs.next()) {
                    Participant cp = findParticipant(rs.getInt("participant_id"));
                    Event ce = findEvent(rs.getInt("event_id"));
                    if (cp != null && ce != null) {
                        Certificate cert = new Certificate(rs.getInt("certificate_id"), cp, ce, rs.getString("achievement"));
                        if (rs.getBoolean("is_issued")) cert.issueCertificate();
                        certificates.add(cert);
                    }
                }
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM Feedbacks")) {
                while (rs.next()) {
                    Participant fp = findParticipant(rs.getInt("participant_id"));
                    Event fe = findEvent(rs.getInt("event_id"));
                    if (fp != null && fe != null) {
                        feedbacks.add(new Feedback(rs.getInt("feedback_id"), fp, fe,
                                rs.getInt("rating"), rs.getString("comments"), rs.getString("category")));
                    }
                }
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM Event_Participants")) {
                while (rs.next()) {
                    Participant p = findParticipant(rs.getInt("participant_id"));
                    Event e = findEvent(rs.getInt("event_id"));
                    if (p != null && e != null) { p.addEvent(e); e.addParticipant(p); }
                }
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM Coordinator_Events")) {
                while (rs.next()) {
                    EventCoordinator c = findCoordinator(rs.getInt("coordinator_id"));
                    Event e = findEvent(rs.getInt("event_id"));
                    if (c != null && e != null) c.assignEvent(e);
                }
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM Admin_Events")) {
                while (rs.next()) {
                    Admin a = findAdmin(rs.getInt("admin_id"));
                    Event e = findEvent(rs.getInt("event_id"));
                    if (a != null && e != null) a.addCreatedEvent(e);
                }
            }

            System.out.println("Data loaded from database successfully!");

        } catch (SQLException e) {
            System.err.println("Database error while loading: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static Participant findParticipant(int id) { return participants.get(id); }

    static Event findEvent(int id) { return events.get(id); }

    static EventCoordinator findCoordinator(int id) { return coordinators.get(id); }

    static Admin findAdmin(int id) { return admins.get(id); }
}
