package com.kushina.customer.android.navigations.ecash;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_ECASH;

public class DepositECashActivity extends AppCompatActivity {

    @BindView(R.id.tv_deposit_ecash_deposit_method) TextView tvDepositEcashDepositMethod;
    @BindView(R.id.btn_choose_deposit_method) LinearLayout btnChooseDepositMethod;
    @BindView(R.id.edt_deposit_ecash_amount) TextInputEditText edtDepositEcashAmount;
    @BindView(R.id.til_deposit_ecash_amount) TextInputLayout tilDepositEcashAmount;
    @BindView(R.id.btn_deposit_ecash) Button btnDepositEcash;
    @BindView(R.id.btn_viewpager_deposit_regular) LinearLayout btnViewpagerDepositRegular;
    @BindView(R.id.btn_viewpager_deposit_history) LinearLayout btnViewpagerDepositHistory;
    @BindView(R.id.ll_view_switcher) LinearLayout llViewSwitcher;
    @BindView(R.id.line) View line;
    @BindView(R.id.rv_deposit_history) RecyclerView rvDepositHistory;
    @BindView(R.id.ll_deposit_history) LinearLayout llDepositHistory;
    @BindView(R.id.tv_deposit_ecash_lbl_deposit_regular) TextView tvDepositEcashLblDepositRegular;
    @BindView(R.id.tv_deposit_ecash_lbl_deposit_history) TextView tvDepositEcashLblDepositHistory;
    @BindView(R.id.iv_proof_of_payment) ImageView ivProofOfPayment;
//    @BindView(R.id.rv_product) RecyclerView rvProduct;
    @BindView(R.id.btn_product) Button btnProduct;
    @BindView(R.id.tv_deposit_ecash_lbl_deposit_express) TextView tvDepositEcashLblDepositExpress;
    @BindView(R.id.btn_viewpager_deposit_express) LinearLayout btnViewpagerDepositExpress;
    @BindView(R.id.ll_deposit_ecash_express) LinearLayout llDepositEcashExpress;
    @BindView(R.id.ll_deposit_ecash_regular) ScrollView llDepositEcashRegular;
//    @BindView(R.id.ll_no_google_billing_yet)  LinearLayout llNoGoogleBillingYet;
    @BindView(R.id.btn_copy_number) Button btnCopyNumber;
    @BindView(R.id.lbl_deposit_method_number) TextView lblDepositMethodNumber;
    @BindView(R.id.tv_deposit_method_number) TextView tvDepositMethodNumber;
    @BindView(R.id.lbl_deposit_method_name) TextView lblDepositMethodName;
    @BindView(R.id.tv_deposit_method_name) TextView tvDepositMethodName;
    @BindView(R.id.ll_deposit_method_details) LinearLayout llDepositMethodDetails;

    @OnClick(R.id.iv_proof_of_payment)
    public void setImage() {
        if (ContextCompat.checkSelfPermission(DepositECashActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ivProofOfPayment.setEnabled(false);
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            selectImage(this);
        }
    }

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    List<RVDepositHistoryModel> dhModel;
    @BindView(R.id.ll_no_history_yet)
    LinearLayout llNoHistoryYet;

    public static final int UPLOAD_PROOF_OF_PAYMENT = 20;
    private Bitmap bitmap;


    private String depositID, depositOption;

    public static final int SELECT_DEPOSIT_METHOD = 22222;

    private BillingClient billingClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_ecash);
        ButterKnife.bind(this);

        getSupportActionBar().hide();
        View mDecorView = this.getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        //    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        //     | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);


        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        llDepositEcashExpress.setVisibility(View.VISIBLE);
        tvDepositEcashLblDepositRegular.setTextColor(getResources().getColor(R.color.colorOrange));
        tvDepositEcashLblDepositHistory.setTextColor(getResources().getColor(R.color.colorOrange));
        edtDepositEcashAmount.addTextChangedListener(new MyTextWatcher(edtDepositEcashAmount));


//        setupBillingClient();
//
//        rvProduct.setHasFixedSize(true);
//        rvProduct.setLayoutManager(new LinearLayoutManager(this));
//
//        btnProduct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (billingClient.isReady()) {
//                    SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
//                            .setSkusList(Arrays.asList("purchase_20_ecash",
//                                    "purchase_100_ecash",
//                                    "purchase_200_ecash",
//                                    "purchase_300_ecash",
//                                    "purchase_500_ecash",
//                                    "purchase_1000_ecash",
//                                    "purchase_5000_ecash"))
//                            .setType(BillingClient.SkuType.INAPP)
//                            .build();
//                    billingClient.querySkuDetailsAsync(skuDetailsParams, new SkuDetailsResponseListener() {
//                        @Override
//                        public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
//                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                                loadProductToRecyclerView(list);
//                            } else {
//                                mGlobals.toast("Cannot query product.");
//                                llNoGoogleBillingYet.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    });
//                } else {
//                    mGlobals.toast("Billing is not ready.");
//                    llNoGoogleBillingYet.setVisibility(View.VISIBLE);
//                }
//            }
//        });

