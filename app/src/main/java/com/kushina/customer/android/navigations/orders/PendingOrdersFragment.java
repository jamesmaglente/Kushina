package com.kushina.customer.android.navigations.orders;


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
import com.kushina.customer.android.navigations.orderhistory.OrderDetailsActivity;
import com.kushina.customer.android.navigations.orderhistory.RVOrderHistoryAdapter;
import com.kushina.customer.android.navigations.orderhistory.RVOrderHistoryModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_ORDERS;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingOrdersFragment extends Fragment {


    public final String TAG = getClass().getSimpleName();

    @BindView(R.id.rv_orders_history)
    RecyclerView rvOrdersHistory;
    @BindView(R.id.ll_no_history_yet)
    LinearLayout llNoHistoryYet;

    Unbinder unbinder;
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    List<RVOrderHistoryModel> ohModel;


    public PendingOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending_orders, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mGlobals = new Globals(getActivity());
        mAPI = new API(getActivity());
        mPreferences = new Preferences(getActivity());

        loadOrders(8);
    }

    private void loadOrders(int status_id) {

        ohModel = new ArrayList<>();
        ohModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("status_id", String.valueOf(status_id));

        mAPI.api_request("POST",
                API_NODE_ORDERS + "getOrders",
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
                                JSONArray items = data_array.getJSONArray("orders");

                                for (int i = 0; i < items.length(); i++) {

                                    String order_id = ((JSONObject) items.get(i)).get("order_id").toString();
                                    String code_id = ((JSONObject) items.get(i)).get("code_id").toString();
                                    String cart_id = ((JSONObject) items.get(i)).get("cart_id").toString();
                                    String status_id = ((JSONObject) items.get(i)).get("status_id").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();
                                    String user_id = ((JSONObject) items.get(i)).get("user_id").toString();
                                    String payment_option_id = ((JSONObject) items.get(i)).get("payment_option_id").toString();
                                    String payment_option = ((JSONObject) items.get(i)).get("payment_option").toString();
                                    String address_id = ((JSONObject) items.get(i)).get("address_id").toString();
                                    String address_title = ((JSONObject) items.get(i)).get("address_title").toString();
                                    String customer_name = ((JSONObject) items.get(i)).get("customer_name").toString();
                                    String reference = ((JSONObject) items.get(i)).get("reference").toString();
                                    String total_amount = ((JSONObject) items.get(i)).get("total_amount").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();

                                    ohModel.add(new RVOrderHistoryModel(order_id,code_id, cart_id, status_id, status, user_id, payment_option_id, payment_option, address_id, address_title, customer_name, reference,total_amount, date_created));

                                }


                                rvOrdersHistory.setHasFixedSize(true);
                                rvOrdersHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
                                RVOrderHistoryAdapter ohAdapter = new RVOrderHistoryAdapter(getActivity(), ohModel);
                                rvOrdersHistory.setAdapter(ohAdapter);


                                if(ohModel.isEmpty()){
                                    llNoHistoryYet.setVisibility(View.VISIBLE);
                                }else {
                                    llNoHistoryYet.setVisibility(View.GONE);
                                }

                                //     mGlobals.dismissLoadingDialog();


                                ohAdapter.setOnItemClickListener(position -> {
                                    RVOrderHistoryModel clickedItem = ohModel.get(position);

                                    Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                                    intent.putExtra("order_id", clickedItem.getOrderID());
                                    intent.putExtra("cart_id", clickedItem.getCartID());
                                    intent.putExtra("reference", clickedItem.getReference());
//                                    intent.putExtra("item_image", clickedItem.getItemPictureLink());
                                    //     intent.putExtra("description", clickedItem.getDesc());
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
