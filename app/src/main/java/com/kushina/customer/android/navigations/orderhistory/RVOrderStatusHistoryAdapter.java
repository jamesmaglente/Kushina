package com.kushina.customer.android.navigations.orderhistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;

import java.util.List;

public class RVOrderStatusHistoryAdapter extends RecyclerView.Adapter<RVOrderStatusHistoryAdapter.mViewHolder> {

    private Context mContext;
    private List<RVOrderStatusHistoryModel> rvModel;
    private RVOrderStatusHistoryAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVOrderStatusHistoryAdapter(Context mContext, List<RVOrderStatusHistoryModel> rvModel) {
        this.mContext = mContext;
        this.rvModel = rvModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVOrderStatusHistoryAdapter.OnItemClickListener listener){
        mListener = listener;
    }




    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_order_status_history_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVOrderStatusHistoryModel item = rvModel.get(position);


        holder.tvOrderStatusHistoryStatus.setText(item.getToStatus());
        holder.tvOrderStatusHistoryDate.setText(mGlobals.dateFormatter(item.getDateCreated()));

        if(!item.getRemarks().equals("") && !item.getRemarks().equals("null")) {
            holder.tvOrderStatusRemarks.setText("Remarks: " + item.getRemarks());
        }else{
            holder.tvOrderStatusRemarks.setText("Remarks: None ");
        }


        if(position == 0){
            holder.llLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorLightGreen));
        }else{
            holder.llLayout.setBackgroundColor(0);
        }

    }

    @Override
    public int getItemCount() {
        return rvModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderStatusHistoryStatus, tvOrderStatusHistoryDate,tvOrderStatusRemarks;
        CardView llLayout;

        public mViewHolder(View itemView) {
            super(itemView);

            tvOrderStatusHistoryStatus = itemView.findViewById(R.id.tv_order_status_history_status);
            tvOrderStatusHistoryDate = itemView.findViewById(R.id.tv_order_status_history_date);
            tvOrderStatusRemarks = itemView.findViewById(R.id.tv_order_status_history_remarks);
            llLayout = itemView.findViewById(R.id.ll_layout_bg);



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
