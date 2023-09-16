package com.kushina.customer.android.navigations.cart;

public class RVMyCartModel {

    private String cartItemID;
    private String itemID;
    private String itemQTY;
    private String remarks;
    private String codeID;
    private String currentStock;
    private String totalAmount;
    private String status;
    private String categoryID;
    private String category;
    private String itemName;
    private String longDescription;
    private String itemPicture;
    private String sku;
    private String itemPrice;
    private String discount;
    private String tax;
    private String likes;
    private String merchantID;
    private String dateCreated;

    public RVMyCartModel(String cartItemID, String itemID, String itemQTY, String remarks, String codeID, String currentStock, String totalAmount, String status, String categoryID, String category, String itemName, String longDescription, String itemPicture, String sku, String itemPrice, String discount, String tax, String likes, String merchantID, String dateCreated) {
        this.cartItemID = cartItemID;
        this.itemID = itemID;
        this.itemQTY = itemQTY;
        this.remarks = remarks;
        this.codeID = codeID;
        this.currentStock = currentStock;
        this.totalAmount = totalAmount;
        this.status = status;
        this.categoryID = categoryID;
        this.category = category;
        this.itemName = itemName;
        this.longDescription = longDescription;
        this.itemPicture = itemPicture;
        this.sku = sku;
        this.itemPrice = itemPrice;
        this.discount = discount;
        this.tax = tax;
        this.likes = likes;
        this.merchantID = merchantID;
        this.dateCreated = dateCreated;
    }

    public String getCartItemID() {
        return cartItemID;
    }

    public String getItemID() {
        return itemID;
    }

    public String getItemQTY() {
        return itemQTY;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getCodeID() {
        return codeID;
    }

    public String getCurrentStock() {
        return currentStock;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
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

    public String getItemPicture() {
        return itemPicture;
    }

    public String getSku() {
        return sku;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public String getTax() {
        return tax;
    }

    public String getLikes() {
        return likes;
    }

    public String getMerchantID() {
        return merchantID;
    }

    public String getDateCreated() {
        return dateCreated;
    }
}
