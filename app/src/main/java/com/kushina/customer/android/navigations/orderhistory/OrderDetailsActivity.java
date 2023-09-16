package com.kushina.customer.android.navigations.orderhistory;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.cart.RVMyCartModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_ORDERS;

public class OrderDetailsActivity extends AppCompatActivity {

    @BindView(R.id.rv_order_status_history)
    RecyclerView rvOrderStatusHistory;


    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    List<RVOrderStatusHistoryModel> shModel;
    List<RVMyCartModel> foModel;
    @BindView(R.id.rv_order_foods_ordered)
    RecyclerView rvOrderFoodsOrdered;
    @BindView(R.id.tv_order_details_total_amount)
    TextView tvOrderDetailsTotalAmount;
    @BindView(R.id.tv_order_details_dropoff_name)
    TextView tvOrderDetailsDropoffName;
    @BindView(R.id.tv_order_details_status)
    TextView tvOrderDetailsStatus;
    @BindView(R.id.tv_order_details_order_id)
    TextView tvOrderDetailsOrderId;
    @BindView(R.id.tv_order_details_order_placed)
    TextView tvOrderDetailsOrderPlaced;
    @BindView(R.id.tv_order_details_remarks)
    TextView tvOrderDetailsRemarks;
    private String orderID, reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        orderID = getIntent().getStringExtra("order_id");
        reference = getIntent().getStringExtra("reference");

        loadOrderStatusHistory();
        loadFoodsOrdered();
    }

    private void loadOrderStatusHistory() {

        shModel = new ArrayList<>();
        shModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("order_id", orderID);

        mAPI.api_request("POST",
                API_NODE_ORDERS + "getOrderStatusHistory",
                request_data,
                false,
                OrderDetailsActivity.this,
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
                                JSONArray items = data_array.getJSONArray("order_status_history");

                                for (int i = 0; i < items.length(); i++) {

                                    String order_history_id = ((JSONObject) items.get(i)).get("order_history_id").toString();
                                    String order_id = ((JSONObject) items.get(i)).get("order_id").toString();
                                    String user_id = ((JSONObject) items.get(i)).get("user_id").toString();
                                    String from_status_id = ((JSONObject) items.get(i)).get("from_status_id").toString();
                                    String from_status = ((JSONObject) items.get(i)).get("from_status").toString();
                                    String to_status_id = ((JSONObject) items.get(i)).get("to_status_id").toString();
                                    String to_status = ((JSONObject) items.get(i)).get("to_status").toString();
                                    String remarks = ((JSONObject) items.get(i)).get("remarks").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();

                                    shModel.add(new RVOrderStatusHistoryModel(order_history_id, order_id, user_id, from_status_id, from_status, to_status_id, to_status, remarks, date_created));

                                }


                                rvOrderStatusHistory.setHasFixedSize(true);
                                rvOrderStatusHistory.setLayoutManager(new LinearLayoutManager(OrderDetailsActivity.this));
                                RVOrderStatusHistoryAdapter shAdapter = new RVOrderStatusHistoryAdapter(OrderDetailsActivity.this, shModel);
                                rvOrderStatusHistory.setAdapter(shAdapter);


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

    private void loadFoodsOrdered() {

        foModel = new ArrayList<>();
        foModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();

        mAPI.api_request("GET",
                API_NODE_ORDERS + "getOrderDetails/" + reference,
                request_data,
                true,
                OrderDetailsActivity.this,
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

                                String order_id = data_array.getString("order_id");
                                String code_id = data_array.getString("code_id");
                                String cart_id = data_array.getString("cart_id");
                                String status_id = data_array.getString("status_id");
                                String status = data_array.getString("status");
                                String total_amount = data_array.getString("total_amount");
                                String user_id = data_array.getString("user_id");
                                String customer_firstname = data_array.getString("customer_firstname");
                                String customer_lastname = data_array.getString("customer_lastname");
                                String customer_fullname = data_array.getString("customer_fullname");
                                String mobile = data_array.getString("mobile");
                                String payment_option_id = data_array.getString("payment_option_id");
                                String payment_option = data_array.getString("payment_option");
                                String address_id = data_array.getString("address_id");
                                String reference = data_array.getString("reference");
                                String order_placed = data_array.getString("order_placed");
                                String customer_name = data_array.getString("customer_name");
                                String remarks = data_array.getString("remarks");

//                                if(!remarks.equals("") && !remarks.equals("null")) {
//                                    tvOrderDetailsRemarks.setText(remarks);
//                                }
                                tvOrderDetailsTotalAmount.setText(mGlobals.moneyFormatter(total_amount));
                                tvOrderDetailsDropoffName.setText(customer_name);
                                tvOrderDetailsOrderId.setText(code_id);
                                tvOrderDetailsStatus.setText(status);
                                tvOrderDetailsOrderPlaced.setText(mGlobals.dateFormatter(order_placed));

                                if (status.toLowerCase().equals("delivered")) {
                                    tvOrderDetailsStatus.setTextColor(getResources().getColor(R.color.colorSuccess));
                                } else if (status.toLowerCase().equals("cancelled")) {
                                    tvOrderDetailsStatus.setTextColor(getResources().getColor(R.color.colorError));
                                }

                                JSONArray items = data_array.getJSONArray("cart");

                                for (int i = 0; i < items.length(); i++) {

                                    String cart_item_id = ((JSONObject) items.get(i)).get("cart_item_id").toString();
                                    String item_id = ((JSONObject) items.get(i)).get("item_id").toString();
                                    String quantity = ((JSONObject) items.get(i)).get("quantity").toString();
                                    String cart_remarks = ((JSONObject) items.get(i)).get("remarks").toString();
                                    String cart_code_id = ((JSONObject) items.get(i)).get("code_id").toString();
                                    String current_stock = ((JSONObject) items.get(i)).get("current_stock").toString();
                                    String cart_total_amount = ((JSONObject) items.get(i)).get("total_amount").toString();
                                    String cart_status = ((JSONObject) items.get(i)).get("status").toString();
                                    String category_id = ((JSONObject) items.get(i)).get("category_id").toString();
                                    String category = ((JSONObject) items.get(i)).get("category").toString();
                                    String description = ((JSONObject) items.get(i)).get("description").toString();
                                    String long_description = ((JSONObject) items.get(i)).get("long_description").toString();
                                    String image = ((JSONObject) items.get(i)).get("image").toString();
                                    String sku = ((JSONObject) items.get(i)).get("sku").toString();
                                    String discount = ((JSONObject) items.get(i)).get("discount").toString();
                                    String srp = ((JSONObject) items.get(i)).get("srp").toString();
                                    String tax = ((JSONObject) items.get(i)).get("tax").toString();
                                    String likes = ((JSONObject) items.get(i)).get("likes").toString();
                                    String merchant_id = ((JSONObject) items.get(i)).get("merchant_id").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();


                                    foModel.add(new RVMyCartModel(cart_item_id, item_id, quantity, cart_remarks, cart_code_id, current_stock, cart_total_amount, cart_status, category_id, category, description, long_description, image, sku, srp, discount, tax, likes, merchant_id, date_created));

                                }


                                rvOrderFoodsOrdered.setHasFixedSize(true);
                                rvOrderFoodsOrdered.setLayoutManager(new LinearLayoutManager(OrderDetailsActivity.this));
                                RVFoodsOrderedAdapter foAdapter = new RVFoodsOrderedAdapter(OrderDetailsActivity.this, foModel);
                                rvOrderFoodsOrdered.setAdapter(foAdapter);


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
