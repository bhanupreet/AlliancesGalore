package com.alliancesgalore.alliancesgalore;

import android.icu.lang.UScript;

import java.io.Serializable;
import java.util.Comparator;

public class UserProfile implements Serializable {
    String TokenID;
    String display_name;
    String email;
    String image;
    String password;
    String role;
    int level;
    double Latitude;
    double Longitude;
    String ReportingTo;
    Long LastUpdated;
    Boolean isSelected = false;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public UserProfile() {
    }

    public UserProfile(String tokenID, String display_name, String email, String image, String password, String role, int level, double latitude, double longitude, String reportingTo, Long lastUpdated, Boolean isSelected) {
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
        this.isSelected = isSelected;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
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

    public void compare1() {
        Comparator<UserProfile> compareByrole = (UserProfile o1, UserProfile o2) ->
                o1.getRole().compareTo(o2.getRole());
    }

    public void compare2() {
        Comparator<UserProfile> compareByName = (UserProfile o1, UserProfile o2) ->
                o1.getDisplay_name().compareTo(o2.getDisplay_name());
    }
}

