package events;

import users.Participant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import util.BaseRecord;

public class Feedback extends BaseRecord {
    private static final long serialVersionUID = 1L;

    private Participant participant;
    private Event event;
    private int rating; 
    private String comments;
    private String category; 

    public Feedback(int feedbackId, Participant participant, Event event, int rating,
            String comments, String category) {
        super(feedbackId);
        this.participant = participant;
        this.event = event;
        this.rating = validateRating(rating);
        this.comments = comments;
        this.category = category;
        this.recordDate = getCurrentDateTime();
    }

    private int validateRating(int rating) {
        if (rating < 1)
            return 1;
        if (rating > 5)
            return 5;
        return rating;
    }

    private String getCurrentDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public int getFeedbackId() {
        return recordId;
    }

    public String getFeedbackDate() {
        return recordDate;
    }

    public Participant getParticipant() {
        return participant;
    }

    public Event getEvent() {
        return event;
    }

    public int getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }

    public String getCategory() {
        return category;
    }

    public String getRatingStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            stars.append("★");
        }
        for (int i = rating; i < 5; i++) {
            stars.append("☆");
        }
        return stars.toString();
    }

    @Override
    public void displayRecord() {
        System.out.println("================================");
        System.out.println("Feedback ID : " + recordId);
        System.out.println("Event       : " + event.getEventName());
        System.out.println("Participant : " + participant.getName());
        System.out.println("Category    : " + category);
        System.out.println("Rating      : " + getRatingStars() + " (" + rating + "/5)");
        System.out.println("Comments    : " + comments);
        System.out.println("Date        : " + recordDate);
        System.out.println("================================");
    }

    @Override
    public String toDataString() {
        return "FEEDBACK|ID:" + recordId + "|ParticipantID:" + participant.getId() + "|EventID:" + event.getEventId()
                + "|Rating:" + rating + "|Comments:" + comments + "|Category:" + category + "|Date:" + recordDate;
    }
}
