package com.kushina.customer.android.navigations.notifications;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

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

import static com.kushina.customer.android.globals.Endpoints.API_NODE_NOTIFICATION;

public class NotificationsActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    List<RVNotificationsModel> nModel;

    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.ll_no_notifications_yet)
    LinearLayout llNoNotificationsYet;
    @BindView(R.id.rv_notifications)
    RecyclerView rvNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadNotifications();
    }

    private void loadNotifications() {

        nModel = new ArrayList<>();
        nModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());

        mAPI.api_request("POST",
                API_NODE_NOTIFICATION + "getMyNotifications",
                request_data,
                true,
                NotificationsActivity.this,
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

                                JSONArray items = data_array.getJSONArray("notifications");


                                for (int i = 0; i < items.length(); i++) {

                                    String notification_id = ((JSONObject) items.get(i)).get("notification_id").toString();
                                    String title = ((JSONObject) items.get(i)).get("title").toString();
                                    String description = ((JSONObject) items.get(i)).get("description").toString();
                                    String unread = ((JSONObject) items.get(i)).get("unread").toString();
                                    String image = ((JSONObject) items.get(i)).get("image").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();
                                    String order_id = ((JSONObject) items.get(i)).get("order_id").toString();
                                    String deposit_id = ((JSONObject) items.get(i)).get("deposit_id").toString();


                                    nModel.add(new RVNotificationsModel(notification_id,title,description,unread,image,date_created,order_id,deposit_id));

                                }

                                rvNotifications.setHasFixedSize(true);
                                rvNotifications.setLayoutManager(new LinearLayoutManager(NotificationsActivity.this));
                                RVNotificationsAdapter nAdapter = new RVNotificationsAdapter(NotificationsActivity.this, nModel);
                                rvNotifications.setAdapter(nAdapter);


                                if (nModel.isEmpty()) {
                                    llNoNotificationsYet.setVisibility(View.VISIBLE);
                                } else {
                                    llNoNotificationsYet.setVisibility(View.GONE);
                                }

                                nAdapter.setOnItemClickListener(new RVNotificationsAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {

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
