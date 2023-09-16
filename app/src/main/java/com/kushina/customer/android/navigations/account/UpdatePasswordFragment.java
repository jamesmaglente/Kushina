package com.kushina.customer.android.navigations.account;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
public class UpdatePasswordFragment extends Fragment {

    @BindView(R.id.edt_update_password_old)
    TextInputEditText edtUpdatePasswordOld;
    @BindView(R.id.til_update_password_old)
    TextInputLayout tilUpdatePasswordOld;
    @BindView(R.id.edt_update_password_new)
    TextInputEditText edtUpdatePasswordNew;
    @BindView(R.id.til_update_password_new)
    TextInputLayout tilUpdatePasswordNew;
    @BindView(R.id.edt_update_password_confirm)
    TextInputEditText edtUpdatePasswordConfirm;
    @BindView(R.id.til_update_password_confirm)
    TextInputLayout tilUpdatePasswordConfirm;
    @BindView(R.id.btn_update_password)
    Button btnUpdatePassword;

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.edt_update_password_otp)
    TextInputEditText edtUpdatePasswordOtp;
    @BindView(R.id.til_update_password_otp)
    TextInputLayout tilUpdatePasswordOtp;
    @BindView(R.id.btn_request_new_otp)
    TextView btnRequestNewOtp;
    Unbinder unbinder;

    public UpdatePasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_password, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mGlobals = new Globals(getActivity());
        mAPI = new API(getActivity());
        mPreferences = new Preferences(getActivity());

        validateFields();

    }

    @OnClick({R.id.btn_update_password, R.id.btn_request_new_otp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_update_password:
                mGlobals.showChoiceDialog("Are you sure you want to update your password?", true, new Globals.Callback() {
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


    private void checkFields() {
        if (!mGlobals.validateField(tilUpdatePasswordOld, edtUpdatePasswordOld, true, getString(R.string.err_msg_old_password)) ||
                !(
                        mGlobals.validateField(tilUpdatePasswordNew, edtUpdatePasswordNew, true, getString(R.string.err_msg_new_password)) ?
                                mGlobals.validateField(tilUpdatePasswordNew, edtUpdatePasswordNew, matchPasswords(edtUpdatePasswordNew.getText().toString().trim(), edtUpdatePasswordConfirm.getText().toString().trim()) && !edtUpdatePasswordNew.equals(""), getString(R.string.err_msg_password_not_match)) :
                                mGlobals.validateField(tilUpdatePasswordNew, edtUpdatePasswordNew, true, getString(R.string.err_msg_new_password))
                ) ||
                !(
                        mGlobals.validateField(tilUpdatePasswordConfirm, edtUpdatePasswordConfirm, true, getString(R.string.err_msg_confirm_password)) ?
                                mGlobals.validateField(tilUpdatePasswordConfirm, edtUpdatePasswordConfirm, matchPasswords(edtUpdatePasswordNew.getText().toString().trim(), edtUpdatePasswordConfirm.getText().toString().trim()) && !edtUpdatePasswordConfirm.equals(""), getString(R.string.err_msg_password_not_match)) :
                                mGlobals.validateField(tilUpdatePasswordConfirm, edtUpdatePasswordConfirm, true, getString(R.string.err_msg_confirm_password))
                ) ||
                !mGlobals.validateField(tilUpdatePasswordOtp, edtUpdatePasswordOtp, true, getString(R.string.err_msg_otp_key))
        ) {
            return;
        } else {
            updatePassword();
        }
    }

    private void updatePassword() {

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("old_password", edtUpdatePasswordOld.getText().toString().trim());
        request_data.put("new_password", edtUpdatePasswordNew.getText().toString().trim());
        request_data.put("confirm_password", edtUpdatePasswordConfirm.getText().toString().trim());
        request_data.put("otp_key", edtUpdatePasswordOtp.getText().toString().trim());

        // api call
        mAPI.api_request("POST",
                API_NODE_PROFILE + "updatePassword",
                request_data,
                true,
                getActivity(),
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {
                        mGlobals.log("UpdatePassword", String.valueOf(result));


                        try {

                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {
                                // mGlobals.dismissLoadingDialog();
                                JSONObject root = result;
                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {

                                            edtUpdatePasswordOtp.setText("");
                                            edtUpdatePasswordNew.setText("");
                                            edtUpdatePasswordConfirm.setText("");
                                            edtUpdatePasswordOld.setText("");
                                            mGlobals.removeError(tilUpdatePasswordConfirm);
                                            mGlobals.removeError(tilUpdatePasswordNew);
                                            mGlobals.removeError(tilUpdatePasswordOld);
                                            mGlobals.removeError(tilUpdatePasswordOtp);
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

    private void requestNewOTP(){

        final Map<String, String> request_data = new HashMap<String, String>();
        // request_data.put("task", "register_customer");
        request_data.put("user_id", mPreferences.getUserId().toString()); //Add the data you'd like to send to the server.
        request_data.put("type", "Password Update");

        // api call
        mAPI.api_request("POST",
                API_NODE_USER + "requestSMSOTP",
                request_data,
                true,
                getActivity(),
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log("Request Password Update", String.valueOf(result));


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


    private void validateFields() {
        edtUpdatePasswordOld.addTextChangedListener(new MyTextWatcher(edtUpdatePasswordOld));
        edtUpdatePasswordNew.addTextChangedListener(new MyTextWatcher(edtUpdatePasswordNew));
        edtUpdatePasswordConfirm.addTextChangedListener(new MyTextWatcher(edtUpdatePasswordConfirm));
        edtUpdatePasswordOtp.addTextChangedListener(new MyTextWatcher(edtUpdatePasswordOtp));
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
                case R.id.edt_update_password_old:
                    mGlobals.validateField(tilUpdatePasswordOld, edtUpdatePasswordOld, true, getString(R.string.err_msg_old_password));
                    break;
                case R.id.edt_update_password_new:
                    if (mGlobals.validateField(tilUpdatePasswordNew, edtUpdatePasswordNew, true, getString(R.string.err_msg_new_password))) {
                        if (mGlobals.validateField(tilUpdatePasswordNew, edtUpdatePasswordNew, matchPasswords(edtUpdatePasswordNew.getText().toString().trim(), edtUpdatePasswordConfirm.getText().toString().trim()) && !edtUpdatePasswordNew.equals(""), getString(R.string.err_msg_password_not_match))) {
                            mGlobals.removeError(tilUpdatePasswordConfirm);
                        }
                    }
                    break;
                case R.id.edt_update_password_confirm:
                    if (mGlobals.validateField(tilUpdatePasswordConfirm, edtUpdatePasswordConfirm, true, getString(R.string.err_msg_confirm_password))) {
                        if (mGlobals.validateField(tilUpdatePasswordConfirm, edtUpdatePasswordConfirm, matchPasswords(edtUpdatePasswordNew.getText().toString().trim(), edtUpdatePasswordConfirm.getText().toString().trim()) && !edtUpdatePasswordConfirm.equals(""), getString(R.string.err_msg_password_not_match))) {
                            mGlobals.removeError(tilUpdatePasswordNew);
                        }
                    }
                    break;
                case R.id.edt_update_password_otp:
                    mGlobals.validateField(tilUpdatePasswordOtp, edtUpdatePasswordOtp, true, getString(R.string.err_msg_otp_key));
                    break;
            }
        }
    }

    /**
     * Validating form
     */
    private static boolean matchPasswords(String password, String confirm_password) {
        return password.equals(confirm_password);
    }

}
