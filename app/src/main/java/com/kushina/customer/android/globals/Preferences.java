package com.kushina.customer.android.globals;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.UUID;

public class Preferences {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "Kushina Customer";

    private static final String FIRST_LAUNCH = "FIRST_LAUNCH";
    private static final String IS_LOGGED = "IS_LOGGED";
    private static final String USER_ID = "USER_ID";
    private static final String USER_TOKEN = "USER_TOKEN";
    private static final String USER_CODE = "USER_CODE";
    private static final String PROFILE_PICTURE = "PROFILE_PICTURE";
    private static final String USERNAME = "USERNAME";
    private static final String FIRSTNAME = "FIRSTNAME";
    private static final String MIDDLENAME = "MIDDLENAME";
    private static final String LASTNAME = "LASTNAME";
    private static final String MOBILE = "MOBILE";
    private static final String EMAIL = "EMAIL";
    private static final String MEMBERSHIP_TYPE_ID = "MEMBERSHIP_TYPE_ID";
    private static final String MEMBERSHIP_TYPE = "MEMBERSHIP_TYPE";
    private static final String NOTIFICATION_COUNT = "NOTIFICATION_COUNT";
    private static final String TOKEN_EXPIRATION = "TOKEN_EXPIRATION";
    private static final String CURRENT_RANK = "CURRENT_RANK";
    private static final String CART_ITEM_COUNT = "CART_ITEMN_COUNT";


    private static String uniqueID = null;
    private static final String UNIQUE_ID = "UNIQUE_ID";

    public Preferences(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public void clearPreferences(){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public void setFirstLaunch(boolean isFirstTime) {
        editor = pref.edit();
        editor.putBoolean(FIRST_LAUNCH, isFirstTime);
        editor.apply();
    }

    public boolean isFirstLaunch() {
        return pref.getBoolean(FIRST_LAUNCH, true);
    }

    public void setTokenExpiration(String expiration) {
        editor = pref.edit();
        editor.putString(TOKEN_EXPIRATION, expiration);
        editor.apply();
    }

    public String getTokenExpiration() {
        return pref.getString(TOKEN_EXPIRATION, "");
    }

    public synchronized static String getUniqueID(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(UNIQUE_ID, uniqueID);
                editor.apply();
            }
        }    return uniqueID;
    }

    public void setUserId(Integer user_id){
        editor = pref.edit();
        editor.putInt(USER_ID, user_id);
        editor.apply();
    }

    public synchronized Integer getUserId(){
        return pref.getInt(USER_ID, 0);
    }

    public void setUserToken(String token){
        editor = pref.edit();
        editor.putString(USER_TOKEN, token);
        editor.apply();
    }

    public synchronized String getUserToken(){
        return pref.getString(USER_TOKEN, "");
    }

    public void setIsLogged(boolean status){
        editor = pref.edit();
        editor.putBoolean(IS_LOGGED, status);
        editor.apply();
    }

    public synchronized boolean isLogged(){
        return pref.getBoolean(IS_LOGGED, false);
    }

    public void setUserCode(String code){
        editor = pref.edit();
        editor.putString(USER_CODE, code);
        editor.apply();
    }

    public synchronized String getUserCode(){
        return pref.getString(USER_CODE, "");
    }

    public void setUserProfilePicture(String profilePicture){
        editor = pref.edit();
        editor.putString(PROFILE_PICTURE, profilePicture);
        editor.apply();
    }

    public synchronized String getUserProfilePicture(){
        return pref.getString(PROFILE_PICTURE, "");
    }

    public void setUsername(String stringToSave){
        editor = pref.edit();
        editor.putString(USERNAME, stringToSave);
        editor.apply();
    }

    public synchronized String getUsername(){
        return pref.getString(USERNAME, "");
    }

    public void setFirstname(String stringToSave){
        editor = pref.edit();
        editor.putString(FIRSTNAME, stringToSave);
        editor.apply();
    }

    public synchronized String getFirstname(){
        return pref.getString(FIRSTNAME, "");
    }

    public void setMiddlename(String stringToSave){
        editor = pref.edit();
        editor.putString(MIDDLENAME, stringToSave);
        editor.apply();
    }

    public synchronized String getMiddlename(){
        return pref.getString(MIDDLENAME, "");
    }

    public void setLastname(String stringToSave){
        editor = pref.edit();
        editor.putString(LASTNAME, stringToSave);
        editor.apply();
    }

    public synchronized String getLastname(){
        return pref.getString(LASTNAME, "");
    }

    public void setMobile(String stringToSave){
        editor = pref.edit();
        editor.putString(MOBILE, stringToSave);
        editor.apply();
    }

    public synchronized String getMobile(){
        return pref.getString(MOBILE, "");
    }

    public void setEmail(String stringToSave){
        editor = pref.edit();
        editor.putString(EMAIL, stringToSave);
        editor.apply();
    }

    public synchronized String getEmail(){
        return pref.getString(EMAIL, "");
    }

    public void setMembershipTypeId(String stringToSave){
        editor = pref.edit();
        editor.putString(MEMBERSHIP_TYPE_ID, stringToSave);
        editor.apply();
    }

    public synchronized String getMembershipTypeId(){
        return pref.getString(MEMBERSHIP_TYPE_ID, "");
    }

    public void setMembershipType(String stringToSave){
        editor = pref.edit();
        editor.putString(MEMBERSHIP_TYPE, stringToSave);
        editor.apply();
    }

    public synchronized String getMembershipType(){
        return pref.getString(MEMBERSHIP_TYPE, "");
    }

    public void setNotificationCount(int count){
        editor = pref.edit();
        editor.putInt(NOTIFICATION_COUNT, count);
        editor.apply();
    }

    public synchronized int getNotificationCount(){
        return pref.getInt(NOTIFICATION_COUNT, 0);
    }

    public void setCurrentRank(String rank){
        editor = pref.edit();
        editor.putString(CURRENT_RANK, rank);
        editor.apply();
    }

    public synchronized String getCurrentRank(){
        return pref.getString(CURRENT_RANK, "");
    }

    public void setCartItemCount(int count){
        editor = pref.edit();
        editor.putInt(CART_ITEM_COUNT, count);
        editor.apply();
    }

    public synchronized int getCartItemCount(){
        return pref.getInt(CART_ITEM_COUNT, 0);
    }
}