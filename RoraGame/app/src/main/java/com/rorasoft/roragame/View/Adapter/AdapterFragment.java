package com.rorasoft.roragame.View.Adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.rorasoft.roragame.R;

public class AdapterFragment extends Fragment {

    private AdapterFragmentCallbacks callbacks;

    private static final String kCallbacks = "kCallBack";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        callbacks = (AdapterFragmentCallbacks) activity;
    }

    public static AdapterFragment newInstance(AdapterFragmentCallbacks callbacks) {

        Bundle args = new Bundle();
        args.putSerializable(kCallbacks, callbacks);
        AdapterFragment fragment = new AdapterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            this.callbacks = (AdapterFragmentCallbacks) getArguments().getSerializable(kCallbacks);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(callbacks.getAdapterFragmentLayoutId(), container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        callbacks.receiveAbsListView((AbsListView) getView().findViewById(R.id.fragmentView));
    }
}
