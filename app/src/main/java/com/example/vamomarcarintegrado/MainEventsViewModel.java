package com.example.vamomarcarintegrado;

import androidx.lifecycle.ViewModel;

public class MainEventsViewModel extends ViewModel {
    int navigationOpSelected = R.id.allEventsViewOp;

    public int getNavigationOpSelected() {
        return navigationOpSelected;
    }

    public void setNavigationOpSelected(int navigationOpSelected) {
        this.navigationOpSelected = navigationOpSelected;
    }
}
