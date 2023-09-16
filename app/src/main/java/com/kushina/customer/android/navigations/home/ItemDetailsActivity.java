package com.kushina.customer.android.navigations.home;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.globals.ui.CountDrawable;
import com.kushina.customer.android.navigations.MainActivity;
import com.kushina.customer.android.navigations.cart.MyCartActivity;
import com.kushina.customer.android.navigations.notifications.NotificationsActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_SHOP;

public class ItemDetailsActivity extends AppCompatActivity {

    @BindView(R.id.iv_item_details_image) ImageView ivItemDetailsImage;
    @BindView(R.id.tv_item_details_item_name) TextView tvItemDetailsItemName;
    @BindView(R.id.tv_item_details_item_desc) TextView tvItemDetailsItemDesc;
    @BindView(R.id.btn_minus_qty) TextView btnMinusQty;
    @BindView(R.id.tv_item_qty) TextView tvItemQty;
    @BindView(R.id.btn_add_qty) TextView btnAddQty;
    @BindView(R.id.btn_add_to_cart) Button btnAddToCart;


    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.ll_no_reviews_yet) LinearLayout llNoReviewsYet;
    @BindView(R.id.rv_reviews) RecyclerView rvReviews;
    @BindView(R.id.rb_item_reviews_rating) AppCompatRatingBar rbItemReviewsRating;
    @BindView(R.id.edt_item_reviews_review) TextInputEditText edtItemReviewsReview;
    @BindView(R.id.til_item_reviews_review) TextInputLayout tilItemReviewsReview;
    @BindView(R.id.btn_add_review) Button btnAddReview;
    @BindView(R.id.btn_item_favorite) ImageView btnItemFavorite;
    @BindView(R.id.ll_reviews)
    LinearLayout llReviews;
    private Integer currentQty = 1;
    private String itemID;
    List<RVReviewsModel> rModel;
    private Boolean favorited;

    @BindView(R.id.tv_bottom_item_description) TextView tvBottomItemDescription;
    @BindView(R.id.tv_bottom_item_price) TextView tvBottomItemPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_details);
        ButterKnife.bind(this);

