package com.kushina.customer.android.navigations.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.navigations.account.RVAccountNavigationsModel;

import java.util.List;

public class RVOrderNavigationsAdapter extends RecyclerView.Adapter<RVOrderNavigationsAdapter.mViewHolder> {

    private Context mContext;
    private List<RVAccountNavigationsModel> avModel;
    private RVOrderNavigationsAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVOrderNavigationsAdapter(Context mContext, List<RVAccountNavigationsModel> avModel) {
        this.mContext = mContext;
        this.avModel = avModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVOrderNavigationsAdapter.OnItemClickListener listener){
        mListener = listener;
    }




    @NonNull
    @Override
    public RVOrderNavigationsAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_order_navigations_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new RVOrderNavigationsAdapter.mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVOrderNavigationsAdapter.mViewHolder holder, int position) {


        RVAccountNavigationsModel item = avModel.get(position);

        holder.tvNavigationTitle.setText(item.getNavigationTitle());
        holder.ivNavigationPicture.setImageResource(item.getNavigationIcon());

    }

    @Override
    public int getItemCount() {
        return avModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        ImageView ivNavigationPicture;
        TextView tvNavigationTitle;
        ImageView ivArrowRight;

        public mViewHolder(View itemView) {
            super(itemView);

            ivNavigationPicture = itemView.findViewById(R.id.iv_navigation_icon);
            tvNavigationTitle = itemView.findViewById(R.id.tv_navigation_title);
            ivArrowRight = itemView.findViewById(R.id.iv_arrow_right);



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