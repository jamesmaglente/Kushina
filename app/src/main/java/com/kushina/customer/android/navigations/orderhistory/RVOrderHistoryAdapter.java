package com.kushina.customer.android.navigations.orderhistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;

import java.util.List;

public class RVOrderHistoryAdapter extends RecyclerView.Adapter<RVOrderHistoryAdapter.mViewHolder> {


    private Context mContext;
    private List<RVOrderHistoryModel> rvModel;
    private RVOrderHistoryAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVOrderHistoryAdapter(Context mContext, List<RVOrderHistoryModel> rvModel) {
        this.mContext = mContext;
        this.rvModel = rvModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVOrderHistoryAdapter.OnItemClickListener listener){
        mListener = listener;
    }



    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_orders_history_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVOrderHistoryModel item = rvModel.get(position);


        holder.tvStatus.setText(item.getStatus());
        holder.tvOrderID.setText(item.getCodeID());
        holder.tvSentTo.setText(item.getAddressContact());
        holder.tvSentAt.setText(item.getAddressTitle());
        holder.tvTotalAmount.setText(mGlobals.moneyFormatter(item.getTotalAmount()));
        holder.tvPaymentOption.setText(item.getPaymentOption());
        holder.tvDate.setText(mGlobals.dateFormatter(item.getDateCreated()));

        if(item.getStatus().toLowerCase().equals("delivered")){
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorSuccess));
        }else if(item.getStatus().toLowerCase().equals("cancelled")){
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorError));
        }

    }

    @Override
    public int getItemCount() {
        return rvModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {


        TextView tvStatus,tvOrderID,tvSentTo,tvSentAt,tvTotalAmount,tvPaymentOption,tvDate;

        public mViewHolder(View itemView) {
            super(itemView);

            tvStatus = itemView.findViewById(R.id.tv_order_history_status);
            tvOrderID = itemView.findViewById(R.id.tv_order_history_order_id);
            tvSentTo = itemView.findViewById(R.id.tv_order_history_sent_to);
            tvSentAt = itemView.findViewById(R.id.tv_order_history_sent_at);
            tvTotalAmount = itemView.findViewById(R.id.tv_order_history_total_amount);
            tvPaymentOption = itemView.findViewById(R.id.tv_order_history_payment_option);
            tvDate = itemView.findViewById(R.id.tv_order_history_date);



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
