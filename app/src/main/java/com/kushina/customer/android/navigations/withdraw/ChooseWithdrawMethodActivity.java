package com.kushina.customer.android.navigations.withdraw;

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
import com.kushina.customer.android.navigations.ecash.ChooseDepositMethodActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_ECASH;

public class ChooseWithdrawMethodActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    List<RVPaymentOptionsModel> poModel;
    @BindView(R.id.rv_withdraw_options)
    RecyclerView rvWithdrawOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_withdraw_method);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        loadWithdrawOptions();
    }

    private void loadWithdrawOptions(){
        poModel = new ArrayList<>();
        poModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();

        mAPI.api_request("GET",
                API_NODE_ECASH+"getWithdrawMethods",
                request_data,
                true,
                ChooseWithdrawMethodActivity.this,
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
                                JSONArray items = data_array.getJSONArray("withdraw_method");

                                for (int i = 0; i < items.length(); i++) {

                                    String withdraw_method_id = ((JSONObject) items.get(i)).get("withdraw_method_id").toString();
                                    String method = ((JSONObject) items.get(i)).get("method").toString();


                                    poModel.add(new RVPaymentOptionsModel(withdraw_method_id,method));

                                }


                                rvWithdrawOptions.setHasFixedSize(true);
                                rvWithdrawOptions.setLayoutManager(new LinearLayoutManager(ChooseWithdrawMethodActivity.this));
                                RVPaymentOptionsAdapter poAdapter = new RVPaymentOptionsAdapter(ChooseWithdrawMethodActivity.this, poModel);
                                rvWithdrawOptions.setAdapter(poAdapter);

                                poAdapter.setOnItemClickListener(position -> {

                                    RVPaymentOptionsModel clickedItem = poModel.get(position);

                                    mGlobals.showChoiceDialog("Are you sure you want to set your withdraw method to " + clickedItem.getPaymentOption() + "?", true, new Globals.Callback() {
                                        @Override
                                        public void onPickCallback(Boolean result) {
                                            if (result){
                                                Intent data = new Intent();
                                                data.putExtra("withdraw_method_id", clickedItem.getPaymentOptionID());
                                                data.putExtra("withdraw_method", clickedItem.getPaymentOption());
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