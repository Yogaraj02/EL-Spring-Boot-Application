package events;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import users.Participant;

import util.BaseRecord;

public class Certificate extends BaseRecord {
    private static final long serialVersionUID = 1L;

    private String certificateNumber;
    private Participant participant;
    private Event event;
    private String achievement;
    private boolean isIssued;

    public Certificate(int certificateId, Participant participant, Event event, String achievement) {
        super(certificateId);
        this.participant = participant;
        this.event = event;
        this.achievement = achievement;
        this.certificateNumber = generateCertificateNumber();
        this.recordDate = getCurrentDate();
        this.isIssued = false;
    }

    private String generateCertificateNumber() {
        return "CERT" + recordId + "-" + System.currentTimeMillis();
    }

    private String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate now = LocalDate.now();
        return dtf.format(now);
    }

    public int getCertificateId() {
        return recordId;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public Participant getParticipant() {
        return participant;
    }

    public Event getEvent() {
        return event;
    }

    public String getIssueDate() {
        return recordDate;
    }

    public String getAchievement() {
        return achievement;
    }

    public boolean isIssued() {
        return isIssued;
    }

    public void issueCertificate() {
        this.isIssued = true;
    }

    @Override
    public void displayRecord() {
        System.out.println("\n========================================");
        System.out.println("     CERTIFICATE OF " + achievement.toUpperCase());
        System.out.println("========================================");
        System.out.println("Certificate No: " + certificateNumber);
        System.out.println("\nThis is to certify that");
        System.out.println("\n        " + participant.getName().toUpperCase());
        System.out.println("\nhas successfully participated in");
        System.out.println("\n        " + event.getEventName().toUpperCase());
        System.out.println("\nEvent Type: " + event.getEventType());
        System.out.println("Date: " + recordDate);
        System.out.println("\nStatus: " + (isIssued ? "ISSUED" : "PENDING"));
        System.out.println("========================================\n");
    }

    @Override
    public String toDataString() {
        return "CERTIFICATE|ID:" + recordId + "|ParticipantID:" + participant.getId() + "|EventID:"
                + event.getEventId() + "|Achievement:" + achievement + "|Number:" + certificateNumber + "|Date:"
                + recordDate + "|Issued:" + isIssued;
    }
}
