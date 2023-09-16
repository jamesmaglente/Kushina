package com.kushina.customer.android.navigations.dashboard;

public class RVLeaderboardModel {

    private String userID;
    private String name;
    private String amount;
    private String rank;

    public RVLeaderboardModel(String userID, String name, String amount, String rank) {
        this.userID = userID;
        this.name = name;
        this.amount = amount;
        this.rank = rank;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getRank() {
        return rank;
    }
}
