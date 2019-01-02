package com.rorasoft.roragame.View.Adapter;

import android.widget.AbsListView;

import java.io.Serializable;

public interface AdapterFragmentCallbacks extends Serializable {
    int getAdapterFragmentLayoutId();
    void receiveAbsListView(AbsListView gridView);
}
