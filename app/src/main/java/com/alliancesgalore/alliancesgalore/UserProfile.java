package com.alliancesgalore.alliancesgalore;

public class UserProfile {
    String TokenID, display_name, email, image, password, role;
    int level;

    public UserProfile() {
    }

    public UserProfile(String tokenID, String display_name, String email, String image, String password, String role, int level) {
        TokenID = tokenID;
        this.display_name = display_name;
        this.email = email;
        this.image = image;
        this.password = password;
        this.role = role;
        this.level = level;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


}
