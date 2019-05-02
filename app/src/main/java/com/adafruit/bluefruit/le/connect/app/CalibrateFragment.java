package com.adafruit.bluefruit.le.connect.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adafruit.bluefruit.le.connect.R;

import static com.adafruit.bluefruit.le.connect.app.ConnectedPeripheralFragment.createFragmentArgs;

public class CalibrateFragment extends Fragment {
    private View rootview;
    
    // region Fragment Lifecycle
    public static CalibrateFragment newInstance(@Nullable String singlePeripheralIdentifier) {
        CalibrateFragment fragment = new CalibrateFragment();
        fragment.setArguments(createFragmentArgs(singlePeripheralIdentifier));
        return fragment;
    }

    public CalibrateFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_calibrate, container, false);
        wireWidgets();
        return rootview;
    }

    private void wireWidgets() {
    }
}
