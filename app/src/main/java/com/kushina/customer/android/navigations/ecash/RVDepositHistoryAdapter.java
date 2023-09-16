package com.kushina.customer.android.navigations.ecash;

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

public class RVDepositHistoryAdapter extends RecyclerView.Adapter<RVDepositHistoryAdapter.mViewHolder> {

    private Context mContext;
    private List<RVDepositHistoryModel> rvModel;
    private RVDepositHistoryAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVDepositHistoryAdapter(Context mContext, List<RVDepositHistoryModel> rvModel) {
        this.mContext = mContext;
        this.rvModel = rvModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVDepositHistoryAdapter.OnItemClickListener listener){
        mListener = listener;
    }


    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_deposit_history_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVDepositHistoryModel item = rvModel.get(position);


        holder.tvStatus.setText(item.getStatus());
        holder.tvDepositHistoryID.setText(item.getDepositID());
        holder.tvDepositMethod.setText(item.getDepositMethod());
        holder.tvAmount.setText(mGlobals.moneyFormatter(item.getAmount()));
        holder.tvDate.setText(mGlobals.dateFormatter(item.getDate_submitted()));

        if(item.getStatus().toLowerCase().equals("approved")){
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorSuccess));
        }else{
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorError));
        }

    }

    @Override
    public int getItemCount() {
        return rvModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {


        TextView tvStatus,tvDepositHistoryID,tvDepositMethod,tvAmount,tvDate;

        public mViewHolder(View itemView) {
            super(itemView);

            tvStatus = itemView.findViewById(R.id.tv_deposit_history_status);
            tvDepositHistoryID = itemView.findViewById(R.id.tv_deposit_history_deposit_id);
            tvDepositMethod = itemView.findViewById(R.id.tv_deposit_history_deposit_method);
            tvAmount = itemView.findViewById(R.id.tv_deposit_history_amount);
            tvDate = itemView.findViewById(R.id.tv_deposit_history_date);



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
