package com.kushina.customer.android.navigations.orderhistory;

public class RVOrderHistoryModel {

    private String orderID;
    private String codeID;
    private String cartID;
    private String statusID;
    private String status;
    private String userID;
    private String paymentOptionID;
    private String paymentOption;
    private String addressID;
    private String addressTitle;
    private String addressContact;
    private String reference;
    private String totalAmount;
    private String dateCreated;


    public RVOrderHistoryModel(String orderID, String codeID, String cartID, String statusID, String status, String userID, String paymentOptionID, String paymentOption, String addressID, String addressTitle, String addressContact, String reference, String totalAmount, String dateCreated) {
        this.orderID = orderID;
        this.codeID = codeID;
        this.cartID = cartID;
        this.statusID = statusID;
        this.status = status;
        this.userID = userID;
        this.paymentOptionID = paymentOptionID;
        this.paymentOption = paymentOption;
        this.addressID = addressID;
        this.addressTitle = addressTitle;
        this.addressContact = addressContact;
        this.reference = reference;
        this.totalAmount = totalAmount;
        this.dateCreated = dateCreated;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getCodeID() {
        return codeID;
    }

    public String getCartID() {
        return cartID;
    }

    public String getStatusID() {
        return statusID;
    }

    public String getStatus() {
        return status;
    }

    public String getUserID() {
        return userID;
    }

    public String getPaymentOptionID() {
        return paymentOptionID;
    }

    public String getPaymentOption() {
        return paymentOption;
    }

    public String getAddressID() {
        return addressID;
    }

    public String getAddressTitle() {
        return addressTitle;
    }

    public String getAddressContact() {
        return addressContact;
    }

    public String getReference() {
        return reference;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getDateCreated() {
        return dateCreated;
    }
}
