package com.kushina.customer.android.navigations.cart;

public class RVPaymentOptionsModel {

    private String paymentOptionID;
    private String paymentOption;


    public RVPaymentOptionsModel(String paymentOptionID, String paymentOption) {
        this.paymentOptionID = paymentOptionID;
        this.paymentOption = paymentOption;
    }

    public String getPaymentOptionID() {
        return paymentOptionID;
    }

    public String getPaymentOption() {
        return paymentOption;
    }
}
