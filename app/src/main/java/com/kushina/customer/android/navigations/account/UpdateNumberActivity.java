package com.kushina.customer.android.navigations.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

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

public class UpdateNumberActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.edt_update_number_new_mobile)
    TextInputEditText edtUpdateNumberNewMobile;
    @BindView(R.id.til_update_number_new_mobile)
    TextInputLayout tilUpdateNumberNewMobile;
    @BindView(R.id.edt_update_number_otp)
    TextInputEditText edtUpdateNumberOtp;
    @BindView(R.id.til_update_number_otp)
    TextInputLayout tilUpdateNumberOtp;
    @BindView(R.id.btn_update_number)
    Button btnUpdateNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_number);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        edtUpdateNumberNewMobile.addTextChangedListener(new MyTextWatcher(edtUpdateNumberNewMobile));
        edtUpdateNumberOtp.addTextChangedListener(new MyTextWatcher(edtUpdateNumberOtp));
    }

    @OnClick(R.id.btn_update_number)
    public void onViewClicked() {
        mGlobals.showChoiceDialog("Are you sure you want to proceed?", true, new Globals.Callback() {
            @Override
            public void onPickCallback(Boolean result) {
                if(result){
                    checkFields();
                }
            }
        });
    }

    private void checkFields(){

        if (
                !mGlobals.validateField(tilUpdateNumberNewMobile, edtUpdateNumberNewMobile, true, getString(R.string.err_msg_mobile)) ||
                        !mGlobals.validateField(tilUpdateNumberOtp, edtUpdateNumberOtp, true, getString(R.string.err_msg_otp_key))
        ) {
//            mGlobals.showErrorMessageWithDelay("Please enter a code.", true, new Globals.Callback() {
//                @Override
//                public void onPickCallback(Boolean result) {
            return;
//                }
//            });
        } else {
            verifyOldNumber();
        }

    }

    private void verifyOldNumber(){

        final Map<String, String> request_data = new HashMap<String, String>();
        // request_data.put("task", "register_customer");
        request_data.put("user_id", mPreferences.getUserId().toString()); //Add the data you'd like to send to the server.
        request_data.put("otp_key", edtUpdateNumberOtp.getText().toString());
        request_data.put("new_mobile", edtUpdateNumberNewMobile.getText().toString());

        // api call
        mAPI.api_request("POST",
                API_NODE_PROFILE + "verifyOldMobileNumber",
                request_data,
                true,
                UpdateNumberActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log("Verify Old Number", String.valueOf(result));


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
                                            Intent intent = new Intent(UpdateNumberActivity.this, VerifyNewNumberActivity.class);
                                            intent.putExtra("new_mobile",edtUpdateNumberNewMobile.getText().toString());
                                            startActivity(intent);
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

                case R.id.edt_update_number_new_mobile:
                    mGlobals.validateField(tilUpdateNumberNewMobile, edtUpdateNumberNewMobile, true, getString(R.string.err_msg_mobile));
                    break;
                case R.id.edt_update_number_otp:
                    mGlobals.validateField(tilUpdateNumberOtp, edtUpdateNumberOtp, true, getString(R.string.err_msg_otp_key));
                    break;
            }
        }
    }
}