package uk.ac.tees.aad.w9491709.Utills;

public class Chat {
    String sms,status,userID;

    public Chat(String sms, String status, String userID) {
        this.sms = sms;
        this.status = status;
        this.userID = userID;
    }

    public Chat() {
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
