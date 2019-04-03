package com.adafruit.bluefruit.le.connect.app;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.adafruit.bluefruit.le.connect.BluefruitApplication;
import com.adafruit.bluefruit.le.connect.BuildConfig;
import com.adafruit.bluefruit.le.connect.R;
import com.adafruit.bluefruit.le.connect.models.PeripheralModeViewModel;
import com.adafruit.bluefruit.le.connect.utils.DialogUtils;
import com.squareup.leakcanary.RefWatcher;

import java.lang.ref.WeakReference;

public class MainFragment extends Fragment {
    // Log
    private final static String TAG = MainFragment.class.getSimpleName();

    // UI
    private BottomNavigationView mNavigationView;

    // Data
    private WeakReference<Fragment> mCurrentFragmentReference;
    private int selectedFragmentId = 0;
    private PeripheralModeViewModel mPeripheralModeViewModel;
    private boolean mIsInitialNavigationItemSelected = false;

    // region Fragment Lifecycle
    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup bottom navigation view
        mNavigationView = view.findViewById(R.id.navigation);
        mNavigationView.setOnNavigationItemSelectedListener(this::selectFragment);
        updateActionBarTitle(mNavigationView.getSelectedItemId());       // Restore title (i.e. when a fragment is popped)
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentActivity activity = getActivity();
        if (activity != null) {
            // ViewModels
            mPeripheralModeViewModel = ViewModelProviders.of(activity).get(PeripheralModeViewModel.class);   // Note: shares viewModel with Activity

            // update options menu with current values
            activity.invalidateOptionsMenu();

            // Setup when activity is created for the first time
            if (!mIsInitialNavigationItemSelected) {
                // Set initial value
                mNavigationView.setSelectedItemId(R.id.navigation_central);
                mIsInitialNavigationItemSelected = true;
            }
        }
    }

    // endregion

    // region Fragments
    private Fragment getCurrentFragment() {
        return mCurrentFragmentReference == null ? null : mCurrentFragmentReference.get();
    }

    private boolean selectFragment(@NonNull MenuItem item) {

        final int navigationSelectedItem = item.getItemId();

        // Change fragment if different
        boolean isFragmentChanged = false;
        if (navigationSelectedItem != selectedFragmentId) {
            Fragment selectedFragment = null;
            selectedFragment = ScannerFragment.newInstance();


            if (selectedFragment != null) {
                updateActionBarTitle(navigationSelectedItem);

                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.navigationContentLayout, selectedFragment)
                        .commit();

                mCurrentFragmentReference = new WeakReference<>(selectedFragment);
                selectedFragmentId = navigationSelectedItem;
                isFragmentChanged = true;
            }
        }


        return isFragmentChanged;
    }

    private void updateActionBarTitle(int navigationSelectedItem) {
        int titleId = 0;
        switch (navigationSelectedItem) {
            case R.id.navigation_central:
                titleId = R.string.main_tabbar_centralmode;
                break;
        }
        setActionBarTitle(getString(titleId));
    }

    private void setActionBarTitle(String title) {
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(title);
                actionBar.setDisplayHomeAsUpEnabled(false);     // Don't show caret for MainFragment
            }
        }
    }

    // endregion

    // region Actions
    public void startScanning() {
        // Send the message to the peripheral mode fragment, or ignore it if is not selected
        if (getCurrentFragment() instanceof ScannerFragment) {
            ((ScannerFragment) getCurrentFragment()).startScanning();
        }
    }

    public void disconnectAllPeripherals() {
        // Send the message to the peripheral mode fragment, or ignore it if is not selected
        if (getCurrentFragment() instanceof ScannerFragment) {
            ((ScannerFragment) getCurrentFragment()).disconnectAllPeripherals();
        }
    }

    // endregion
}