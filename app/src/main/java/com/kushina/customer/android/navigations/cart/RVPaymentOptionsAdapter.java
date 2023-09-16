package com.kushina.customer.android.navigations.cart;

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

import java.util.List;

public class RVPaymentOptionsAdapter extends RecyclerView.Adapter<RVPaymentOptionsAdapter.mViewHolder> {

    private Context mContext;
    private List<RVPaymentOptionsModel> rvModel;
    private RVPaymentOptionsAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVPaymentOptionsAdapter(Context mContext, List<RVPaymentOptionsModel> rvModel) {
        this.mContext = mContext;
        this.rvModel = rvModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVPaymentOptionsAdapter.OnItemClickListener listener){
        mListener = listener;
    }





    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_payment_options_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {


        RVPaymentOptionsModel item = rvModel.get(position);

//        try {
//            Picasso.get()
//                    .load("")
//                    //.load(item.getItemImage())
//                    .resize(100,100)
//                    .placeholder(R.drawable.splash)
//                    .into(holder.ivNavigationPicture);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        holder.tvPaymentOptionTitle.setText(item.getPaymentOption());

    }

    @Override
    public int getItemCount() {
        return rvModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPaymentOptionPicture;
        TextView tvPaymentOptionTitle;

        public mViewHolder(View itemView) {
            super(itemView);

            ivPaymentOptionPicture = itemView.findViewById(R.id.iv_payment_options_icon);
            tvPaymentOptionTitle = itemView.findViewById(R.id.tv_payment_options_title);



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
