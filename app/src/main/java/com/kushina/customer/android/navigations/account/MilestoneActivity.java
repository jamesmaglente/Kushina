package com.kushina.customer.android.navigations.account;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.achievements_and_rewards.RVMilestonesAdapter;
import com.kushina.customer.android.navigations.achievements_and_rewards.RVMilestonesModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_REWARD;

public class MilestoneActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    Unbinder unbinder;

    List<RVMilestonesModel> mModel;
    @BindView(R.id.rv_milestones)
    RecyclerView rvMilestones;
    @BindView(R.id.tv_current_toque)
    TextView tvCurrentToque;
    @BindView(R.id.tv_milestone_membership_type)
    TextView tvMilestoneMembershipType;
    @BindView(R.id.tv_milestone_from_item)
    TextView tvMilestoneFromItem;
    @BindView(R.id.tv_milestone_from_purchased)
    TextView tvMilestoneFromPurchased;
    @BindView(R.id.tv_milestone_from_referrals)
    TextView tvMilestoneFromReferrals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestone);
        ButterKnife.bind(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        mAPI = new API(MilestoneActivity.this);
        mGlobals = new Globals(MilestoneActivity.this);
        mPreferences = new Preferences(MilestoneActivity.this);

        ButterKnife.bind(this);

        loadMilestones();
    }

    private void loadMilestones() {
        mModel = new ArrayList<>();
        mModel.clear();


        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());

        mAPI.api_request("POST",
                API_NODE_REWARD + "getAllRewards",
                request_data,
                true,
                MilestoneActivity.this,
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
                                String currentToque = data_array.getString("current_toque");
                                String membershipType = data_array.getString("membership_type");
                                JSONObject toque_breakdown = data_array.getJSONObject("toque_breakdown");
                                String from_item = toque_breakdown.getString("from_item");
                                String from_purchase = toque_breakdown.getString("from_purchase");
                                String from_referrals = toque_breakdown.getString("from_referrals");
                                JSONArray items = data_array.getJSONArray("rewards");


                                tvCurrentToque.setText(mGlobals.twoDecimalFormatter(currentToque));
                                tvMilestoneFromItem.setText(mGlobals.twoDecimalFormatter(from_item));
                                tvMilestoneFromPurchased.setText(mGlobals.twoDecimalFormatter(from_purchase));
                                tvMilestoneFromReferrals.setText(mGlobals.twoDecimalFormatter(from_referrals));
                                tvMilestoneMembershipType.setText(membershipType);

                                for (int i = 0; i < items.length(); i++) {

                                    String membership_type_id = ((JSONObject) items.get(i)).get("membership_type_id").toString();
                                    String membership_type = ((JSONObject) items.get(i)).get("membership_type").toString();
                                    String description = ((JSONObject) items.get(i)).get("description").toString();
                                    String level = ((JSONObject) items.get(i)).get("level").toString();
                                    String discount = ((JSONObject) items.get(i)).get("discount").toString();
                                    String required_toque = ((JSONObject) items.get(i)).get("required_toque").toString();


                                    mModel.add(new RVMilestonesModel(membership_type_id, membership_type, description, level, discount, required_toque, currentToque));

                                }


                                rvMilestones.setHasFixedSize(true);
                                rvMilestones.setLayoutManager(new LinearLayoutManager(MilestoneActivity.this));
                                RVMilestonesAdapter mAdapter = new RVMilestonesAdapter(MilestoneActivity.this, mModel);
                                rvMilestones.setAdapter(mAdapter);


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
