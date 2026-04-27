import re

filepath = r"C:\Users\Yogaraj\OneDrive\Desktop\College Database\java\main\EventManagementSystem.java"

with open(filepath, "r") as f:
    text = f.read()

# Backup
with open(filepath + ".bak", "w") as f:
    f.write(text)

# 1. Imports
text = text.replace("import java.util.concurrent.CopyOnWriteArrayList;", "import java.util.concurrent.*;\nimport java.util.Map;\nimport java.util.List;\nimport java.util.ArrayList;")

# 2. Data Structures
text = text.replace("public static CopyOnWriteArrayList<Event> events = new CopyOnWriteArrayList<>();", "public static Map<Integer, Event> events = new ConcurrentHashMap<>();")
text = text.replace("public static CopyOnWriteArrayList<Venue> venues = new CopyOnWriteArrayList<>();", "public static Map<Integer, Venue> venues = new ConcurrentHashMap<>();")
text = text.replace("public static CopyOnWriteArrayList<Admin> admins = new CopyOnWriteArrayList<>();", "public static Map<Integer, Admin> admins = new ConcurrentHashMap<>();")
text = text.replace("public static CopyOnWriteArrayList<EventCoordinator> coordinators = new CopyOnWriteArrayList<>();", "public static Map<Integer, EventCoordinator> coordinators = new ConcurrentHashMap<>();")
text = text.replace("public static CopyOnWriteArrayList<Participant> participants = new CopyOnWriteArrayList<>();", 
"""public static Map<Integer, Participant> participants = new ConcurrentHashMap<>();
    public static Map<String, Participant> participantEmailMap = new ConcurrentHashMap<>();""")

# Record lists keep CopyOnWriteArrayList
text = text.replace("public static CopyOnWriteArrayList<Payment> payments = new CopyOnWriteArrayList<>();", "public static List<Payment> payments = new CopyOnWriteArrayList<>();")
text = text.replace("public static CopyOnWriteArrayList<Certificate> certificates = new CopyOnWriteArrayList<>();", "public static List<Certificate> certificates = new CopyOnWriteArrayList<>();")
text = text.replace("public static CopyOnWriteArrayList<Feedback> feedbacks = new CopyOnWriteArrayList<>();", "public static List<Feedback> feedbacks = new CopyOnWriteArrayList<>();")

# 3. addEvent/Registration Logic Updates
text = text.replace("events.add(new Event(id, name, type, date, time, venue, fee));", 
"""Event newEvent = new Event(id, name, type, date, time, venue, fee);
        events.put(id, newEvent);""")

text = text.replace("participants.add(user);", 
"""participants.put(user.getId(), user);
                    participantEmailMap.put(user.getEmail().toLowerCase(), user);""")

# 4. View Methods (Iteration)
text = text.replace("for (Event e : events)", "for (Event e : events.values())")
text = text.replace("for (Venue v : venues)", "for (Venue v : venues.values())")
text = text.replace("for (Admin a : admins)", "for (Admin a : admins.values())")
text = text.replace("for (EventCoordinator coord : coordinators)", "for (EventCoordinator coord : coordinators.values())")
text = text.replace("for (EventCoordinator c : coordinators)", "for (EventCoordinator c : coordinators.values())")
text = text.replace("for (Participant p : participants)", "for (Participant p : participants.values())")

# 5. Remove Logic
text = text.replace("events.removeIf(e -> e.getEventId() == id);", "events.remove(id);")
text = text.replace("for (Participant p : participants.values()) p.getRegisteredEvents().removeIf(e -> e.getEventId() == id);", 
                    "for (Participant p : participants.values()) p.getRegisteredEvents().removeIf(e -> e.getEventId() == id);") # Already fine

# 6. Find Methods (O(1) optimization)
text = re.sub(r'static Participant findParticipant\(int id\) \{.*?return null;\s*\}', 
              'static Participant findParticipant(int id) { return participants.get(id); }', text, flags=re.DOTALL)
text = re.sub(r'static Event findEvent\(int id\) \{.*?return null;\s*\}', 
              'static Event findEvent(int id) { return events.get(id); }', text, flags=re.DOTALL)
