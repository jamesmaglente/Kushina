package com.kushina.customer.android.start_up_screens;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreenActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();

    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    private static int SPLASH_TIME_OUT = 8000;
    @BindView(R.id.tv_vesion)
    TextView tvVersion;
    @BindView(R.id.lottie_cooking)
    LottieAnimationView lottieCooking;
    @BindView(R.id.iv_splash)
    ImageView ivSplash;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        ButterKnife.bind(this);

        mGlobals = new Globals(this);
        mAPI = new API(this);
        mPreferences = new Preferences(this);

        getSupportActionBar().hide();
        View mDecorView = SplashScreenActivity.this.getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(!mGlobals.isNetworkAvailable()){
                    mGlobals.showDialog("Notice", "It looks like you do not have an internet connection. The application needs an internet connection to work properly.", true, new Globals.Callback() {
                        @Override
                        public void onPickCallback(Boolean result) {
                            onBackPressed();
                        }
                    });
                } else {
                    startApp();

                    finish();
                }

            }
        }, SPLASH_TIME_OUT);

        String current_version = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            current_version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvVersion.setText("v" + current_version);
    }

    private void startApp() {

        lottieCooking.pauseAnimation();
        lottieCooking.setVisibility(View.INVISIBLE);
        ivSplash.setVisibility(View.INVISIBLE);

        if(mPreferences.isLogged()){

            // Get Current Date Time
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String getCurrentDateTime = sdf.format(c.getTime());
//
//        String getMyTime="08/19/2020 09:45:00";
//        Log.d("getCurrentDateTime",getCurrentDateTime);

            if (getCurrentDateTime.compareTo(mPreferences.getTokenExpiration()) < 0) {
                mGlobals.log(TAG, "<0");
//                mGlobals.log(TAG, getCurrentDateTime + "::" + mPreferences.getTokenExpiration());
//                mGlobals.dialog(getCurrentDateTime + "::" + mPreferences.getTokenExpiration(), SplashScreenActivity.this);

//            mGlobals.log(TAG, getCurrentDateTime);
//            mGlobals.log(TAG, getMyTime);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else {
                Log.d("Return","getMyTime older than getCurrentDateTime ");
                mGlobals.log(TAG, "else");
//            mGlobals.log(TAG, getCurrentDateTime);
//            mGlobals.log(TAG, getMyTime);
//            mGlobals.log(TAG, String.valueOf(getCurrentDateTime.compareTo(getMyTime)));
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                mPreferences.clearPreferences();
                finish();
            }


        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }


    }
}
