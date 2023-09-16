package com.kushina.customer.android.navigations.addresses;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.Preferences;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_PROFILE;

public class AddEditAddressActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerDragListener {

    @BindView(R.id.iv_address_transparent_image) ImageView ivAddressTransparentImage;
    @BindView(R.id.rl_search_bar) RelativeLayout rlSearchBar;
    @BindView(R.id.edt_address_title) TextInputEditText edtAddressTitle;
    @BindView(R.id.til_address_title) TextInputLayout tilAddressTitle;
    @BindView(R.id.edt_address_customer_name) TextInputEditText edtAddressCustomerName;
    @BindView(R.id.til_address_customer_name) TextInputLayout tilAddressCustomerName;
    @BindView(R.id.edt_address_contact_number) TextInputEditText edtAddressContactNumber;
    @BindView(R.id.til_address_contact_number) TextInputLayout tilAddressContactNumber;
    @BindView(R.id.til_address_house_address) TextInputLayout tilAddressHouseAddress;
    @BindView(R.id.edt_address_landmarks) TextInputEditText edtAddressLandmarks;
    @BindView(R.id.til_address_landmarks) TextInputLayout tilAddressLandmarks;
    @BindView(R.id.edt_address_house_address) TextInputEditText edtAddressHouseAddress;
    @BindView(R.id.edt_address_zip_code) TextInputEditText edtAddressZipCode;
    @BindView(R.id.til_address_zip_code) TextInputLayout tilAddressZipCode;
    @BindView(R.id.btn_save_address) Button btnSaveAddress;

    public final String TAG = getClass().getSimpleName();
    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;
    @BindView(R.id.sv_add_edit_address) ScrollView svAddEditAddress;
    @BindView(R.id.tv_full_address) TextView tvFullAddress;
    @BindView(R.id.ll_full_address) LinearLayout llFullAddress;

    private String addressLine, zipCode, placeID;
    private String task;
    Double latitude, longitude;