        WebView browser = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        browser.loadUrl("https://pm.link/Kushina/test/Rw2NW3A");

    }

    @Override
    protected void onResume() {
        super.onResume();

        mGlobals.removeError(tilDepositEcashAmount);
    }

//    public void loadProductToRecyclerView(List<SkuDetails> skuDetailsList) {
//        MyProductAdapter adapter = new MyProductAdapter(this, skuDetailsList, billingClient);
//        rvProduct.setAdapter(adapter);
//    }
//
//    @Override
//    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
//        mGlobals.toast("Purchase item: " + list.size());
//    }
//
//
//    public void setupBillingClient() {
//        mGlobals.showLoadingDialog();
//        billingClient = BillingClient.newBuilder(this).setListener(this).enablePendingPurchases().build();
//        billingClient.startConnection(new BillingClientStateListener() {
//            @Override
//            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
//                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                    //  mGlobals.toast("Success connect to billing.");
//                    if (billingClient.isReady()) {
//                        SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
//                                .setSkusList(Arrays.asList("purchase_20_ecash",
//                                        "purchase_100_ecash",
//                                        "purchase_200_ecash",
//                                        "purchase_300_ecash",
//                                        "purchase_500_ecash",
//                                        "purchase_1000_ecash",
//                                        "purchase_5000_ecash"))
//                                .setType(BillingClient.SkuType.INAPP)
//                                .build();
//                        billingClient.querySkuDetailsAsync(skuDetailsParams, new SkuDetailsResponseListener() {
//                            @Override
//                            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
//                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                                    loadProductToRecyclerView(list);
//                                    mGlobals.dismissLoadingDialog();
//                                } else {
//                                    mGlobals.toast("Cannot query product.");
//                                    llNoGoogleBillingYet.setVisibility(View.VISIBLE);
//                                }
//                            }
//                        });
//                    } else {
//                        mGlobals.toast("Billing is not ready.");
//                        llNoGoogleBillingYet.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    mGlobals.log(TAG, String.valueOf(billingResult));
//                    mGlobals.toast("Billing is not ready.");
//                    llNoGoogleBillingYet.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onBillingServiceDisconnected() {
//                mGlobals.toast("You are disconnect from Billing");
//            }
//        });
//    }


    @OnClick({R.id.btn_choose_deposit_method, R.id.btn_deposit_ecash, R.id.btn_viewpager_deposit_regular, R.id.btn_viewpager_deposit_history, R.id.btn_viewpager_deposit_express, R.id.btn_copy_number})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_choose_deposit_method:
                Intent intent = new Intent(DepositECashActivity.this, ChooseDepositMethodActivity.class);
                startActivityForResult(intent, SELECT_DEPOSIT_METHOD);
                break;
            case R.id.btn_deposit_ecash:
                mGlobals.showChoiceDialog("Submit deposit request?", true, new Globals.Callback() {
                    @Override
                    public void onPickCallback(Boolean result) {
                        if (result) {
                            if (depositID != null && bitmap != null) {
                                checkFields();
                            } else {
                                mGlobals.showErrorMessageWithDelay("Please make sure you select a deposit method and uploaded a proof of payment.", true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {
                                            return;
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
                break;
            case R.id.btn_viewpager_deposit_regular:
                llDepositHistory.setVisibility(View.GONE);
                llDepositEcashExpress.setVisibility(View.GONE);
                llDepositEcashRegular.setVisibility(View.VISIBLE);
                tvDepositEcashLblDepositExpress.setTextColor(getResources().getColor(R.color.colorOrange));
                tvDepositEcashLblDepositHistory.setTextColor(getResources().getColor(R.color.colorOrange));
                tvDepositEcashLblDepositRegular.setTextColor(getResources().getColor(R.color.colorWhite));
                break;
            case R.id.btn_viewpager_deposit_express:

                llDepositHistory.setVisibility(View.GONE);
                llDepositEcashRegular.setVisibility(View.GONE);
                llDepositEcashExpress.setVisibility(View.VISIBLE);
                tvDepositEcashLblDepositRegular.setTextColor(getResources().getColor(R.color.colorOrange));
                tvDepositEcashLblDepositHistory.setTextColor(getResources().getColor(R.color.colorOrange));
                tvDepositEcashLblDepositExpress.setTextColor(getResources().getColor(R.color.colorWhite));
//                setupBillingClient();
                break;
            case R.id.btn_viewpager_deposit_history:
                llDepositEcashRegular.setVisibility(View.GONE);
                llDepositEcashExpress.setVisibility(View.GONE);
                llDepositHistory.setVisibility(View.VISIBLE);
                tvDepositEcashLblDepositExpress.setTextColor(getResources().getColor(R.color.colorOrange));
                tvDepositEcashLblDepositRegular.setTextColor(getResources().getColor(R.color.colorOrange));
                tvDepositEcashLblDepositHistory.setTextColor(getResources().getColor(R.color.colorWhite));
                loadDepositHistory();
                break;
            case R.id.btn_copy_number:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Number", tvDepositMethodNumber.getText().toString());
                clipboard.setPrimaryClip(clip);
                mGlobals.toast("Copied to Clipboard");
                break;
//            case R.id.btn_deposit:
//                mGlobals.toast("deposit");
//                break;
        }
    }

    private void checkFields() {
        if (

                !mGlobals.validateField(tilDepositEcashAmount, edtDepositEcashAmount, true, getString(R.string.err_msg_amount))

        ) {
            return;

        } else {
            submitDeposit();
        }
    }

    private void submitDeposit() {

        // get image name and file
        Bitmap proof_of_payment = bitmap;
        String encodedImage = "";
        if (proof_of_payment != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            proof_of_payment.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        }

        final Map<String, String> request_data = new HashMap<String, String>();

        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("proof_of_payment_image", encodedImage);
        request_data.put("amount", edtDepositEcashAmount.getText().toString());
        request_data.put("deposit_method_id", depositID);


        mAPI.api_request("POST",
                API_NODE_ECASH + "depositEcash",
                request_data,
                true,
                DepositECashActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {

                        mGlobals.log(getClass().getEnclosingMethod().getName(), String.valueOf(result));


                        try {
                            // parse response object
                            JSONObject jsonObject = result.getJSONObject("data");
                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {


                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {
                                            edtDepositEcashAmount.setText("");

                                            recreate();
//                                            llDepositEcashRegular.setVisibility(View.GONE);
//                                            llDepositEcashExpress.setVisibility(View.GONE);
//                                            llDepositHistory.setVisibility(View.VISIBLE);
//                                            tvDepositEcashLblDepositRegular.setTextColor(getResources().getColor(R.color.colorOrange));
//                                            tvDepositEcashLblDepositHistory.setTextColor(getResources().getColor(R.color.colorWhite));
//                                            loadDepositHistory();

                                        }
                                    }
                                });

                            } else {
                                mGlobals.log(getClass().getEnclosingMethod().getName(), status_message);


                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(getClass().getEnclosingMethod().getName(), e.toString());


                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == SELECT_DEPOSIT_METHOD) {

            depositID = data.getStringExtra("deposit_option_id");
            depositOption = data.getStringExtra("deposit_option");
            tvDepositEcashDepositMethod.setText(depositOption);

        }
        super.onActivityResult(requestCode, resultCode, data);
        if (UPLOAD_PROOF_OF_PAYMENT == requestCode && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            ivProofOfPayment.setImageBitmap(bitmap);
        }
    }

    private void loadDepositHistory() {

        dhModel = new ArrayList<>();
        dhModel.clear();

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());


        mAPI.api_request("POST",
                API_NODE_ECASH + "getDeposits",
                request_data,
                true,
                DepositECashActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {


                        try {
                            mGlobals.log(getClass().getEnclosingMethod().getName(), result.toString(4));

                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;
                                JSONObject data_array = root.getJSONObject("data");
                                JSONArray items = data_array.getJSONArray("deposits");

                                for (int i = 0; i < items.length(); i++) {

                                    String deposit_id = ((JSONObject) items.get(i)).get("deposit_id").toString();
                                    String reference = ((JSONObject) items.get(i)).get("reference").toString();
                                    String status_id = ((JSONObject) items.get(i)).get("status_id").toString();
                                    String status = ((JSONObject) items.get(i)).get("status").toString();
                                    String deposit_method_id = ((JSONObject) items.get(i)).get("deposit_method_id").toString();
                                    String deposit_method = ((JSONObject) items.get(i)).get("deposit_method").toString();
                                    String user_id = ((JSONObject) items.get(i)).get("user_id").toString();
                                    String amount = ((JSONObject) items.get(i)).get("amount").toString();
                                    String proof_of_payment_image = ((JSONObject) items.get(i)).get("proof_of_payment_image").toString();
                                    String date_submitted = ((JSONObject) items.get(i)).get("date_submitted").toString();


                                    dhModel.add(new RVDepositHistoryModel(deposit_id, reference, status_id, status, deposit_method_id, deposit_method, user_id, amount, proof_of_payment_image, date_submitted));

                                }


                                rvDepositHistory.setHasFixedSize(true);
                                rvDepositHistory.setLayoutManager(new LinearLayoutManager(DepositECashActivity.this));
                                RVDepositHistoryAdapter dhAdapter = new RVDepositHistoryAdapter(DepositECashActivity.this, dhModel);
                                rvDepositHistory.setAdapter(dhAdapter);


                                if (dhModel.isEmpty()) {
                                    llNoHistoryYet.setVisibility(View.VISIBLE);
                                } else {
                                    llNoHistoryYet.setVisibility(View.GONE);
                                }

                                //     mGlobals.dismissLoadingDialog();


                                dhAdapter.setOnItemClickListener(position -> {
                                    RVDepositHistoryModel clickedItem = dhModel.get(position);

                                    Intent intent = new Intent(DepositECashActivity.this, DepositDetailsActivity.class);
                                    intent.putExtra("deposit_amount", clickedItem.getAmount());
                                    intent.putExtra("deposit_method", clickedItem.getDepositMethod());
                                    intent.putExtra("deposit_status", clickedItem.getStatus());
                                    intent.putExtra("deposit_id", clickedItem.getDepositID());
                                    intent.putExtra("date", clickedItem.getDate_submitted());
                                    intent.putExtra("proof_of_payment_image", clickedItem.getProofOfPaymentImage());
                                    startActivity(intent);
                                });


                            } else {
                                // show error
                                mGlobals.log(getClass().getEnclosingMethod().getName(), status_message);


                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(getClass().getEnclosingMethod().getName(), e.toString());
                        }


                    }
                });

    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                case R.id.edt_deposit_ecash_amount:
                    mGlobals.validateField(tilDepositEcashAmount, edtDepositEcashAmount, true, getString(R.string.err_msg_amount));
                    break;

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mGlobals.log(TAG, "onRequestPermissionsResult()");
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                ivProofOfPayment.setEnabled(true);
                selectImage(this);
            }
        }
    }

    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your item picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_CANCELED) {
//            switch (requestCode) {
//                case 0:
//                    if (resultCode == RESULT_OK && data != null) {
//                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
//                        bitmap = selectedImage;
//                        ivProofOfPayment.setImageBitmap(selectedImage);
//                    }
//
//                    break;
//                case 1:
//                    if (resultCode == RESULT_OK && data != null) {
//                        try {
//                            Uri selectedImg = data.getData();
//                            Bitmap imageFromGallery = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImg);
//                            if (imageFromGallery != null) {
//                                bitmap = imageFromGallery;
//                                ivProofOfPayment.setImageBitmap(imageFromGallery);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                    break;
//                case SELECT_DEPOSIT_METHOD:
//                    depositID = data.getStringExtra("deposit_option_id");
//                    depositOption = data.getStringExtra("deposit_option");
//                    if (depositID.equals("19")) {
//                        lblDepositMethodNumber.setText("G-Cash Number:");
//                        lblDepositMethodName.setText("G-Cash Name:");
//                        tvDepositMethodNumber.setText("09672130954");
//                        tvDepositMethodName.setText("Alexis Balag");
//                        llDepositMethodDetails.setVisibility(View.VISIBLE);
//                    } else if (depositID.equals("20")) {
//                        lblDepositMethodNumber.setText("Paymaya Number:");
//                        lblDepositMethodName.setText("Paymaya Name:");
//                        tvDepositMethodNumber.setText("09365468780");
//                        tvDepositMethodName.setText("Alexis Balag");
//                        llDepositMethodDetails.setVisibility(View.VISIBLE);
//                    }
//
//                    tvDepositEcashDepositMethod.setText(depositOption);
//                    break;
//            }
//        }
//    }

}
