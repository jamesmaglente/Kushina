package com.kushina.customer.android.navigations.my_team;

public class RVMyTeamModel {

    private String referrorUserID;
    private String level;
    private String dateCreated;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private String email;
    private String mobile;
    private String status;
    private String total_purchased;

    public RVMyTeamModel(String referrorUserID, String level, String dateCreated, String firstName, String middleName, String lastName, String fullName, String email, String mobile, String status, String total_purchased) {
        this.referrorUserID = referrorUserID;
        this.level = level;
        this.dateCreated = dateCreated;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.status = status;
        this.total_purchased = total_purchased;
    }

    public String getReferrorUserID() {
        return referrorUserID;
    }

    public String getLevel() {
        return level;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getStatus() {
        return status;
    }

    public String getTotal_purchased() {
        return total_purchased;
    }
}
