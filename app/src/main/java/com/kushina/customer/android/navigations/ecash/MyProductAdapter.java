package com.kushina.customer.android.navigations.ecash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;
import com.kushina.customer.android.R;
import com.kushina.customer.android.navigations.MainActivity;

import java.util.List;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.MyViewHolder>{

    DepositECashActivity depositECashActivity;
    List<SkuDetails> skuDetailsList;
    BillingClient billingClient;

    public MyProductAdapter(DepositECashActivity depositECashActivity, List<SkuDetails> skuDetailsList, BillingClient billingClient) {
        this.depositECashActivity = depositECashActivity;
        this.skuDetailsList = skuDetailsList;
        this.billingClient = billingClient;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(depositECashActivity.getBaseContext())
                .inflate(R.layout.layout_product_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_product.setText(skuDetailsList.get(position).getTitle());

        holder.setiProductClickListener(new IProductListener() {
            @Override
            public void onProductClickListener(View view, int position) {
                BillingFlowParams  billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList.get(position))
                        .build();
                billingClient.launchBillingFlow(depositECashActivity, billingFlowParams);
            }
        });
    }

    @Override
    public int getItemCount() {
        return skuDetailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_product;

        IProductListener iProductListener;

        public void setiProductClickListener(IProductListener iProductClickListener){
            this.iProductListener = iProductClickListener;
        }


        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            tv_product = (TextView) itemView.findViewById(R.id.tv_product_name);

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            iProductListener.onProductClickListener(v, getAdapterPosition());
        }
    }
}
