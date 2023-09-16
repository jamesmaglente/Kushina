package com.kushina.customer.android.globals;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class API extends AppCompatActivity {
    public final String TAG = getClass().getSimpleName();

    public Object[] emptyObject = {};

    Globals mGlobals;
    Context mContext;
    Preferences mPreferences;

    private SpotsDialog loadingDialog;

    // constructor
    public API(Context context){
        this.mContext = context;
    }

    // set callback
    public interface VolleyCallback{
        void onResponseCallback(JSONObject result);
    }

    public void api_request(final String request_method,
                            final String request_url,
                            final Map<String, String> request_data,
                            final Boolean loading,
                            final Context context,
                            final VolleyCallback callback){

        mGlobals = new Globals(context);
        mPreferences = new Preferences(context);


        if(loading){
            mGlobals.showLoadingDialog();
//            loadingDialog = new SpotsDialog(context, R.style.Custom);
//            loadingDialog.setCancelable(false);
//            loadingDialog.show();
        }

        RequestQueue MyRequestQueue = Volley.newRequestQueue(context);

        Integer method = (request_method == "POST") ? Request.Method.POST : Request.Method.GET;


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method,request_url, new JSONObject(request_data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // log response
                        for( String line : response.toString().split("\n") ) {
                            mGlobals.log(TAG, line );
                        }
                        if(loading){
                            mGlobals.dismissLoadingDialog();
                        }
                        //This code is executed if the server responds, whether or not the response contains data.
                        //The String 'response' contains the server's response.
                        callback.onResponseCallback(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // log error results
                for( String line : error.toString().split("\n") ) {
                    mGlobals.log(TAG, line );
                }

                String status_message = "";

                NetworkResponse response = error.networkResponse;
                try {
                    String res = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    // Now you can use any deserializer to make sense of data
                    JSONObject obj = new JSONObject(res);
                    mGlobals.log("KOK",String.valueOf(res));
                    status_message = obj.getString("error");
//                    mGlobals.dialog(status_message);
                } catch (UnsupportedEncodingException e1) {
                    // Couldn't properly decode data to string
                    mGlobals.log(TAG, String.valueOf(e1));
                    e1.printStackTrace();
                } catch (JSONException e2) {
                    // returned data is not JSONObject?
                    mGlobals.log(TAG, String.valueOf(e2));
                    e2.printStackTrace();
                } catch (NullPointerException e){
                    mGlobals.log(TAG,String.valueOf(e));
                }

                // check for error response
                if (error instanceof NetworkError) {
                   try {
                       mGlobals.log(TAG, "NETWORK ERROR");
                       mGlobals.showErrorMessage("Internet connection error. Please check your connection and try again.",
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
                   } catch (Exception e){
                       e.printStackTrace();
                   }
                } else if (error instanceof ServerError) {
                    try{
                        mGlobals.log(TAG, "SERVER ERROR");
                        mGlobals.showErrorMessage(status_message,
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
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (error instanceof AuthFailureError) {
                    try {
                        mGlobals.log(TAG, "AUTH FAILURE ERROR");
                        NetworkResponse errorRes = error.networkResponse;
                        String stringData = "";
                        if(errorRes != null && errorRes.data != null){
                            stringData = new String(errorRes.data, StandardCharsets.UTF_8);
                        }
                        JsonObject jsonObject = new JsonParser().parse(stringData).getAsJsonObject();
                        status_message = jsonObject.get("error").getAsString();
                        Log.e("Error",stringData);

                        mGlobals.showErrorMessage(status_message,
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
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (error instanceof ParseError) {
                    mGlobals.log(TAG, "PARSE ERROR");
                } else if (error instanceof NoConnectionError) {
                    try {
                        mGlobals.log(TAG, "NO CONNECTION ERROR");
                        mGlobals.showErrorMessage("Internet connection error. Please check your connection and try again.",
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
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (error instanceof TimeoutError) {
                    try {
                        mGlobals.log(TAG, "TIMEOUT ERROR");
                        mGlobals.showErrorMessage("Request Timeout.",
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
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    try {
                        mGlobals.showErrorMessage("Internet connection error. Please check your connection and try again.",
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
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if(loading){
                   // loadingDialog.dismiss();
                    mGlobals.dismissLoadingDialog();
                }
            }

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", "Bearer " + mPreferences.getUserToken());
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

//        // send api request
//        MyRequestQueue.add(MyStringRequest);

        // send api request
        MyRequestQueue.add(jsonObjectRequest);

    }
}
