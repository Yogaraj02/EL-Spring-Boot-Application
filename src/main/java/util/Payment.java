package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class Payment extends BaseRecord {
    private static final long serialVersionUID = 1L;

    private int participantId;
    private int eventId;
    private int amount;
    private PaymentType paymentType;
    private String transactionId;
    private boolean isSuccessful;

    public Payment(int paymentId, int participantId, int eventId, int amount, PaymentType paymentType) {
        super(paymentId);
        this.participantId = participantId;
        this.eventId = eventId;
        this.amount = amount;
        this.paymentType = paymentType;
        this.transactionId = generateTransactionId();
        this.recordDate = getCurrentDateTime();
        this.isSuccessful = false;
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }

    private String getCurrentDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public int getPaymentId() {
        return recordId;
    }

    public int getParticipantId() {
        return participantId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getAmount() {
        return amount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getPaymentDate() {
        return recordDate;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void markAsSuccessful() {
        this.isSuccessful = true;
    }

    @Override
    public void displayRecord() {
        System.out.println("================================");
        System.out.println("Payment ID      : " + recordId);
        System.out.println("Transaction ID  : " + transactionId);
        System.out.println("Participant ID  : " + participantId);
        System.out.println("Event ID        : " + eventId);
        System.out.println("Amount          : Rs." + amount);
        System.out.println("Payment Type    : " + paymentType);
        System.out.println("Date            : " + recordDate);
        System.out.println("Status          : " + (isSuccessful ? "Success" : "Pending"));
    }

    @Override
    public String toDataString() {
        return "PAYMENT|ID:" + recordId + "|ParticipantID:" + participantId + "|EventID:" + eventId + "|Amount:"
                + amount + "|Type:" + paymentType + "|TransactionID:" + transactionId + "|Date:" + recordDate
                + "|Success:" + isSuccessful;
    }
}