//        getSupportActionBar().hide();
//        View mDecorView = ItemDetailsActivity.this.getWindow().getDecorView();
//        mDecorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        //     | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        //    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        ivItemDetailsImage.setMaxHeight(getScreenHeight() / 2);

        tvItemQty.setText(String.valueOf(currentQty));


    }

    @Override
    protected void onResume() {
        super.onResume();


        loadItemDetails();
    }

    private void loadItemDetails() {

        itemID = getIntent().getStringExtra("item_id");


        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("item_id", itemID);


        mAPI.api_request("POST",
                API_NODE_SHOP + "getItemDetails",
                request_data,
                false,
                ItemDetailsActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log(getClass().getEnclosingMethod().getName(), String.valueOf(result));


                        try {
                            // parse response object
                            JSONObject jsonObject = result.getJSONObject("data");
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;
                                JSONObject data_array = root.getJSONObject("data");
                                JSONObject item_details = data_array.getJSONObject("item_details");
                                String item_id = item_details.getString("item_id");
                                String category_id = item_details.getString("category_id");
                                String category = item_details.getString("category");
                                String item_name = item_details.getString("item_name");
                                String item_description = item_details.getString("item_description");
                                String image = item_details.getString("image");
                                String favorite = item_details.getString("favorite");
                                String srp = item_details.getString("srp");
                                String quantity = item_details.getString("quantity");
                                String status = item_details.getString("status");
                                String date_created = item_details.getString("date_created");
                                Boolean ordered_before = item_details.getBoolean("ordered_before");
                                String review = item_details.getString("review");


                                if (favorite.toLowerCase().equals("false")) {
                                    btnItemFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_outline));
                                    favorited = false;
                                } else {
                                    btnItemFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_filled));
                                    favorited = true;
                                }

                                if(ordered_before){
                                    if(review.equals("")) {
                                        llReviews.setVisibility(View.VISIBLE);
                                    }else{
                                        llReviews.setVisibility(View.GONE);
                                    }
                                }else{
                                    llReviews.setVisibility(View.GONE);
                                }


                                tvItemDetailsItemName.setText(item_name);
                                tvItemDetailsItemDesc.setText(item_description);

                                tvBottomItemDescription.setText(item_name);
                                tvBottomItemPrice.setText(mGlobals.moneyFormatter(srp));

                                try {
                                    Picasso.get()
                                            //.load(ITEMS_URL + getIntent().getStringExtra("item_image"))
                                            //.load(item.getItemImage())
                                            .load(image)
                                            .resize(getScreenWidth(), getScreenHeight() / 2)
                                            .placeholder(R.drawable.applogo)
                                            .resize(getScreenWidth(), getScreenHeight() / 2)
                                            .into(ivItemDetailsImage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            } else {
                                mGlobals.log(getClass().getEnclosingMethod().getName(), status_message);


                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(getClass().getEnclosingMethod().getName(), e.toString());


                        }
                    }
                });


        loadItemReviews();


    }

    @OnClick({R.id.btn_minus_qty, R.id.btn_add_qty, R.id.btn_add_to_cart, R.id.btn_add_review, R.id.btn_item_favorite, R.id.btn_buy_now})
    public void onViewClicked(View view) {
        Double itemAmount = Double.valueOf(getIntent().getStringExtra("item_amount"));
        switch (view.getId()) {
            case R.id.btn_minus_qty:
                currentQty = Integer.valueOf(tvItemQty.getText().toString());
                if (currentQty > 1) {
                    currentQty -= 1;
                    tvItemQty.setText(String.valueOf(currentQty));
                    tvBottomItemPrice.setText(mGlobals.moneyFormatter(String.valueOf(itemAmount * currentQty)));
                }
                break;
            case R.id.btn_add_qty:
                currentQty = Integer.valueOf(tvItemQty.getText().toString());
                currentQty += 1;
                tvItemQty.setText(String.valueOf(currentQty));
                tvBottomItemPrice.setText(mGlobals.moneyFormatter(String.valueOf(itemAmount * currentQty)));
                break;
            case R.id.btn_add_to_cart:
//                mGlobals.showChoiceDialog("Add " + currentQty + " " + getIntent().getStringExtra("item_name") + " to your basket?", true, new Globals.Callback() {
//                    @Override
//                    public void onPickCallback(Boolean result) {
//                        if (result) {
                            addToCart("add to cart");
//                        }
//                    }
//                });
                break;
            case R.id.btn_buy_now:
//                mGlobals.showChoiceDialog("Add " + currentQty + " " + getIntent().getStringExtra("item_name") + " to your basket?", true, new Globals.Callback() {
//                    @Override
//                    public void onPickCallback(Boolean result) {
//                        if (result) {
                            addToCart("buy now");
//                        }
//                    }
//                });
                break;
            case R.id.btn_add_review:
                mGlobals.showChoiceDialog("Add a " + String.valueOf(rbItemReviewsRating.getRating()) + " star rating " + " to this item?", true, new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        if (result) {
                            if (rbItemReviewsRating.getRating() != 0 && !edtItemReviewsReview.getText().toString().trim().equals("")) {
                                addReview();
                            } else {
                                mGlobals.showErrorMessageWithDelay("Please enter a rating and a review.", true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {
                                            return;
                                        }
                                    }
                                });
                            }

                        }
                    }
                });
                break;
            case R.id.btn_item_favorite:
                toggleFavorite();
                break;
        }
    }

    private void addToCart(String redirectTo) {

        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("item_id", itemID);
        request_data.put("quantity", tvItemQty.getText().toString());


        mAPI.api_request("POST",
                API_NODE_SHOP + "addToCart",
                request_data,
                true,
                ItemDetailsActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log(getClass().getEnclosingMethod().getName(), String.valueOf(result));


                        try {
                            // parse response object
                            JSONObject jsonObject = result.getJSONObject("data");
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {


                                mGlobals.toast(status_message);
                                if(redirectTo.equals("buy now")){
                                    Intent intent = new Intent(ItemDetailsActivity.this, MyCartActivity.class);
                                    startActivity(intent);
                                }
//                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
//                                    @Override
//                                    public void onPickCallback(Boolean result) {
//                                        if (result) {
//                                            mGlobals.showChoiceDialogWithDelay("Go to Basket?", true, new Globals.Callback() {
//                                                @Override
//                                                public void onPickCallback(Boolean result) {
//                                                    if (result) {
//                                                        Intent intent = new Intent(ItemDetailsActivity.this, MyCartActivity.class);
//                                                        startActivity(intent);
//                                                    } else {
//                                                        onBackPressed();
//                                                    }
//                                                }
//                                            });
//                                        }
//                                    }
//                                });

                            } else {
                                mGlobals.log(getClass().getEnclosingMethod().getName(), status_message);


                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(getClass().getEnclosingMethod().getName(), e.toString());


                        }
                    }
                });
    }

    private void addReview() {

        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("item_id", itemID);
        request_data.put("rating", String.valueOf(rbItemReviewsRating.getRating()));
        request_data.put("review", String.valueOf(edtItemReviewsReview.getText()));


        mAPI.api_request("POST",
                API_NODE_SHOP + "addReview",
                request_data,
                true,
                ItemDetailsActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log(getClass().getEnclosingMethod().getName(), String.valueOf(result));


                        try {
                            // parse response object
                            JSONObject jsonObject = result.getJSONObject("data");
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {


                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {
                                            rbItemReviewsRating.setRating(0f);
                                            edtItemReviewsReview.setText("");
                                            mGlobals.removeError(tilItemReviewsReview);
                                            loadItemDetails();
                                           // loadItemReviews();
                                        }
                                    }
                                });

                            } else {
                                mGlobals.log(getClass().getEnclosingMethod().getName(), status_message);


                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(getClass().getEnclosingMethod().getName(), e.toString());


                        }
                    }
                });

    }

    private void toggleFavorite() {
        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("item_id", itemID);


        mAPI.api_request("POST",
                API_NODE_SHOP + "toggleFavorite",
                request_data,
                true,
                ItemDetailsActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log(getClass().getEnclosingMethod().getName(), String.valueOf(result));


                        try {
                            // parse response object
                            JSONObject jsonObject = result.getJSONObject("data");
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                if (favorited) {
                                    btnItemFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_outline));
                                    favorited = false;
                                } else {
                                    btnItemFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_filled));
                                    favorited = true;
                                }
