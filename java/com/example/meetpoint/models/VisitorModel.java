package com.example.meetpoint.models;

public class VisitorModel {

    // ================= BASIC =================
    private String id;
    private String token;
    private String name;
    private String phone;
    private String email;

    // ================= ADDRESS =================
    private String tempAddressLine;
    private String tempCity;
    private String tempPincode;

    private String permAddressLine;
    private String permCity;
    private String permPincode;

    // ================= VISIT =================
    private String purpose;
    private String visitDateTime;
    private String description;

    // ================= WHOM TO MEET =================
    private String whomToMeet;
    private String whomPhone;

    // ================= EXIT =================
    private String exitDateTime;
    private String remark;
    private String againVisit;   // "Yes" or "No" (STRING to avoid crash)
    private String nextVisit;

    // ================= IMAGE =================
    private String photoUrl;

    // ================= STATUS =================
    private String status; // pending / approved / rejected / exited
    private long timestamp;

    // ================= EMPTY CONSTRUCTOR =================
    // REQUIRED by Firestore
    public VisitorModel() {}

    // ================= GETTERS & SETTERS =================

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTempAddressLine() { return tempAddressLine; }
    public void setTempAddressLine(String tempAddressLine) { this.tempAddressLine = tempAddressLine; }

    public String getTempCity() { return tempCity; }
    public void setTempCity(String tempCity) { this.tempCity = tempCity; }

    public String getTempPincode() { return tempPincode; }
    public void setTempPincode(String tempPincode) { this.tempPincode = tempPincode; }

    public String getPermAddressLine() { return permAddressLine; }
    public void setPermAddressLine(String permAddressLine) { this.permAddressLine = permAddressLine; }

    public String getPermCity() { return permCity; }
    public void setPermCity(String permCity) { this.permCity = permCity; }

    public String getPermPincode() { return permPincode; }
    public void setPermPincode(String permPincode) { this.permPincode = permPincode; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getVisitDateTime() { return visitDateTime; }
    public void setVisitDateTime(String visitDateTime) { this.visitDateTime = visitDateTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getWhomToMeet() { return whomToMeet; }
    public void setWhomToMeet(String whomToMeet) { this.whomToMeet = whomToMeet; }

    public String getWhomPhone() { return whomPhone; }
    public void setWhomPhone(String whomPhone) { this.whomPhone = whomPhone; }

    public String getExitDateTime() { return exitDateTime; }
    public void setExitDateTime(String exitDateTime) { this.exitDateTime = exitDateTime; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getAgainVisit() { return againVisit; }
    public void setAgainVisit(String againVisit) { this.againVisit = againVisit; }

    public String getNextVisit() { return nextVisit; }
    public void setNextVisit(String nextVisit) { this.nextVisit = nextVisit; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // ================= STATUS HELPERS =================
    public boolean isApproved() {
        return "approved".equalsIgnoreCase(status);
    }

    public boolean isRejected() {
        return "rejected".equalsIgnoreCase(status);
    }

    public boolean isPending() {
        return "pending".equalsIgnoreCase(status);
    }

    public boolean isExited() {
        return "exited".equalsIgnoreCase(status);
    }
}
