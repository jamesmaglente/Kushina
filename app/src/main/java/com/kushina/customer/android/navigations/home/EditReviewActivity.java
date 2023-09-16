package com.kushina.customer.android.navigations.home;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_SHOP;

public class EditReviewActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.rb_item_reviews_rating)
    AppCompatRatingBar rbItemReviewsRating;
    @BindView(R.id.edt_item_reviews_review)
    TextInputEditText edtItemReviewsReview;
    @BindView(R.id.til_item_reviews_review)
    TextInputLayout tilItemReviewsReview;
    @BindView(R.id.btn_add_review)
    Button btnAddReview;
    private String itemRatingID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        itemRatingID = getIntent().getStringExtra("item_rating_id");
        rbItemReviewsRating.setRating(Float.valueOf(getIntent().getStringExtra("rating")));
        edtItemReviewsReview.setText(getIntent().getStringExtra("review"));
    }

    @OnClick(R.id.btn_add_review)
    public void onViewClicked() {
        mGlobals.showChoiceDialog("Save this review?", true, new Globals.Callback() {
            @Override
            public void onPickCallback(Boolean result) {
                if (result) {
                    if(rbItemReviewsRating.getRating()!= 0 && !edtItemReviewsReview.getText().toString().trim().equals("")){
                        saveReview();
                    }else{
                        mGlobals.showErrorMessageWithDelay("Please enter a rating and a review.", true, new Globals.Callback() {
                            @Override
                            public void onPickCallback(Boolean result) {
                                if(result){
                                    return;
                                }
                            }
                        });
                    }

                }
            }
        });
    }

    private void saveReview(){

        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("item_rating_id", itemRatingID);
        request_data.put("rating", String.valueOf(rbItemReviewsRating.getRating()));
        request_data.put("review", String.valueOf(edtItemReviewsReview.getText()));



        mAPI.api_request("POST",
                API_NODE_SHOP + "editReview",
                request_data,
                true,
                EditReviewActivity.this,
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
                                            onBackPressed();
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
}
