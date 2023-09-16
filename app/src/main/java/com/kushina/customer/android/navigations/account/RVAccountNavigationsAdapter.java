package com.kushina.customer.android.navigations.account;

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

public class RVAccountNavigationsAdapter extends RecyclerView.Adapter<RVAccountNavigationsAdapter.mViewHolder> {

    private Context mContext;
    private List<RVAccountNavigationsModel> avModel;
    private RVAccountNavigationsAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVAccountNavigationsAdapter(Context mContext, List<RVAccountNavigationsModel> avModel) {
        this.mContext = mContext;
        this.avModel = avModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVAccountNavigationsAdapter.OnItemClickListener listener){
        mListener = listener;
    }




    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_account_navigations_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {


        RVAccountNavigationsModel item = avModel.get(position);

        holder.tvNavigationTitle.setText(item.getNavigationTitle());
        holder.ivNavigationPicture.setImageResource(item.getNavigationIcon());

    }

    @Override
    public int getItemCount() {
        return avModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        ImageView ivNavigationPicture;
        TextView tvNavigationTitle;
        ImageView ivArrowRight;

        public mViewHolder(View itemView) {
            super(itemView);

            ivNavigationPicture = itemView.findViewById(R.id.iv_navigation_icon);
            tvNavigationTitle = itemView.findViewById(R.id.tv_navigation_title);
            ivArrowRight = itemView.findViewById(R.id.iv_arrow_right);



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
