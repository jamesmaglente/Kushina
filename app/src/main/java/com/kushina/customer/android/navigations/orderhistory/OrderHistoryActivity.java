package com.kushina.customer.android.navigations.orderhistory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import butterknife.OnClick;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_ORDERS;

public class OrderHistoryActivity extends AppCompatActivity {

    @BindView(R.id.btn_ongoing_orders)
    LinearLayout btnOngoingOrders;
    @BindView(R.id.btn_processed_orders)
    LinearLayout btnProcessedOrders;
    @BindView(R.id.ll_view_switcher)
    LinearLayout llViewSwitcher;
    @BindView(R.id.rv_orders_history)
    RecyclerView rvOrdersHistory;

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    List<RVOrderHistoryModel> ohModel;
    @BindView(R.id.tv_order_history_ongoing)
    TextView tvOrderHistoryOngoing;
    @BindView(R.id.tv_order_history_processed)
    TextView tvOrderHistoryProcessed;
    @BindView(R.id.ll_no_history_yet)
    LinearLayout llNoHistoryYet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        if(getIntent().getStringExtra("task").equals("ongoing")) {
            setTitle("Ongoing Orders");
        }else{
            setTitle("Order History");
        }

        loadOrderHistory(getIntent().getStringExtra("task"));
        tvOrderHistoryProcessed.setTextColor(getResources().getColor(R.color.colorOrange));
    }

    @OnClick({R.id.btn_ongoing_orders, R.id.btn_processed_orders})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ongoing_orders:
                loadOrderHistory("ongoing");
                tvOrderHistoryProcessed.setTextColor(getResources().getColor(R.color.colorOrange));
                tvOrderHistoryOngoing.setTextColor(getResources().getColor(R.color.colorWhite));
                break;
            case R.id.btn_processed_orders:
                loadOrderHistory("processed");
                tvOrderHistoryOngoing.setTextColor(getResources().getColor(R.color.colorOrange));
                tvOrderHistoryProcessed.setTextColor(getResources().getColor(R.color.colorWhite));
                break;
        }
    }

    private void loadOrderHistory(String orderType) {

        ohModel = new ArrayList<>();
        ohModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("status", orderType);

        mAPI.api_request("POST",
                API_NODE_ORDERS + "getOrders",
                request_data,
                true,
                OrderHistoryActivity.this,
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
                                rvOrdersHistory.setLayoutManager(new LinearLayoutManager(OrderHistoryActivity.this));
                                RVOrderHistoryAdapter ohAdapter = new RVOrderHistoryAdapter(OrderHistoryActivity.this, ohModel);
                                rvOrdersHistory.setAdapter(ohAdapter);


                                if(ohModel.isEmpty()){
                                    llNoHistoryYet.setVisibility(View.VISIBLE);
                                }else {
                                    llNoHistoryYet.setVisibility(View.GONE);
                                }

                                //     mGlobals.dismissLoadingDialog();


                                ohAdapter.setOnItemClickListener(position -> {
                                    RVOrderHistoryModel clickedItem = ohModel.get(position);

                                    Intent intent = new Intent(OrderHistoryActivity.this, OrderDetailsActivity.class);
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
