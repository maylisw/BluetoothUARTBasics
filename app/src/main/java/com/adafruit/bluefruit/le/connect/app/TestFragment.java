package com.adafruit.bluefruit.le.connect.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.adafruit.bluefruit.le.connect.R;

import static com.adafruit.bluefruit.le.connect.app.ConnectedPeripheralFragment.createFragmentArgs;

public class TestFragment extends Fragment {

    private final static String TAG = ScannerFragment.class.getSimpleName();
    private View rootview;
    private Button calibrate, sync;
    private Context context;
    private static String singlePeripheralIdentifierMaster;

    // region Fragment Lifecycle
    public static TestFragment newInstance(@Nullable String singlePeripheralIdentifier) {
        TestFragment fragment = new TestFragment();
        fragment.setArguments(createFragmentArgs(singlePeripheralIdentifier));
        singlePeripheralIdentifierMaster = singlePeripheralIdentifier;
        return fragment;
    }

    public TestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_test, container, false);
        wireWidgets();
        return rootview;
    }

    private void wireWidgets() {
        context = getActivity();
        FragmentManager fragmentManager = getFragmentManager();
        calibrate = rootview.findViewById(R.id.calibration);
        calibrate.setText(R.string.calibrateButtonText);
        calibrate.setOnClickListener(view -> {
            fragmentManager.beginTransaction().replace(((ViewGroup)getView().getParent()).getId(), CalibrateFragment.newInstance(singlePeripheralIdentifierMaster))
                    .addToBackStack(null)
                    .commit();
        });

        sync = rootview.findViewById(R.id.sync);
        sync.setText(R.string.syncButtonText);
        sync.setOnClickListener(view -> {
            fragmentManager.beginTransaction().replace(((ViewGroup)getView().getParent()).getId(), SyncFragment.newInstance(singlePeripheralIdentifierMaster))
                    .addToBackStack(null)
                    .commit();
        });

    }
}
