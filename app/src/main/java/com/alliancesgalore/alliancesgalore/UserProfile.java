package com.alliancesgalore.alliancesgalore;

public class UserProfile {
    String TokenID;
    String display_name;
    String email;
    String image;
    String password;
    String role;
    String level;
    String Latitude;
    String Longitude;
    String ReportingTo;
    Long LastUpdated;


    public UserProfile(String tokenID, String display_name, String email, String image, String password, String role, String level, String latitude, String longitude, String reportingTo, Long lastUpdated) {
        TokenID = tokenID;
        this.display_name = display_name;
        this.email = email;
        this.image = image;
        this.password = password;
        this.role = role;
        this.level = level;
        Latitude = latitude;
        Longitude = longitude;
        ReportingTo = reportingTo;
        LastUpdated = lastUpdated;
    }

    public UserProfile() {
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public Long getLastUpdated() {
        return LastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        LastUpdated = lastUpdated;
    }

    public String getReportingTo() {
        return ReportingTo;
    }

    public void setReportingTo(String reportingTo) {
        ReportingTo = reportingTo;
    }

    public String getTokenID() {
        return TokenID;
    }

    public void setTokenID(String tokenID) {
        TokenID = tokenID;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }


}
