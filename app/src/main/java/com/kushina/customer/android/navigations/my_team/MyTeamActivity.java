package com.kushina.customer.android.navigations.my_team;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.account.RankingActivity;
import com.kushina.customer.android.navigations.dashboard.RVLeaderboardAdapter;
import com.kushina.customer.android.navigations.dashboard.RVLeaderboardModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_DASHBOARD;
import static com.kushina.customer.android.globals.Endpoints.API_NODE_PROFILE;

public class MyTeamActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.tv_account_referral_code)
    TextView tvAccountReferralCode;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.ll_no_team_yet)
    LinearLayout llNoTeamYet;
    @BindView(R.id.rv_my_team)
    RecyclerView rvMyTeam;
    @BindView(R.id.tv_copy) TextView tvCopy;

    List<RVMyTeamModel> mtModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        tvAccountReferralCode.setText(mPreferences.getUserCode());

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadMyTeam();
    }

    private void loadMyTeam() {

        mtModel = new ArrayList<>();
        mtModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("level", "1");

        mAPI.api_request("POST",
                API_NODE_PROFILE + "getMyTeam",
                request_data,
                true,
                MyTeamActivity.this,
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
                                JSONArray items = data_array.getJSONArray("my_team");

                                for (int i = 0; i < items.length(); i++) {

                                    String referror_user_id = ((JSONObject) items.get(i)).get("user_id").toString();
                                    String level = ((JSONObject) items.get(i)).get("level").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();
                                    String firstname = ((JSONObject) items.get(i)).get("firstname").toString();
                                    String middlename = ((JSONObject) items.get(i)).get("middlename").toString();
                                    String lastname = ((JSONObject) items.get(i)).get("lastname").toString();
                                    String full_name = ((JSONObject) items.get(i)).get("full_name").toString();
                                    String email = ((JSONObject) items.get(i)).get("email").toString();
                                    String mobile = ((JSONObject) items.get(i)).get("mobile").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();
                                    String total_purchase = ((JSONObject) items.get(i)).get("total_purchase").toString();



                                    mtModel.add(new RVMyTeamModel(referror_user_id,level,date_created,firstname,middlename,lastname,full_name,email,mobile,status,total_purchase));

                                }


                                rvMyTeam.setHasFixedSize(true);
                                rvMyTeam.setLayoutManager(new LinearLayoutManager(MyTeamActivity.this));
                                RVMyTeamAdapter mtAdapter = new RVMyTeamAdapter(MyTeamActivity.this, mtModel);
                                rvMyTeam.setAdapter(mtAdapter);

                                if (mtModel.isEmpty()) {
                                    llNoTeamYet.setVisibility(View.VISIBLE);
                                } else {
                                    llNoTeamYet.setVisibility(View.GONE);
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

    @OnClick({R.id.tv_copy, R.id.iv_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_copy:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Kushina Referral Code", tvAccountReferralCode.getText().toString());
                clipboard.setPrimaryClip(clip);
                mGlobals.toast("Copied to Clipboard");
                break;
            case R.id.iv_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBody = "Kushina Referral Code: " + tvAccountReferralCode.getText().toString();
                String shareSub = "Kushina Referral Code";
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                startActivity(Intent.createChooser(intent, "Share using"));
                break;
        }
    }
}
