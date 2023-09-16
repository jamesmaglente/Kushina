package com.kushina.customer.android.navigations.my_team;

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
import com.kushina.customer.android.navigations.dashboard.RVLeaderboardAdapter;
import com.kushina.customer.android.navigations.dashboard.RVLeaderboardModel;

import java.util.List;

public class RVMyTeamAdapter extends RecyclerView.Adapter<RVMyTeamAdapter.mViewHolder> {

    private Context mContext;
    private List<RVMyTeamModel> rvModel;
    private RVMyTeamAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVMyTeamAdapter(Context mContext, List<RVMyTeamModel> rvModel) {
        this.mContext = mContext;
        this.rvModel = rvModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVMyTeamAdapter.OnItemClickListener listener){
        mListener = listener;
    }



    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_my_team_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVMyTeamModel item = rvModel.get(position);


        holder.tvStatus.setText(item.getStatus());
        holder.tvName.setText(item.getFirstName()+" "+item.getLastName());
        holder.tvMobile.setText(item.getMobile());
        holder.tvTotalPurchased.setText(mGlobals.moneyFormatter(item.getTotal_purchased()));

        if(item.getStatus().toLowerCase().equals("active")){
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

        TextView tvStatus,tvName,tvMobile,tvTotalPurchased;

        public mViewHolder(View itemView) {
            super(itemView);

            tvStatus = itemView.findViewById(R.id.tv_my_team_status);
            tvName = itemView.findViewById(R.id.tv_my_team_name);
            tvMobile = itemView.findViewById(R.id.tv_my_team_number);
            tvTotalPurchased = itemView.findViewById(R.id.tv_my_team_total_purchased);



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
