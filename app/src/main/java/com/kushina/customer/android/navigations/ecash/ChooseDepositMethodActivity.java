package com.kushina.customer.android.navigations.ecash;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.cart.RVPaymentOptionsAdapter;
import com.kushina.customer.android.navigations.cart.RVPaymentOptionsModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_ECASH;

public class ChooseDepositMethodActivity extends AppCompatActivity {

    @BindView(R.id.rv_deposit_options)
    RecyclerView rvDepositOptions;

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    List<RVPaymentOptionsModel> poModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_deposit_method);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        loadDepositOptions();
    }

    private void loadDepositOptions(){

        poModel = new ArrayList<>();
        poModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();

        mAPI.api_request("GET",
                API_NODE_ECASH+"getDepositMethods",
                request_data,
                true,
                ChooseDepositMethodActivity.this,
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
                                JSONArray items = data_array.getJSONArray("deposit_methods");

                                for (int i = 0; i < items.length(); i++) {

                                    String payment_option_id = ((JSONObject) items.get(i)).get("deposit_id").toString();
                                    String payment_option = ((JSONObject) items.get(i)).get("description").toString();


                                    poModel.add(new RVPaymentOptionsModel(payment_option_id,payment_option));

                                }


                                rvDepositOptions.setHasFixedSize(true);
                                rvDepositOptions.setLayoutManager(new LinearLayoutManager(ChooseDepositMethodActivity.this));
                                RVPaymentOptionsAdapter poAdapter = new RVPaymentOptionsAdapter(ChooseDepositMethodActivity.this, poModel);
                                rvDepositOptions.setAdapter(poAdapter);

                                poAdapter.setOnItemClickListener(position -> {

                                    RVPaymentOptionsModel clickedItem = poModel.get(position);

                                    mGlobals.showChoiceDialog("Are you sure you want to set your deposit method to " + clickedItem.getPaymentOption() + "?", true, new Globals.Callback() {
                                        @Override
                                        public void onPickCallback(Boolean result) {
                                            if (result){
                                                Intent data = new Intent();
                                                data.putExtra("deposit_option_id", clickedItem.getPaymentOptionID());
                                                data.putExtra("deposit_option", clickedItem.getPaymentOption());
                                                setResult(RESULT_OK, data);
                                                finish();
                                            }
                                        }
                                    });

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
