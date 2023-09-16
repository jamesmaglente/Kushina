package com.kushina.customer.android.globals;

public class Endpoints {

    // node endpoints
    public static final String NODE_PROTOCOL = "http://";
    public static final String NODE_BASE_URL = "tacticalcodes.xyz"; // 10.0.2.2 | tacticalcodes.xyz | localhost
    public static final String NODE_PORT = "3011";
    public static final String NODE_PATH = "api";
    public static final String NODE_MAIN_URL = NODE_PROTOCOL + NODE_BASE_URL + ":" + NODE_PORT + "/" + NODE_PATH + "/";

    public static final String API_NODE_LOGIN = NODE_MAIN_URL + "user/login";
    public static final String API_NODE_SHOP = NODE_MAIN_URL + "shop/";
    public static final String API_NODE_PROFILE = NODE_MAIN_URL + "profile/";
    public static final String API_NODE_ECASH = NODE_MAIN_URL + "ecash/";
    public static final String API_NODE_ORDERS = NODE_MAIN_URL + "order/";
    public static final String API_NODE_DASHBOARD = NODE_MAIN_URL + "dashboard/";
    public static final String API_NODE_E_WALLET = NODE_MAIN_URL + "ewallet/";
    public static final String API_NODE_NOTIFICATION = NODE_MAIN_URL + "notification/";
    public static final String API_NODE_REWARD = NODE_MAIN_URL + "reward/";
    public static final String API_NODE_ITEMS = NODE_MAIN_URL + "item/";

    public static final String API_NODE_USER = NODE_MAIN_URL + "user/";



    public static final String ITEMS_URL = NODE_PROTOCOL + NODE_BASE_URL + "/image/item/";
    public static final String DEPOSIT_URL = NODE_PROTOCOL + NODE_BASE_URL + "/image/deposit/";
}