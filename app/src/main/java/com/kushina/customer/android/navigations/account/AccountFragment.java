package com.kushina.customer.android.navigations.account;


import android.Manifest;
import android.accounts.Account;
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
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kushina.customer.android.navigations.MainActivity;
import com.kushina.customer.android.navigations.my_team.MyTeamActivity;
import com.kushina.customer.android.start_up_screens.LoginActivity;
import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;
import com.kushina.customer.android.navigations.addresses.AddressListActivity;
import com.kushina.customer.android.navigations.earn_ecash.EarnECashActivity;
import com.kushina.customer.android.navigations.ecash.DepositECashActivity;
import com.kushina.customer.android.navigations.ecash.ECashActivity;
import com.kushina.customer.android.navigations.home.MyFavoritesActivity;
import com.kushina.customer.android.navigations.orderhistory.OrderHistoryActivity;
import com.kushina.customer.android.navigations.withdraw.WithdrawECashActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.kushina.customer.android.globals.Endpoints.API_NODE_ECASH;
import static com.kushina.customer.android.globals.Endpoints.API_NODE_PROFILE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {


    Unbinder unbinder;
    @BindView(R.id.iv_account_profile_picture) CircleImageView ivAccountProfilePicture;
    @BindView(R.id.tv_account_profile_name) TextView tvAccountProfileName;
    @BindView(R.id.tv_account_customer_type) TextView tvAccountCustomerType;
    @BindView(R.id.tv_account_ecash) TextView tvAccountEcash;
    @BindView(R.id.tv_account_cashback) TextView tvAccountCashback;
    @BindView(R.id.tv_account_referral_bonus) TextView tvAccountReferralBonus;
    @BindView(R.id.rv_account_navigations) RecyclerView rvAccountNavigations;
    @BindView(R.id.rl_main_layout) RelativeLayout rlMainLayout;
//    @BindView(R.id.tv_copy) TextView tvCopy;
    @BindView(R.id.iv_share) ImageView ivShare;
    @BindView(R.id.iv_medal) ImageView ivMedal;
    @BindView(R.id.tv_dashboard_ranking) TextView tvDashboardRanking;


    private Bitmap bitmap;
    public static final int TAKE_PHOTO_REQUEST_CODE = 0;
    public static final int CHOOSE_FROM_GALLERY_REQUEST_CODE = 1;

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    List<RVAccountNavigationsModel> avModel;
    @BindView(R.id.tv_account_referral_code)
    TextView tvAccountReferralCode;


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mAPI = new API(getActivity());
        mGlobals = new Globals(getActivity());
        mPreferences = new Preferences(getActivity());

        loadProfile();
        loadAccountNavigations();

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("");

        switch (mPreferences.getMembershipType().toLowerCase()){
            case "basic":
                ivAccountProfilePicture.setBorderColor(getResources().getColor(R.color.colorBasic));
                ivMedal.setImageResource(R.drawable.ic_medal_basic);
                break;
            case "bronze":
                ivAccountProfilePicture.setBorderColor(getResources().getColor(R.color.colorBronze));
                ivMedal.setImageResource(R.drawable.ic_medal_bronze);
                break;
            case "silver":
                ivAccountProfilePicture.setBorderColor(getResources().getColor(R.color.colorSilver));
                ivMedal.setImageResource(R.drawable.ic_medal_silver);
                break;
            case "gold":
                ivAccountProfilePicture.setBorderColor(getResources().getColor(R.color.colorGold));
                ivMedal.setImageResource(R.drawable.ic_medal_gold);
                break;
            case "platinum":
                ivAccountProfilePicture.setBorderColor(getResources().getColor(R.color.colorPlatinum));
                ivMedal.setImageResource(R.drawable.ic_medal_platinum);
                break;
            case "diamond":
                ivAccountProfilePicture.setBorderColor(getResources().getColor(R.color.colorDiamond));
                ivMedal.setImageResource(R.drawable.ic_medal_diamond);
                break;
        }

    }

    private void loadAccountNavigations() {

        avModel = new ArrayList<>();
        avModel.clear();

//        avModel.add(new RVAccountNavigationsModel("http://tacticalcodes.xyz/im1ages/asianpalate/baseline_add_location_alt_black_18dp.png", "Addresses"));
        avModel.add(new RVAccountNavigationsModel( R.drawable.ic_heart_outline, "My Favorites"));
//        avModel.add(new RVAccountNavigationsModel("http://tacticalcodes.xyz/imag1es/asianpalate/baseline_history_black_18dp.png", "Ongoing Orders"));
//        avModel.add(new RVAccountNavigationsModel("http://tacticalcodes.xyz/imag1es/asianpalate/baseline_history_black_18dp.png", "Order History"));
//        avModel.add(new RVAccountNavigationsModel("http://tacticalcodes.xyz/ima1ges/asianpalate/baseline_account_balance_wallet_black_18dp.png", "Deposit E-Cash"));
//        avModel.add(new RVAccountNavigationsModel("http://tacticalcodes.xyz/ima1ges/asianpalate/baseline_account_balance_wallet_black_18dp.png", "Withdraw E-Cash"));
//        avModel.add(new RVAccountNavigationsModel("http://tacticalcodes.xyz/ima1ges/asianpalate/baseline_account_balance_wallet_black_18dp.png", "E-Cash"));
        //  avModel.add(new RVAccountNavigationsModel("http://tacticalcodes.xyz/images/asianpalate/baseline_payments_black_48dp.png", "Earn E-Cash"));
//        avModel.add(new RVAccountNavigationsModel("http://tacticalcodes.xyz/ima1ges/asianpalate/baseline_account_circle_black_18dp.png", "Profile"));
//        avModel.add(new RVAccountNavigationsModel(R.drawable.ic_milestone_feature, "Password"));
//
//        avModel.add(new RVAccountNavigationsModel(R.drawable.ic_milestone_feature, "ECash"));
        avModel.add(new RVAccountNavigationsModel(R.drawable.ic_milestone_feature, "Milestone"));
        avModel.add(new RVAccountNavigationsModel(R.drawable.ic_leaderboard, "Ranking"));
        avModel.add(new RVAccountNavigationsModel(R.drawable.ic_profile_bottom, "Account"));
        avModel.add(new RVAccountNavigationsModel(R.drawable.ic_referral_group, "My Referral Group"));

        avModel.add(new RVAccountNavigationsModel(R.drawable.ic_logout, "Logout"));


        rvAccountNavigations.setHasFixedSize(true);
        rvAccountNavigations.setLayoutManager(new LinearLayoutManager(getActivity()));
        RVAccountNavigationsAdapter avAdapter = new RVAccountNavigationsAdapter(getActivity(), avModel);
        rvAccountNavigations.setAdapter(avAdapter);


        avAdapter.setOnItemClickListener(new RVAccountNavigationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent;
                RVAccountNavigationsModel clickedItem = avModel.get(position);

                if (clickedItem.getNavigationTitle().toLowerCase().contains("earn")) {
                    intent = new Intent(getActivity(), EarnECashActivity.class);
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().contains("deposit")) {
                    intent = new Intent(getActivity(), DepositECashActivity.class);
                    startActivity(intent);
                }else if (clickedItem.getNavigationTitle().toLowerCase().contains("withdraw")) {
                    intent = new Intent(getActivity(), WithdrawECashActivity.class);
                    startActivity(intent);
                }else if (clickedItem.getNavigationTitle().toLowerCase().contains("favorites")) {
                    intent = new Intent(getActivity(), MyFavoritesActivity.class);
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().equals("e-cash")) {
                    intent = new Intent(getActivity(), ECashActivity.class);
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().contains("profile")) {
                    intent = new Intent(getActivity(), UpdateProfileActivity.class);
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().contains("password")) {
                    intent = new Intent(getActivity(), UpdatePasswordActivity.class);
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().contains("addresses")) {
                    intent = new Intent(getActivity(), AddressListActivity.class);
                    startActivity(intent);
                }else if (clickedItem.getNavigationTitle().toLowerCase().contains("ongoing")) {
                    intent = new Intent(getActivity(), OrderHistoryActivity.class);
                    intent.putExtra("task","ongoing");
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().contains("history")) {
                    intent = new Intent(getActivity(), OrderHistoryActivity.class);
                    intent.putExtra("task","processed");
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().equals("ecash")) {
                    intent = new Intent(getActivity(), ECashMainActivity.class);
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().contains("milestone")) {
                    intent = new Intent(getActivity(), MilestoneActivity.class);
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().contains("ranking")) {
                    intent = new Intent(getActivity(), RankingActivity.class);
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().contains("account")) {
                    intent = new Intent(getActivity(), AccountActivity.class);
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().contains("my referral group")) {
                    intent = new Intent(getActivity(), MyTeamActivity.class);
                    startActivity(intent);
                } else if (clickedItem.getNavigationTitle().toLowerCase().contains("logout")) {
                    mGlobals.showChoiceDialog("You are about to logout. Continue?", true, new Globals.Callback() {
                        @Override
                        public void onPickCallback(Boolean result) {
                            if (result) {
                                mPreferences.clearPreferences();
                                getActivity().finish();
                                mPreferences.clearPreferences();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }


            }
        });

    }

    @OnClick(R.id.iv_account_profile_picture)
    public void updateProfilePicture(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ivAccountProfilePicture.setEnabled(false);
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            selectImage(getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mGlobals.log(TAG, "onRequestPermissionsResult()");
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                ivAccountProfilePicture.setEnabled(true);
                selectImage(getActivity());
            }
        }
    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your item picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, TAKE_PHOTO_REQUEST_CODE);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , CHOOSE_FROM_GALLERY_REQUEST_CODE);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case TAKE_PHOTO_REQUEST_CODE:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        bitmap = selectedImage;
                        ivAccountProfilePicture.setImageBitmap(selectedImage);
                    }
                    uploadProfilePicture();
                    break;
                case CHOOSE_FROM_GALLERY_REQUEST_CODE:
                    if (resultCode == RESULT_OK && data != null) {
                        try {
                            Uri selectedImg = data.getData();
                            Bitmap imageFromGallery = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImg);
                            if(imageFromGallery != null) {
                                bitmap = imageFromGallery;
                                ivAccountProfilePicture.setImageBitmap(imageFromGallery);
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    uploadProfilePicture();
                    break;
            }


        }
    }

