package com.kushina.customer.android.navigations.ecash;

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

public class RVDepositMethodsAdapter extends RecyclerView.Adapter<RVDepositMethodsAdapter.mViewHolder> {

    private Context mContext;
    private List<RVDepositMethodsModel> avModel;
    private RVDepositMethodsAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVDepositMethodsAdapter(Context mContext, List<RVDepositMethodsModel> avModel) {
        this.mContext = mContext;
        this.avModel = avModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVDepositMethodsAdapter.OnItemClickListener listener){
        mListener = listener;
    }




    @NonNull
    @Override
    public RVDepositMethodsAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_deposit_methods_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new RVDepositMethodsAdapter.mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVDepositMethodsAdapter.mViewHolder holder, int position) {


        RVDepositMethodsModel item = avModel.get(position);

        holder.tvDepositMethodTitle.setText(item.getDepositMethodTitle());
        holder.ivDepositMethodIcon.setImageResource(item.getDepositMethodIcon());


    }

    @Override
    public int getItemCount() {
        return avModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        ImageView ivDepositMethodIcon;
        TextView tvDepositMethodTitle;

        public mViewHolder(View itemView) {
            super(itemView);

            ivDepositMethodIcon = itemView.findViewById(R.id.iv_deposit_icon);
            tvDepositMethodTitle = itemView.findViewById(R.id.tv_deposit_title);



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