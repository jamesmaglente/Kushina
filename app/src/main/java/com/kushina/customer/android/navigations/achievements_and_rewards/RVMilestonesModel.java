package com.kushina.customer.android.navigations.achievements_and_rewards;

public class RVMilestonesModel {

    private String membership_type_id;
    private String membership_type;
    private String description;
    private String level;
    private String discount;
    private String requiredToque;
    private String currentToque;

    public RVMilestonesModel(String membership_type_id, String membership_type, String description, String level, String discount, String requiredToque, String currentToque) {
        this.membership_type_id = membership_type_id;
        this.membership_type = membership_type;
        this.description = description;
        this.level = level;
        this.discount = discount;
        this.requiredToque = requiredToque;
        this.currentToque = currentToque;
    }

    public String getMembership_type_id() {
        return membership_type_id;
    }

    public String getMembership_type() {
        return membership_type;
    }

    public String getDescription() {
        return description;
    }

    public String getLevel() {
        return level;
    }

    public String getDiscount() {
        return discount;
    }

    public String getRequiredToque() {
        return requiredToque;
    }

    public String getCurrentToque() {
        return currentToque;
    }
}
