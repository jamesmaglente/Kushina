package com.kushina.customer.android.navigations.ecash;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DepositDetailsActivity extends AppCompatActivity {

    @BindView(R.id.iv_deposit_details_image)
    ImageView ivDepositDetailsImage;
    @BindView(R.id.tv_deposit_details_amount)
    TextView tvDepositDetailsAmount;
    @BindView(R.id.tv_deposit_details_deposit_method)
    TextView tvDepositDetailsDepositMethod;
    @BindView(R.id.tv_deposit_details_status)
    TextView tvDepositDetailsStatus;
    @BindView(R.id.tv_deposit_details_deposit_id)
    TextView tvDepositDetailsDepositId;
    @BindView(R.id.tv_deposit_details_date_submitted)
    TextView tvDepositDetailsDateSubmitted;

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_details);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        try {
            Picasso.get()
                    .load(getIntent().getStringExtra("proof_of_payment_image"))
                    //.load(item.getItemImage())
                    .resize(400,400)
                    .placeholder(R.drawable.splash)
                    .into(ivDepositDetailsImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvDepositDetailsAmount.setText(mGlobals.moneyFormatter(getIntent().getStringExtra("deposit_amount")));
        tvDepositDetailsDepositMethod.setText(getIntent().getStringExtra("deposit_method"));
        tvDepositDetailsDepositId.setText(getIntent().getStringExtra("deposit_id"));
        tvDepositDetailsStatus.setText(getIntent().getStringExtra("deposit_status"));
        tvDepositDetailsDateSubmitted.setText(mGlobals.dateFormatter(getIntent().getStringExtra("date")));

        if(getIntent().getStringExtra("deposit_status").toLowerCase().equals("approved")){
            tvDepositDetailsStatus.setTextColor(getResources().getColor(R.color.colorSuccess));
        }else{
            tvDepositDetailsStatus.setTextColor(getResources().getColor(R.color.colorError));
        }


    }
}
