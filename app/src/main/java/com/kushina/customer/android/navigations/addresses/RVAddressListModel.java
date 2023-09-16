package com.kushina.customer.android.navigations.addresses;

public class RVAddressListModel {

    private String deliveryAddressID;
    private String userID;
    private String mainAddress;
    private String title;
    private String customerName;
    private String contactNumber;
    private String addressLine;
    private String houseAddress;
    private String zipCode;
    private String landmarks;
    private String latitude;
    private String longitude;
    private String placeID;
    private String viewable;
    private String dateAdded;


    public RVAddressListModel(String deliveryAddressID, String userID, String mainAddress, String title, String customerName, String contactNumber, String addressLine, String houseAddress, String zipCode, String landmarks, String latitude, String longitude, String placeID, String viewable, String dateAdded) {
        this.deliveryAddressID = deliveryAddressID;
        this.userID = userID;
        this.mainAddress = mainAddress;
        this.title = title;
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.addressLine = addressLine;
        this.houseAddress = houseAddress;
        this.zipCode = zipCode;
        this.landmarks = landmarks;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeID = placeID;
        this.viewable = viewable;
        this.dateAdded = dateAdded;
    }

    public String getDeliveryAddressID() {
        return deliveryAddressID;
    }

    public String getUserID() {
        return userID;
    }

    public String getMainAddress() {
        return mainAddress;
    }

    public String getTitle() {
        return title;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getHouseAddress() {
        return houseAddress;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getLandmarks() {
        return landmarks;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPlaceID() {
        return placeID;
    }

    public String getViewable() {
        return viewable;
    }

    public String getDateAdded() {
        return dateAdded;
    }
}
