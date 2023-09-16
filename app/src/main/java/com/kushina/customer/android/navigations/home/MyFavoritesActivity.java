package com.kushina.customer.android.navigations.home;

import android.content.Intent;
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

import static com.kushina.customer.android.globals.Endpoints.API_NODE_SHOP;

public class MyFavoritesActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.ll_no_favorites_yet)
    LinearLayout llNoFavoritesYet;
    @BindView(R.id.rv_my_favorites)
    RecyclerView rvMyFavorites;

    List<RVFavoritesModel> fModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorites);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);
        setTitle("My Favorites");
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadFavorites();
    }

    private void loadFavorites() {

        fModel = new ArrayList<>();
        fModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id",mPreferences.getUserId().toString());


        mAPI.api_request("POST",
                API_NODE_SHOP + "getMyFavorites",
                request_data,
                false,
                MyFavoritesActivity.this,
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
                                JSONArray items = data_array.getJSONArray("favorites");

                                for (int i = 0; i < items.length(); i++) {

                                    String favorite_id = ((JSONObject) items.get(i)).get("favorite_id").toString();
                                    String item_id = ((JSONObject) items.get(i)).get("item_id").toString();
                                    String category_id = ((JSONObject) items.get(i)).get("category_id").toString();
                                    String category = ((JSONObject) items.get(i)).get("category").toString();
                                    String item_name = ((JSONObject) items.get(i)).get("item_name").toString();
                                    String image = ((JSONObject) items.get(i)).get("image").toString();
                                    String srp = ((JSONObject) items.get(i)).get("srp").toString();
                                    String quantity = ((JSONObject) items.get(i)).get("quantity").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();


                                    fModel.add(new RVFavoritesModel(favorite_id,item_id,category_id,category,item_name,image,srp,quantity,status,date_created));

                                }


                                rvMyFavorites.setHasFixedSize(true);
                                rvMyFavorites.setLayoutManager(new LinearLayoutManager(MyFavoritesActivity.this));
                                RVFavoritesAdapter rAdapter = new RVFavoritesAdapter(MyFavoritesActivity.this, fModel);
                                rvMyFavorites.setAdapter(rAdapter);


                                if (fModel.isEmpty()) {
                                    llNoFavoritesYet.setVisibility(View.VISIBLE);
                                } else {
                                    llNoFavoritesYet.setVisibility(View.GONE);
                                }

                                //     mGlobals.dismissLoadingDialog();


                                rAdapter.setOnItemClickListener(position -> {
                                    RVFavoritesModel clickedItem = fModel.get(position);

                                        Intent intent = new Intent(MyFavoritesActivity.this, ItemDetailsActivity.class);
                                    intent.putExtra("item_id", clickedItem.getItemID());
                                    intent.putExtra("item_name", clickedItem.getItemName());
                                    intent.putExtra("item_amount", clickedItem.getItemAmount());
                                    intent.putExtra("item_image", clickedItem.getItemImage());

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
