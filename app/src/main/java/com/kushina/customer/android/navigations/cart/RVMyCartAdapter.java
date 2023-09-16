package com.kushina.customer.android.navigations.cart;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_SHOP;

public class RVMyCartAdapter extends RecyclerView.Adapter<RVMyCartAdapter.mViewHolder> {

    private Context mContext;
    private List<RVMyCartModel> mcModel;
    private RVMyCartAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVMyCartAdapter(Context mContext, List<RVMyCartModel> mcModel) {
        this.mContext = mContext;
        this.mcModel = mcModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVMyCartAdapter.OnItemClickListener listener){
        mListener = listener;
    }


    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_my_cart_row_items, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVMyCartModel item = mcModel.get(position);

        try {
            Picasso.get()
                //    .load(ITEMS_URL+item.getItemPicture())
                    .load(item.getItemPicture())
                    //.load(item.getItemImage())
                    .resize(200,200)
                    .placeholder(R.drawable.applogo)
                    .into(holder.ivItemPicture);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tvItemName.setText(item.getItemName());
        String discount = "<font color='#EE0000'>" + "-" + mGlobals.moneyFormatter(item.getDiscount()) + "</font>";
        holder.tvItemPrice.setText(mGlobals.moneyFormatter(item.getItemPrice()));
        holder.tvItemQTY.setText("x"+item.getItemQTY());
        holder.tvItemTotal.setText(Html.fromHtml(mGlobals.moneyFormatter(item.getTotalAmount())+" ("+discount+")"));

        holder.ivRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGlobals.showChoiceDialog("Remove " + item.getItemName() + " in your cart?", true, new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        if(result){
                            removeItem(item.getCartItemID());
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mcModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        ImageView ivItemPicture,ivRemoveItem;
        TextView tvItemName,tvItemPrice,tvItemQTY,tvItemTotal;

        public mViewHolder(View itemView) {
            super(itemView);

            ivItemPicture = itemView.findViewById(R.id.iv_my_cart_item_picture);
            ivRemoveItem = itemView.findViewById(R.id.iv_my_cart_remove_item);
            tvItemName = itemView.findViewById(R.id.tv_my_cart_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_my_cart_item_price);
            tvItemQTY = itemView.findViewById(R.id.tv_my_cart_item_qty);
            tvItemTotal = itemView.findViewById(R.id.tv_my_cart_total);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    private void removeItem(String cartItemID){

        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("cart_item_id", cartItemID);



        mAPI.api_request("POST",
                API_NODE_SHOP+"removeCartItem",
                request_data,
                true,
                mContext,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log("Remove Item", String.valueOf(result));
                        try {
                            // parse response object
                            JSONObject jsonObject = result.getJSONObject("data");
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if(result){
                                            Activity activity =  (Activity) mContext;
                                            activity.recreate();
                                        }
                                    }
                                });



                            } else {
                                mGlobals.log("CreateBooking", "onResponseCallback: " + status_message);
                                mGlobals.log("CreateBooking()", status_message);
                            }

                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log("CreateBooking()", e.toString());
                        }

                    }
                });
    }

}
