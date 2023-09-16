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

public class RVRecentTransactionsAdapter extends RecyclerView.Adapter<RVRecentTransactionsAdapter.mViewHolder> {

    private Context mContext;
    private List<RVTransactionHistoryModel> thModel;
    private RVRecentTransactionsAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVRecentTransactionsAdapter(Context mContext, List<RVTransactionHistoryModel> thModel) {
        this.mContext = mContext;
        this.thModel = thModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVRecentTransactionsAdapter.OnItemClickListener listener){
        mListener = listener;
    }


    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_recent_transactions_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVTransactionHistoryModel item = thModel.get(position);


        holder.tvTransactionType.setText(item.getTransactionType());
        holder.tvDate.setText(item.getDate());


        if(item.getType().toLowerCase().equals("credit")){
            holder.tvAmount.setTextColor(mContext.getResources().getColor(R.color.colorSuccess));
            holder.tvAmount.setText("+"+mGlobals.moneyFormatter(item.getAmountCredited()));
        }else {
            holder.tvAmount.setTextColor(mContext.getResources().getColor(R.color.colorError));
            holder.tvAmount.setText("-"+mGlobals.moneyFormatter(item.getAmountDebited()));
        }

    }

    @Override
    public int getItemCount() {
        return thModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {


        TextView tvTransactionType,tvDate,tvAmount;

        public mViewHolder(View itemView) {
            super(itemView);

            tvTransactionType = itemView.findViewById(R.id.tv_recent_transactions_type);
            tvDate = itemView.findViewById(R.id.tv_recent_transactions_date);
            tvAmount = itemView.findViewById(R.id.tv_recent_transactions_amount);



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
