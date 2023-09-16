package com.kushina.customer.android.navigations.ecash;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import butterknife.Unbinder;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_ECASH;

/**
 * A simple {@link Fragment} subclass.
 */
public class DepositECashHistoryFragment extends Fragment {


    Unbinder unbinder;
    Globals mGlobals;
    API mAPI;
    Preferences mPreferences;

    List<RVDepositHistoryModel> dhModel;

    @BindView(R.id.rv_deposit_history) RecyclerView rvDepositHistory;
    @BindView(R.id.ll_no_history_yet) LinearLayout llNoHistoryYet;

    public final String TAG = getClass().getSimpleName();

    public DepositECashHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_deposit_ecash_history, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mGlobals = new Globals(getActivity());
        mAPI = new API(getActivity());
        mPreferences = new Preferences(getActivity());

        loadDepositHistory();
    }

    private void loadDepositHistory() {

        dhModel = new ArrayList<>();
        dhModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());


        mAPI.api_request("POST",
                API_NODE_ECASH + "getDeposits",
                request_data,
                true,
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
                                JSONArray items = data_array.getJSONArray("deposits");

                                for (int i = 0; i < items.length(); i++) {

                                    String deposit_id = ((JSONObject) items.get(i)).get("deposit_id").toString();
                                    String reference = ((JSONObject) items.get(i)).get("reference").toString();
                                    String status_id = ((JSONObject) items.get(i)).get("status_id").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();
                                    String deposit_method_id = ((JSONObject) items.get(i)).get("deposit_method_id").toString();
                                    String deposit_method = ((JSONObject) items.get(i)).get("deposit_method").toString();
                                    String user_id = ((JSONObject) items.get(i)).get("user_id").toString();
                                    String amount = ((JSONObject) items.get(i)).get("amount").toString();
                                    String proof_of_payment_image = ((JSONObject) items.get(i)).get("proof_of_payment_image").toString();
                                    String date_submitted = ((JSONObject) items.get(i)).get("date_submitted").toString();


                                    dhModel.add(new RVDepositHistoryModel(deposit_id, reference, status_id, status, deposit_method_id, deposit_method, user_id, amount, proof_of_payment_image, date_submitted));

                                }


                                rvDepositHistory.setHasFixedSize(true);
                                rvDepositHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
                                RVDepositHistoryAdapter dhAdapter = new RVDepositHistoryAdapter(getActivity(), dhModel);
                                rvDepositHistory.setAdapter(dhAdapter);


                                if (dhModel.isEmpty()) {
                                    llNoHistoryYet.setVisibility(View.VISIBLE);
                                } else {
                                    llNoHistoryYet.setVisibility(View.GONE);
                                }

                                //     mGlobals.dismissLoadingDialog();


                                dhAdapter.setOnItemClickListener(position -> {
                                    RVDepositHistoryModel clickedItem = dhModel.get(position);

                                    Intent intent = new Intent(getActivity(), DepositDetailsActivity.class);
                                    intent.putExtra("deposit_amount", clickedItem.getAmount());
                                    intent.putExtra("deposit_method", clickedItem.getDepositMethod());
                                    intent.putExtra("deposit_status", clickedItem.getStatus());
                                    intent.putExtra("deposit_id", clickedItem.getDepositID());
                                    intent.putExtra("date", clickedItem.getDate_submitted());
                                    intent.putExtra("proof_of_payment_image", clickedItem.getProofOfPaymentImage());
                                    startActivity(intent);
                                });


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
