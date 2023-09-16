package com.kushina.customer.android.navigations.home;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


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


public class RVReviewsAdapter extends RecyclerView.Adapter<RVReviewsAdapter.mViewHolder> {

    private Context mContext;
    private List<RVReviewsModel> rsModel;
    private RVReviewsAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;
    Preferences mPreferences;


    public RVReviewsAdapter(Context mContext, List<RVReviewsModel> rsModel) {
        this.mContext = mContext;
        this.rsModel = rsModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVReviewsAdapter.OnItemClickListener listener){
        mListener = listener;
    }



    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_item_reviews_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        mPreferences = new Preferences(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVReviewsModel item = rsModel.get(position);

        try {
            Picasso.get()
                    .load(item.getReviewerImage())
                    .resize(100,100)
                    .placeholder(R.drawable.ic_person_black_24dp)
                    .into(holder.ivReview);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tvName.setText(item.getReviewerName());
        holder.tvDate.setText(mGlobals.dateFormatter(item.getDateCreated()));
        holder.tvReview.setText(item.getReview());

        holder.rbRating.setRating(Float.valueOf(item.getRating()));

        if(item.getUserID().equals(mPreferences.getUserId().toString())){
            holder.ivRemove.setVisibility(View.VISIBLE);
            holder.tvClick.setVisibility(View.VISIBLE);
        }else{
            holder.ivRemove.setVisibility(View.GONE);
            holder.tvClick.setVisibility(View.GONE);
        }

        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGlobals.showChoiceDialog("Remove this review?", true, new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        if(result){
                            removeReview(item.getItemRatingID());
                        }
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return rsModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        ImageView ivReview,ivRemove;
        RatingBar rbRating;
        TextView tvName,tvReview,tvDate,tvClick;

        public mViewHolder(View itemView) {
            super(itemView);

            ivReview = itemView.findViewById(R.id.iv_item_reviews_image);
            ivRemove = itemView.findViewById(R.id.iv_item_reviews_remove);
            rbRating = itemView.findViewById(R.id.rb_item_reviews_rating);
            tvName = itemView.findViewById(R.id.tv_item_reviews_name);
            tvReview = itemView.findViewById(R.id.tv_item_review);
            tvDate = itemView.findViewById(R.id.tv_item_reviews_date);
            tvClick = itemView.findViewById(R.id.tv_item_reviews_click);


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

    private void removeReview(String itemRatingID){

        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("item_rating_id", itemRatingID);




        mAPI.api_request("POST",
                API_NODE_SHOP + "removeReview",
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


                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
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
