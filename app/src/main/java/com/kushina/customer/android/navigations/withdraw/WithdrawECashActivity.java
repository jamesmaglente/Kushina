package com.kushina.customer.android.navigations.withdraw;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_ECASH;
import static com.kushina.customer.android.globals.Endpoints.API_NODE_USER;

public class WithdrawECashActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.tv_withdraw_ecash_lbl_withdraw)
    TextView tvWithdrawEcashLblWithdraw;
    @BindView(R.id.btn_viewpager_withdraw)
    LinearLayout btnViewpagerWithdraw;
    @BindView(R.id.tv_withdraw_ecash_lbl_withdraw_history)
    TextView tvWithdrawEcashLblWithdrawHistory;
    @BindView(R.id.btn_viewpager_withdraw_history)
    LinearLayout btnViewpagerWithdrawHistory;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.ll_view_switcher)
    LinearLayout llViewSwitcher;
    @BindView(R.id.tv_withdraw_ecash_withdraw_method)
    TextView tvWithdrawEcashWithdrawMethod;
    @BindView(R.id.btn_choose_withdraw_method)
    LinearLayout btnChooseWithdrawMethod;
    @BindView(R.id.edt_withdraw_ecash_account_number)
    TextInputEditText edtWithdrawEcashAccountNumber;
    @BindView(R.id.til_withdraw_ecash_account_number)
    TextInputLayout tilWithdrawEcashAccountNumber;
    @BindView(R.id.edt_withdraw_ecash_account_name)
    TextInputEditText edtWithdrawEcashAccountName;
    @BindView(R.id.til_withdraw_ecash_account_name)
    TextInputLayout tilWithdrawEcashAccountName;
    @BindView(R.id.tv_withdraw_ecash_available_ecash)
    TextView tvWithdrawEcashAvailableEcash;
    @BindView(R.id.edt_withdraw_ecash_amount)
    TextInputEditText edtWithdrawEcashAmount;
    @BindView(R.id.til_withdraw_ecash_amount)
    TextInputLayout tilWithdrawEcashAmount;
    @BindView(R.id.edt_withdraw_ecash_otp)
    TextInputEditText edtWithdrawEcashOtp;
    @BindView(R.id.til_withdraw_ecash_otp)
    TextInputLayout tilWithdrawEcashOtp;
    @BindView(R.id.btn_request_otp)
    Button btnRequestOtp;
    @BindView(R.id.btn_withdraw_ecash)
    Button btnWithdrawEcash;
    @BindView(R.id.btn_product)
    Button btnProduct;
    @BindView(R.id.ll_withdraw_ecash)
    ScrollView llWithdrawEcash;
    @BindView(R.id.ll_no_history_yet)
    LinearLayout llNoHistoryYet;
    @BindView(R.id.rv_withdraw_history)
    RecyclerView rvWithdrawHistory;
    @BindView(R.id.ll_withdraw_history)
    LinearLayout llWithdrawHistory;

    private String withdrawMethodID, withdrawMethod;

    public static final int SELECT_WITHDRAW_METHOD = 22222;
    List<RVWithdrawHistoryModel> whModel;
    private String availableEcash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_e_cash);
        ButterKnife.bind(this);

        getSupportActionBar().hide();
        View mDecorView = this.getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        //    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        //     | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);


        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);


        loadAvailableECash();
        validateFields();

        llWithdrawEcash.setVisibility(View.VISIBLE);
        tvWithdrawEcashLblWithdrawHistory.setTextColor(getResources().getColor(R.color.colorOrange));

    }

    @Override
    protected void onResume() {
        super.onResume();

        mGlobals.removeError(tilWithdrawEcashAccountNumber);
        mGlobals.removeError(tilWithdrawEcashAccountName);
        mGlobals.removeError(tilWithdrawEcashAmount);
        mGlobals.removeError(tilWithdrawEcashOtp);


    }

    @OnClick({R.id.btn_viewpager_withdraw, R.id.btn_viewpager_withdraw_history, R.id.btn_choose_withdraw_method, R.id.btn_request_otp, R.id.btn_withdraw_ecash})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_viewpager_withdraw:
                llWithdrawHistory.setVisibility(View.GONE);
                llWithdrawEcash.setVisibility(View.VISIBLE);
                tvWithdrawEcashLblWithdrawHistory.setTextColor(getResources().getColor(R.color.colorOrange));
                tvWithdrawEcashLblWithdraw.setTextColor(getResources().getColor(R.color.colorWhite));
                loadAvailableECash();
                break;
            case R.id.btn_viewpager_withdraw_history:
                llWithdrawEcash.setVisibility(View.GONE);
                llWithdrawHistory.setVisibility(View.VISIBLE);
                tvWithdrawEcashLblWithdraw.setTextColor(getResources().getColor(R.color.colorOrange));
                tvWithdrawEcashLblWithdrawHistory.setTextColor(getResources().getColor(R.color.colorWhite));
                loadWithdrawHistory();
                break;
            case R.id.btn_choose_withdraw_method:
                Intent intent = new Intent(WithdrawECashActivity.this, ChooseWithdrawMethodActivity.class);
                startActivityForResult(intent, SELECT_WITHDRAW_METHOD);
                break;
            case R.id.btn_request_otp:
                if(Double.valueOf(availableEcash) < 100){
                    mGlobals.showErrorMessage("You don't have enough E-cash to make a withdraw and request for an OTP. Minimum (₱ 100)", true, new Globals.Callback() {
                        @Override
                        public void onPickCallback(Boolean result) {
                            if(result){
                                return;
                            }
                        }
                    });
                }else {
                    mGlobals.showChoiceDialog("Are you sure you want to request an OTP Key?", true, new Globals.Callback() {
                        @Override
                        public void onPickCallback(Boolean result) {
                            if (result) {
                                requestOTP();
                            }
                        }
                    });
                }
                break;
            case R.id.btn_withdraw_ecash:
                if(Double.valueOf(availableEcash) < 100){
                    mGlobals.showErrorMessage("You don't have enough E-cash to make a withdraw and request for an OTP. Minimum (₱ 100)", true, new Globals.Callback() {
                        @Override
                        public void onPickCallback(Boolean result) {
                            if(result){
                                return;
                            }
                        }
                    });
                }else {
                    if (withdrawMethodID != null) {
                        mGlobals.showChoiceDialog("Submit Withdraw Request?", true, new Globals.Callback() {
                            @Override
                            public void onPickCallback(Boolean result) {
                                if (result) {
                                    checkFields();
                                }
                            }
                        });
                    } else {
                        mGlobals.showErrorMessageWithDelay("Please select a withdraw method.", true, new Globals.Callback() {
                            @Override
                            public void onPickCallback(Boolean result) {
                                if (result) {
                                    return;
                                }
                            }
                        });
                    }
                }
                break;
        }
    }

    private void checkFields() {
        if (

                !mGlobals.validateField(tilWithdrawEcashAccountNumber, edtWithdrawEcashAccountNumber, true, getString(R.string.err_msg_account_number)) ||
                !mGlobals.validateField(tilWithdrawEcashAccountName, edtWithdrawEcashAccountName, true, getString(R.string.err_msg_account_name)) ||
                !mGlobals.validateField(tilWithdrawEcashAmount, edtWithdrawEcashAmount, true, getString(R.string.err_msg_amount)) ||
                !mGlobals.validateField(tilWithdrawEcashOtp, edtWithdrawEcashOtp, true, getString(R.string.err_msg_otp_key))

        ) {
            return;

        } else {
            Double withdrawAmount = Double.valueOf(edtWithdrawEcashAmount.getText().toString());
            if(withdrawAmount < 100 || (withdrawAmount % 50) != 0){
                mGlobals.showErrorMessageWithDelay("Amount should be equal or greater than ₱ 100 and should be divisible by 50.", true, new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        if(result){
                            return;
                        }
                    }
                });
            }else if(withdrawAmount > Double.valueOf(availableEcash)){
                mGlobals.showErrorMessageWithDelay("Amount should not be greater than your available E-Cash.", true, new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        if(result){
                            return;
                        }
                    }
                });
            }else {
               submitWithdrawRequest();
            }

        }
    }

    private void submitWithdrawRequest(){

        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("account_name", edtWithdrawEcashAccountName.getText().toString());
        request_data.put("account_number", edtWithdrawEcashAccountNumber.getText().toString());
        request_data.put("otp_key", edtWithdrawEcashOtp.getText().toString());
        request_data.put("withdraw_method_id", withdrawMethodID);
        request_data.put("amount", edtWithdrawEcashAmount.getText().toString());



        mAPI.api_request("POST",
                API_NODE_ECASH + "withdraw",
                request_data,
                true,
                WithdrawECashActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log(getClass().getEnclosingMethod().getName(), String.valueOf(result));


                        try {
                            // parse response object
                            JSONObject jsonObject = result.getJSONObject("data");
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {


                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {
                                            edtWithdrawEcashAccountName.setText("");
                                            edtWithdrawEcashAmount.setText("");
                                            edtWithdrawEcashOtp.setText("");
                                            edtWithdrawEcashAccountNumber.setText("");

                                            recreate();
//                                            llWithdrawEcash.setVisibility(View.GONE);
//                                            llWithdrawHistory.setVisibility(View.VISIBLE);
//                                            tvWithdrawEcashLblWithdraw.setTextColor(getResources().getColor(R.color.colorOrange));
//                                            tvWithdrawEcashLblWithdrawHistory.setTextColor(getResources().getColor(R.color.colorWhite));
//                                            loadWithdrawHistory();
                                        }
                                    }
                                });

                            } else {
                                mGlobals.log(getClass().getEnclosingMethod().getName(), status_message);


                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(getClass().getEnclosingMethod().getName(), e.toString());


                        }
                    }
                });

    }


    private void requestOTP(){

        final Map<String, String> request_data = new HashMap<String, String>();
        // request_data.put("task", "register_customer");
        request_data.put("user_id", mPreferences.getUserId().toString()); //Add the data you'd like to send to the server.
        request_data.put("type", "withdraw");

        // api call
        mAPI.api_request("POST",
                API_NODE_USER + "requestSMSOTP",
                request_data,
                true,
                WithdrawECashActivity.this,
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

    private void loadWithdrawHistory() {

        whModel = new ArrayList<>();
        whModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());


        mAPI.api_request("POST",
                API_NODE_ECASH + "getAllWithdraw",
                request_data,
                true,
                WithdrawECashActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {


                        try {
                            mGlobals.log(getClass().getEnclosingMethod().getName(), result.toString(4));

                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;
                                JSONObject data_array = root.getJSONObject("data");
                                JSONArray items = data_array.getJSONArray("withdraws");

                                for (int i = 0; i < items.length(); i++) {

                                    String withdraw_id = ((JSONObject) items.get(i)).get("withdraw_id").toString();
                                    String code_id = ((JSONObject) items.get(i)).get("code_id").toString();
                                    String user_id = ((JSONObject) items.get(i)).get("user_id").toString();
                                    String status_id = ((JSONObject) items.get(i)).get("status_id").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();
                                    String withdraw_method_id = ((JSONObject) items.get(i)).get("withdraw_method_id").toString();
                                    String withdraw_method = ((JSONObject) items.get(i)).get("withdraw_method").toString();
                                    String account_name = ((JSONObject) items.get(i)).get("account_name").toString();
                                    String account_number = ((JSONObject) items.get(i)).get("account_number").toString();
                                    String amount = ((JSONObject) items.get(i)).get("amount").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();


                                    whModel.add(new RVWithdrawHistoryModel(withdraw_id,code_id,user_id,status_id,status,withdraw_method_id,withdraw_method,account_name,account_number,amount,date_created));

                                }


                                rvWithdrawHistory.setHasFixedSize(true);
                                rvWithdrawHistory.setLayoutManager(new LinearLayoutManager(WithdrawECashActivity.this));
                                RVWithdrawHistoryAdapter whAdapter = new RVWithdrawHistoryAdapter(WithdrawECashActivity.this, whModel);
                                rvWithdrawHistory.setAdapter(whAdapter);


                                if (whModel.isEmpty()) {
                                    llNoHistoryYet.setVisibility(View.VISIBLE);
                                } else {
                                    llNoHistoryYet.setVisibility(View.GONE);
                                }

                                //     mGlobals.dismissLoadingDialog();


                                whAdapter.setOnItemClickListener(position -> {
//                                    RVDepositHistoryModel clickedItem = whModel.get(position);
//
//                                    Intent intent = new Intent(DepositECashActivity.this, DepositDetailsActivity.class);
//                                    intent.putExtra("deposit_amount", clickedItem.getAmount());
//                                    intent.putExtra("deposit_method", clickedItem.getDepositMethod());
//                                    intent.putExtra("deposit_status", clickedItem.getStatus());
//                                    intent.putExtra("deposit_id", clickedItem.getDepositID());
//                                    intent.putExtra("date", clickedItem.getDate_submitted());
//                                    intent.putExtra("proof_of_payment_image", clickedItem.getProofOfPaymentImage());
//                                    startActivity(intent);
                                });


                            } else {
                                // show error
                                mGlobals.log(getClass().getEnclosingMethod().getName(), status_message);


                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(getClass().getEnclosingMethod().getName(), e.toString());
                        }


                    }
                });

    }


    private void loadAvailableECash() {
        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());

        mAPI.api_request("POST",
                API_NODE_ECASH + "loadEarnEcash",
                request_data,
                false,
                WithdrawECashActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {


                        try {
                            mGlobals.log(getClass().getEnclosingMethod().getName(), result.toString(4));
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;
                                JSONObject data_array = root.getJSONObject("data");
                                String current_ecash = data_array.getString("current_ecash");
                                String total_cashback = data_array.getString("total_cashback");
                                String total_referral_bonus = data_array.getString("total_referral_bonus");

                                tvWithdrawEcashAvailableEcash.setText(mGlobals.moneyFormatter(current_ecash));
                                availableEcash = current_ecash;


                            } else {
                                // show error
                                mGlobals.log(getClass().getEnclosingMethod().getName(), status_message);


                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(getClass().getEnclosingMethod().getName(), e.toString());
                        }


                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
//                case 0:
//                    if (resultCode == RESULT_OK && data != null) {
//                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
//                        bitmap = selectedImage;
//                        ivProofOfPayment.setImageBitmap(selectedImage);
//                    }
//
//                    break;
//                case 1:
//                    if (resultCode == RESULT_OK && data != null) {
//                        try {
//                            Uri selectedImg = data.getData();
//                            Bitmap imageFromGallery = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImg);
//                            if (imageFromGallery != null) {
//                                bitmap = imageFromGallery;
//                                ivProofOfPayment.setImageBitmap(imageFromGallery);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                    break;
                case SELECT_WITHDRAW_METHOD:
                    withdrawMethodID = data.getStringExtra("withdraw_method_id");
                    withdrawMethod = data.getStringExtra("withdraw_method");
                    tvWithdrawEcashWithdrawMethod.setText(withdrawMethod);
                    break;
            }
        }
    }


    private void validateFields() {
        edtWithdrawEcashAccountNumber.addTextChangedListener(new MyTextWatcher(edtWithdrawEcashAccountNumber));
        edtWithdrawEcashAccountName.addTextChangedListener(new MyTextWatcher(edtWithdrawEcashAccountName));
        edtWithdrawEcashAmount.addTextChangedListener(new MyTextWatcher(edtWithdrawEcashAmount));
        edtWithdrawEcashOtp.addTextChangedListener(new MyTextWatcher(edtWithdrawEcashOtp));

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

                case R.id.edt_withdraw_ecash_account_number:
                    mGlobals.validateField(tilWithdrawEcashAccountNumber, edtWithdrawEcashAccountNumber, true, getString(R.string.err_msg_account_number));
                    break;
                case R.id.edt_withdraw_ecash_account_name:
                    mGlobals.validateField(tilWithdrawEcashAccountName, edtWithdrawEcashAccountName, true, getString(R.string.err_msg_account_name));
                    break;
                case R.id.edt_withdraw_ecash_amount:
                    mGlobals.validateField(tilWithdrawEcashAmount, edtWithdrawEcashAmount, true, getString(R.string.err_msg_amount));
                    break;
                case R.id.edt_withdraw_ecash_otp:
                    mGlobals.validateField(tilWithdrawEcashOtp, edtWithdrawEcashOtp, true, getString(R.string.err_msg_otp_key));
                    break;

            }
        }
    }

}