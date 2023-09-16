package com.kushina.customer.android.start_up_screens;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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

import static com.kushina.customer.android.globals.Endpoints.API_NODE_LOGIN;

public class LoginActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();

    @BindView(R.id.edt_login_email) TextInputEditText edtLoginEmail;
    @BindView(R.id.til_login_email) TextInputLayout tilLoginEmail;
    @BindView(R.id.edt_login_password) TextInputEditText edtLoginPassword;
    @BindView(R.id.til_login_password) TextInputLayout tilLoginPassword;
    @BindView(R.id.btn_login) Button btnLogin;
    @BindView(R.id.btn_login_forgot_password) TextView btnLoginForgotPassword;
    @BindView(R.id.btn_login_create_account) TextView btnLoginCreateAccount;
    @BindView(R.id.tv_or) TextView tvOr;
    @BindView(R.id.tv_version) TextView tvVersion;

    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    static final long ONE_MINUTE_IN_MILLIS = 60000; //millisecs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        getSupportActionBar().hide();
//        View mDecorView = this.getWindow().getDecorView();
//        mDecorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    //    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                 //       | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE);


        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        setTitle("Login");

//        btnLoginForgotPassword.setVisibility(View.GONE);
//        tvOr.setVisibility(View.GONE);

        String current_version = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            current_version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvVersion.setText("Ver: " + current_version);

        validateFields();
    }

    @OnClick({R.id.btn_login, R.id.btn_login_forgot_password, R.id.btn_login_create_account})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_login_forgot_password:
                intent = new Intent(this, ForgotPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login_create_account:
                intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void login(){
        //mGlobals.showLoadingDialog();

        if (
                !mGlobals.validateField(tilLoginEmail, edtLoginEmail, isValidEmail(edtLoginEmail.getText().toString().trim()), getString(R.string.err_msg_valid_email))
            ||
                !mGlobals.validateField(tilLoginPassword, edtLoginPassword, true, getString(R.string.err_msg_password))) {
//            mGlobals.dismissLoadingDialog();
            return;

        } else {

            final Map<String, String> request_data = new HashMap<String, String>();

            request_data.put("email", edtLoginEmail.getText().toString().trim()); //Add the data you'd like to send to the server.
            request_data.put("password", edtLoginPassword.getText().toString().trim());
            request_data.put("application", "kushina_customer");

            // api call
            mAPI.api_request("POST",
                    API_NODE_LOGIN,
                    request_data,
                    true,
                    LoginActivity.this,
                    new API.VolleyCallback(){
                        @Override
                        public void onResponseCallback(JSONObject jsonObject){
                            mGlobals.log(TAG, String.valueOf(jsonObject));
                            try {
                                // parse response object
                                Integer status_code = jsonObject.getInt("status_code");
                                boolean ok = jsonObject.getBoolean("status_ok");
                                String message = jsonObject.getString("status_message");

                                if(ok){

                                    JSONObject data = jsonObject.getJSONObject("data");

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

//                                    Calendar date = Calendar.getInstance();
//                                    long t= date.getTimeInMillis();
//                                    Date afterAddingTenMins = new Date(t + (60 * ONE_MINUTE_IN_MILLIS));
//                                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
//
//                                    mPreferences.setTokenExpiration(String.valueOf(formatter.parse(String.valueOf(afterAddingTenMins))));
//
//                                    mGlobals.log(TAG, mGlobals.getCurrentDate("dd-MMM-yyyy HH:mm:ss"));
//
//
//                                    mGlobals.log(TAG, String.valueOf(formatter.parse(mGlobals.getCurrentDate("dd-MMM-yyyy HH:mm:ss"))));
//
//                                    if(formatter.parse(mGlobals.getCurrentDate("dd-MMM-yyyy HH:mm:ss")).compareTo(afterAddingTenMins) > 0)
//                                    {
//
//                                        mGlobals.toast("cxzszdxfcghj");
//                                    }
//                                    mGlobals.log(TAG, String.valueOf(afterAddingTenMins));
//                                    mGlobals.log(TAG, String.valueOf(formatter.parse(mGlobals.getCurrentDate("dd-MMM-yyyy HH:mm:ss")).compareTo(afterAddingTenMins)));
//                                    mGlobals.toast(String.valueOf(afterAddingTenMins));

                                    //   mGlobals.dismissLoadingDialog();
                                    // move to next activity
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                    // show welcome back message
                                    mGlobals.toast("Welcome back " + first_name + "!");
                                }else{
                                    // show error
                                   // mGlobals.dismissLoadingDialog();
//                                    mGlobals.showErrorMessage(message,
//                                            true,
//                                            new Globals.Callback() {
//                                                @Override
//                                                public void onPickCallback(Boolean result) {
//                                                    if (result) {
//                                                        return;
//                                                    }
//                                                }
//                                            }
//                                    );
                                }
                            }catch(Exception e) {
                                // show exception error
                            //    mGlobals.dismissLoadingDialog();
                                Log.d(TAG, jsonObject+e.toString());
                                mGlobals.dialog(jsonObject+e.toString());
                            }
                        }
                    });

        }
    }

    private void validateFields() {
        edtLoginEmail.addTextChangedListener(new LoginActivity.MyTextWatcher(edtLoginEmail));
        edtLoginPassword.addTextChangedListener(new LoginActivity.MyTextWatcher(edtLoginPassword));
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
                case R.id.edt_login_email:
                    mGlobals.validateField(tilLoginEmail, edtLoginEmail, isValidEmail(edtLoginEmail.getText().toString().trim()), getString(R.string.err_msg_valid_email));
                    break;
                case R.id.edt_login_password:
                    mGlobals.validateField(tilLoginPassword, edtLoginPassword, true, getString(R.string.err_msg_password));
                    break;
            }
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
