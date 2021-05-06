package uk.ac.tees.aad.w9491709.Utills;

public class Friends {

    private String address, phone, profileImageUrl, username;

    public Friends() {
    }

    public Friends(String address, String phone, String profileImageUrl, String username) {
        this.address = address;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
