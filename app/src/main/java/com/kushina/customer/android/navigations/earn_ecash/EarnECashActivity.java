package com.kushina.customer.android.navigations.earn_ecash;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EarnECashActivity extends AppCompatActivity {

    public static final String TAG = "EarnECashActivity";

    @BindView(R.id.tv_wallet)
    TextView tvWallet;
    @BindView(R.id.btn_claim)
    Button btnClaim;

    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    private RewardedAd rewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earn_ecash);
        ButterKnife.bind(this);

        mGlobals = new Globals(this);
        mAPI = new API(this);
        mPreferences = new Preferences(this);

        tvWallet.setText("Wallet: "+mGlobals.moneyFormatter("0"));

        //Sample App ID: ca-app-pub-3940256099942544~3347511713
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
        mGlobals.initilizeAdMob();

        loadAd();


    }

    @OnClick(R.id.btn_claim)
    public void onViewClicked() {
        showAd();
    }

    private void loadAd(){
        mGlobals.showLoadingDialog();

        this.rewardedAd = new RewardedAd(this,"ca-app-pub-3940256099942544/5224354917");
        RewardedAdLoadCallback callback = new RewardedAdLoadCallback(){
            @Override
            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
                super.onRewardedAdFailedToLoad(loadAdError);
                mGlobals.log(TAG,"onRewardedAdFailedToLoad");
            }

            @Override
            public void onRewardedAdLoaded() {
                super.onRewardedAdLoaded();
                mGlobals.log(TAG,"OnRewardedAdLoaded");
                mGlobals.dismissLoadingDialog();
            }
        };
        this.rewardedAd.loadAd(new AdRequest.Builder().build(),callback);
    }

    private void showAd(){
        if(this.rewardedAd.isLoaded()){
            RewardedAdCallback callback = new RewardedAdCallback() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    mGlobals.log(TAG,"OnUserEarnedReward");
                    mGlobals.toast("Claimed "+mGlobals.moneyFormatter("10"));
                    tvWallet.setText("Wallet: "+mGlobals.moneyFormatter("10"));
                }

                @Override
                public void onRewardedAdOpened() {
                    super.onRewardedAdOpened();
                    mGlobals.log(TAG,"OnRewardedAdOpened");
                }

                @Override
                public void onRewardedAdClosed() {
                    super.onRewardedAdClosed();
                    mGlobals.log(TAG,"OnRewardedAdClosed");
                    recreate();
                }

                @Override
                public void onRewardedAdFailedToShow(AdError adError) {
                    super.onRewardedAdFailedToShow(adError);
                    mGlobals.log(TAG,adError.toString());
                }
            };

            this.rewardedAd.show(this,callback);

        }else{
            mGlobals.log(TAG,"Ad not loaded.");
        }
    }

}
