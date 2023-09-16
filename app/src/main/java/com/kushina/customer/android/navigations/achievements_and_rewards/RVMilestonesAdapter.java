package com.kushina.customer.android.navigations.achievements_and_rewards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.navigations.account.RVAccountNavigationsAdapter;
import com.kushina.customer.android.navigations.account.RVAccountNavigationsModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RVMilestonesAdapter extends RecyclerView.Adapter<RVMilestonesAdapter.mViewHolder> {


    private Context mContext;
    private List<RVMilestonesModel> rvModel;
    private RVMilestonesAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVMilestonesAdapter(Context mContext, List<RVMilestonesModel> rvModel) {
        this.mContext = mContext;
        this.rvModel = rvModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVMilestonesAdapter.OnItemClickListener listener){
        mListener = listener;
    }




    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_milestones_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVMilestonesModel item = rvModel.get(position);

        holder.tvMilestoneName.setText(item.getMembership_type() + " (" + item.getDescription() + ")");
        holder.sbProgress.setEnabled(false);

        switch (item.getMembership_type().toLowerCase()){
            case "basic":
                holder.tvMilestoneImage.setImageResource(R.drawable.ic_medal_basic);
                break;
            case "bronze":
                holder.tvMilestoneImage.setImageResource(R.drawable.ic_medal_bronze);
                break;
            case "silver":
                holder.tvMilestoneImage.setImageResource(R.drawable.ic_medal_silver);
                break;
            case "gold":
                holder.tvMilestoneImage.setImageResource(R.drawable.ic_medal_gold);
                break;
            case "platinum":
                holder.tvMilestoneImage.setImageResource(R.drawable.ic_medal_platinum);
                break;
            case "diamond":
                holder.tvMilestoneImage.setImageResource(R.drawable.ic_medal_diamond);
                break;
        }

        if (item.getMembership_type().toLowerCase().equals("basic")) {
            holder.sbProgress.setMax(50);
            holder.sbProgress.setProgress(50);
        } else{
            holder.sbProgress.setMax(Integer.valueOf(item.getRequiredToque()));
        holder.sbProgress.setProgress(Double.valueOf(item.getCurrentToque()).intValue());
      }

        if(Double.valueOf(item.getCurrentToque()) >= Double.valueOf(item.getRequiredToque())){
            holder.tvMilestoneReached.setVisibility(View.VISIBLE);
        }else{
            holder.tvMilestoneReached.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return rvModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        SeekBar sbProgress;
        TextView tvMilestoneName,tvMilestoneReached;
        ImageView tvMilestoneImage;

        public mViewHolder(View itemView) {
            super(itemView);

            sbProgress = itemView.findViewById(R.id.sb_milestone_progress);
            tvMilestoneName = itemView.findViewById(R.id.tv_milestone_name);
            tvMilestoneImage = itemView.findViewById(R.id.iv_milestone_image);
            tvMilestoneReached = itemView.findViewById(R.id.tv_milestone_reached);



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