    private float DEFAULT_ZOOM = 15f;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private GoogleMap mMap;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_address);
        ButterKnife.bind(this);

        mAPI = new API(this);
        mGlobals = new Globals(this);
        mPreferences = new Preferences(this);

        task = getIntent().getStringExtra("task");


        getLocationPermission();

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setCountry("PH");
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID,
                Place.Field.LAT_LNG,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mGlobals.log(TAG, String.valueOf(place));
                //       mGlobals.log(TAG, place.getAddressComponents().asList().get(place.geta).toString());

                Geocoder geocoder = new Geocoder(AddEditAddressActivity.this, Locale.getDefault());

                List<Address> list = new ArrayList<>();

                try {

                    list = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);

                    Address address = list.get(0);

                    // addressLine = address.getAddressLine(0);
                    addressLine = place.getAddress();
                    zipCode = address.getPostalCode();
                    if (zipCode != null) {
                        edtAddressZipCode.setText(zipCode);
                    }
                    mGlobals.removeError(tilAddressZipCode);
                    edtAddressTitle.setText(edtAddressTitle.getText().toString());
                    latitude = address.getLatitude();
                    longitude = address.getLongitude();
                    placeID = String.valueOf(place.getId());
                    llFullAddress.setVisibility(View.VISIBLE);
                    tvFullAddress.setText(edtAddressHouseAddress.getText().toString()+" "+addressLine + " "+ edtAddressZipCode.getText().toString());
                    mGlobals.log(TAG, "Found location: " + address.toString());
                    mGlobals.log(TAG, "AddressLines: " + address.getAddressLine(0));
                    mGlobals.log(TAG, "===========================================");
                    mGlobals.log(TAG, "feature: " + address.getFeatureName());
                    mGlobals.log(TAG, "admin: " + address.getAdminArea());
                    mGlobals.log(TAG, "sub-admin: " + address.getSubAdminArea());
                    mGlobals.log(TAG, "locality: " + address.getLocality());
                    mGlobals.log(TAG, "sub-locality: " + address.getSubLocality());
                    mGlobals.log(TAG, "thoroughfare: " + address.getThoroughfare());
                    mGlobals.log(TAG, "sub-thoroughfare: " + address.getSubThoroughfare());
                    mGlobals.log(TAG, "postalCode: " + address.getPostalCode());
                    mGlobals.log(TAG, "===========================================");
                } catch (IOException e) {
                    mGlobals.log(TAG, "geoLocate: IOException: " + e.getMessage());
                }

                moveCamera(place.getLatLng(), DEFAULT_ZOOM, place.getName());
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });


        validateFields();
        preventScrollViewonMap();

        if (task.toLowerCase().equals("add_new_address")) {
            edtAddressTitle.setText("My House");
            llFullAddress.setVisibility(View.GONE);
        } else if (task.toLowerCase().contains("update_address")) {
            //     mGlobals.toast(task);
            edtAddressTitle.setText(getIntent().getStringExtra("title"));
            edtAddressCustomerName.setText(getIntent().getStringExtra("customer_name"));
            edtAddressContactNumber.setText(getIntent().getStringExtra("customer_number"));
            edtAddressHouseAddress.setText(getIntent().getStringExtra("house_address"));
            edtAddressZipCode.setText(getIntent().getStringExtra("zip_code"));
            edtAddressLandmarks.setText(getIntent().getStringExtra("landmarks"));
            latitude = Double.valueOf(getIntent().getStringExtra("latitude"));
            longitude = Double.valueOf(getIntent().getStringExtra("longitude"));
            placeID = getIntent().getStringExtra("place_id");
            addressLine = getIntent().getStringExtra("address_line");
            tvFullAddress.setText(edtAddressHouseAddress.getText().toString()+" "+addressLine + " "+ edtAddressZipCode.getText().toString());

        }



    }

    @OnClick(R.id.btn_save_address)
    public void onViewClicked() {
        mGlobals.showChoiceDialog("Save this address?", true, new Globals.Callback() {
            @Override
            public void onPickCallback(Boolean result) {
                if (result) {
                    if (addressLine != null) {
                        checkFields();
                    } else {
                        mGlobals.showErrorMessageWithDelay("Please search a location on the map.", true, new Globals.Callback() {
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
    }


    private void checkFields() {
        if (
                !mGlobals.validateField(tilAddressTitle, edtAddressTitle, true, getString(R.string.err_msg_address_title)) ||
                        !mGlobals.validateField(tilAddressCustomerName, edtAddressCustomerName, true, getString(R.string.err_msg_address_dropoff_name)) ||
                        !mGlobals.validateField(tilAddressContactNumber, edtAddressContactNumber, true, getString(R.string.err_msg_address_dropoff_number)) ||
                        !mGlobals.validateField(tilAddressHouseAddress, edtAddressHouseAddress, true, getString(R.string.err_msg_address_house_address)) ||
                        !mGlobals.validateField(tilAddressZipCode, edtAddressZipCode, true, getString(R.string.err_msg_address_zip_code)) ||
                        !mGlobals.validateField(tilAddressLandmarks, edtAddressLandmarks, true, getString(R.string.err_msg_address_landmarks))
        ) {

            mGlobals.showErrorMessageWithDelay(
                    "Fields cannot be empty.",
                    true,
                    new Globals.Callback() {
                        @Override
                        public void onPickCallback(Boolean result) {
                            if (result) {
                                return;
                            }
                        }
                    }
            );
        } else {
            if (task.toLowerCase().equals("add_new_address")) {
                addAddress();
            } else if (task.toLowerCase().contains("update_address")) {
                updateAddress();

            }

        }
    }

    private void addAddress() {

        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("user_id", mPreferences.getUserId().toString());
        request_data.put("title", edtAddressTitle.getText().toString());
        request_data.put("customer_name", edtAddressCustomerName.getText().toString());
        request_data.put("customer_number", edtAddressContactNumber.getText().toString());
        request_data.put("address_line", addressLine);
        request_data.put("house_address", edtAddressHouseAddress.getText().toString());
        request_data.put("zip_code", edtAddressZipCode.getText().toString());
        request_data.put("landmarks", edtAddressLandmarks.getText().toString());
        request_data.put("latitude", String.valueOf(latitude));
        request_data.put("longitude", String.valueOf(longitude));
        request_data.put("place_id", placeID);


        // api call
        mAPI.api_request("POST",
                API_NODE_PROFILE + "addNewDeliveryAddress",
                request_data,
                true,
                AddEditAddressActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {
                        mGlobals.log(getClass().getEnclosingMethod().getName(), result.toString());
                        try {

                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;

                                // mGlobals.dismissLoadingDialog();

                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {
                                            onBackPressed();
                                        }
                                    }
                                });


                            } else {
                                // show error
                                mGlobals.log(TAG, status_message);
                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(TAG, e.toString());
                        }
                    }
                });

    }

    private void updateAddress() {


        final Map<String, String> request_data = new HashMap<String, String>();
        request_data.put("address_id", getIntent().getStringExtra("address_id"));
        request_data.put("title", edtAddressTitle.getText().toString());
        request_data.put("customer_name", edtAddressCustomerName.getText().toString());
        request_data.put("customer_number", edtAddressContactNumber.getText().toString());
        request_data.put("address_line", addressLine);
        request_data.put("house_address", edtAddressHouseAddress.getText().toString());
        request_data.put("zip_code", edtAddressZipCode.getText().toString());
        request_data.put("landmarks", edtAddressLandmarks.getText().toString());
        request_data.put("latitude", String.valueOf(latitude));
        request_data.put("longitude", String.valueOf(longitude));
        request_data.put("place_id", placeID);


        // api call
        mAPI.api_request("POST",
                API_NODE_PROFILE + "updateDeliveryAddress",
                request_data,
                true,
                AddEditAddressActivity.this,
                new API.VolleyCallback() {
                    @Override
                    public void onResponseCallback(JSONObject result) {
                        mGlobals.log(getClass().getEnclosingMethod().getName(), result.toString());
                        try {

                            String status_message = result.getString("status_message");

                            Integer status_code = result.getInt("status_code");

                            if (status_code == 200) {

                                JSONObject root = result;

                                // mGlobals.dismissLoadingDialog();

                                mGlobals.showSuccessDialog(status_message, true, new Globals.Callback() {
                                    @Override
                                    public void onPickCallback(Boolean result) {
                                        if (result) {
                                            onBackPressed();
                                        }
                                    }
                                });


                            } else {
                                // show error
                                mGlobals.log(TAG, status_message);
                            }
                        } catch (Exception e) {
                            // show exception error
                            mGlobals.log(TAG, e.toString());
                        }
                    }
                });

    }

    private void preventScrollViewonMap() {
        ivAddressTransparentImage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        svAddEditAddress.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        svAddEditAddress.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        svAddEditAddress.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

//        mLastLocation = location;
//        if (mCurrLocationMarker != null) {
//            mCurrLocationMarker.remove();
//        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//        //move map camera
////        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
////        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
//
//        MarkerOptions markerOptions = new MarkerOptions()
//                .position(latLng)
//                .title("You are here")
//
//                .draggable(true);
//        mCurrLocationMarker = mMap.addMarker(markerOptions);

        if (task.toLowerCase().equals("add_new_address")) {
            latitude = latLng.latitude;
            longitude = latLng.longitude;
            moveCamera(latLng, DEFAULT_ZOOM, "You are here");

            geoLocate("", latitude, longitude);

        } else if (task.toLowerCase().contains("update_address")) {
            moveCamera(new LatLng(latitude, longitude), DEFAULT_ZOOM, addressLine);
        }

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        mGlobals.log(TAG, "onMarkerDrag(): Marker " + marker.getId() + " Drag@" + marker.getPosition());
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        mGlobals.log(TAG, "onMarkerDragStart(): Marker " + marker.getId() + " DragStart");
    }


    @Override
    public void onMarkerDragEnd(Marker marker) {
        DEFAULT_ZOOM = mMap.getCameraPosition().zoom;
        mGlobals.log(TAG, "onMarkerDragEnd(): Marker " + marker.getId() + " DragEnd");
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        geoLocate(null, marker.getPosition().latitude, marker.getPosition().longitude);
    }

    private void geoLocate(String search_string, Double latitude, Double longitude) { // Double latitude, Double longitude
        mGlobals.log(TAG, "geoLocate: geoLocating");
        mGlobals.toast("Loading your location");

        Geocoder geocoder = new Geocoder(AddEditAddressActivity.this, Locale.getDefault());

        List<Address> list = new ArrayList<>();
        try {
            if (latitude != null && longitude != null) {
                list = geocoder.getFromLocation(latitude, longitude, 1);
            } else {
                list = geocoder.getFromLocationName(search_string, 1);
            }

        } catch (IOException e) {
            mGlobals.log(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            mGlobals.log(TAG, "Found location: " + address.toString());
            mGlobals.log(TAG, "===========================================");
            mGlobals.log(TAG, "feature: " + address.getFeatureName());
            mGlobals.log(TAG, "admin: " + address.getAdminArea());
            mGlobals.log(TAG, "sub-admin: " + address.getSubAdminArea());
            mGlobals.log(TAG, "locality: " + address.getLocality());
            mGlobals.log(TAG, "sub-locality: " + address.getSubLocality());
            mGlobals.log(TAG, "thoroughfare: " + address.getThoroughfare());
            mGlobals.log(TAG, "sub-thoroughfare: " + address.getSubThoroughfare());
            mGlobals.log(TAG, "postalCode: " + address.getPostalCode());
            mGlobals.log(TAG, "===========================================");

            mGlobals.log(TAG, address.getAddressLine(0));
            addressLine = address.getAddressLine(0);
            llFullAddress.setVisibility(View.VISIBLE);
            tvFullAddress.setText(addressLine);

//            try {
//                String[] split = addressLine.split(",");
////                    mGlobals.dialog("country:" + split[split.length - 1]); // country
////                    mGlobals.dialog("province:" + split[split.length - 2]); // province
////                    mGlobals.dialog("city:" + split[split.length - 3]); // city
////                    mGlobals.dialog("addressLine:" + addressLine); // addressLine
//
//                String country_name = split[split.length - 1].replaceAll("[0-9]", "").trim();
//                String province_name = split[split.length - 2].replaceAll("[0-9]", "").trim();
//                String city_name = split[split.length - 3].replaceAll("[0-9]", "").trim();
//
//                if (province_name.equals("Kalakhang Maynila")) {
//                    province_name = "Metro Manila";
//                }
//
//                if (city_name.equals("Lungsod Quezon")) {
//                    city_name = "Quezon City";
//                }
//
//                moveCamera(new LatLng(latitude, longitude), DEFAULT_ZOOM, split[0] + ", " + split[1]);
//            } catch (Exception e) {
//                mGlobals.dialog("Please select a specific location.");
//            }

            moveCamera(new LatLng(latitude, longitude), DEFAULT_ZOOM, addressLine);
        } else {
            addressLine = search_string;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void getLocationPermission() {
        mGlobals.log(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.google_map);
                mapFragment.getMapAsync(this);
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mGlobals.log(TAG, "onRequestPermissionsResult: called.");

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    Boolean permissionDenied = false;
                    int failed = 0;
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            failed++;
                            mGlobals.log(TAG, "onRequestPermissionsResult: permission failed");
                        }

                        if (failed > 0) {
                            onBackPressed();
                        }
                    }

                    if (failed == 2) {
                        onBackPressed();
                    }

                    mGlobals.log(TAG, "onRequestPermissionsResult: permission granted");
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.google_map);
                    mapFragment.getMapAsync(this);
                }
            }
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        mGlobals.log(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {
            mMap.clear();

            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_location_user))
                    .draggable(true);

            mMap.addMarker(options).showInfoWindow();
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void validateFields() {
        edtAddressTitle.addTextChangedListener(new MyTextWatcher(edtAddressTitle));
        edtAddressCustomerName.addTextChangedListener(new MyTextWatcher(edtAddressCustomerName));
        edtAddressContactNumber.addTextChangedListener(new MyTextWatcher(edtAddressContactNumber));
        edtAddressHouseAddress.addTextChangedListener(new MyTextWatcher(edtAddressHouseAddress));
        edtAddressZipCode.addTextChangedListener(new MyTextWatcher(edtAddressZipCode));
        edtAddressLandmarks.addTextChangedListener(new MyTextWatcher(edtAddressLandmarks));
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

                case R.id.edt_address_title:
                    mGlobals.validateField(tilAddressTitle, edtAddressTitle, true, getString(R.string.err_msg_address_title));
                    break;
                case R.id.edt_address_customer_name:
                    mGlobals.validateField(tilAddressCustomerName, edtAddressCustomerName, true, getString(R.string.err_msg_address_dropoff_name));
                    break;
                case R.id.edt_address_contact_number:
                    mGlobals.validateField(tilAddressContactNumber, edtAddressContactNumber, true, getString(R.string.err_msg_address_dropoff_number));
                    break;
                case R.id.edt_address_house_address:
                    mGlobals.validateField(tilAddressHouseAddress, edtAddressHouseAddress, true, getString(R.string.err_msg_address_house_address));
                    tvFullAddress.setText(edtAddressHouseAddress.getText().toString()+" "+addressLine + " "+ edtAddressZipCode.getText().toString());
                    break;
                case R.id.edt_address_zip_code:
                    mGlobals.validateField(tilAddressZipCode, edtAddressZipCode, true, getString(R.string.err_msg_address_zip_code));
                    tvFullAddress.setText(edtAddressHouseAddress.getText().toString()+" "+addressLine + " "+ edtAddressZipCode.getText().toString());
                    break;
                case R.id.edt_address_landmarks:
                    mGlobals.validateField(tilAddressLandmarks, edtAddressLandmarks, true, getString(R.string.err_msg_address_landmarks));
                    break;

            }
        }
    }

}
