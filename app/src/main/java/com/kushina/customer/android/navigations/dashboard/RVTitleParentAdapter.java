package com.kushina.customer.android.navigations.dashboard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.navigations.home.ItemDetailsActivity;
import com.kushina.customer.android.navigations.home.RVHomeAdapter;
import com.kushina.customer.android.navigations.home.RVHomeModel;

import java.util.List;

public class RVTitleParentAdapter extends RecyclerView.Adapter<RVTitleParentAdapter.mViewHolder>{

    private Context mContext;
    private Globals mGlobals;
    private List<RVTitleParentModel> rvModel;
    private RVTitleParentAdapter.OnItemClickListener mListener;
    Boolean autoScroll = true;

    public RVTitleParentAdapter(Context mContext, List<RVTitleParentModel> rvModel) {
        this.mContext = mContext;
        this.rvModel = rvModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVTitleParentAdapter.OnItemClickListener listener){
        mListener = listener;
    }


    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_title_parent_row_item, parent, false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVTitleParentModel item = rvModel.get(position);

        holder.tvParentTitle.setText(item.getTitle());

        List<RVHomeModel> itemData = item.getRvModel();


        RVHomeAdapter hAdapter = new RVHomeAdapter(mContext,itemData);
        LinearLayoutManager HorizontalLayout;
        holder.rvParent.setHasFixedSize(true);
        HorizontalLayout = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        holder.rvParent.setLayoutManager(HorizontalLayout);
        holder.rvParent.setAdapter(hAdapter);
        holder.rvParent.setNestedScrollingEnabled(false);

        if(itemData.isEmpty()){
            holder.itemView.setVisibility(View.GONE);
        }else{
            holder.itemView.setVisibility(View.VISIBLE);
        }

        hAdapter.setOnItemClickListener(i -> {
            RVHomeModel clickedItem = itemData.get(i);

            Intent intent = new Intent(mContext, ItemDetailsActivity.class);
            intent.putExtra("item_id", clickedItem.getItemID());
            intent.putExtra("item_name", clickedItem.getItemName());
            intent.putExtra("item_amount", clickedItem.getSrp());
            //      intent.putExtra("item_rating", clickedItem.getItemRating());
            intent.putExtra("item_image", clickedItem.getImage());
            intent.putExtra("description", clickedItem.getLongDescription());
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return rvModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {


        TextView tvParentTitle;
        RecyclerView rvParent;


        public mViewHolder(View itemView) {
            super(itemView);

            tvParentTitle = itemView.findViewById(R.id.tv_parent_title);
            rvParent = itemView.findViewById(R.id.rv_parent);

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
