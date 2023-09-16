package com.kushina.customer.android.start_up_screens;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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


public class RegistrationActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();

    @BindView(R.id.edt_register_email)
    TextInputEditText edtRegisterEmail;
    @BindView(R.id.til_register_email)
    TextInputLayout tilRegisterEmail;
    @BindView(R.id.edt_register_password)
    TextInputEditText edtRegisterPassword;
    @BindView(R.id.til_register_password)
    TextInputLayout tilRegisterPassword;
    @BindView(R.id.edt_register_confirm_password)
    TextInputEditText edtRegisterConfirmPassword;
    @BindView(R.id.til_register_confirm_password)
    TextInputLayout tilRegisterConfirmPassword;
    @BindView(R.id.edt_register_firstname)
    TextInputEditText edtRegisterFirstname;
    @BindView(R.id.til_register_firstname)
    TextInputLayout tilRegisterFirstname;
    @BindView(R.id.edt_register_lastname)
    TextInputEditText edtRegisterLastname;
    @BindView(R.id.til_register_lastname)
    TextInputLayout tilRegisterLastname;
    @BindView(R.id.edt_register_referral_code)
    TextInputEditText edtRegisterReferralCode;
//    @BindView(R.id.til_register_referral_code)
//    TextInputLayout tilRegisterReferralCode;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.btn_back_to_login)
    Button btnBackToLogin;

    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    private Boolean isChecked;
    @BindView(R.id.edt_register_mobile)
    TextInputEditText edtRegisterMobile;
    @BindView(R.id.til_register_mobile)
    TextInputLayout tilRegisterMobile;
    @BindView(R.id.cb_terms_and_agreement)
    CheckBox cbTermsAndAgreement;
    @BindView(R.id.btn_privacy_policy)
    TextView btnPrivacyPolicy;
    @BindView(R.id.btn_terms_and_conditions)
    TextView btnTermsAndConditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        getSupportActionBar().hide();
        View mDecorView = this.getWindow().getDecorView();
