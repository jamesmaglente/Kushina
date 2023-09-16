package com.kushina.customer.android.navigations.ecash;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DepositECashViewPager extends FragmentStateAdapter {

    public DepositECashViewPager(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DepositECashFragment();
            default:
                return new DepositECashHistoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}