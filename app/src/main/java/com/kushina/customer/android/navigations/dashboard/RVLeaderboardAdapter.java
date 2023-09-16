package com.kushina.customer.android.navigations.dashboard;

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

public class RVLeaderboardAdapter extends RecyclerView.Adapter<RVLeaderboardAdapter.mViewHolder> {

    private Context mContext;
    private List<RVLeaderboardModel> rvModel;
    private RVLeaderboardAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVLeaderboardAdapter(Context mContext, List<RVLeaderboardModel> rvModel) {
        this.mContext = mContext;
        this.rvModel = rvModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVLeaderboardAdapter.OnItemClickListener listener){
        mListener = listener;
    }




    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_ranking_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVLeaderboardModel item = rvModel.get(position);


        holder.tvName.setText(item.getName());
        holder.tvRank.setText("Ranking: "+(item.getRank()));
        holder.tvAmount.setText(mGlobals.moneyFormatter(item.getAmount()));

    }

    @Override
    public int getItemCount() {
        return rvModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        TextView tvName,tvRank,tvAmount;

        public mViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_ranking_name);
            tvRank = itemView.findViewById(R.id.tv_ranking_rank);
            tvAmount = itemView.findViewById(R.id.tv_ranking_amount);



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
