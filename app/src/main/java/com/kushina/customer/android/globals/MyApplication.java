package com.kushina.customer.android.globals;


import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.data.StringFormat;
import com.kushina.customer.android.R;

//import org.acra.BuildConfig;
//import org.acra.config.CoreConfigurationBuilder;
//import org.acra.config.DialogConfigurationBuilder;

//@ReportsCrashes(mailTo = "danchrnez@gmail.com")
//@ReportsCrashes(mailTo = "danchrnez@gmail.com", // my email here
//        mode = ReportingInteractionMode.TOAST,
//        resToastText = R.string.acra_toast_text)
@AcraCore(buildConfigClass = BuildConfig.class,
        reportFormat = StringFormat.JSON)
public class MyApplication extends Application {

    public static final String CHANNEL_ID = "Kushina";

    public void onCreate(){
        super.onCreate();

        createNotificationChannel();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

//        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this);
//        builder.setBuildConfigClass(BuildConfig.class).setReportFormat(StringFormat.JSON);
//        builder.getPluginConfigurationBuilder(DialogConfigurationBuilder.class).setCommentPrompt("comment");
//        builder.getPluginConfigurationBuilder(DialogConfigurationBuilder.class).setEmailPrompt("danchrnez@gmail.com");
//        builder.getPluginConfigurationBuilder(DialogConfigurationBuilder.class).setEnabled(true);
//        builder.getPluginConfigurationBuilder(DialogConfigurationBuilder.class).setNegativeButtonText("negative btn");
//        builder.getPluginConfigurationBuilder(DialogConfigurationBuilder.class).setPositiveButtonText("positive btn");
//        builder.getPluginConfigurationBuilder(DialogConfigurationBuilder.class).setText("text");
//        builder.getPluginConfigurationBuilder(DialogConfigurationBuilder.class).setTitle("title");
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name) + " Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
