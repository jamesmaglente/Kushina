package com.kushina.customer.android.navigations.addresses;

import android.app.Activity;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_PROFILE;

public class RVAddressListAdapter extends RecyclerView.Adapter<RVAddressListAdapter.mViewHolder> {


    private Context mContext;
    private List<RVAddressListModel> alModel;
    private RVAddressListAdapter.OnItemClickListener mListener;
    Globals mGlobals;
    API mAPI;



    public RVAddressListAdapter(Context mContext, List<RVAddressListModel> alModel) {
        this.mContext = mContext;
        this.alModel = alModel;
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(RVAddressListAdapter.OnItemClickListener listener){
        mListener = listener;
    }


    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.rv_address_list_row_item, parent, false);
        mGlobals = new Globals(mContext);
        mAPI = new API(mContext);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        RVAddressListModel item = alModel.get(position);


        holder.tvTitle.setText(item.getTitle());
        holder.tvCustomerName.setText(item.getCustomerName()+" ("+item.getContactNumber()+")");
        holder.tvAddress.setText(item.getHouseAddress()+" "+item.getAddressLine()+" "+item.getZipCode());

        holder.btnRemoveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGlobals.showChoiceDialog("Remove " + item.getTitle() + " in your address list?", true, new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        if(result){
                            removeAddress(item.getDeliveryAddressID());
                        }
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return alModel.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {


        TextView tvTitle,tvCustomerName,tvAddress;
        ImageView btnRemoveAddress;

        public mViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvAddress = itemView.findViewById(R.id.tv_customer_address);
            btnRemoveAddress = itemView.findViewById(R.id.btn_address_remove_address);



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

    private void removeAddress(String addressID){
        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("address_id", addressID);



        mAPI.api_request("POST",
                API_NODE_PROFILE+"removeDeliveryAddress",
                request_data,
                true,
                mContext,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log("Remove Address", String.valueOf(result));
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
                                mGlobals.log("CreateBooking", "onResponseCallback: " + status_message);
                                mGlobals.log("CreateBooking()", status_message);
                            }

                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log("CreateBooking()", e.toString());
                        }

                    }
                });
    }
}
