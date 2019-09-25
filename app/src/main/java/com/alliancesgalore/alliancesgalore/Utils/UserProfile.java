package com.alliancesgalore.alliancesgalore.Utils;

public class UserProfile {
    private String Name, Email, Password, Displayimage, Reportingto;
    private Double Latitude;
    private Double Longitude;
    private int Level;

    public UserProfile() {}

    public UserProfile(String name, String email, String password, String displayimage, String reportingto, Double latitude, Double longitude, int level) {
        Name = name;
        Email = email;
        Password = password;
        Displayimage = displayimage;
        Reportingto = reportingto;
        Latitude = latitude;
        Longitude = longitude;
        Level = level;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getDisplayimage() {
        return Displayimage;
    }

    public void setDisplayimage(String displayimage) {
        Displayimage = displayimage;
    }

    public String getReportingto() {
        return Reportingto;
    }

    public void setReportingto(String reportingto) {
        Reportingto = reportingto;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        Level = level;
    }


}
