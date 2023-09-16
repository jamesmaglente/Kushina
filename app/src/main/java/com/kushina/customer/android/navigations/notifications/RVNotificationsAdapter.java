package com.kushina.customer.android.navigations.notifications;

import android.content.Context;
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

import java.util.List;

public class RVNotificationsAdapter extends RecyclerView.Adapter<RVNotificationsAdapter.mViewHolder> {

    private Context mContext;
    private List<RVNotificationsModel> rvModel;
    private RVNotificationsAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVNotificationsAdapter(Context mContext, List<RVNotificationsModel> rvModel) {
        this.mContext = mContext;
        this.rvModel = rvModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVNotificationsAdapter.OnItemClickListener listener){
        mListener = listener;
    }


    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_notifications_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVNotificationsModel item = rvModel.get(position);

        try {
            Picasso.get()
                    .load(item.getImage())
                    //.load(item.getItemImage())
                    .resize(100,100)
                    .placeholder(R.drawable.applogo)
                    .into(holder.ivNotificationImage);
        }catch (Exception e){
            mGlobals.log("Notif",e.toString());
        }




        holder.tvNotificationTitle.setText(item.getTitle());
        holder.tvNotificationDescription.setText(item.getDescription());
        holder.tvNotificationsDate.setText(item.getDateCreated());



    }

    @Override
    public int getItemCount() {
        return rvModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        ImageView ivNotificationImage;
        TextView tvNotificationTitle,tvNotificationDescription,tvNotificationsDate;

        public mViewHolder(View itemView) {
            super(itemView);

            ivNotificationImage = itemView.findViewById(R.id.iv_notifications_image);
            tvNotificationTitle = itemView.findViewById(R.id.tv_notifications_title);
            tvNotificationDescription = itemView.findViewById(R.id.tv_notifications_description);
            tvNotificationsDate = itemView.findViewById(R.id.tv_notifications_date);



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
}
