package com.kushina.customer.android.navigations.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.MainActivity;
import com.kushina.customer.android.navigations.orders.OrdersViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AccountActivity extends AppCompatActivity {

    Globals mGlobals;
    API mAPI;
    Preferences mPreferences;

    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager2 viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_viewpager);
    }

    @Override
    public void onResume() {
        super.onResume();

        mGlobals = new Globals(AccountActivity.this);
        mAPI = new API(AccountActivity.this);
        mPreferences = new Preferences(AccountActivity.this);

        ButterKnife.bind(this);

        viewpager.setAdapter(new AccountViewPager(getSupportFragmentManager(), getLifecycle()));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewpager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                switch (position) {
                    case 0: {
                        tab.setText("Addresses");
                        break;
                    }
                    case 1: {
                        tab.setText("Profile");
                        break;
                    }
                    case 2: {
                        tab.setText("Password");
                        break;
                    }
                }
            }
        }
        );
        tabLayoutMediator.attach();

//        setHasOptionsMenu(true);
    }
}
