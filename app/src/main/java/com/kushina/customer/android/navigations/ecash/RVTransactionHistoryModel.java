package com.kushina.customer.android.navigations.ecash;

public class RVTransactionHistoryModel {

    private String eCashHistoryID;
    private String transactionTypeID;
    private String transactionType;
    private String userID;
    private String orderID;
    private String tax;
    private String amountDebited;
    private String amountCredited;
    private String endingBalance;
    private String claimed;
    private String fromID;
    private String from;
    private String level;
    private String date;
    private String type;

    public RVTransactionHistoryModel(String eCashHistoryID, String transactionTypeID, String transactionType, String userID, String orderID, String tax, String amountDebited, String amountCredited, String endingBalance, String claimed, String fromID, String from, String level, String date, String type) {
        this.eCashHistoryID = eCashHistoryID;
        this.transactionTypeID = transactionTypeID;
        this.transactionType = transactionType;
        this.userID = userID;
        this.orderID = orderID;
        this.tax = tax;
        this.amountDebited = amountDebited;
        this.amountCredited = amountCredited;
        this.endingBalance = endingBalance;
        this.claimed = claimed;
        this.fromID = fromID;
        this.from = from;
        this.level = level;
        this.date = date;
        this.type = type;
    }

    public String geteCashHistoryID() {
        return eCashHistoryID;
    }

    public String getTransactionTypeID() {
        return transactionTypeID;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getUserID() {
        return userID;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getTax() {
        return tax;
    }

    public String getAmountDebited() {
        return amountDebited;
    }

    public String getAmountCredited() {
        return amountCredited;
    }

    public String getEndingBalance() {
        return endingBalance;
    }

    public String getClaimed() {
        return claimed;
    }

    public String getFromID() {
        return fromID;
    }

    public String getFrom() {
        return from;
    }

    public String getLevel() {
        return level;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }
}