//        mDecorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        //    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        //   | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE);


        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        setTitle("Registration");
        isChecked = false;
        btnPrivacyPolicy.setPaintFlags(btnPrivacyPolicy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btnTermsAndConditions.setPaintFlags(btnTermsAndConditions.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        cbTermsAndAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    isChecked = true;
                } else {
                    isChecked = false;
                }
            }
        });
        validateFields();
        // ATTENTION: This was auto-generated to handle app links.
        handleIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent();
    }

    private void handleIntent() {
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if(appLinkData != null){
//            mGlobals.toast(appLinkData.getLastPathSegment());
            edtRegisterReferralCode.setText(appLinkData.getLastPathSegment());
        }
    }

    @OnClick(R.id.btn_register)
    public void onViewClicked() {
        checkingBeforeSubmit();
    }

    private void backToLoginConfirmation(){
        mGlobals.showChoiceDialog("All fields will be cleared. Are you sure you want to go back?", true, new Globals.Callback() {
            @Override
            public void onPickCallback(Boolean result) {
                if (result) {
                    mPreferences.clearPreferences();
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        backToLoginConfirmation();
    }

    @OnClick(R.id.btn_back_to_login)
    public void backToLogin() {
        backToLoginConfirmation();
    }

    @OnClick({R.id.btn_privacy_policy, R.id.btn_terms_and_conditions})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_privacy_policy:
                openWebURL("https://kushina.xyz/customer_privacy_policy.html");
                break;
            case R.id.btn_terms_and_conditions:
                openWebURL("https://kushina.xyz/customer_terms_and_conditions.html");
                break;
        }
    }

    public void openWebURL( String inURL ) {
        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );

        startActivity( browse );
    }

    ;

    private void checkingBeforeSubmit() {
        if (
                !(
                        mGlobals.validateField(tilRegisterEmail, edtRegisterEmail, true, getString(R.string.err_msg_email)) ?
                                mGlobals.validateField(tilRegisterEmail, edtRegisterEmail, isValidEmail(edtRegisterEmail.getText().toString().trim()), getString(R.string.err_msg_valid_email)) :
                                mGlobals.validateField(tilRegisterEmail, edtRegisterEmail, true, getString(R.string.err_msg_email))
                ) ||
                        !(
                                mGlobals.validateField(tilRegisterPassword, edtRegisterPassword, true, getString(R.string.err_msg_password)) ?
                                        mGlobals.validateField(tilRegisterPassword, edtRegisterPassword, matchPasswords(edtRegisterPassword.getText().toString().trim(), edtRegisterConfirmPassword.getText().toString().trim()) && !edtRegisterPassword.equals(""), getString(R.string.err_msg_password_not_match)) :
                                        mGlobals.validateField(tilRegisterPassword, edtRegisterPassword, true, getString(R.string.err_msg_password))
                        ) ||
                        !(
                                mGlobals.validateField(tilRegisterConfirmPassword, edtRegisterConfirmPassword, true, getString(R.string.err_msg_confirm_password)) ?
                                        mGlobals.validateField(tilRegisterConfirmPassword, edtRegisterConfirmPassword, matchPasswords(edtRegisterPassword.getText().toString().trim(), edtRegisterConfirmPassword.getText().toString().trim()) && !edtRegisterConfirmPassword.equals(""), getString(R.string.err_msg_password_not_match)) :
                                        mGlobals.validateField(tilRegisterConfirmPassword, edtRegisterConfirmPassword, true, getString(R.string.err_msg_confirm_password))
                        ) ||
                        !mGlobals.validateField(tilRegisterFirstname, edtRegisterFirstname, true, getString(R.string.err_msg_first_name)) ||
                        !mGlobals.validateField(tilRegisterLastname, edtRegisterLastname, true, getString(R.string.err_msg_last_name)) ||
                        !mGlobals.validateField(tilRegisterMobile, edtRegisterMobile, true, getString(R.string.err_msg_mobile))
        ) {
            mGlobals.showErrorMessageWithDelay("Please answer all fields.", true, new Globals.Callback() {
                @Override
                public void onPickCallback(Boolean result) {
                    return;
                }
            });
        } else {
            if(isChecked) {
                createAccount();
            }else{
                mGlobals.showErrorMessageWithDelay("You need to agree to our Privacy Policy and Terms and Conditions before you can create an account.", true, new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        return;
                    }
                });
            }
        }

    }

    private void createAccount() {

        final Map<String, String> request_data = new HashMap<String, String>();
        // request_data.put("task", "register_customer");
        request_data.put("email", edtRegisterEmail.getText().toString().trim()); //Add the data you'd like to send to the server.
        request_data.put("password", edtRegisterPassword.getText().toString().trim());
        request_data.put("confirm_password", edtRegisterConfirmPassword.getText().toString().trim());
        request_data.put("firstname", edtRegisterFirstname.getText().toString().trim());
        request_data.put("lastname", edtRegisterLastname.getText().toString().trim());
        request_data.put("mobile", edtRegisterMobile.getText().toString().trim());
        request_data.put("referral_code", edtRegisterReferralCode.getText().toString().trim());
        request_data.put("uuid", mPreferences.getUniqueID(RegistrationActivity.this));

        // api call
        mAPI.api_request("POST",
                API_NODE_USER + "registerCustomer",
                request_data,
                true,
                RegistrationActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log("Register", String.valueOf(result));


                        try {
                            // parse response object
                            // JSONObject jsonObject = result.getJSONObject("data");
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;
                                JSONObject user_data = root.getJSONObject("data");
                                String user_id = user_data.getString("user_id");

                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {
                                            Intent intent = new Intent(RegistrationActivity.this, SubmitSMSOTPActivity.class);
                                            intent.putExtra("user_id", user_id);
                                            intent.putExtra("email", edtRegisterEmail.getText().toString());
                                            intent.putExtra("password", edtRegisterPassword.getText().toString());
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


    private void validateFields() {
        edtRegisterEmail.addTextChangedListener(new MyTextWatcher(edtRegisterEmail));
        edtRegisterPassword.addTextChangedListener(new MyTextWatcher(edtRegisterPassword));
        edtRegisterConfirmPassword.addTextChangedListener(new MyTextWatcher(edtRegisterConfirmPassword));
        edtRegisterFirstname.addTextChangedListener(new MyTextWatcher(edtRegisterFirstname));
        edtRegisterLastname.addTextChangedListener(new MyTextWatcher(edtRegisterLastname));
        edtRegisterMobile.addTextChangedListener(new MyTextWatcher(edtRegisterMobile));

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
                case R.id.edt_register_email:
                    if (mGlobals.validateField(tilRegisterEmail, edtRegisterEmail, true, getString(R.string.err_msg_email))) {
                        mGlobals.validateField(tilRegisterEmail, edtRegisterEmail, isValidEmail(edtRegisterEmail.getText().toString().trim()), getString(R.string.err_msg_valid_email));
                    }
                    break;
                case R.id.edt_register_password:
                    if (mGlobals.validateField(tilRegisterPassword, edtRegisterPassword, true, getString(R.string.err_msg_password))) {
                        if (mGlobals.validateField(tilRegisterPassword, edtRegisterPassword, matchPasswords(edtRegisterPassword.getText().toString().trim(), edtRegisterConfirmPassword.getText().toString().trim()), getString(R.string.err_msg_password_not_match))) {
                            mGlobals.removeError(tilRegisterConfirmPassword);
                        }
                    }
                    break;
                case R.id.edt_register_confirm_password:
                    if (mGlobals.validateField(tilRegisterConfirmPassword, edtRegisterConfirmPassword, true, getString(R.string.err_msg_confirm_password))) {
                        if (mGlobals.validateField(tilRegisterConfirmPassword, edtRegisterConfirmPassword, matchPasswords(edtRegisterPassword.getText().toString().trim(), edtRegisterConfirmPassword.getText().toString().trim()), getString(R.string.err_msg_password_not_match))) {
                            mGlobals.removeError(tilRegisterPassword);
                        }
                    }
                    break;
                case R.id.edt_register_firstname:
                    mGlobals.validateField(tilRegisterFirstname, edtRegisterFirstname, true, getString(R.string.err_msg_first_name));
                    break;
                case R.id.edt_register_lastname:
                    mGlobals.validateField(tilRegisterLastname, edtRegisterLastname, true, getString(R.string.err_msg_last_name));
                    break;
                case R.id.edt_register_mobile:
                    mGlobals.validateField(tilRegisterMobile, edtRegisterMobile, true, getString(R.string.err_msg_mobile));
                    break;
            }
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean matchPasswords(String password, String confirm_password) {
        return password.equals(confirm_password);
    }
}