text = re.sub(r'static EventCoordinator findCoordinator\(int id\) \{.*?return null;\s*\}', 
              'static EventCoordinator findCoordinator(int id) { return coordinators.get(id); }', text, flags=re.DOTALL)
text = re.sub(r'static Admin findAdmin\(int id\) \{.*?return null;\s*\}', 
              'static Admin findAdmin(int id) { return admins.get(id); }', text, flags=re.DOTALL)

# 7. Login Email Optimization
text = re.sub(r'for \(Participant p : participants\.values\(\)\) \{.*?break;\s*\}\s*\}', 
              'user = participantEmailMap.get(email.toLowerCase());', text, flags=re.DOTALL)

text = re.sub(r'boolean exists = false;.*?if \(exists\)', 
              'boolean exists = participantEmailMap.containsKey(email.toLowerCase());\n\n                if (exists)', text, flags=re.DOTALL)

# 8. LoadData updates (clear and replace adds)
text = text.replace("events.clear();", "events.clear(); participantEmailMap.clear();")
text = text.replace("events.add(new Event(rs.getInt(\"event_id\"), rs.getString(\"name\"),", 
                    "Event e_ld = new Event(rs.getInt(\"event_id\"), rs.getString(\"name\"),")
text = text.replace("rs.getString(\"time\"), rs.getString(\"venue\"), rs.getInt(\"fee\")));",
                    "rs.getString(\"time\"), rs.getString(\"venue\"), rs.getInt(\"fee\"));\n                    events.put(e_ld.getEventId(), e_ld);")

text = text.replace("admins.add(new Admin(rs.getInt(\"admin_id\"), rs.getString(\"name\"),",
                    "Admin a_ld = new Admin(rs.getInt(\"admin_id\"), rs.getString(\"name\"),")
text = text.replace("rs.getString(\"email\"), rs.getString(\"password\")));",
                    "rs.getString(\"email\"), rs.getString(\"password\"));\n                    admins.put(a_ld.getId(), a_ld);")

text = text.replace("participants.add(new Participant(rs.getInt(\"participant_id\"), rs.getString(\"name\"),",
                    "Participant p_ld = new Participant(rs.getInt(\"participant_id\"), rs.getString(\"name\"),")
text = text.replace("Role.valueOf(rs.getString(\"role\"))));",
                    "Role.valueOf(rs.getString(\"role\")));\n                    participants.put(p_ld.getId(), p_ld);\n                    participantEmailMap.put(p_ld.getEmail().toLowerCase(), p_ld);")

text = text.replace("coordinators.add(new EventCoordinator(rs.getInt(\"coordinator_id\"), rs.getString(\"name\"),",
                    "EventCoordinator c_ld = new EventCoordinator(rs.getInt(\"coordinator_id\"), rs.getString(\"name\"),")
text = text.replace("rs.getString(\"email\"), rs.getString(\"department\"), rs.getString(\"phone\")));",
                    "rs.getString(\"email\"), rs.getString(\"department\"), rs.getString(\"phone\"));\n                    coordinators.put(c_ld.getId(), c_ld);")

text = text.replace("Venue v = new Venue(rs.getInt(\"venue_id\"), rs.getString(\"name\"),",
                    "Venue v = new Venue(rs.getInt(\"venue_id\"), rs.getString(\"name\"),")
text = text.replace("venues.add(v);", "venues.put(v.getVenueId(), v);")

# 9. UI List handling (converting Set/Map to List for indexed access)
text = text.replace("Participant selectedParticipant = selectedEvent.getRegisteredParticipants().get(pIndex - 1);",
                    """List<Participant> tempList = new ArrayList<>(selectedEvent.getRegisteredParticipants());
        Participant selectedParticipant = tempList.get(pIndex - 1);""")

text = text.replace("Event selectedEvent = user.getRegisteredEvents().get(choice - 1);",
                    """List<Event> tempList = new ArrayList<>(user.getRegisteredEvents());
        Event selectedEvent = tempList.get(choice - 1);""")

with open(filepath, "w") as f:
    f.write(text)

print("Refactoring Complete!")
