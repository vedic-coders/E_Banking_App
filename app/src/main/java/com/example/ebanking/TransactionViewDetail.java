package com.example.ebanking;

public class TransactionViewDetail {
    String name, email, profilePic;
    double amount;
    long timeStamp;
    char sign;

    public TransactionViewDetail() {

    }

    public TransactionViewDetail(String name, String email, String profilePic, double amount, long timeStamp, char sign) {
        this.name = name;
        this.email = email;
        this.profilePic = profilePic;
        this.amount = amount;
        this.timeStamp = timeStamp;
        this.sign = sign;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public char getSign() {
        return sign;
    }

    public void setSign(char sign) {
        this.sign = sign;
    }
}
