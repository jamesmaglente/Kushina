package com.kushina.customer.android.navigations.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_PROFILE;
import static com.kushina.customer.android.globals.Endpoints.API_NODE_USER;

public class VerifyNewNumberActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.edt_submit_otp)
    TextInputEditText edtSubmitOtp;
    @BindView(R.id.til_new_number_otp)
    TextInputLayout tilNewNumberOtp;
    @BindView(R.id.btn_new_number_submit)
    Button btnNewNumberSubmit;
    @BindView(R.id.btn_request_new_otp)
    TextView btnRequestNewOtp;

    private String newMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_new_number);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        newMobile = getIntent().getStringExtra("new_mobile");
        edtSubmitOtp.addTextChangedListener(new MyTextWatcher(edtSubmitOtp));
    }

    @OnClick({R.id.btn_new_number_submit, R.id.btn_request_new_otp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_new_number_submit:
                mGlobals.showChoiceDialog("Are you sure you want to submit this code?", true, new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        if(result){
                            checkFields();
                        }
                    }
                });

                break;
            case R.id.btn_request_new_otp:
                mGlobals.showChoiceDialog("Are you sure you want to request a new code?", true, new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        if(result){
                            requestNewOTP();
                        }
                    }
                });
                break;
        }
    }

    private void checkFields(){
        if (

                !mGlobals.validateField(tilNewNumberOtp, edtSubmitOtp, true, getString(R.string.err_msg_otp_key))
        ) {
            mGlobals.showErrorMessageWithDelay("Please enter a code.", true, new Globals.Callback() {
                @Override
                public void onPickCallback(Boolean result) {
                    return;
                }
            });
        } else {
            updateNumber();
        }
    }

    private void updateNumber(){
        final Map<String, String> request_data = new HashMap<String, String>();
        // request_data.put("task", "register_customer");
        request_data.put("user_id", mPreferences.getUserId().toString()); //Add the data you'd like to send to the server.
        request_data.put("new_mobile", newMobile);
        request_data.put("new_mobile_otp", edtSubmitOtp.getText().toString());

        // api call
        mAPI.api_request("POST",
                API_NODE_PROFILE + "updateMobileNumber",
                request_data,
                true,
                VerifyNewNumberActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log("Update New Mobile", String.valueOf(result));


                        try {
                            // parse response object
                            // JSONObject jsonObject = result.getJSONObject("data");
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;


                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {
                                            mPreferences.setMobile(newMobile);
                                            Intent intent = new Intent(VerifyNewNumberActivity.this,UpdateProfileActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });


                            } else {
                                // show error
                                mGlobals.log(TAG, result.toString());
                                mGlobals.dialog(status_message);
                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(TAG, result.toString());
                            mGlobals.dialog(result.toString());
                        }
                    }
                });


    }

    private void requestNewOTP(){

        final Map<String, String> request_data = new HashMap<String, String>();
        // request_data.put("task", "register_customer");
        request_data.put("user_id", mPreferences.getUserId().toString()); //Add the data you'd like to send to the server.
        request_data.put("type", "Mobile Update");

        // api call
        mAPI.api_request("POST",
                API_NODE_USER + "requestSMSOTP",
                request_data,
                true,
                VerifyNewNumberActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log("Request SMS OTP", String.valueOf(result));


                        try {
                            // parse response object
                            // JSONObject jsonObject = result.getJSONObject("data");
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;


                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {
                                            return;
                                        }
                                    }
                                });


                            } else {
                                // show error
                                mGlobals.log(TAG, result.toString());
                                mGlobals.dialog(status_message);
                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(TAG, result.toString());
                            mGlobals.dialog(result.toString());
                        }
                    }
                });


    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                case R.id.edt_submit_otp:
                    mGlobals.validateField(tilNewNumberOtp, edtSubmitOtp, true, getString(R.string.err_msg_otp_key));
                    break;
            }
        }
    }
}