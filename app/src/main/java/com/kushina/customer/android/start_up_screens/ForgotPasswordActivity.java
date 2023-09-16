package com.kushina.customer.android.start_up_screens;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
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

import static com.kushina.customer.android.globals.Endpoints.API_NODE_USER;

public class ForgotPasswordActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.edt_forgot_password_email)
    TextInputEditText edtForgotPasswordEmail;
    @BindView(R.id.til_forgot_password_email)
    TextInputLayout tilForgotPasswordEmail;
    @BindView(R.id.edt_forgot_password_mobile)
    TextInputEditText edtForgotPasswordMobile;
    @BindView(R.id.til_forgot_password_mobile)
    TextInputLayout tilForgotPasswordMobile;
    @BindView(R.id.edt_forgot_password_otp)
    TextInputEditText edtForgotPasswordOtp;
    @BindView(R.id.til_forgot_password_otp)
    TextInputLayout tilForgotPasswordOtp;
    @BindView(R.id.btn_forgot_password)
    Button btnForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        getSupportActionBar().hide();

        edtForgotPasswordMobile.addTextChangedListener(new MyTextWatcher(edtForgotPasswordMobile));
        edtForgotPasswordEmail.addTextChangedListener(new MyTextWatcher(edtForgotPasswordEmail));
    }

    @OnClick(R.id.btn_back_to_login)
    public void backToLogin() {
        onBackPressed();
    }

    @OnClick(R.id.btn_forgot_password)
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

    private void backToLoginConfirmation(){
        mGlobals.showChoiceDialog("All fields will be cleared. Are you sure you want to go back?", true, new Globals.Callback() {
            @Override
            public void onPickCallback(Boolean result) {
                if (result) {
                    mPreferences.clearPreferences();
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void checkFields(){

        if (
                !(
                        mGlobals.validateField(tilForgotPasswordEmail, edtForgotPasswordEmail, true, getString(R.string.err_msg_email)) ?
                                mGlobals.validateField(tilForgotPasswordEmail, edtForgotPasswordEmail, isValidEmail(edtForgotPasswordEmail.getText().toString().trim()), getString(R.string.err_msg_valid_email)) :
                                mGlobals.validateField(tilForgotPasswordEmail, edtForgotPasswordEmail, true, getString(R.string.err_msg_email))
                ) ||
                !mGlobals.validateField(tilForgotPasswordMobile, edtForgotPasswordMobile, true, getString(R.string.err_msg_mobile))
        ) {
//            mGlobals.showErrorMessageWithDelay("Please enter a code.", true, new Globals.Callback() {
//                @Override
//                public void onPickCallback(Boolean result) {
                    return;
//                }
//            });
        } else {
            forgotPassword();
        }

    }

    private void forgotPassword(){

        final Map<String, String> request_data = new HashMap<String, String>();
        // request_data.put("task", "register_customer");
        request_data.put("mobile", edtForgotPasswordMobile.getText().toString()); //Add the data you'd like to send to the server.
        request_data.put("email", edtForgotPasswordEmail.getText().toString());

        // api call
        mAPI.api_request("POST",
                API_NODE_USER + "forgotPassword",
                request_data,
                true,
                ForgotPasswordActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log("Forgot Password", String.valueOf(result));


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
                                            Intent intent = new Intent(ForgotPasswordActivity.this,ResetPasswordActivity.class);
                                            intent.putExtra("mobile",edtForgotPasswordMobile.getText().toString());
                                            intent.putExtra("email",edtForgotPasswordEmail.getText().toString());
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

                case R.id.edt_forgot_password_email:
                    mGlobals.validateField(tilForgotPasswordEmail, edtForgotPasswordEmail, true, getString(R.string.err_msg_email));
                    if (mGlobals.validateField(tilForgotPasswordEmail, edtForgotPasswordEmail, true, getString(R.string.err_msg_email))) {
                        mGlobals.validateField(tilForgotPasswordEmail, edtForgotPasswordEmail, isValidEmail(edtForgotPasswordEmail.getText().toString().trim()), getString(R.string.err_msg_valid_email));
                    }
                    break;
                case R.id.edt_forgot_password_mobile:
                    mGlobals.validateField(tilForgotPasswordMobile, edtForgotPasswordMobile, true, getString(R.string.err_msg_mobile));
                    break;
            }
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}