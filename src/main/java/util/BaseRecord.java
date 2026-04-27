package util;

import java.io.Serializable;

public abstract class BaseRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int recordId;
    protected String recordDate;

    public BaseRecord(int recordId) {
        this.recordId = recordId;
    }

    public int getRecordId() {
        return recordId;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public abstract void displayRecord();
    public abstract String toDataString();
}
