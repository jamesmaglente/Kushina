package com.kushina.customer.android.navigations.orders;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OrdersViewPager extends FragmentStateAdapter {

    public OrdersViewPager(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PendingOrdersFragment();
            case 1:
                return new PreparingAndCookingFragment();
            case 2:
                return new ShippingFragment();
            case 3:
                return new DeliveredFragment();
            default:
                return new CancelledFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}