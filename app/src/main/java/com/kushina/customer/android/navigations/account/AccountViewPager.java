package com.kushina.customer.android.navigations.account;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AccountViewPager extends FragmentStateAdapter {

    public AccountViewPager(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AddressListFragment();
            case 1:
                return new UpdateProfileFragment();
            default:
                return new UpdatePasswordFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}