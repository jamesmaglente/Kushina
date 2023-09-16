package com.kushina.customer.android.navigations.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_SHOP;


public class RVFavoritesAdapter extends RecyclerView.Adapter<RVFavoritesAdapter.mViewHolder> {


    private Context mContext;
    private List<RVFavoritesModel> fModel;
    private RVFavoritesAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;
    Preferences mPreferences;
    String orderDetID;


    public RVFavoritesAdapter(Context mContext, List<RVFavoritesModel> fModel) {
        this.mContext = mContext;
        this.fModel = fModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVFavoritesAdapter.OnItemClickListener listener){
        mListener = listener;
    }



    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_favorites_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        mPreferences = new Preferences(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVFavoritesModel item = fModel.get(position);

        try {
            Picasso.get()
                        .load(item.getItemImage())

                    //.load(item.getItemImage())
                    .resize(300,300)
                    .placeholder(R.drawable.applogo)
                    .into(holder.ivItemImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tvItemName.setText(item.getItemName());
        holder.tvItemPrice.setText(mGlobals.moneyFormatter(item.getItemAmount()));

        holder.ivAddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGlobals.toast(item.getItemName() +" was added to your cart");
            }
        });

        holder.ivRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGlobals.showChoiceDialog("Are you sure you want to remove " + item.getItemName() + " in your favorites?", true, new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        if(result){
                            removeInFavorites(item.getItemID());
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return fModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        ImageView ivItemImage,ivRemoveButton;
        Button ivAddtoCart;
        TextView tvItemName,tvItemPrice;

        public mViewHolder(View itemView) {
            super(itemView);

            ivItemImage = itemView.findViewById(R.id.iv_favorites_item_image);
            ivAddtoCart = itemView.findViewById(R.id.btn_favorites_add_to_cart);
            ivRemoveButton = itemView.findViewById(R.id.btn_favorites_remove);
            tvItemName = itemView.findViewById(R.id.tv_favorites_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_favorites_item_price);

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

    private void removeInFavorites(String itemID){

        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("item_id", itemID);


        mAPI.api_request("POST",
                API_NODE_SHOP + "toggleFavorite",
                request_data,
                true,
                mContext,
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


                                mGlobals.showSuccessDialog("Item successfully removed in favorites.", true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {
                                            Activity activity = (Activity) mContext;
                                            activity.recreate();
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
