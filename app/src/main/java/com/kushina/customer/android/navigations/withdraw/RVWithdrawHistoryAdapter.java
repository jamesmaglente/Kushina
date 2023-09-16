package com.kushina.customer.android.navigations.withdraw;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.ecash.RVDepositHistoryAdapter;
import com.kushina.customer.android.navigations.ecash.RVDepositHistoryModel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_ECASH;
import static com.kushina.customer.android.globals.Endpoints.API_NODE_SHOP;

public class RVWithdrawHistoryAdapter extends RecyclerView.Adapter<RVWithdrawHistoryAdapter.mViewHolder> {

    private Context mContext;
    private List<RVWithdrawHistoryModel> rvModel;
    private RVWithdrawHistoryAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;
    Preferences mPreferences;



    public RVWithdrawHistoryAdapter(Context mContext, List<RVWithdrawHistoryModel> rvModel) {
        this.mContext = mContext;
        this.rvModel = rvModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVWithdrawHistoryAdapter.OnItemClickListener listener){
        mListener = listener;
    }



    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_withdraw_history_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        mPreferences = new Preferences(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVWithdrawHistoryModel item = rvModel.get(position);


        holder.tvStatus.setText(item.getStatus());
        holder.tvWithdrawHistoryID.setText(item.getCodeID());
        holder.tvWithdrawMethod.setText(item.getWithdrawMethod());
        holder.tvAccountName.setText(item.getAccountName());
        holder.tvAccountNumber.setText(item.getAccountNumber());
        holder.tvAmount.setText(mGlobals.moneyFormatter(item.getAmount()));
        holder.tvDate.setText(mGlobals.dateFormatter(item.getDateCreated()));

        if(item.getStatus().toLowerCase().equals("approved")){
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorSuccess));
        }else{
            holder.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorError));
        }

        if(item.getStatus().toLowerCase().equals("pending")){
            holder.btnCancel.setVisibility(View.VISIBLE);
        }else{
            holder.btnCancel.setVisibility(View.GONE);
        }

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGlobals.showChoiceDialog("Are you sure you want to cancel this withdraw request?", true, new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        if(result){
                            cancelWithdraw(item.getWithdrawID());
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return rvModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {


        TextView tvStatus,tvWithdrawHistoryID,tvWithdrawMethod,tvAccountName,tvAccountNumber,tvAmount,tvDate;
        Button btnCancel;

        public mViewHolder(View itemView) {
            super(itemView);

            tvStatus = itemView.findViewById(R.id.tv_withdraw_history_status);
            tvWithdrawHistoryID = itemView.findViewById(R.id.tv_withdraw_history_withdraw_id);
            tvWithdrawMethod = itemView.findViewById(R.id.tv_withdraw_history_withdraw_method);
            tvAccountName = itemView.findViewById(R.id.tv_withdraw_history_account_name);
            tvAccountNumber = itemView.findViewById(R.id.tv_withdraw_history_account_number);
            tvAmount = itemView.findViewById(R.id.tv_withdraw_history_amount);
            tvDate = itemView.findViewById(R.id.tv_withdraw_history_date);
            btnCancel = itemView.findViewById(R.id.btn_cancel_withdraw);



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


    private void cancelWithdraw(String withdrawID){


        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("withdraw_id", withdrawID);
        request_data.put("status", "cancelled");



        mAPI.api_request("POST",
                API_NODE_ECASH+"updateWithdrawStatus",
                request_data,
                true,
                mContext,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log("Cancel Withdraw", String.valueOf(result));
                        try {
                            // parse response object
                            JSONObject jsonObject = result.getJSONObject("data");
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if(result){
                                            Activity activity =  (Activity) mContext;
                                            activity.recreate();
                                        }
                                    }
                                });



                            } else {
                                mGlobals.log("Cancel Withdraw", "onResponseCallback: " + status_message);
                                mGlobals.log("Cancel Withdraw()", status_message);
                            }

                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log("Withdraw()", e.toString());
                        }

                    }
                });

    }
}
