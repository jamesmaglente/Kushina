package com.kushina.customer.android.globals;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.kushina.customer.android.navigations.MainActivity;

@SuppressLint("ParcelCreator")
public class MainReceiver extends ResultReceiver {

    private MainActivity.ResponseResult message;

    public MainReceiver(MainActivity.ResponseResult message){
        super(new Handler());

        this.message = message;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData){
        message.displayMessage(resultCode, resultData);
    }

}
