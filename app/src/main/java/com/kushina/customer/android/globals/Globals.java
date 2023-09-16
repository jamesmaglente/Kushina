package com.kushina.customer.android.globals;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.textfield.TextInputLayout;
import com.kushina.customer.android.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Globals extends AppCompatActivity {

    public static final String project_status = "development"; // development || production

    Boolean status = false;
    Context mContext;
    AlertDialog alertDialog;
    AlertDialog loadingDialog;

    // constructor
    public Globals(Context context){
        this.mContext = context;
    }

    // set callback
    public interface Callback{
        void onPickCallback(Boolean result);
    }

    public View alert(final String title,
                      final String message,
                      final String positive_choice,
                      final String negative_choice,
                      final Integer layout,
                      final Context context,
                      final Boolean wantToCloseDialog,
                      final Callback callback){
        View viewInflated = null;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false);

        if(title != null){
            alertDialogBuilder.setTitle(title);
        }

        if(message != null){
            alertDialogBuilder.setMessage(message);
        }

        if(layout != null){
            viewInflated = LayoutInflater.from(context).inflate(layout, null);
            alertDialogBuilder.setView(viewInflated);
        }

        if(positive_choice != null){
            alertDialogBuilder.setPositiveButton(positive_choice,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            callback.onPickCallback(true);
                        }
                    });
        }

        if(negative_choice != null){
            alertDialogBuilder.setNegativeButton(negative_choice, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callback.onPickCallback(false);
                }
            });
        }

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callback.onPickCallback(true);
                //Do stuff, possibly set wantToCloseDialog to true then...
                if(wantToCloseDialog)
                    alertDialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
        return viewInflated;
    }

    public void dialog(String text, Context context){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Notice:");
        alertDialogBuilder.setMessage(text);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void toast(String text){
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    public boolean validateField(TextInputLayout textInputLayout, EditText editText, Boolean condition, String error_message) {
        if (editText.getText().toString().trim().isEmpty() || !condition) {

            try {
//                toast(String.valueOf(editText.getError()) + ":" + String.valueOf(textInputLayout.isErrorEnabled()));
                if(textInputLayout.isErrorEnabled()){
                    if(editText.getError() != null){

                        if(!editText.getError().equals(error_message)){
                            textInputLayout.setError(error_message);
                            editText.requestFocus();
                            return false;
                        }
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            textInputLayout.setError(error_message);
            editText.requestFocus();

            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validate(TextInputLayout textInputLayout, Boolean condition, String error_message) {
        if (!condition) {
            textInputLayout.setError(error_message);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean removeError(TextInputLayout layout){
        layout.setErrorEnabled(false);
        return true;
    }

    public void log(String tag, String message){

        if(project_status.equals("development")){
            // log error results
            for( String line : message.split("\n") ) {
                Log.d(tag, line);
            }
        }
    }

    public String getCurrentDate(String format){
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(format);
        String formattedDate = df.format(date);
        return  formattedDate;
    }

    public void okMessage(String title, String message, Context context){
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public String moneyFormatter(String amount){
        NumberFormat formatter = new DecimalFormat("#,###");
        String totalAmount = String.format("%.2f",Double.valueOf(amount));
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        formatter.setCurrency(Currency.getInstance(Locale.getDefault()));
        String formattedTotal;
        if(Double.valueOf(amount) != 0) {
            formattedTotal = formatter.format(Double.valueOf(totalAmount));
        }else{
            formattedTotal = "0.00";
        }
        return "₱ " +formattedTotal;
    }

    public String twoDecimalFormatter(String number){
        NumberFormat formatter = new DecimalFormat("#,###");
        String totalAmount = String.format("%.2f",Double.valueOf(number));
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        formatter.setCurrency(Currency.getInstance(Locale.getDefault()));
        String formattedTotal;
        if(Double.valueOf(number) != 0) {
            formattedTotal = formatter.format(Double.valueOf(totalAmount));
        }else{
            formattedTotal = "0.00";
        }

        return  formattedTotal;
    }

    public String dateFormatter(String dateToParse){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        inputFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String dateStr = String.valueOf(dateToParse);
        Date date = null;
        String niceDateStr = "";
        try {
            date = inputFormat.parse(dateStr);
            niceDateStr = DateUtils.getRelativeTimeSpanString(date.getTime() , Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
            if(niceDateStr.equals("0 minutes ago")){
                niceDateStr = "a moment ago";
            }else if(niceDateStr.equals("In 0 minutes")){
                niceDateStr = "a moment ago";
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return niceDateStr;

    }

    public String dateFormatterNew(String dateToParse){
        String niceDateStr = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String dateInString = dateToParse;

        SimpleDateFormat formatterOut = new SimpleDateFormat("MMM d, yyyy h:mm a");


        try {
            Date date = formatter.parse(dateInString);
            niceDateStr = formatterOut.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return niceDateStr;


    }


    public void dtoast(String text){
        if(project_status.equals("development")) {
            Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void initilizeAdMob(){
        MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

    }


//    public boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }
//
//    public boolean isNetworkConnected() {
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
//    }
//
//    public boolean isInternetAvailable() {
//        try {
//            InetAddress ipAddr = InetAddress.getByName("google.com");
//            //You can replace it with your name
//            return !ipAddr.equals("");
//
//        } catch (Exception e) {
//            return false;
//        }
//    }

    public void dialog(String text){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage(text);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void cursorDump(String tag, Cursor cursor){
        log(tag,"========================================");
        log(tag, DatabaseUtils.dumpCursorToString(cursor));
        log(tag,"========================================");
    }

    public View showSuccessDialog(
            final String message,
            final Boolean wantToCloseDialog,
            final Callback callback){
        View viewInflated = null;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alertDialogBuilder.setCancelable(false);

        viewInflated = LayoutInflater.from(mContext).inflate(R.layout.success_dialog, null);
        alertDialogBuilder.setView(viewInflated);

        if(message != null){
            ((TextView) viewInflated.findViewById(R.id.tv_dialog_message)).setText(message);
        }

        alertDialog = alertDialogBuilder.create();
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
        viewInflated.findViewById(R.id.btn_dialog_action).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callback.onPickCallback(true);
                //Do stuff, possibly set wantToCloseDialog to true then...
                if(wantToCloseDialog)
                    alertDialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
        return viewInflated;
    }

    public View showChoiceDialog(
            final String message,
            final Boolean wantToCloseDialog,
            final Callback callback){
        View viewInflated = null;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alertDialogBuilder.setCancelable(false);

        viewInflated = LayoutInflater.from(mContext).inflate(R.layout.choice_dialog, null);
        alertDialogBuilder.setView(viewInflated);

        if(message != null){
            ((TextView) viewInflated.findViewById(R.id.tv_dialog_message)).setText(message);
        }

        alertDialog = alertDialogBuilder.create();
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
        viewInflated.findViewById(R.id.btn_dialog_action_yes).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callback.onPickCallback(true);
                //Do stuff, possibly set wantToCloseDialog to true then...
                if(wantToCloseDialog)
                    alertDialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });

        viewInflated.findViewById(R.id.btn_dialog_action_no).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callback.onPickCallback(false);
                alertDialog.dismiss();
            }
        });
        return viewInflated;
    }

    public View showChoiceDialogwithCustomTitle(
            final String title,
            final String message,
            final Boolean wantToCloseDialog,
            final Callback callback){
        View viewInflated = null;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alertDialogBuilder.setCancelable(false);

        viewInflated = LayoutInflater.from(mContext).inflate(R.layout.choice_dialog, null);
        alertDialogBuilder.setView(viewInflated);

        if(title != null){
            ((TextView) viewInflated.findViewById(R.id.tv_dialog_title)).setText(title);
        }

        if(message != null){
            ((TextView) viewInflated.findViewById(R.id.tv_dialog_message)).setText(message);
        }

        alertDialog = alertDialogBuilder.create();
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
        viewInflated.findViewById(R.id.btn_dialog_action_yes).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callback.onPickCallback(true);
                //Do stuff, possibly set wantToCloseDialog to true then...
                if(wantToCloseDialog)
                    alertDialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });

        viewInflated.findViewById(R.id.btn_dialog_action_no).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //callback.onPickCallback(false);
                alertDialog.dismiss();
            }
        });
        return viewInflated;
    }



    public View showErrorMessage(
            final String message,
            final Boolean wantToCloseDialog,
            final Callback callback){
        View viewInflated = null;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alertDialogBuilder.setCancelable(false);

        viewInflated = LayoutInflater.from(mContext).inflate(R.layout.error_dialog, null);
        alertDialogBuilder.setView(viewInflated);

        if(message != null){
            ((TextView) viewInflated.findViewById(R.id.tv_dialog_message)).setText(message);
        }

        alertDialog = alertDialogBuilder.create();
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
        viewInflated.findViewById(R.id.btn_dialog_action).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callback.onPickCallback(true);
                //Do stuff, possibly set wantToCloseDialog to true then...
                if(wantToCloseDialog)
                    alertDialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
        return viewInflated;
    }


    public View showLoadingDialog(){
        View viewInflated = null;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alertDialogBuilder.setCancelable(false);

        viewInflated = LayoutInflater.from(mContext).inflate(R.layout.loading_dialog, null);
        alertDialogBuilder.setView(viewInflated);

        loadingDialog = alertDialogBuilder.create();

        if(loadingDialog.getWindow() != null){
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }


        loadingDialog.show();

        return viewInflated;
    }

    public void dismissLoadingDialog(){
        loadingDialog.dismiss();
    }

    public void showChoiceDialogWithDelay(final String message,
                                          final Boolean wantToCloseDialog,
                                          final Callback callback){
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                showChoiceDialog(message,wantToCloseDialog,callback);
            }
        }, 100 );//time in milisecond
    }

    public void showErrorMessageWithDelay(final String message,
                                           final Boolean wantToCloseDialog,
                                           final Callback callback){
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
               showErrorMessage(message,wantToCloseDialog,callback);
            }
        }, 100 );//time in milisecond
    }

    public View showDialog(
            final String title,
            final String message,
            final Boolean wantToCloseDialog,
            final Callback callback){
        View viewInflated = null;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        alertDialogBuilder.setCancelable(false);

        viewInflated = LayoutInflater.from(mContext).inflate(R.layout.dialog, null);
        alertDialogBuilder.setView(viewInflated);

        if(title != null){
            ((TextView) viewInflated.findViewById(R.id.tv_dialog_title)).setText(title);
        }

        if(message != null){
            ((TextView) viewInflated.findViewById(R.id.tv_dialog_message)).setText(message);
        }

        alertDialog = alertDialogBuilder.create();
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
        viewInflated.findViewById(R.id.btn_dialog_action).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callback.onPickCallback(true);
                //Do stuff, possibly set wantToCloseDialog to true then...
                if(wantToCloseDialog)
                    alertDialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
        return viewInflated;
    }

}