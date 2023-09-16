package com.kushina.customer.android.navigations;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.kushina.customer.android.navigations.home.SearchItemsActivity;
import com.kushina.customer.android.start_up_screens.LoginActivity;
import com.kushina.customer.android.R;
import com.kushina.customer.android.globals.API;
import com.kushina.customer.android.globals.Globals;
import com.kushina.customer.android.globals.MainReceiver;
import com.kushina.customer.android.globals.MainService;
import com.kushina.customer.android.globals.Preferences;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kushina.customer.android.globals.ui.CountDrawable;
import com.kushina.customer.android.navigations.cart.MyCartActivity;
import com.kushina.customer.android.navigations.notifications.NotificationsActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();

    API mAPI;
    Globals mGlobals;
    Preferences mPreferences;

    private int UPDATE_DIALOG_VISIBLE;
    MainReceiver receiver = new MainReceiver(new ResponseResult());

    TextInputEditText edtSearch;
    ImageView ivSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        getSupportActionBar().setElevation(0);

        ButterKnife.bind(this);
        mGlobals = new Globals(this);
        mAPI = new API(this);
        mPreferences = new Preferences(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_orders,
                R.id.navigation_ecash,
                R.id.navigation_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        ActionBar actionBar = getSupportActionBar();
        // add the custom view to the action bar
        actionBar.setCustomView(R.layout.actionbar_view);
        edtSearch = (TextInputEditText) actionBar.getCustomView().findViewById(
                R.id.edt_search);
        edtSearch.setFocusable(false);
        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchItemsActivity.class);
                startActivity(intent);
            }
        });

        ivSearch = (ImageView) actionBar.getCustomView().findViewById(
                R.id.iv_search);

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchItemsActivity.class);
                startActivity(intent);
            }
        });

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.menu_search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch(item.getItemId()){

            case R.id.action_my_cart:
                intent = new Intent(MainActivity.this, MyCartActivity.class);
                startActivity(intent);
                break;

            case R.id.action_notifications:
                intent = new Intent(MainActivity.this, NotificationsActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setCount(this, String.valueOf(mPreferences.getNotificationCount()), menu, R.id.action_notifications, R.id.ic_notification_count);
        setCount(this, String.valueOf(mPreferences.getCartItemCount()), menu, R.id.action_my_cart, R.id.ic_cart_count);
        return true;
    }

    public void setCount(Context context, String count, Menu defaultMenu, int menuID, int drawableCount) {
        MenuItem menuItem = defaultMenu.findItem(menuID);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(drawableCount);
        if (reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(drawableCount, badge);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Override this method in the activity that hosts the Fragment and call super
        // in order to receive the result inside onActivityResult from the fragment.
        super.onActivityResult(requestCode, resultCode, data);
//        if(data != null){
//            if(data.hasExtra("data")){
//                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                ivProfilePicture.setImageBitmap(bitmap);
//            }
//        }
    }

    public class ResponseResult {
        public void displayMessage(int resultCode, Bundle resultData){

            if(UPDATE_DIALOG_VISIBLE == 0){

                String title = resultData.getString("title");
                String message = resultData.getString("message");
                String task = resultData.getString("task");

//                mGlobals.toast(task);
                switch (task){
                    case "show_dialog":
                        UPDATE_DIALOG_VISIBLE = 1;
                        mGlobals.showDialog(title, message, true, new Globals.Callback() {
                            @Override
                            public void onPickCallback(Boolean result) {
                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        });
                        break;
                    case "update_badge":
                        invalidateOptionsMenu();
                        break;
                }

            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UPDATE_DIALOG_VISIBLE = 0;

        // start service
        Intent serviceIntent = new Intent(this, MainService.class);
        serviceIntent.putExtra("receiver", receiver);
        startService(serviceIntent);

    }

    @Override
    public void onBackPressed() {
        FragmentManager ft = getSupportFragmentManager();
//        mGlobals.toast(String.valueOf(ft.getBackStackEntryCount()));
        if(ft.getBackStackEntryCount() == 0){
           mGlobals.showChoiceDialog("You are about to exit. Continue?", true, new Globals.Callback() {
               @Override
               public void onPickCallback(Boolean result) {
                   if (result) {
//                       mPreferences.clearPreferences();
//                       Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                       startActivity(intent);
                       finish();
                   }
               }
           });

        } else {
            super.onBackPressed();
        }
    }

}
