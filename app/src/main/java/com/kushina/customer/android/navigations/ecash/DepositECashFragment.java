package com.kushina.customer.android.navigations.ecash;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.account.AccountActivity;
import com.kushina.customer.android.navigations.account.ECashMainActivity;
import com.kushina.customer.android.navigations.account.MilestoneActivity;
import com.kushina.customer.android.navigations.account.RVAccountNavigationsAdapter;
import com.kushina.customer.android.navigations.account.RankingActivity;
import com.kushina.customer.android.navigations.account.UpdatePasswordActivity;
import com.kushina.customer.android.navigations.account.UpdateProfileActivity;
import com.kushina.customer.android.navigations.addresses.AddressListActivity;
import com.kushina.customer.android.navigations.earn_ecash.EarnECashActivity;
import com.kushina.customer.android.navigations.home.MyFavoritesActivity;
import com.kushina.customer.android.navigations.my_team.MyTeamActivity;
import com.kushina.customer.android.navigations.orderhistory.OrderHistoryActivity;
import com.kushina.customer.android.navigations.withdraw.WithdrawECashActivity;
import com.kushina.customer.android.start_up_screens.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class DepositECashFragment extends Fragment {

    Unbinder unbinder;
    Globals mGlobals;
    API mAPI;
    Preferences mPreferences;

    List<RVDepositMethodsModel> dmModel;
    @BindView(R.id.rv_deposit_methods) RecyclerView rvDepositMethods;

    public final String TAG = getClass().getSimpleName();

    public DepositECashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_deposit_ecash, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mGlobals = new Globals(getActivity());
        mAPI = new API(getActivity());
        mPreferences = new Preferences(getActivity());

        loadDepositMethods();
    }

    public void loadDepositMethods(){
        dmModel = new ArrayList<>();
        dmModel.clear();

        dmModel.add(new RVDepositMethodsModel(R.drawable.ic_over_the_counter_cashier, "Over the Counter"));
        dmModel.add(new RVDepositMethodsModel(R.drawable.ic_credit_card_amount, "Credit/Debit Card"));
        dmModel.add(new RVDepositMethodsModel(R.drawable.gcash, "Gcash"));
        dmModel.add(new RVDepositMethodsModel(R.drawable.grabpay, "Grab Pay"));


        rvDepositMethods.setHasFixedSize(true);
        rvDepositMethods.setLayoutManager(new LinearLayoutManager(getActivity()));
        RVDepositMethodsAdapter avAdapter = new RVDepositMethodsAdapter(getActivity(), dmModel);
        rvDepositMethods.setAdapter(avAdapter);


        avAdapter.setOnItemClickListener(new RVDepositMethodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent;
                RVDepositMethodsModel clickedItem = dmModel.get(position);

                if (clickedItem.getDepositMethodTitle().toLowerCase().contains("counter")) {
                    intent = new Intent(getActivity(), OverTheCounterActivity.class);
                    startActivity(intent);
                } else if (clickedItem.getDepositMethodTitle().toLowerCase().contains("card")) {
                    intent = new Intent(getActivity(), CreditDebitCardActivity.class);
                    startActivity(intent);
                } else if (clickedItem.getDepositMethodTitle().toLowerCase().contains("gcash")) {
                    intent = new Intent(getActivity(), GcashOrGrabPayActivity.class);
                    intent.putExtra("deposit_method", "gcash");
                    startActivity(intent);
                } else if (clickedItem.getDepositMethodTitle().toLowerCase().contains("grab pay")) {
                    intent = new Intent(getActivity(), GcashOrGrabPayActivity.class);
                    intent.putExtra("deposit_method", "grab pay");
                    startActivity(intent);
                }


            }
        });
    }


}