//                                mGlobals.toast(status_message);

                            } else {
                                mGlobals.log(getClass().getEnclosingMethod().getName(), status_message);


                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(getClass().getEnclosingMethod().getName(), e.toString());


                        }
                    }
                });
    }

    private void loadItemReviews() {

        rModel = new ArrayList<>();
        rModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("item_id", itemID);


        mAPI.api_request("POST",
                API_NODE_SHOP + "getAllReviews",
                request_data,
                false,
                ItemDetailsActivity.this,
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
                                JSONArray items = data_array.getJSONArray("reviews");

                                for (int i = 0; i < items.length(); i++) {

                                    String item_rating_id = ((JSONObject) items.get(i)).get("item_rating_id").toString();
                                    String user_id = ((JSONObject) items.get(i)).get("user_id").toString();
                                    String reviewer_name = ((JSONObject) items.get(i)).get("reviewer_name").toString();
                                    String reviewer_image = ((JSONObject) items.get(i)).get("reviewer_image").toString();
                                    String rating = ((JSONObject) items.get(i)).get("rating").toString();
                                    String review = ((JSONObject) items.get(i)).get("review").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();


                                    rModel.add(new RVReviewsModel(item_rating_id, user_id, reviewer_name, reviewer_image, rating, review, date_created));

                                }


                                rvReviews.setHasFixedSize(true);
                                rvReviews.setLayoutManager(new LinearLayoutManager(ItemDetailsActivity.this));
                                RVReviewsAdapter rAdapter = new RVReviewsAdapter(ItemDetailsActivity.this, rModel);
                                rvReviews.setAdapter(rAdapter);


                                if (rModel.isEmpty()) {
                                    llNoReviewsYet.setVisibility(View.VISIBLE);
                                } else {
                                    llNoReviewsYet.setVisibility(View.GONE);
                                }

                                //     mGlobals.dismissLoadingDialog();


                                rAdapter.setOnItemClickListener(position -> {
                                    RVReviewsModel clickedItem = rModel.get(position);

                                    if (clickedItem.getUserID() == mPreferences.getUserId().toString()) {
                                        Intent intent = new Intent(ItemDetailsActivity.this, EditReviewActivity.class);
                                        intent.putExtra("item_rating_id", clickedItem.getItemRatingID());
                                        intent.putExtra("rating", clickedItem.getRating());
                                        intent.putExtra("review", clickedItem.getReview());

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

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.menu_search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch(item.getItemId()){

            case R.id.action_my_cart:
                intent = new Intent(ItemDetailsActivity.this, MyCartActivity.class);
                startActivity(intent);
                break;

            case R.id.action_notifications:
                intent = new Intent(ItemDetailsActivity.this, NotificationsActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setCount(this, String.valueOf(mPreferences.getNotificationCount()), menu);
        return true;
    }

    public void setCount(Context context, String count, Menu defaultMenu) {
        MenuItem menuItem = defaultMenu.findItem(R.id.action_notifications);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_notification_count);
        if (reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_notification_count, badge);
    }

}
