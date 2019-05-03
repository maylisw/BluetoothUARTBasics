package com.adafruit.bluefruit.le.connect.app;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adafruit.bluefruit.le.connect.R;
import com.adafruit.bluefruit.le.connect.ble.UartPacket;
import com.adafruit.bluefruit.le.connect.ble.UartPacketManagerBase;
import com.adafruit.bluefruit.le.connect.ble.central.BlePeripheral;
import com.adafruit.bluefruit.le.connect.ble.central.BlePeripheralUart;
import com.adafruit.bluefruit.le.connect.ble.central.BleScanner;
import com.adafruit.bluefruit.le.connect.ble.central.UartPacketManager;
import com.adafruit.bluefruit.le.connect.utils.DialogUtils;

import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.adafruit.bluefruit.le.connect.app.ConnectedPeripheralFragment.createFragmentArgs;

public class SyncFragment extends Fragment implements UartPacketManagerBase.Listener{
    private static final String TAG = "SYNCING";
    private static final String ARG_SINGLEPERIPHERALIDENTIFIER = "SinglePeripheralIdentifier";
    private View rootview;
    private TextView textView;
    private Context context;
    private UartPacketManagerBase mUartData;
    private List<BlePeripheralUart> mBlePeripheralsUart = new ArrayList<>();
    private BlePeripheral mBlePeripheral;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());


    // region Fragment Lifecycle
    public static SyncFragment newInstance(@Nullable String singlePeripheralIdentifier) {
        SyncFragment fragment = new SyncFragment();
        fragment.setArguments(createFragmentArgs(singlePeripheralIdentifier));
        return fragment;
    }

    public SyncFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String singlePeripheralIdentifier = getArguments().getString(ARG_SINGLEPERIPHERALIDENTIFIER);
            mBlePeripheral = BleScanner.getInstance().getPeripheralWithIdentifier(singlePeripheralIdentifier);
        }
        //isUITimerRunning = false;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        FragmentActivity activity = getActivity();
//        if (activity != null) {
//            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        }
//        isUITimerRunning = false;
//        mUIRefreshTimerHandler.postDelayed(mUIRefreshTimerRunnable, 0);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_sync, container, false);
        wireWidgets();
        return rootview;
    }

    private void wireWidgets() {
        context = getActivity();
        textView = rootview.findViewById(R.id.syncedText);
        Log.d(TAG, "wireWidgets: ");
        setupUart();

        send();
    }

    protected void setupUart() {
        mUartData = new UartPacketManager(context, this, true);
        mUartData.getReceivedBytes();
        // Enable uart

        if (!BlePeripheralUart.isUartInitialized(mBlePeripheral, mBlePeripheralsUart)) { // If was not previously setup (i.e. orientation change)
            BlePeripheralUart blePeripheralUart = new BlePeripheralUart(mBlePeripheral);
            mBlePeripheralsUart.add(blePeripheralUart);
            blePeripheralUart.uartEnable(mUartData, status -> mMainHandler.post(() -> {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    // Done
                    Log.d(TAG, "Uart enabled");
                } else {
                    WeakReference<BlePeripheralUart> weakBlePeripheralUart = new WeakReference<>(blePeripheralUart);
                    Context context1 = getContext();
                    if (context1 != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context1);
                        AlertDialog dialog = builder.setMessage(R.string.uart_error_peripheralinit)
                                .setPositiveButton(android.R.string.ok, (dialogInterface, which) -> {
                                    BlePeripheralUart strongBlePeripheralUart = weakBlePeripheralUart.get();
                                    if (strongBlePeripheralUart != null) {
                                        strongBlePeripheralUart.disconnect();
                                    }
                                })
                                .show();
                        DialogUtils.keepDialogOnOrientationChanges(dialog);
                    }
                }
            }));
        }
        send();
    }

    public void send() {
        if (!(mUartData instanceof UartPacketManager)) {
            Log.e(TAG, "Error send with invalid uartData class");
            return;
        }

        if (mBlePeripheralsUart.size() == 0) {
            Log.e(TAG, "mBlePeripheralsUart not initialized");
            return;
        }
        String data = "2";
        UartPacketManager uartData = (UartPacketManager) mUartData;

        BlePeripheralUart blePeripheralUart = mBlePeripheralsUart.get(0);
        uartData.send(blePeripheralUart, "2");
    }

    @Override
    public void onUartPacket(UartPacket packet) {
        Log.d(TAG, "onUartPacket: ");
        byte[] bytes = packet.getData();
        String text = new String(bytes, Charset.forName("UTF-8"));
        //todo parse text for stuff
        textView.setText(text);
    }
}
