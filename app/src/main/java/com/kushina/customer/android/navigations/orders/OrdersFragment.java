package com.kushina.customer.android.navigations.orders;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.MainActivity;
import com.kushina.customer.android.navigations.account.RVAccountNavigationsAdapter;
import com.kushina.customer.android.navigations.account.RVAccountNavigationsModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_ORDERS;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {

    public final String TAG = getClass().getSimpleName();

    Unbinder unbinder;
    Globals mGlobals;
    API mAPI;
    Preferences mPreferences;

    @BindView(R.id.rv_order_navigations)
    RecyclerView rvOrderNavigations;
    List<RVAccountNavigationsModel> avModel;
    @BindView(R.id.tv_ecash_total_ecash)
    TextView tvEcashTotalEcash;
    @BindView(R.id.tv_waiting_to_serve)
    TextView tvWaitingToServe;
    @BindView(R.id.tv_serving)
    TextView tvServing;
    @BindView(R.id.tv_cancelled)
    TextView tvCancelled;
    @BindView(R.id.tv_cooking)
    TextView tvCooking;
    @BindView(R.id.tv_served)
    TextView tvServed;
    @BindView(R.id.ll_top_card)
    CardView llTopCard;
    @BindView(R.id.rl_main_layout)
    RelativeLayout rlMainLayout;
    @BindView(R.id.tv_total_overall_orders)
    TextView tvTotalOverallOrders;

//    @BindView(R.id.tabLayout) TabLayout tabLayout;
//    @BindView(R.id.viewpager) ViewPager2 viewpager;

    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mGlobals = new Globals(getActivity());
        mAPI = new API(getActivity());
        mPreferences = new Preferences(getActivity());

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("");

        loadMyOrdersSummary();
        loadOrderNavigations();

//        viewpager.setAdapter(new OrdersViewPager(getChildFragmentManager(), getLifecycle()));
//
//        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
//                tabLayout, viewpager, new TabLayoutMediator.TabConfigurationStrategy() {
//            @Override
//            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//
//                switch (position) {
//                    case 0: {
//                        tab.setText("Waiting to Serve");
//                        break;
//                    }
//                    case 1: {
//                        tab.setText("Cooking");
//                        break;
//                    }
//                    case 2: {
//                        tab.setText("Serving");
//                        break;
//                    }
//                    case 3: {
//                        tab.setText("Served");
//                        break;
//                    }
//                    case 4: {
//                        tab.setText("Cancelled");
//                        break;
//                    }
//
//                }
//            }
//        }
//        );
//        tabLayoutMediator.attach();
//
//        setHasOptionsMenu(true);
    }

    private void loadMyOrdersSummary() {

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());

        mAPI.api_request("POST",
                API_NODE_ORDERS + "getMyOrdersSummary",
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
                                String total_overall_orders = data_array.getString("total_overall_orders");
                                String total_pending = data_array.getString("total_pending");
                                String total_cooking = data_array.getString("total_cooking");
                                String total_shipping = data_array.getString("total_shipping");
                                String total_delivered = data_array.getString("total_delivered");
                                String total_cancelled = data_array.getString("total_cancelled");


                                tvTotalOverallOrders.setText(total_overall_orders);
                                tvWaitingToServe.setText(total_pending);
                                tvCooking.setText(total_cooking);
                                tvServing.setText(total_shipping);
                                tvServed.setText(total_delivered);
                                tvCancelled.setText(total_cancelled);


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

    private void loadOrderNavigations() {

        avModel = new ArrayList<>();
        avModel.clear();

        avModel.add(new RVAccountNavigationsModel(R.drawable.ic_order_waiting, "Waiting to Serve"));
        avModel.add(new RVAccountNavigationsModel(R.drawable.ic_order_cooking, "Cooking"));
        avModel.add(new RVAccountNavigationsModel(R.drawable.ic_order_serving_deliver, "Serving"));
        avModel.add(new RVAccountNavigationsModel(R.drawable.ic_order_completed_2, "Served"));
        avModel.add(new RVAccountNavigationsModel(R.drawable.ic_order_cancelled, "Cancelled"));
//        avModel.add(new RVAccountNavigationsModel(R.drawable.ic_return, "Returned"));

        rvOrderNavigations.setHasFixedSize(true);
        rvOrderNavigations.setLayoutManager(new LinearLayoutManager(getActivity()));
        RVAccountNavigationsAdapter avAdapter = new RVAccountNavigationsAdapter(getActivity(), avModel);
        rvOrderNavigations.setAdapter(avAdapter);


        avAdapter.setOnItemClickListener(new RVAccountNavigationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent;
                RVAccountNavigationsModel clickedItem = avModel.get(position);

                if (clickedItem.getNavigationTitle().toLowerCase().contains("waiting to serve")) {
                    intent = new Intent(getActivity(), OrdersActivity.class);
                    intent.putExtra("status_id", 8);
                    intent.putExtra("title", "Waiting to Serve");
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().contains("cooking")) {
                    intent = new Intent(getActivity(), OrdersActivity.class);
                    intent.putExtra("status_id", 9);
                    intent.putExtra("title", "Cooking");
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().contains("serving")) {
                    intent = new Intent(getActivity(), OrdersActivity.class);
                    intent.putExtra("status_id", 10);
                    intent.putExtra("title", "Serving");
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().contains("served")) {
                    intent = new Intent(getActivity(), OrdersActivity.class);
                    intent.putExtra("status_id", 11);
                    intent.putExtra("title", "Served");
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().contains("cancelled")) {
                    intent = new Intent(getActivity(), OrdersActivity.class);
                    intent.putExtra("status_id", 22);
                    intent.putExtra("title", "Cancelled");
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().contains("returned")) {
                    intent = new Intent(getActivity(), OrdersActivity.class);
                    intent.putExtra("status_id", 22);
                    intent.putExtra("title", "Returned");
                    startActivity(intent);
                }


            }
        });

    }

}
