package com.kushina.customer.android.navigations.ecash;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kushina.customer.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WithdrawECashHistoryFragment extends Fragment {


    public WithdrawECashHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_withdraw_ecash_history, container, false);
    }

}
