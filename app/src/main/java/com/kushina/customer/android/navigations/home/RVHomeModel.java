package com.kushina.customer.android.navigations.home;

public class RVHomeModel {

    private String itemID;
    private String codeID;
    private String categoryID;
    private String category;
    private String itemName;
    private String longDescription;
    private String image;
    private String sku;
    private String srp;
    private String toque;
    private String quantity;
    private String merchantID;
    private String rating;
    private String likes;
    private String status;
    private String dateCreated;

    public RVHomeModel(String itemID, String codeID, String categoryID, String category, String itemName, String longDescription, String image, String sku, String srp, String toque, String quantity, String merchantID, String rating, String likes, String status, String dateCreated) {
        this.itemID = itemID;
        this.codeID = codeID;
        this.categoryID = categoryID;
        this.category = category;
        this.itemName = itemName;
        this.longDescription = longDescription;
        this.image = image;
        this.sku = sku;
        this.srp = srp;
        this.toque = toque;
        this.quantity = quantity;
        this.merchantID = merchantID;
        this.rating = rating;
        this.likes = likes;
        this.status = status;
        this.dateCreated = dateCreated;
    }

    public String getItemID() {
        return itemID;
    }

    public String getCodeID() {
        return codeID;
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

    public String getLongDescription() {
        return longDescription;
    }

    public String getImage() {
        return image;
    }

    public String getSku() {
        return sku;
    }

    public String getSrp() {
        return srp;
    }

    public String getToque() {
        return toque;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getMerchantID() {
        return merchantID;
    }

    public String getRating() {
        return rating;
    }

    public String getLikes() {
        return likes;
    }

    public String getStatus() {
        return status;
    }

    public String getDateCreated() {
        return dateCreated;
    }
}
