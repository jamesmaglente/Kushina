package com.kushina.customer.android.navigations.ecash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.MainActivity;
import com.kushina.customer.android.navigations.withdraw.WithdrawECashActivity;

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

public class TransactionHistoryActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;


    List<RVTransactionHistoryModel> thModel;
    @BindView(R.id.rv_ecash_transaction_history) RecyclerView rvEcashTransactionHistory;
    @BindView(R.id.ll_no_history_yet) LinearLayout llNoHistoryYet;
    @BindView(R.id.tv_transaction_history_credited) TextView tvTransactionHistoryCredited;
    @BindView(R.id.tv_transaction_history_debited) TextView tvTransactionHistoryDebited;
    @BindView(R.id.tv_transaction_history_type) TextView tvTransactionHistoryType;
    @BindView(R.id.tv_transaction_history_from) TextView tvTransactionHistoryFrom;
    @BindView(R.id.tv_transaction_history_tax) TextView tvTransactionHistoryTax;
    @BindView(R.id.tv_transaction_history_order_id) TextView tvTransactionHistoryOrderId;
    @BindView(R.id.tv_transaction_history_date) TextView tvTransactionHistoryDate;
    @BindView(R.id.ll_tx_details_dialog) RelativeLayout llTxDetailsDialog;
    @BindView(R.id.btn_claim_rewards) LinearLayout btnClaimRewards;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        ButterKnife.bind(this);

        mGlobals = new Globals(TransactionHistoryActivity.this);
        mAPI = new API(TransactionHistoryActivity.this);
        mPreferences = new Preferences(TransactionHistoryActivity.this);

        llTxDetailsDialog.setVisibility(View.GONE);
        //  loadAccountNavigations();
        loadTransactionHistory();
    }

    @OnClick({R.id.btn_transaction_history_okay})
    public void onViewClicked(View view) {
        llTxDetailsDialog.setVisibility(View.GONE);
    }

    private void loadTransactionHistory() {

        thModel = new ArrayList<>();
        thModel.clear();


        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());

        mAPI.api_request("POST",
                API_NODE_ECASH + "getECashSummary",
                request_data,
                true,
                TransactionHistoryActivity.this,
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
                                JSONArray items = data_array.getJSONArray("ecash_history");

//                                tvEcashTotalEcash.setText(mGlobals.moneyFormatter(current_ecash));
//                                tvEcashCashback.setText(mGlobals.moneyFormatter(total_cashback));
//                                tvEcashReferralBonus.setText(mGlobals.moneyFormatter(total_referral_bonus));

                                for (int i = 0; i < items.length(); i++) {

                                    String ecash_history_id = ((JSONObject) items.get(i)).get("ecash_history_id").toString();
                                    String transaction_type_id = ((JSONObject) items.get(i)).get("transaction_type_id").toString();
                                    String transaction_type = ((JSONObject) items.get(i)).get("transaction_type").toString();
                                    String user_id = ((JSONObject) items.get(i)).get("user_id").toString();
                                    String order_id = ((JSONObject) items.get(i)).get("order_id").toString();
                                    String tax = ((JSONObject) items.get(i)).get("tax").toString();
                                    String debit = ((JSONObject) items.get(i)).get("debit").toString();
                                    String credit = ((JSONObject) items.get(i)).get("credit").toString();
                                    String ending_balance = ((JSONObject) items.get(i)).get("ending_balance").toString();
                                    String claimed = ((JSONObject) items.get(i)).get("claimed").toString();
                                    String created_by_id = ((JSONObject) items.get(i)).get("created_by_id").toString();
                                    String creator = ((JSONObject) items.get(i)).get("creator").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();
                                    String type = ((JSONObject) items.get(i)).get("type").toString();


                                    thModel.add(new RVTransactionHistoryModel(ecash_history_id, transaction_type_id, transaction_type, user_id, order_id, tax, debit, credit, ending_balance, claimed, created_by_id, creator, "0", date_created, type));

                                }


                                rvEcashTransactionHistory.setHasFixedSize(true);
                                rvEcashTransactionHistory.setLayoutManager(new LinearLayoutManager(TransactionHistoryActivity.this));
                                RVRecentTransactionsAdapter thAdapter = new RVRecentTransactionsAdapter(TransactionHistoryActivity.this, thModel);
                                rvEcashTransactionHistory.setAdapter(thAdapter);

                                if (thModel.isEmpty()) {
                                    llNoHistoryYet.setVisibility(View.VISIBLE);
                                } else {
                                    llNoHistoryYet.setVisibility(View.GONE);
                                }

                                thAdapter.setOnItemClickListener(new RVRecentTransactionsAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {

                                        RVTransactionHistoryModel clickedItem = thModel.get(position);

                                        tvTransactionHistoryType.setText(clickedItem.getTransactionType());
                                        tvTransactionHistoryCredited.setText(mGlobals.moneyFormatter(clickedItem.getAmountCredited()));
                                        tvTransactionHistoryDebited.setText(mGlobals.moneyFormatter(clickedItem.getAmountDebited()));
                                        tvTransactionHistoryFrom.setText(clickedItem.getFrom());
                                        tvTransactionHistoryTax.setText(mGlobals.moneyFormatter(clickedItem.getTax()));
                                        tvTransactionHistoryType.setText(clickedItem.getTransactionType());
                                        tvTransactionHistoryOrderId.setText(clickedItem.getOrderID());
                                        tvTransactionHistoryDate.setText(clickedItem.getDate());

                                        llTxDetailsDialog.setVisibility(View.VISIBLE);

                                    }
                                });

                                //     mGlobals.dismissLoadingDialog();
//                                if(mGlobals.isNetworkAvailable()){
//                                    isRewardEarned =false;
//                                    loadAd();
//                                }


//                                thAdapter.setOnItemClickListener(position -> {
//                                    RVTransactionHistoryModel clickedItem = thModel.get(position);
//                                    if (clickedItem.getClaimed().equals("0")) {
//                                        mGlobals.showChoiceDialog("Watch a short video to claim this reward?", true, new Globals.Callback() {
//                                            @Override
//                                            public void onPickCallback(Boolean result) {
//                                                if (result) {
//                                                    if (isAdLoaded) {
//                                                        showAd(clickedItem.geteCashHistoryID());
//                                                    } else {
//                                                        mGlobals.showErrorMessageWithDelay("Could not claim rewards right now. Please try again later.", true, new Globals.Callback() {
//                                                            @Override
//                                                            public void onPickCallback(Boolean result) {
//                                                                if (result) {
//                                                                    recreate();
//                                                                }
//                                                            }
//                                                        });
//                                                    }
//
//                                                }
//                                            }
//                                        });
//
//                                    }
//                                });


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
}
