package com.kushina.customer.android.navigations.cart;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.addresses.AddressListActivity;

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

import static android.app.Activity.RESULT_CANCELED;
import static com.kushina.customer.android.globals.Endpoints.API_NODE_SHOP;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    Unbinder unbinder;
    List<RVMyCartModel> mcModel;
    @BindView(R.id.rv_my_cart)
    RecyclerView rvMyCart;
    @BindView(R.id.rl_main_layout)
    RelativeLayout rlMainLayout;
    @BindView(R.id.tv_cart_total_price)
    TextView tvCartTotalPrice;
    @BindView(R.id.btn_choose_delivery_address)
    LinearLayout btnChooseDeliveryAddress;
    @BindView(R.id.btn_choose_payment_method)
    LinearLayout btnChoosePaymentMethod;
    @BindView(R.id.tv_cart_delivery_address)
    TextView tvCartDeliveryAddress;
    @BindView(R.id.tv_cart_payment_method)
    TextView tvCartPaymentMethod;

    public static final int SELECT_PAYMENT_METHOD = 22222;
    public static final int SELECT_DELIVERY_ADDRESS = 33333;
    @BindView(R.id.btn_cart_checkout)
    Button btnCartCheckout;
    @BindView(R.id.ll_no_items_in_cart)
    LinearLayout llNoItemsInCart;
    @BindView(R.id.tv_cart_total_ecash)
    TextView tvCartTotalEcash;


    private String paymentOptionID, paymentOption, deliveryAddressID, deliveryAddress, cartID;


    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mAPI = new API(getActivity());
        mGlobals = new Globals(getActivity());
        mPreferences = new Preferences(getActivity());

        loadMyCart();

    }

    @OnClick({R.id.btn_choose_delivery_address, R.id.btn_choose_payment_method, R.id.btn_cart_checkout})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_choose_delivery_address:
                intent = new Intent(getActivity(), AddressListActivity.class);
                intent.putExtra("task","choose_address");
                startActivityForResult(intent, SELECT_DELIVERY_ADDRESS);
                break;
            case R.id.btn_choose_payment_method:
                intent = new Intent(getActivity(), ChoosePaymentOptionsActivity.class);
                startActivityForResult(intent, SELECT_PAYMENT_METHOD);
                break;
            case R.id.btn_cart_checkout:

                if (!mcModel.isEmpty()) {
                    if (deliveryAddressID != null) {
                        if (paymentOptionID != null) {
                            mGlobals.showChoiceDialog("Are you sure you want to checkout?", true, new Globals.Callback() {
                                @Override
                                public void onPickCallback(Boolean result) {
                                    if(result){
                                        checkout();
                                    }
                                }
                            });

                        } else {
                            mGlobals.showErrorMessage("Choose a payment method before checking out.", true, new Globals.Callback() {
                                @Override
                                public void onPickCallback(Boolean result) {
                                    if (result) {
                                        return;
                                    }
                                }
                            });
                        }

                    } else {
                        mGlobals.showErrorMessage("Choose an address before checking out.", true, new Globals.Callback() {
                            @Override
                            public void onPickCallback(Boolean result) {
                                if (result) {
                                    return;
                                }
                            }
                        });
                    }
                } else {
                    mGlobals.showErrorMessage("You don't have any items to checkout.", true, new Globals.Callback() {
                        @Override
                        public void onPickCallback(Boolean result) {
                            if (result) {
                                return;
                            }
                        }
                    });
                }
                break;
        }
    }

    private void checkout() {

        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("cart_id", cartID);
        request_data.put("payment_option_id", paymentOptionID);
        request_data.put("address_id", deliveryAddressID);


        mAPI.api_request("POST",
                API_NODE_SHOP + "checkout",
                request_data,
                true,
                getActivity(),
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
                                            getActivity().recreate();
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

    private void loadMyCart() {

        mcModel = new ArrayList<>();
        mcModel.clear();


        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());

        mAPI.api_request("POST",
                API_NODE_SHOP + "getMyCart",
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
                                cartID = data_array.getString("cart_id");
                                String cartTotalAmount = data_array.getString("total_amount");
                                String totalECash = data_array.getString("current_ecash");
                                JSONArray items = data_array.getJSONArray("items");

                                for (int i = 0; i < items.length(); i++) {

                                    String cart_item_id = ((JSONObject) items.get(i)).get("cart_item_id").toString();
                                    String item_id = ((JSONObject) items.get(i)).get("item_id").toString();
                                    String quantity = ((JSONObject) items.get(i)).get("quantity").toString();
                                    String remarks = ((JSONObject) items.get(i)).get("remarks").toString();
                                    String code_id = ((JSONObject) items.get(i)).get("code_id").toString();
                                    String current_stock = ((JSONObject) items.get(i)).get("current_stock").toString();
                                    String total_amount = ((JSONObject) items.get(i)).get("total_amount").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();
                                    String category_id = ((JSONObject) items.get(i)).get("category_id").toString();
                                    String category = ((JSONObject) items.get(i)).get("category").toString();
                                    String description = ((JSONObject) items.get(i)).get("description").toString();
                                    String long_description = ((JSONObject) items.get(i)).get("long_description").toString();
                                    String image = ((JSONObject) items.get(i)).get("image").toString();

                                    String sku = ((JSONObject) items.get(i)).get("sku").toString();
                                    String srp = ((JSONObject) items.get(i)).get("srp").toString();
                                    String discount = ((JSONObject) items.get(i)).get("discount").toString();
                                    String tax = ((JSONObject) items.get(i)).get("tax").toString();
                                    String likes = ((JSONObject) items.get(i)).get("likes").toString();
                                    String merchant_id = ((JSONObject) items.get(i)).get("merchant_id").toString();
                                    String date_created = ((JSONObject) items.get(i)).get("date_created").toString();


                                    mcModel.add(new RVMyCartModel(cart_item_id, item_id, quantity, remarks, code_id, current_stock, total_amount, status, category_id, category, description, long_description, image, sku,discount, srp, tax, likes, merchant_id, date_created));

                                }


                                rvMyCart.setHasFixedSize(true);
                                rvMyCart.setLayoutManager(new LinearLayoutManager(getActivity()));
                                RVMyCartAdapter mcAdapter = new RVMyCartAdapter(getActivity(), mcModel);
                                rvMyCart.setAdapter(mcAdapter);

                                tvCartTotalPrice.setText(mGlobals.moneyFormatter(cartTotalAmount));
                                tvCartTotalEcash.setText(mGlobals.moneyFormatter(totalECash));

                                if (mcModel.isEmpty()) {
                                    llNoItemsInCart.setVisibility(View.VISIBLE);
                                } else {
                                    llNoItemsInCart.setVisibility(View.GONE);
                                }


//
//                                mcAdapter.setOnItemClickListener(position -> {
//                                    RVMyCartModel clickedItem = mcModel.get(position);
//
//                                    Intent intent = new Intent(getActivity(), ItemDetailsActivity.class);
//                                    intent.putExtra("item_id", clickedItem.getItemID());
//                                    intent.putExtra("item_name", clickedItem.getItemName());
//                                    intent.putExtra("item_amount", clickedItem.getItemPrice());
//                                    //      intent.putExtra("item_rating", clickedItem.getItemRating());
//                                    intent.putExtra("item_image", clickedItem.getI());
//                                    //     intent.putExtra("description", clickedItem.getDesc());
//                                    startActivity(intent);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == SELECT_DELIVERY_ADDRESS) {
            deliveryAddressID = data.getStringExtra("delivery_address_id");
            deliveryAddress = data.getStringExtra("delivery_address");
            tvCartDeliveryAddress.setText(deliveryAddress);


        } else if (requestCode == SELECT_PAYMENT_METHOD) {
            paymentOptionID = data.getStringExtra("payment_option_id");
            paymentOption = data.getStringExtra("payment_option");
            tvCartPaymentMethod.setText(paymentOption);
        }
    }


}
