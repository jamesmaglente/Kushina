package com.kushina.customer.android.navigations.account;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import butterknife.Unbinder;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_PROFILE;
import static com.kushina.customer.android.globals.Endpoints.API_NODE_USER;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateProfileFragment extends Fragment {


    public final String TAG = getClass().getSimpleName();

    @BindView(R.id.edt_update_profile_email)
    TextInputEditText edtUpdateProfileEmail;
    @BindView(R.id.til_update_profile_email)
    TextInputLayout tilUpdateProfileEmail;
    @BindView(R.id.edt_update_profile_firstname)
    TextInputEditText edtUpdateProfileFirstname;
    @BindView(R.id.til_update_profile_firstname)
    TextInputLayout tilUpdateProfileFirstname;
    @BindView(R.id.edt_update_profile_lastname)
    TextInputEditText edtUpdateProfileLastname;
    @BindView(R.id.til_update_profile_lastname)
    TextInputLayout tilUpdateProfileLastname;
    @BindView(R.id.btn_update_profile)
    Button btnUpdateProfile;

    Unbinder unbinder;
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.edt_update_profile_mobile)
    TextInputEditText edtUpdateProfileMobile;
    @BindView(R.id.til_update_profile_mobile)
    TextInputLayout tilUpdateProfileMobile;
    @BindView(R.id.btn_update_number)
    Button btnUpdateNumber;
    @BindView(R.id.edt_update_profile_middlename)
    TextInputEditText edtUpdateProfileMiddlename;
    @BindView(R.id.til_update_profile_middlename)
    TextInputLayout tilUpdateProfileMiddlename;

    public UpdateProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mGlobals = new Globals(getActivity());
        mAPI = new API(getActivity());
        mPreferences = new Preferences(getActivity());

        loadProfileInfo();
        validateFields();

    }

    @OnClick({R.id.btn_update_number, R.id.btn_update_profile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_update_number:
                mGlobals.showChoiceDialog(
                        "Update your Mobile Number?",
                        true,
                        new Globals.Callback() {
                            @Override
                            public void onPickCallback(Boolean result) {
                                if (result) {

                                    requestNewOTP();
                                }
                            }
                        }
                );
                break;
            case R.id.btn_update_profile:

                mGlobals.showChoiceDialog(
                        "Update your Profile?",
                        true,
                        new Globals.Callback() {
                            @Override
                            public void onPickCallback(Boolean result) {
                                if (result) {

                                    checkFields();

                                }
                            }
                        }
                );
                break;
        }
    }

    private void requestNewOTP(){

        final Map<String, String> request_data = new HashMap<String, String>();
        // request_data.put("task", "register_customer");
        request_data.put("user_id", mPreferences.getUserId().toString()); //Add the data you'd like to send to the server.
        request_data.put("type", "Mobile Update Request");

        // api call
        mAPI.api_request("POST",
                API_NODE_USER + "requestSMSOTP",
                request_data,
                true,
                getActivity(),
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
                                            Intent intent = new Intent(getActivity(),UpdateNumberActivity.class);
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




    private void loadProfileInfo() {
        edtUpdateProfileEmail.setText(mPreferences.getEmail());
        edtUpdateProfileFirstname.setText(mPreferences.getFirstname());
        edtUpdateProfileLastname.setText(mPreferences.getLastname());
        edtUpdateProfileMiddlename.setText(mPreferences.getMiddlename());
        edtUpdateProfileMobile.setText(mPreferences.getMobile());
        edtUpdateProfileMobile.setEnabled(false);
        tilUpdateProfileMobile.setEnabled(false);
        tilUpdateProfileMobile.setAlpha(0.5f);
    }

    private void checkFields() {
        if (
                !(
                        mGlobals.validateField(tilUpdateProfileEmail, edtUpdateProfileEmail, true, getString(R.string.err_msg_email)) ?
                                mGlobals.validateField(tilUpdateProfileEmail, edtUpdateProfileEmail, isValidEmail(edtUpdateProfileEmail.getText().toString().trim()), getString(R.string.err_msg_valid_email)) :
                                mGlobals.validateField(tilUpdateProfileEmail, edtUpdateProfileEmail, true, getString(R.string.err_msg_email))
                ) ||
                        !mGlobals.validateField(tilUpdateProfileFirstname, edtUpdateProfileFirstname, true, getString(R.string.err_msg_first_name)) ||
                        !mGlobals.validateField(tilUpdateProfileLastname, edtUpdateProfileLastname, true, getString(R.string.err_msg_last_name))
        ) {
            //  mGlobals.dismissLoadingDialog();
            return;
//            mGlobals.showErrorMessage(
//                    "Fields cannot be empty.",
//                    true,
//                    new Globals.Callback() {
//                        @Override
//                        public void onPickCallback(Boolean result) {
//                            if (result) {
//                                return;
//                            }
//                        }
//                    }
//            );
        } else {
            updateProfile();
        }
    }

    private void updateProfile() {

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("email", edtUpdateProfileEmail.getText().toString());
        request_data.put("firstname", edtUpdateProfileFirstname.getText().toString());
        request_data.put("middlename", edtUpdateProfileMiddlename.getText().toString());
        request_data.put("lastname", edtUpdateProfileLastname.getText().toString());


        // api call
        mAPI.api_request("POST",
                API_NODE_PROFILE + "updateProfile",
                request_data,
                true,
                getActivity(),
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {
                        mGlobals.log(getClass().getEnclosingMethod().getName(), result.toString());
                        try {

                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;


                                JSONObject data = result.getJSONObject("data");


                                Integer user_id = Integer.parseInt(data.get("user_id").toString());
                                Integer user_group_id = Integer.parseInt(data.get("user_group_id").toString());
                                String code = data.get("code").toString();
                                String code_id = data.get("code_id").toString();
                                String username = data.get("username").toString();
                                String email = data.get("email").toString();
                                String first_name = data.get("firstname").toString();
                                String middlename = data.get("middlename").toString();
                                String last_name = data.get("lastname").toString();
                                String full_name = first_name + " " + last_name;


                                mPreferences.setFirstname(first_name);
                                mPreferences.setMiddlename(middlename);
                                mPreferences.setLastname(last_name);
                                mPreferences.setEmail(email);

                                // mGlobals.dismissLoadingDialog();

                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {
                                            getActivity().recreate();
                                        }
                                    }
                                });


                            } else {
                                // show error
                                mGlobals.log(TAG, status_message);
                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(TAG, e.toString());
                        }
                    }
                });

    }


    private void validateFields() {
        edtUpdateProfileFirstname.addTextChangedListener(new MyTextWatcher(edtUpdateProfileFirstname));
        edtUpdateProfileLastname.addTextChangedListener(new MyTextWatcher(edtUpdateProfileLastname));
        edtUpdateProfileEmail.addTextChangedListener(new MyTextWatcher(edtUpdateProfileEmail));
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

                case R.id.edt_update_profile_firstname:
                    mGlobals.validateField(tilUpdateProfileFirstname, edtUpdateProfileFirstname, true, getString(R.string.err_msg_first_name));
                    break;
                case R.id.edt_update_profile_lastname:
                    mGlobals.validateField(tilUpdateProfileLastname, edtUpdateProfileLastname, true, getString(R.string.err_msg_last_name));
                    break;
                case R.id.edt_update_profile_email:
                    if (mGlobals.validateField(tilUpdateProfileEmail, edtUpdateProfileEmail, true, getString(R.string.err_msg_email))) {
                        mGlobals.validateField(tilUpdateProfileEmail, edtUpdateProfileEmail, isValidEmail(edtUpdateProfileEmail.getText().toString().trim()), getString(R.string.err_msg_valid_email));
                    }
                    break;
            }
        }
    }


}
