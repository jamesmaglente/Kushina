package com.kushina.customer.android.navigations.home;

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

public class RVHomeAdapter extends RecyclerView.Adapter<RVHomeAdapter.mViewHolder> {


    private Context mContext;
    private List<RVHomeModel> hModel;
    private RVHomeAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVHomeAdapter(Context mContext, List<RVHomeModel> hModel) {
        this.mContext = mContext;
        this.hModel = hModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVHomeAdapter.OnItemClickListener listener){
        mListener = listener;
    }



    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_home_products_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVHomeModel item = hModel.get(position);

      //  mGlobals.log("{PIC",item.getItemPictureLink());

        try {
            Picasso.get()
                  //  .load(ITEMS_URL+item.getItemPictureLink())
                    .load(item.getImage())
                    //.load(item.getItemImage())
                    .resize(200,200)
                    .placeholder(R.drawable.applogo)
                    .into(holder.ivItemPicture);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tvItemName.setText(item.getItemName());
        holder.tvItemPrice.setText(mGlobals.moneyFormatter(item.getSrp()));

    }

    @Override
    public int getItemCount() {
        return hModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        ImageView ivItemPicture;
        TextView tvItemName,tvItemPrice;

        public mViewHolder(View itemView) {
            super(itemView);

            ivItemPicture = itemView.findViewById(R.id.iv_home_product_picture);
            tvItemName = itemView.findViewById(R.id.tv_home_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_home_item_price);



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
