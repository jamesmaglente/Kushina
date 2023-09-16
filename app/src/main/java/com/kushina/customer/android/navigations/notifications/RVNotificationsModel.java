package com.kushina.customer.android.navigations.notifications;

public class RVNotificationsModel {

    private String notificationID;
    private String title;
    private String description;
    private String unread;
    private String image;
    private String dateCreated;
    private String orderID;
    private String depositID;

    public RVNotificationsModel(String notificationID, String title, String description, String unread, String image, String dateCreated, String orderID, String depositID) {
        this.notificationID = notificationID;
        this.title = title;
        this.description = description;
        this.unread = unread;
        this.image = image;
        this.dateCreated = dateCreated;
        this.orderID = orderID;
        this.depositID = depositID;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUnread() {
        return unread;
    }

    public String getImage() {
        return image;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getDepositID() {
        return depositID;
    }
}
