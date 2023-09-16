package com.kushina.customer.android.navigations.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_ITEMS;

public class SearchItemsActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    List<RVHomeModel> hModel;
    RVSearchItemsAdapter hAdapter;
    @BindView(R.id.edt_search_items)
    TextInputEditText edtSearchItems;
    @BindView(R.id.rv_items)
    RecyclerView rvItems;
    @BindView(R.id.til_search_items)
    TextInputLayout tilSearchItems;
    ImageView ivBackButton;

    TextInputEditText edtSearch;
    TextView tvSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_items);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        edtSearchItems.addTextChangedListener(new MyTextWatcher(edtSearchItems));

        loadItems();

        ActionBar actionBar = getSupportActionBar();
        // add the custom view to the action bar
        actionBar.setCustomView(R.layout.actionbar_search_view);
        edtSearch = actionBar.getCustomView().findViewById(
                R.id.edt_search);
        tvSearch = actionBar.getCustomView().findViewById(
                R.id.tv_search);
        ivBackButton = actionBar.getCustomView().findViewById(
                R.id.iv_back_button);

        edtSearch.setFocusable(true);
        edtSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                hAdapter.getFilter().filter(edtSearch.getText().toString().trim());
                return false;
            }
        });

        edtSearch.addTextChangedListener(new MyTextWatcher(edtSearch));

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mGlobals.toast("click search icon");
                search(edtSearch.getText().toString().trim());
            }
        });

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                onBackPressed();
            }
        });

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);
    }

    private void loadItems() {

        hModel = new ArrayList<>();
        hModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());

        mAPI.api_request("POST",
                API_NODE_ITEMS + "getAllItems",
                request_data,
                true,
                SearchItemsActivity.this,
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
                                JSONArray items = data_array.getJSONArray("items");

                                for (int i = 0; i < items.length(); i++) {

                                    String item_id = ((JSONObject) items.get(i)).get("item_id").toString();
                                    String code_id = ((JSONObject) items.get(i)).get("code_id").toString();
                                    String category_id = ((JSONObject) items.get(i)).get("category_id").toString();
                                    String category = ((JSONObject) items.get(i)).get("category").toString();
                                    String item_name = ((JSONObject) items.get(i)).get("item_name").toString();
                                    String long_description = ((JSONObject) items.get(i)).get("long_description").toString();
                                    String image = ((JSONObject) items.get(i)).get("image").toString();
                                    String sku = ((JSONObject) items.get(i)).get("sku").toString();
                                    String srp = ((JSONObject) items.get(i)).get("srp").toString();
                                    String toque = ((JSONObject) items.get(i)).get("toque").toString();
                                    String quantity = ((JSONObject) items.get(i)).get("quantity").toString();
                                    String merchant_id = ((JSONObject) items.get(i)).get("merchant_id").toString();
                                    String rating = ((JSONObject) items.get(i)).get("rating").toString();
                                    String likes = ((JSONObject) items.get(i)).get("likes").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();


                                    hModel.add(new RVHomeModel(item_id, code_id, category_id, category, item_name, long_description, image, sku, srp, toque, quantity, merchant_id, rating, likes, status, date_created));

                                }


                                hAdapter = new RVSearchItemsAdapter(SearchItemsActivity.this, hModel);
                                rvItems.setHasFixedSize(true);
                                rvItems.setLayoutManager(new GridLayoutManager(SearchItemsActivity.this, 2));
                                rvItems.setAdapter(hAdapter);


//                                if(ilModel.isEmpty()){
//                                    llNoItemsYet.setVisibility(View.VISIBLE);
//                                }else{
//                                    llNoItemsYet.setVisibility(View.GONE);
//                                }

                                hAdapter.setOnItemClickListener(i -> {
                                    RVHomeModel clickedItem = hModel.get(i);

                                    Intent intent = new Intent(SearchItemsActivity.this, ItemDetailsActivity.class);
                                    intent.putExtra("item_id", clickedItem.getItemID());
                                    intent.putExtra("item_name", clickedItem.getItemName());
                                    intent.putExtra("item_amount", clickedItem.getSrp());
                                    //      intent.putExtra("item_rating", clickedItem.getItemRating());
                                    intent.putExtra("item_image", clickedItem.getImage());
                                    intent.putExtra("description", clickedItem.getLongDescription());
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

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                case R.id.edt_search_items:
                    hAdapter.getFilter().filter(edtSearchItems.getText().toString());
                    break;

                case R.id.edt_search:
                    hAdapter.getFilter().filter(edtSearch.getText().toString());
                    break;

            }
        }
    }

    public void search(String query){
        hAdapter.getFilter().filter(query);
    }


}