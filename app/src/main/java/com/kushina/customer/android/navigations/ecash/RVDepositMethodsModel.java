package com.kushina.customer.android.navigations.ecash;

public class RVDepositMethodsModel {

    private int depositMethodIcon;
    private String depositMethodTitle;

    public RVDepositMethodsModel(int depositMethodIcon, String depositMethodTitle) {
        this.depositMethodIcon = depositMethodIcon;
        this.depositMethodTitle = depositMethodTitle;
    }

    public String getDepositMethodTitle() {
        return depositMethodTitle;
    }

    public int getDepositMethodIcon() {
        return depositMethodIcon;
    }


}
