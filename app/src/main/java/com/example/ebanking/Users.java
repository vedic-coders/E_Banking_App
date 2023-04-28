package com.example.ebanking;

import android.widget.EditText;

public class Users {


    public String userId, email, userName, password, profilePic;
    long timeStamp;

    public Users(String id, String email, String userName, String password, String profilePic) {
        this.userId = id;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.profilePic = profilePic;
    }

    public Users() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
