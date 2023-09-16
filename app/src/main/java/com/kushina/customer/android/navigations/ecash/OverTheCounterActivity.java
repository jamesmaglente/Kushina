package com.kushina.customer.android.navigations.ecash;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_ECASH;

public class OverTheCounterActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    @BindView(R.id.edt_deposit_ecash_amount) TextInputEditText edtDepositEcashAmount;
    @BindView(R.id.til_deposit_ecash_amount) TextInputLayout tilDepositEcashAmount;
    @BindView(R.id.btn_deposit_ecash) Button btnDepositEcash;
    @BindView(R.id.tv_deposit_method_number) TextView tvDepositMethodNumber;
    @BindView(R.id.tv_deposit_ecash_deposit_method) TextView tvDepositEcashDepositMethod;
    @BindView(R.id.iv_proof_of_payment) ImageView ivProofOfPayment;

    private Bitmap bitmap;
    public static final int TAKE_PHOTO_REQUEST_CODE = 0;
    public static final int CHOOSE_FROM_GALLERY_REQUEST_CODE = 1;


    private String depositID, depositOption;

    public static final int SELECT_DEPOSIT_METHOD = 22222;

    @OnClick(R.id.iv_proof_of_payment)
    public void setImage() {
        if (ContextCompat.checkSelfPermission(OverTheCounterActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ivProofOfPayment.setEnabled(false);
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            selectImage(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over_the_counter);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        edtDepositEcashAmount.addTextChangedListener(new OverTheCounterActivity.MyTextWatcher(edtDepositEcashAmount));
    }

    @OnClick({R.id.btn_choose_deposit_method, R.id.btn_deposit_ecash, R.id.btn_copy_number})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_choose_deposit_method:
                Intent intent = new Intent(OverTheCounterActivity.this, ChooseDepositMethodActivity.class);
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
                OverTheCounterActivity.this,
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

                                            finish();
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

        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case SELECT_DEPOSIT_METHOD:
                    depositID = data.getStringExtra("deposit_option_id");
                    depositOption = data.getStringExtra("deposit_option");
                    tvDepositEcashDepositMethod.setText(depositOption);
                    break;
                case TAKE_PHOTO_REQUEST_CODE:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        bitmap = selectedImage;
                        ivProofOfPayment.setImageBitmap(selectedImage);
                    }
                    break;
                case CHOOSE_FROM_GALLERY_REQUEST_CODE:
                    if (resultCode == RESULT_OK && data != null) {
                        try {
                            Uri selectedImg = data.getData();
                            Bitmap imageFromGallery = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImg);
                            if(imageFromGallery != null) {
                                bitmap = imageFromGallery;
                                ivProofOfPayment.setImageBitmap(imageFromGallery);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    break;
            }


        }
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
                    startActivityForResult(takePicture, TAKE_PHOTO_REQUEST_CODE);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, CHOOSE_FROM_GALLERY_REQUEST_CODE);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
}
