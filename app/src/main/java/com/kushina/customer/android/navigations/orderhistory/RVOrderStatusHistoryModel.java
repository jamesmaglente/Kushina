package com.kushina.customer.android.navigations.orderhistory;

public class RVOrderStatusHistoryModel {

    private String orderHistoryID;
    private String orderID;
    private String userID;
    private String fromStatusID;
    private String fromStatus;
    private String toStatusID;
    private String toStatus;
    private String remarks;
    private String dateCreated;

    public RVOrderStatusHistoryModel(String orderHistoryID, String orderID, String userID, String fromStatusID, String fromStatus, String toStatusID, String toStatus, String remarks, String dateCreated) {
        this.orderHistoryID = orderHistoryID;
        this.orderID = orderID;
        this.userID = userID;
        this.fromStatusID = fromStatusID;
        this.fromStatus = fromStatus;
        this.toStatusID = toStatusID;
        this.toStatus = toStatus;
        this.remarks = remarks;
        this.dateCreated = dateCreated;
    }

    public String getOrderHistoryID() {
        return orderHistoryID;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getUserID() {
        return userID;
    }

    public String getFromStatusID() {
        return fromStatusID;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public String getToStatusID() {
        return toStatusID;
    }

    public String getToStatus() {
        return toStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getDateCreated() {
        return dateCreated;
    }
}
