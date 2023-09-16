package com.kushina.customer.android.globals;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.toolbox.StringRequest;
import com.kushina.customer.android.R;
import com.kushina.customer.android.navigations.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.kushina.customer.android.globals.Endpoints.API_NODE_NOTIFICATION;
import static com.kushina.customer.android.globals.MyApplication.CHANNEL_ID;

public class MainService extends IntentService {

    private Context mContext;

    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    public final String TAG = getClass().getSimpleName();
    private final int INTERVAL = 1000 * 2;
    private StringRequest MyStringRequest;

    public MainService(){
        super("Main Service");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mContext = this;

        mAPI = new API(mContext);
        mGlobals = new Globals(mContext);
        mPreferences = new Preferences(mContext);

        Log.v(TAG, "Main service has started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent){
        int i = 0;
        if(intent == null){

            while(true){
                // Log.v(TAG, "i (intent is null) = "+ i);

                i++;
                try{
                    Thread.sleep(INTERVAL);
                } catch (Exception e){
                    break;
                }

                if(mPreferences.isLogged()){
                    getNotifications(intent);
                }

            }
            return;
        }

        while(true){
            // Log.v(TAG, "i (intent is not null) = " + i);

            i++;
            try{
                Thread.sleep(INTERVAL);
            } catch (Exception e){
                break;
            }

            if(mPreferences.isLogged()){
                getNotifications(intent);
            }

        }


    }

    public void getNotifications(Intent intent){

        String current_version = "";
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(getPackageName(), 0);
            current_version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String androidVersion = android.os.Build.VERSION.RELEASE; // e.g. androidVersion := "1.6"
        int sdkVersion = android.os.Build.VERSION.SDK_INT; // e.g. sdkVersion := 8;
//        mGlobals.toast(androidVersion + ":" + sdkVersion);

        // setup request data
        final Map<String, String> request_data = new HashMap<String, String>();
        //request_data.put("task", "check_application_version"); //Add the data you'd like to send to the server.
        request_data.put("application", "kushina_customer");
        request_data.put("user_id", String.valueOf(mPreferences.getUserId()));
        request_data.put("application_version", current_version);
        request_data.put("android_version", androidVersion);
        request_data.put("sdk_version", String.valueOf(sdkVersion));



        try {
            mAPI.api_request("POST",
                    API_NODE_NOTIFICATION+"checkApplicationVersion",
                    request_data,
                    false,
                    mContext,
                    new API.VolleyCallback() {
                        @Override
                        public void onResponseCallback(JSONObject result) {

                            mGlobals.log(TAG, String.valueOf(result));


                            try {
                                // parse response object
                                // JSONObject jsonObject = result.getJSONObject("data");
                                String status_message = result.getString("status_message");

                                Integer status_code = result.getInt("status_code");

                                if (status_code == 200) {
                                    //parse response object

                                    JSONObject data = result.getJSONObject("data");
                                    JSONObject application_info = data.getJSONObject("application_info");
                                    String title = application_info.getString("title");
                                    String description = application_info.getString("description");
                                    Boolean up_to_date = application_info.getBoolean("up_to_date");
                                    JSONArray notifications = data.getJSONArray("notifications");
                                    int total_unread_notifications = data.getInt("total_unread_notifications");
                                    int total_cart_items = data.getInt("total_cart_items");

                                    mPreferences.setNotificationCount(total_unread_notifications);
                                    mPreferences.setCartItemCount(total_cart_items);

//                                if(!up_to_date){
                                    ResultReceiver receiver = intent.getParcelableExtra("receiver");
                                    Bundle bundle = new Bundle();
                                    bundle.putString("title", title);
                                    bundle.putString("message", description);
                                    bundle.putString("task", up_to_date ? "update_badge" : "show_dialog");

                                    receiver.send(1234, bundle);
//                                }

                                    mGlobals.log(TAG, String.valueOf(notifications.length()));
                                    if(notifications.length() > 0){
                                        for(int i = 0; i < notifications.length(); i++){
                                            int notification_id = ((JSONObject) notifications.get(i)).getInt("notification_id");
                                            String notification_title = ((JSONObject) notifications.get(i)).getString("title");
                                            String notification_description = ((JSONObject) notifications.get(i)).getString("description");
                                            createBasicNotification(notification_id, notification_title, notification_description);
                                        }
                                    }


                                } else {
                                    mGlobals.log(TAG, "onResponseCallback: " + status_message);
                                }
                            } catch (Exception e) {
                                // show exception error
                                mGlobals.log(TAG, e.toString());
                            }
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }


//        String current_version = "";
//        try {
//            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(getPackageName(), 0);
//            current_version = pInfo.versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        // setup request data
//        final Map<String, String> request_data = new HashMap<String, String>();
//        //request_data.put("task", "check_application_version"); //Add the data you'd like to send to the server.
//        request_data.put("application", "manong_rider");
//        request_data.put("user_id", String.valueOf(mPreferences.getUserId()));
//        request_data.put("application_version", current_version);
//
//        RequestQueue MyRequestQueue = Volley.newRequestQueue(mContext);
////        RequestQueue MyRequestQueue = VolleySingleton.getInstance(mContext).getmRequestQueue();
//
//        MyStringRequest = new StringRequest(Request.Method.POST, API_NODE_NOTIFICATIONS+"checkApplicationVersion", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                //This code is executed if the server responds, whether or not the response contains data.
//                //The String 'response' contains the server's response.
//                mGlobals.log(TAG, response);
//
//                try {
//                    // parse response object
//                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
//                    JsonObject data = jsonObject.get("data").getAsJsonObject();
//
//                    JsonObject application_info = data.get("application_info").getAsJsonObject();
//                    String title = application_info.get("title").getAsString();
//                    String description = application_info.get("description").getAsString();
//                    Boolean up_to_date = application_info.get("up_to_date").getAsBoolean();
//                    JsonArray notifications = data.get("notifications").getAsJsonArray();
//
//                    if(!up_to_date){
//                        ResultReceiver receiver = intent.getParcelableExtra("receiver");
//                        Bundle bundle = new Bundle();
//                        bundle.putString("title", title);
//                        bundle.putString("message", description);
//
//                        receiver.send(1234, bundle);
//                    }
//
//                    if(notifications.size() > 0){
//                        for(int i = 0; i < notifications.size(); i++){
//                            int notification_id = ((JsonObject) notifications.get(i)).get("notification_id").getAsInt();
//                            String notification_title = ((JsonObject) notifications.get(i)).get("title").toString();
//                            String notification_description = ((JsonObject) notifications.get(i)).get("description").toString();
//                            createBasicNotification(notification_id, notification_title, notification_description);
//                        }
//                    }
//
//                } catch(Exception e) {
//                    // show exception error
//                    mGlobals.log(TAG, e.toString());
//                    mGlobals.log(TAG, response);
//                }
//            }
//        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                // log error results
//                for( String line : error.toString().split("\n") ) {
//                    Log.d( "api_request(): ", line );
//                }
//
//            }
//        }) {
//            // add request data
//            protected Map<String, String> getParams() {
//                return request_data;
//            }
//        };
//
//        // send api request
//        MyRequestQueue.add(MyStringRequest);
    }

    public void createNotification(){
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this);
        nb.setContentText("Timer done");
        nb.setContentTitle("Hi!");
        nb.setSmallIcon(R.mipmap.ic_launcher);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        nb.setSound(alarmSound);
        nb.setDefaults(NotificationCompat.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= 21) nb.setVibrate(new long[0]);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1, nb.build());
    }

    public void createBasicNotification(Integer notification_id, String title, String description){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_kushina_logo_v3);
        builder.setContentTitle(title);
        builder.setContentText(description);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

//        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));

        if (Build.VERSION.SDK_INT >= 21) builder.setVibrate(new long[0]);

        Intent notificationIntent = new Intent(mContext, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("data", "fromoutside");
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notification_id, builder.build());
    }

    public void redirectToActivity(){
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_none_black_24dp)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());

    }

    // Key for the string that's delivered in the action's intent.
    private static final String KEY_TEXT_REPLY = "key_text_reply";

//    public void replyNotification(){
//
//        String replyLabel = "Reply";
//        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
//                .setLabel(replyLabel)
//                .build();
//
//        // Build a PendingIntent for the reply action to trigger.
//        PendingIntent replyPendingIntent =
//                PendingIntent.getBroadcast(getApplicationContext(),
//                        conversation.getConversationId(),
//                        getMessageReplyIntent(conversation.getConversationId()),
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Create the reply action and add the remote input.
//        NotificationCompat.Action action =
//                new NotificationCompat.Action.Builder(R.drawable.ic_reply_icon,
//                        getString(R.string.label), replyPendingIntent)
//                        .addRemoteInput(remoteInput)
//                        .build();
//
//        // Build the notification and add the action.
//        MyApplication newMessageNotification = new MyApplication.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_message)
//                .setContentTitle(getString(R.string.title))
//                .setContentText(getString(R.string.content))
//                .addAction(action)
//                .build();
//
//// Issue the notification.
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(notificationId, newMessageNotification);
//
//
//
//    }
}
