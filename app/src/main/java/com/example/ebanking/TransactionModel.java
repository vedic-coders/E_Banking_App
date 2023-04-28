package com.example.ebanking;

public class TransactionModel {
    double amount;
    String senderId;
    long timeStamp;

    public TransactionModel(double amount, String senderId, long timeStamp) {
        this.amount = amount;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
    }
    public TransactionModel()
    {}

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
