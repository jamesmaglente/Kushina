package com.kushina.customer.android.navigations.dashboard;


import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.ecash.DepositECashActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.kushina.customer.android.globals.Endpoints.API_NODE_DASHBOARD;
import static com.kushina.customer.android.globals.Endpoints.API_NODE_ECASH;
import static com.kushina.customer.android.globals.Endpoints.API_NODE_PROFILE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    Unbinder unbinder;
    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.rv_leaderboard) RecyclerView rvLeaderboard;
    @BindView(R.id.ll_no_rankings_yet) LinearLayout llNoRankingsYet;


    List<RVLeaderboardModel> lbModel;


    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mAPI = new API(getActivity());
        mGlobals = new Globals(getActivity());
        mPreferences = new Preferences(getActivity());

//        loadProfile();
        loadLeaderboard();
    }

    private void loadLeaderboard() {

        lbModel = new ArrayList<>();
        lbModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());

        mAPI.api_request("POST",
                API_NODE_DASHBOARD + "getDashboard",
                request_data,
                true,
                getActivity(),
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log(getClass().getEnclosingMethod().getName(), result.toString());

                        try {

                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;
                                JSONObject data_array = root.getJSONObject("data");
                                String current_rank = data_array.getString("current_rank");
                                JSONArray items = data_array.getJSONArray("rankings");

                                for (int i = 0; i < items.length(); i++) {

                                    String user_id = ((JSONObject) items.get(i)).get("user_id").toString();
                                    String firstname = ((JSONObject) items.get(i)).get("firstname").toString();
                                    String total_purchased = ((JSONObject) items.get(i)).get("total_purchased").toString();
                                    String rank = ((JSONObject) items.get(i)).get("rank").toString();


                                    lbModel.add(new RVLeaderboardModel(user_id, firstname, total_purchased,rank));

                                }


                                rvLeaderboard.setHasFixedSize(true);
                                rvLeaderboard.setLayoutManager(new LinearLayoutManager(getActivity()));
                                RVLeaderboardAdapter lbAdapter = new RVLeaderboardAdapter(getActivity(), lbModel);
                                rvLeaderboard.setAdapter(lbAdapter);

                                if (lbModel.isEmpty()) {
                                    llNoRankingsYet.setVisibility(View.VISIBLE);
                                } else {
                                    llNoRankingsYet.setVisibility(View.GONE);
                                }

//                                lbAdapter.setOnItemClickListener(position -> {
//
//                                    RVPaymentOptionsModel clickedItem = poModel.get(position);
//
//                                    mGlobals.showChoiceDialog("Are you sure you want to set your payment method to " + clickedItem.getPaymentOption() + "?", true, new Globals.Callback() {
//                                        @Override
//                                        public void onPickCallback(Boolean result) {
//                                            if (result) {
//                                                Intent data = new Intent();
//                                                data.putExtra("payment_option_id", clickedItem.getPaymentOptionID());
//                                                data.putExtra("payment_option", clickedItem.getPaymentOption());
//                                                setResult(RESULT_OK, data);
//                                                finish();
//                                            }
//                                        }
//                                    });
//
//                                });


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
