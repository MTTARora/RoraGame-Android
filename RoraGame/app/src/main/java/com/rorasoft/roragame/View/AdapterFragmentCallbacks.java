package com.rorasoft.roragame.View;

import android.widget.AbsListView;

public interface AdapterFragmentCallbacks {
    int getAdapterFragmentLayoutId();
    void receiveAbsListView(AbsListView gridView);
}
