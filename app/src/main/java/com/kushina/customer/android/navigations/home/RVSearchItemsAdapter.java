package com.kushina.customer.android.navigations.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RVSearchItemsAdapter extends RecyclerView.Adapter<RVSearchItemsAdapter.mViewHolder> implements Filterable {

    private Context mContext;
    private List<RVHomeModel> rvModel;
    private List<RVHomeModel> rvModelAll;
    private RVSearchItemsAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVSearchItemsAdapter(Context mContext, List<RVHomeModel> rvModel) {
        this.mContext = mContext;
        this.rvModel = rvModel;
        rvModelAll = new ArrayList<>(rvModel);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<RVHomeModel> filteredModel = new ArrayList<>();

            if(charSequence.toString().isEmpty()){
                filteredModel.addAll(rvModelAll);
            }else{
                for(int i = 0; i < rvModelAll.size(); i++){
                    if(rvModelAll.get(i).getItemName().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredModel.add(rvModelAll.get(i));
                    }
//                    else if(rModelall.get(i).getCategory().toLowerCase().contains(charSequence.toString().toLowerCase())){
//                        filteredModel.add(rModelall.get(i));
//                    }else if(rModelall.get(i).getMerchantName().toLowerCase().contains(charSequence.toString().toLowerCase())){
//                        filteredModel.add(rModelall.get(i));
//                    }
                }

            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredModel;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            rvModel.clear();
            rvModel.addAll((Collection<? extends RVHomeModel>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVSearchItemsAdapter.OnItemClickListener listener){
        mListener = listener;
    }



    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_search_items_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVHomeModel item = rvModel.get(position);

        //  mGlobals.log("{PIC",item.getItemPictureLink());

        try {
            Picasso.get()
                    //  .load(ITEMS_URL+item.getItemPictureLink())
                    .load(item.getImage())
                    //.load(item.getItemImage())
                    .resize(200,200)
                    .placeholder(R.drawable.applogo)
                    .into(holder.ivItemPicture);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tvItemName.setText(item.getItemName());
        holder.tvItemPrice.setText(mGlobals.moneyFormatter(item.getSrp()));

    }

    @Override
    public int getItemCount() {
        return rvModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        ImageView ivItemPicture;
        TextView tvItemName,tvItemPrice;

        public mViewHolder(View itemView) {
            super(itemView);

            ivItemPicture = itemView.findViewById(R.id.iv_home_product_picture);
            tvItemName = itemView.findViewById(R.id.tv_home_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_home_item_price);



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
