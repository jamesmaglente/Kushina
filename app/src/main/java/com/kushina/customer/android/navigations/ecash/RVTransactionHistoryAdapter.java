package com.kushina.customer.android.navigations.ecash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;

import java.util.List;

public class RVTransactionHistoryAdapter extends RecyclerView.Adapter<RVTransactionHistoryAdapter.mViewHolder> {


    private Context mContext;
    private List<RVTransactionHistoryModel> thModel;
    private RVTransactionHistoryAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVTransactionHistoryAdapter(Context mContext, List<RVTransactionHistoryModel> thModel) {
        this.mContext = mContext;
        this.thModel = thModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVTransactionHistoryAdapter.OnItemClickListener listener){
        mListener = listener;
    }




    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_ecash_transaction_history_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVTransactionHistoryModel item = thModel.get(position);


        holder.tvTransactionType.setText(item.getTransactionType());
        holder.tvAmountCredited.setText(mGlobals.moneyFormatter(item.getAmountCredited()));
        holder.tvAmountDebited.setText(mGlobals.moneyFormatter(item.getAmountDebited()));
        holder.tvFrom.setText(item.getFrom());
        holder.tvTax.setText(mGlobals.moneyFormatter(item.getTax()));
        holder.tvOrderID.setText(item.getOrderID());
        holder.tvDate.setText(mGlobals.dateFormatter(item.getDate()));

        if(item.getClaimed().equals("1")){
            holder.btnClaim.setVisibility(View.GONE);
        }else {
            holder.btnClaim.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return thModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {


        TextView tvTransactionType,tvAmountCredited,tvAmountDebited,tvFrom,tvTax,tvOrderID,tvDate;
        LinearLayout btnClaim;

        public mViewHolder(View itemView) {
            super(itemView);

            tvTransactionType = itemView.findViewById(R.id.tv_transaction_history_type);
            tvAmountCredited = itemView.findViewById(R.id.tv_transaction_history_credited);
            tvAmountDebited = itemView.findViewById(R.id.tv_transaction_history_debited);
            tvFrom = itemView.findViewById(R.id.tv_transaction_history_from);
            tvTax = itemView.findViewById(R.id.tv_transaction_history_tax);
            tvOrderID = itemView.findViewById(R.id.tv_transaction_history_order_id);
            tvDate = itemView.findViewById(R.id.tv_transaction_history_date);
            btnClaim = itemView.findViewById(R.id.btn_claim_rewards);



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
