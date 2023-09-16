package com.kushina.customer.android.navigations.ecash;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
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

import static com.kushina.customer.android.globals.Endpoints.API_NODE_ECASH;

public class ECashActivity extends AppCompatActivity {

    @BindView(R.id.tv_ecash_total_ecash)
    TextView tvEcashTotalEcash;
    @BindView(R.id.tv_ecash_cashback)
    TextView tvEcashCashback;
    @BindView(R.id.tv_ecash_referral_bonus)
    TextView tvEcashReferralBonus;
    @BindView(R.id.rv_ecash_transaction_history)
    RecyclerView rvEcashTransactionHistory;
    @BindView(R.id.ll_no_history_yet)
    LinearLayout llNoHistoryYet;

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    private RewardedAd rewardedAd;
    private Boolean isAdLoaded;
    private Boolean isRewardEarned;

    List<RVTransactionHistoryModel> thModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecash);
        ButterKnife.bind(this);

        getSupportActionBar().hide();
        View mDecorView = this.getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                //    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                //       | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                //     | View.SYSTEM_UI_FLAG_IMMERSIVE
        );

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        if(mGlobals.isNetworkAvailable()) {
           // mGlobals.showLoadingDialog();
           // mGlobals.initilizeAdMob();
        }
        loadTransactionHistory();

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
                ECashActivity.this,
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

                                tvEcashTotalEcash.setText(mGlobals.moneyFormatter(current_ecash));
                                tvEcashCashback.setText(mGlobals.moneyFormatter(total_cashback));
                                tvEcashReferralBonus.setText(mGlobals.moneyFormatter(total_referral_bonus));

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


                                    thModel.add(new RVTransactionHistoryModel(ecash_history_id, transaction_type_id, transaction_type, user_id, order_id, tax, debit, credit, ending_balance, claimed, created_by_id, creator, "0", date_created,type));

                                }


                                rvEcashTransactionHistory.setHasFixedSize(true);
                                rvEcashTransactionHistory.setLayoutManager(new LinearLayoutManager(ECashActivity.this));
                                RVTransactionHistoryAdapter thAdapter = new RVTransactionHistoryAdapter(ECashActivity.this, thModel);
                                rvEcashTransactionHistory.setAdapter(thAdapter);

                                if(thModel.isEmpty()){
                                    llNoHistoryYet.setVisibility(View.VISIBLE);
                                }else{
                                    llNoHistoryYet.setVisibility(View.GONE);
                                }

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

    private void loadAd() {


        this.rewardedAd = new RewardedAd(this, "ca-app-pub-3940256099942544/5224354917");
        RewardedAdLoadCallback callback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
                super.onRewardedAdFailedToLoad(loadAdError);
                mGlobals.log(TAG, "onRewardedAdFailedToLoad");
                isAdLoaded = false;
                mGlobals.dismissLoadingDialog();
            }

            @Override
            public void onRewardedAdLoaded() {
                super.onRewardedAdLoaded();
                mGlobals.log(TAG, "OnRewardedAdLoaded");
                isAdLoaded = true;
                mGlobals.dismissLoadingDialog();
            }
        };
        this.rewardedAd.loadAd(new AdRequest.Builder().build(), callback);
    }

    private void showAd(String ecashHistoryID) {
        if (this.rewardedAd.isLoaded()) {
            RewardedAdCallback callback = new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    mGlobals.log(TAG, "OnUserEarnedReward");
                    isRewardEarned = true;

                }

                @Override
                public void onRewardedAdOpened() {
                    super.onRewardedAdOpened();
                    mGlobals.log(TAG, "OnRewardedAdOpened");
                }

                @Override
                public void onRewardedAdClosed() {
                    super.onRewardedAdClosed();
                    mGlobals.log(TAG, "OnRewardedAdClosed");
                    if(isRewardEarned) {
                        claimReward(ecashHistoryID);

                    }else{
                        mGlobals.showErrorMessage("You have to complete the video to claim the reward.", true, new Globals.Callback() {
                            @Override
                            public void onPickCallback(Boolean result) {
                                if(result){
                                    recreate();
                                }
                            }
                        });
                    }

                }

                @Override
                public void onRewardedAdFailedToShow(AdError adError) {
                    super.onRewardedAdFailedToShow(adError);
                    mGlobals.log(TAG, adError.toString());
                }
            };

            this.rewardedAd.show(this, callback);

        } else {
            mGlobals.log(TAG, "Ad not loaded.");
        }
    }

    private void claimReward(String ecashHistoryID) {
        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("ecash_history_id", ecashHistoryID);


        mAPI.api_request("POST",
                API_NODE_ECASH + "claimEcash",
                request_data,
                false,
                ECashActivity.this,
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

                                mGlobals.showSuccessDialog("Successfully claimed reward.", true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {
                                            recreate();
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

}
