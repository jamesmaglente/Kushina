package com.kushina.customer.android.navigations.ecash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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

import static com.kushina.customer.android.globals.Endpoints.API_NODE_ECASH;

public class CreditDebitCardActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    @BindView(R.id.tv_ecash_to_deposit) TextView tvECashToDeposit;
    @BindView(R.id.tv_convenience_fee) TextView tvConvenienceFee;
    @BindView(R.id.tv_total_amount) TextView tvTotalAmount;

    @BindView(R.id.til_deposit_ecash_amount) TextInputLayout tilDepositEcashAmount;
    @BindView(R.id.edt_deposit_ecash_amount) TextInputEditText edtDepositEcashAmount;
    @BindView(R.id.til_card_number) TextInputLayout tilCardNumber;
    @BindView(R.id.edt_card_number) TextInputEditText edtCardNumber;
    @BindView(R.id.til_exp_month) TextInputLayout tilExpMonth;
    @BindView(R.id.edt_exp_month) TextInputEditText edtExpMonth;
    @BindView(R.id.til_exp_year) TextInputLayout tilExpYear;
    @BindView(R.id.edt_exp_year) TextInputEditText edtExpYear;
    @BindView(R.id.til_cvc) TextInputLayout tilCVC;
    @BindView(R.id.edt_cvc) TextInputEditText edtCVC;

    private Double amount_to_deposit = 0.00;
    private Double convenience_fee = 0.00;
    private Double total_amount = 0.00;


    @OnClick(R.id.btn_deposit)
    public void checkDepositFields(){
        if (
                !mGlobals.validateField(tilCardNumber, edtCardNumber, true, getString(R.string.err_msg_card_number)) ||
                !mGlobals.validateField(tilExpMonth, edtExpMonth, true, getString(R.string.err_msg_card_exp_month)) ||
                !mGlobals.validateField(tilExpYear, edtExpYear, true, getString(R.string.err_msg_card_exp_year)) ||
                !mGlobals.validateField(tilCVC, edtCVC, true, getString(R.string.err_msg_cvc)) ||
                !mGlobals.validateField(tilDepositEcashAmount, edtDepositEcashAmount, true, getString(R.string.err_msg_amount))
        ) {
            return;
        } else {
            mGlobals.showChoiceDialog("Are you sure you want to proceed?", true, new Globals.Callback() {
                @Override
                public void onPickCallback(Boolean result) {
                    if(result){
                        deposit();
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_debit_card);

        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        edtDepositEcashAmount.addTextChangedListener(new CreditDebitCardActivity.MyTextWatcher(edtDepositEcashAmount));
    }

    public void deposit(){
        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("type", "card");
        request_data.put("card_number", edtCardNumber.getText().toString().trim());
        request_data.put("exp_month", edtExpMonth.getText().toString().trim());
        request_data.put("exp_year", edtExpYear.getText().toString().trim());
        request_data.put("cvc", edtCVC.getText().toString().trim());
        request_data.put("amount", String.valueOf(amount_to_deposit));
        request_data.put("convenience_fee", String.valueOf(convenience_fee));
        request_data.put("currency", "PHP");
        request_data.put("payment_method_allowed", "card");

        mAPI.api_request("POST",
                API_NODE_ECASH + "cardDeposit",
                request_data,
                true,
                CreditDebitCardActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log(TAG, String.valueOf(result));

                        try {
                            // parse response object
                            JSONObject data = result.getJSONObject("data");
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {
                                            finish();
                                        }
                                    }
                                });

                            } else {
                                mGlobals.log(TAG, status_message);
                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(TAG, e.toString());


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

                case R.id.edt_deposit_ecash_amount:
                    if(mGlobals.validateField(tilDepositEcashAmount, edtDepositEcashAmount, true, getString(R.string.err_msg_amount))){
                        amount_to_deposit = Double.valueOf(edtDepositEcashAmount.getText().toString().trim());
                        convenience_fee = amount_to_deposit * 0.20;
                        total_amount = amount_to_deposit + convenience_fee;

                        tvECashToDeposit.setText(mGlobals.moneyFormatter(edtDepositEcashAmount.getText().toString().trim()));
                        tvConvenienceFee.setText(mGlobals.moneyFormatter(String.valueOf(convenience_fee)));
                        tvTotalAmount.setText(mGlobals.moneyFormatter(String.valueOf(total_amount)));
                    } else {
                        tvECashToDeposit.setText("₱ 0.00");
                        tvConvenienceFee.setText("₱ 0.00");
                        tvTotalAmount.setText("₱ 0.00");
                    }
                    break;

            }
        }
    }
}
