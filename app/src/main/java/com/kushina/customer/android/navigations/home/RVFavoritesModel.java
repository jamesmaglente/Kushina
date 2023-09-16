package com.kushina.customer.android.navigations.home;

public class RVFavoritesModel {

    private String favoriteID;
    private String itemID;
    private String categoryID;
    private String category;
    private String itemName;
    private String itemImage;
    private String itemAmount;
    private String quantity;
    private String status;
    private String dateCreated;


    public RVFavoritesModel(String favoriteID, String itemID, String categoryID, String category, String itemName, String itemImage, String itemAmount, String quantity, String status, String dateCreated) {
        this.favoriteID = favoriteID;
        this.itemID = itemID;
        this.categoryID = categoryID;
        this.category = category;
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.itemAmount = itemAmount;
        this.quantity = quantity;
        this.status = status;
        this.dateCreated = dateCreated;
    }

    public String getFavoriteID() {
        return favoriteID;
    }

    public String getItemID() {
        return itemID;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public String getCategory() {
        return category;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public String getItemAmount() {
        return itemAmount;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public String getDateCreated() {
        return dateCreated;
    }
}
