package com.kushina.customer.android.start_up_screens;

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
import com.kushina.customer.android.navigations.MainActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_USER;

public class SubmitSMSOTPActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.edt_submit_otp)
    TextInputEditText edtSubmitOtp;
    @BindView(R.id.til_submit_otp)
    TextInputLayout tilSubmitOtp;
    @BindView(R.id.btn_submit_otp)
    Button btnSubmitOtp;
    @BindView(R.id.btn_request_new_otp)
    TextView btnRequestNewOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_s_m_s_o_t_p);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        edtSubmitOtp.addTextChangedListener(new MyTextWatcher(edtSubmitOtp));

        getSupportActionBar().hide();
    }

    @OnClick({R.id.btn_submit_otp, R.id.btn_request_new_otp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_submit_otp:
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

    private void submitOTP(){

        final Map<String, String> request_data = new HashMap<String, String>();
        // request_data.put("task", "register_customer");
        request_data.put("user_id", getIntent().getStringExtra("user_id")); //Add the data you'd like to send to the server.
        request_data.put("type", "Registration");
        request_data.put("otp_key", edtSubmitOtp.getText().toString().trim());
        request_data.put("email", getIntent().getStringExtra("email"));
        request_data.put("password",getIntent().getStringExtra("password"));


        // api call
        mAPI.api_request("POST",
                API_NODE_USER + "submitSMSOTP",
                request_data,
                true,
                SubmitSMSOTPActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log(TAG, String.valueOf(result));


                        try {
                            // parse response object
                            // JSONObject jsonObject = result.getJSONObject("data");
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;
                                JSONObject data = root.getJSONObject("data");

                                // get each data from response object
                                Integer user_id = Integer.parseInt(data.get("user_id").toString());
                                String username = data.get("username").toString();
                                String first_name = data.get("firstname").toString();
                                String middle_name = data.get("middlename").toString();
                                String last_name = data.get("lastname").toString();
                                String full_name = first_name + " " + last_name;
                                String email = data.get("email").toString();
                                Integer user_group_id = Integer.parseInt(data.get("user_group_id").toString());
                                String code = data.get("code").toString();
                                String token = data.get("token").toString();
                                String profile_picture = data.get("profile_picture").toString();
                                String mobile = data.get("mobile").toString();
                                String membership_type_id = data.get("membership_type_id").toString();
                                String membership_type = data.get("membership_type").toString();
                                String token_expiration = data.get("token_expiration").toString();
                                String current_rank = data.get("current_rank").toString();

                                // mGlobals.toast(token);

                                // save share preferences
                                mPreferences.setIsLogged(true);
                                mPreferences.setUserId(user_id);
                                mPreferences.setUserToken(token);
                                mPreferences.setUserCode(code);
                                mPreferences.setUsername(username);
                                mPreferences.setFirstname(first_name);
                                mPreferences.setMiddlename(middle_name);
                                mPreferences.setLastname(last_name);
                                mPreferences.setEmail(email);
                                mPreferences.setUserProfilePicture(profile_picture);
                                mPreferences.setMembershipTypeId(membership_type_id);
                                mPreferences.setMembershipType(membership_type);
                                mPreferences.setMobile(mobile);
                                mPreferences.setTokenExpiration(token_expiration);
                                mPreferences.setCurrentRank(current_rank);

                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {

                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();

                                            // show welcome back message
                                            mGlobals.toast("Welcome back " + first_name + "!");
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

    private void checkFields(){
        if (

                        !mGlobals.validateField(tilSubmitOtp, edtSubmitOtp, true, getString(R.string.err_msg_otp_key))
        ) {
            mGlobals.showErrorMessageWithDelay("Please enter a code.", true, new Globals.Callback() {
                @Override
                public void onPickCallback(Boolean result) {
                    return;
                }
            });
        } else {
           submitOTP();
        }
    }

    private void requestNewOTP(){

        final Map<String, String> request_data = new HashMap<String, String>();
        // request_data.put("task", "register_customer");
        request_data.put("user_id", getIntent().getStringExtra("user_id")); //Add the data you'd like to send to the server.
        request_data.put("type", "Registration");

        // api call
        mAPI.api_request("POST",
                API_NODE_USER + "requestSMSOTP",
                request_data,
                true,
                SubmitSMSOTPActivity.this,
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
                    mGlobals.validateField(tilSubmitOtp, edtSubmitOtp, true, getString(R.string.err_msg_otp_key));
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {


        mGlobals.showChoiceDialog("Are you sure you want to exit this screen? You wont be able to verify your number if you exit this screen.", true, new Globals.Callback() {
            @Override
            public void onPickCallback(Boolean result) {
                if(result){
                    finish();
                }
            }
        });
    }
}