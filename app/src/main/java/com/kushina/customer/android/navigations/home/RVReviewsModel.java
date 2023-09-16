package com.kushina.customer.android.navigations.home;

public class RVReviewsModel {

    private String itemRatingID;
    private String userID;
    private String reviewerName;
    private String reviewerImage;
    private String rating;
    private String review;
    private String dateCreated;

    public RVReviewsModel(String itemRatingID, String userID, String reviewerName, String reviewerImage, String rating, String review, String dateCreated) {
        this.itemRatingID = itemRatingID;
        this.userID = userID;
        this.reviewerName = reviewerName;
        this.reviewerImage = reviewerImage;
        this.rating = rating;
        this.review = review;
        this.dateCreated = dateCreated;
    }

    public String getItemRatingID() {
        return itemRatingID;
    }

    public String getUserID() {
        return userID;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public String getReviewerImage() {
        return reviewerImage;
    }

    public String getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }

    public String getDateCreated() {
        return dateCreated;
    }
}