//    @OnClick(R.id.tv_copy)
//    public void onViewClicked() {
//        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
//        ClipData clip = ClipData.newPlainText("Kushina Referral Code", tvAccountReferralCode.getText().toString());
//        clipboard.setPrimaryClip(clip);
//        mGlobals.toast("Copied to Clipboard");
//    }

    @OnClick(R.id.tv_account_referral_code)
    public void copy() {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Kushina Referral Code", tvAccountReferralCode.getText().toString());
        clipboard.setPrimaryClip(clip);
        mGlobals.toast("Copied to Clipboard");
    }

    @OnClick(R.id.iv_share)
    public void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody = "Kushina Referral Code: " + tvAccountReferralCode.getText().toString();
        String shareSub = "Kushina Referral Code";
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
        startActivity(Intent.createChooser(intent, "Share using"));
    }

    private void loadProfile() {

        tvAccountProfileName.setText(mPreferences.getFirstname() + " " + mPreferences.getLastname());
        String upperString = mPreferences.getMembershipType().substring(0, 1).toUpperCase() + mPreferences.getMembershipType().substring(1).toLowerCase();
        tvAccountCustomerType.setText("Golden Retriever");
        tvAccountReferralCode.setText(mPreferences.getUserCode());
        tvDashboardRanking.setText(mPreferences.getCurrentRank());

        Picasso.get()
                .load(mPreferences.getUserProfilePicture())
                .placeholder(R.drawable.ic_profile)
                .into(ivAccountProfilePicture);

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());

        mAPI.api_request("POST",
                API_NODE_ECASH + "loadEarnEcash",
                request_data,
                false,
                getActivity(),
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
                                String current_ecash = data_array.getString("current_ecash");
                                String total_cashback = data_array.getString("total_cashback");
                                String total_referral_bonus = data_array.getString("total_referral_bonus");

                                tvAccountEcash.setText(mGlobals.moneyFormatter(current_ecash));
                                tvAccountCashback.setText(mGlobals.moneyFormatter(total_cashback));
                                tvAccountReferralBonus.setText(mGlobals.moneyFormatter(total_referral_bonus));

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

    private void uploadProfilePicture() {

        mGlobals.toast("Updating profile picture...");

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
        request_data.put("profile_picture", encodedImage);

        mAPI.api_request("POST",
                API_NODE_PROFILE + "updateProfilePicture",
                request_data,
                false,
                getActivity(),
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
                                String profile_picture = data_array.getString("profile_picture");

                                mPreferences.setUserProfilePicture(profile_picture);
                                loadProfile();
                                mGlobals.toast("Profile picture successfully updated.");
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
}
