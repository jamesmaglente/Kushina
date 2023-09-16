package com.kushina.customer.android.navigations.ecash;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.MainActivity;
import com.kushina.customer.android.navigations.account.RVAccountNavigationsModel;
import com.kushina.customer.android.navigations.withdraw.WithdrawECashActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_ECASH;


/**
 * A simple {@link Fragment} subclass.
 */
public class ECashFragment extends Fragment {

    Unbinder unbinder;
    Globals mGlobals;
    API mAPI;
    Preferences mPreferences;

    public final String TAG = getClass().getSimpleName();


    @BindView(R.id.rl_main_layout) RelativeLayout rlMainLayout;
    @BindView(R.id.tv_ecash_total_ecash) TextView tvEcashTotalEcash;
    @BindView(R.id.tv_ecash_cashback) TextView tvEcashCashback;
    @BindView(R.id.tv_ecash_referral_bonus) TextView tvEcashReferralBonus;

    @BindView(R.id.btn_deposit_ecash) LinearLayout btnDepositEcash;
    @BindView(R.id.btn_withdraw_ecash) LinearLayout btnWithdrawEcash;
    @BindView(R.id.ll_top_card) CardView llTopCard;



    @OnClick(R.id.ll_transaction_history)
    public void loadTransactionHistory(){
        Intent intent = new Intent(getActivity(), TransactionHistoryActivity.class);
        startActivity(intent);
    }


    public ECashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ecash, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mGlobals = new Globals(getActivity());
        mAPI = new API(getActivity());
        mPreferences = new Preferences(getActivity());


        ((MainActivity) getActivity()).getSupportActionBar().setTitle("");

        loadECashCard();
    }

    @OnClick({R.id.btn_deposit_ecash, R.id.btn_withdraw_ecash})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_deposit_ecash:
                intent = new Intent(getActivity(), DepositECashBaseActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_withdraw_ecash:
                intent = new Intent(getActivity(), WithdrawECashActivity.class);
                startActivity(intent);
                break;
        }
    }


    private void loadECashCard() {


        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());

        mAPI.api_request("POST",
                API_NODE_ECASH + "loadEarnEcash",
                request_data,
                false,
                getActivity(),
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

                                tvEcashTotalEcash.setText(mGlobals.moneyFormatter(current_ecash));
                                tvEcashCashback.setText(mGlobals.moneyFormatter(total_cashback));
                                tvEcashReferralBonus.setText(mGlobals.moneyFormatter(total_referral_bonus));

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
