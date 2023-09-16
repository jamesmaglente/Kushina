package com.kushina.customer.android.navigations.addresses;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import static com.kushina.customer.android.globals.Endpoints.API_NODE_PROFILE;

public class AddressListActivity extends AppCompatActivity {

    @BindView(R.id.no_address_yet) LinearLayout noAddressYet;
    @BindView(R.id.rv_address_list) RecyclerView rvAddressList;
    @BindView(R.id.fab_label) TextView fabLabel;
    @BindView(R.id.fab_add_address) FloatingActionButton fabAddAddress;
    @BindView(R.id.ll_add) LinearLayout llAdd;
    List<RVAddressListModel> alModel;

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        loadAddressList();
    }

    @OnClick(R.id.fab_add_address)
    public void onViewClicked() {
        Intent intent = new Intent(AddressListActivity.this,AddEditAddressActivity.class);
        intent.putExtra("task","add_new_address");
        startActivity(intent);
    }

    private void loadAddressList(){

        alModel = new ArrayList<>();
        alModel.clear();


        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());


        mAPI.api_request("POST",
                API_NODE_PROFILE + "getAllDeliveryAddress",
                request_data,
                true,
                AddressListActivity.this,
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
                                JSONArray items = data_array.getJSONArray("delivery_addresses");

                                for (int i = 0; i < items.length(); i++) {

                                    String address_id = ((JSONObject) items.get(i)).get("address_id").toString();
                                    String user_id = ((JSONObject) items.get(i)).get("user_id").toString();
                                    String main_address = ((JSONObject) items.get(i)).get("main_address").toString();
                                    String title = ((JSONObject) items.get(i)).get("title").toString();
                                    String customer_name = ((JSONObject) items.get(i)).get("customer_name").toString();
                                    String customer_number = ((JSONObject) items.get(i)).get("customer_number").toString();
                                    String address_line = ((JSONObject) items.get(i)).get("address_line").toString();
                                    String house_address = ((JSONObject) items.get(i)).get("house_address").toString();
                                    String zip_code = ((JSONObject) items.get(i)).get("zip_code").toString();
                                    String landmarks = ((JSONObject) items.get(i)).get("landmarks").toString();
                                    String latitude = ((JSONObject) items.get(i)).get("latitude").toString();
                                    String longitude = ((JSONObject) items.get(i)).get("longitude").toString();
                                    String place_id = ((JSONObject) items.get(i)).get("place_id").toString();
                                    String viewable = ((JSONObject) items.get(i)).get("viewable").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();

                                    alModel.add(new RVAddressListModel(address_id,user_id,main_address,title,customer_name,customer_number,address_line,house_address,zip_code,landmarks,latitude,longitude,place_id,viewable,date_created));

                                }



                                rvAddressList.setHasFixedSize(true);
                                rvAddressList.setLayoutManager(new LinearLayoutManager(AddressListActivity.this));
                                RVAddressListAdapter alAdapter = new RVAddressListAdapter(AddressListActivity.this, alModel);
                                rvAddressList.setAdapter(alAdapter);

                                if(alModel.isEmpty()){
                                    noAddressYet.setVisibility(View.VISIBLE);
                                }else {
                                    noAddressYet.setVisibility(View.GONE);
                                }

                                alAdapter.setOnItemClickListener(position -> {
                                    RVAddressListModel clickedItem = alModel.get(position);

                                    if(getIntent().getStringExtra("task") != null){
                                        mGlobals.showChoiceDialog("Are you sure you want to set your delivery address to " + clickedItem.getTitle() + "?", true, new Globals.Callback() {
                                            @Override
                                            public void onPickCallback(Boolean result) {
                                                if (result){
                                                    Intent data = new Intent();
                                                    data.putExtra("delivery_address_id", clickedItem.getDeliveryAddressID());
                                                    data.putExtra("delivery_address", clickedItem.getHouseAddress() + " "+clickedItem.getAddressLine()+ " "+clickedItem.getZipCode());
                                                    setResult(RESULT_OK, data);
                                                    finish();
                                                }
                                            }
                                        });
                                    }else {

                                        Intent intent = new Intent(AddressListActivity.this, AddEditAddressActivity.class);
                                        intent.putExtra("task", "update_address");
                                        intent.putExtra("address_id", clickedItem.getDeliveryAddressID());
                                        intent.putExtra("title", clickedItem.getTitle());
                                        intent.putExtra("customer_name", clickedItem.getCustomerName());
                                        intent.putExtra("customer_number", clickedItem.getContactNumber());
                                        intent.putExtra("address_line", clickedItem.getAddressLine());
                                        intent.putExtra("house_address", clickedItem.getHouseAddress());
                                        intent.putExtra("zip_code", clickedItem.getZipCode());
                                        intent.putExtra("landmarks", clickedItem.getLandmarks());
                                        intent.putExtra("latitude", clickedItem.getLatitude());
                                        intent.putExtra("longitude", clickedItem.getLongitude());
                                        intent.putExtra("place_id", clickedItem.getPlaceID());
                                        startActivity(intent);
                                    }
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
