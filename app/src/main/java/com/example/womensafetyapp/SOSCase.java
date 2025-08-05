package com.example.womensafetyapp;
public class SOSCase {
    private String caseId;
    private String userId;
    private String userName;
    private String userPhone;
    private String location;
    private String status;
    private String timestamp; // Add timestamp if needed

    // Default constructor (required for Firebase)
    public SOSCase() {
    }

    // Constructor
    public SOSCase(String caseId, String userId, String userName, String userPhone, String location, String status, String timestamp) {
        this.caseId = caseId;
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.location = location;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters
    public String getCaseId() {
        return caseId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    // **Add this setter method**
    public void setStatus(String status) {
        this.status = status;
    }
}
