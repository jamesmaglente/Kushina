package com.kushina.customer.android.navigations.account;

public class RVAccountNavigationsModel {

    private int navigationIcon;
    private String navigationTitle;

    public RVAccountNavigationsModel(int navigationIcon, String navigationTitle) {
        this.navigationIcon = navigationIcon;
        this.navigationTitle = navigationTitle;
    }

    public int getNavigationIcon() {
        return navigationIcon;
    }

    public String getNavigationTitle() {
        return navigationTitle;
    }
}
