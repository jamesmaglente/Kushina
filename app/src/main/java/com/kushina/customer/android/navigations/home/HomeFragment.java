package com.kushina.customer.android.navigations.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.MainActivity;
import com.kushina.customer.android.navigations.dashboard.RVTitleParentAdapter;
import com.kushina.customer.android.navigations.dashboard.RVTitleParentModel;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_SHOP;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    Unbinder unbinder;

    List<RVHomeModel> hModel;
    List<RVTitleParentModel> rvParentModel;

    @BindView(R.id.carousel)
    CarouselView carousel;
    @BindView(R.id.btn_home_see_more)
    TextView btnHomeSeeMore;
    @BindView(R.id.rv_home_products)
    RecyclerView rvHomeProducts;
    @BindView(R.id.rl_main_layout)
    RelativeLayout rlMainLayout;

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.btn_home_fragment_search)
    TextInputEditText btnHomeFragmentSearch;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mAPI = new API(getActivity());
        mGlobals = new Globals(getActivity());
        mPreferences = new Preferences(getActivity());

//        mGlobals.showLoadingDialog();
        loadCarousel();
        loadProducts();

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("");

    }

    @OnClick(R.id.btn_home_fragment_search)
    public void onViewClicked() {
        Intent intent = new Intent(getActivity(),SearchItemsActivity.class);
        startActivity(intent);
    }

    private void loadProducts() {

        rvParentModel = new ArrayList<>();
        rvParentModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());

        mAPI.api_request("POST",
                API_NODE_SHOP + "getAllItemsByCategories",
                request_data,
                true,
                getActivity(),
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
                                JSONArray categories = data_array.getJSONArray("categories");

                                for (int j = 0; j < categories.length(); j++) {

                                    String category = ((JSONObject) categories.get(j)).get("category").toString();
                                    JSONArray items = ((JSONObject) categories.get(j)).getJSONArray("items");
                                    hModel = new ArrayList<>();
                                    hModel.clear();

                                    for (int i = 0; i < items.length(); i++) {

                                        String item_id = ((JSONObject) items.get(i)).get("item_id").toString();
                                        String code_id = ((JSONObject) items.get(i)).get("code_id").toString();
                                        String category_id = ((JSONObject) items.get(i)).get("category_id").toString();
                                        String itemCategory = ((JSONObject) items.get(i)).get("category").toString();
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


                                        hModel.add(new RVHomeModel(item_id, code_id, category_id, itemCategory, item_name, long_description, image, sku, srp, toque, quantity, merchant_id, rating, likes, status, date_created));

                                    }

                                    rvParentModel.add(new RVTitleParentModel(category, hModel));

                                }


                                rvHomeProducts.setHasFixedSize(true);
                                rvHomeProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
                                RVTitleParentAdapter hAdapter = new RVTitleParentAdapter(getActivity(), rvParentModel);
                                rvHomeProducts.setAdapter(hAdapter);

                                //     mGlobals.dismissLoadingDialog();


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

    private void loadCarousel() {
        String[] mImagesTitle = new String[]{"Image 1", "Image 2", "Image 3", "Image 4", "Image 5"};
        CarouselView carouselView;
        carouselView = getView().findViewById(R.id.carousel);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
//                imageView.setImageURI(mImages[position]);
                String url = "";
//                mAPI.api_request();
                switch (position) {
                    case 0:
                        url = "https://i.imgur.com/LbUn2wz.png";
                        break;
                    case 1:
                        url = "https://i.imgur.com/3Qkfv41.jpg";
                        break;
                    case 2:
                        url = "https://i.imgur.com/iAfgoY7.jpg";
                        break;
                    case 3:
                        url = "http://tacticalcodes.xyz/images/asianpalate/siomai3.jpg";
                        break;

                }
                Picasso.get()
                        .load(url)
                        .resize(500, 300)
                        .placeholder(R.drawable.applogo)
                        .into(imageView);
            }
        });
        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(getActivity(), mImagesTitle[position], Toast.LENGTH_SHORT).show();
            }
        });
        carouselView.setPageCount(4);
    }


}
