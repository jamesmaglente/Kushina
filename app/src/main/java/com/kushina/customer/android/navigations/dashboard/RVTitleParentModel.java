package com.kushina.customer.android.navigations.dashboard;

import com.kushina.customer.android.navigations.home.RVHomeModel;

import java.util.List;

public class RVTitleParentModel {

    private String title;
    private List<RVHomeModel> rvModel;

    public RVTitleParentModel(String title, List<RVHomeModel> rvModel) {
        this.title = title;
        this.rvModel = rvModel;
    }

    public String getTitle() {
        return title;
    }

    public List<RVHomeModel> getRvModel() {
        return rvModel;
    }
}
