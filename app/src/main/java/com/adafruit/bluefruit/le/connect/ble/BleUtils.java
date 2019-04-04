package com.adafruit.bluefruit.le.connect.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.UUID;

public class BleUtils {
    private final static String TAG = BleUtils.class.getSimpleName();

    public static final int STATUS_BLE_ENABLED = 0;
    public static final int STATUS_BLUETOOTH_NOT_AVAILABLE = 1;
    public static final int STATUS_BLE_NOT_AVAILABLE = 2;
    public static final int STATUS_BLUETOOTH_DISABLED = 3;
/*
    private static ResetBluetoothAdapter sResetHelper;
*/
    // Use this check to determine whether BLE is supported on the device.  Then you can  selectively disable BLE-related features.
    public static int getBleStatus(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return STATUS_BLE_NOT_AVAILABLE;
        }

        final BluetoothAdapter adapter = getBluetoothAdapter(context);
        // Checks if Bluetooth is supported on the device.
        if (adapter == null) {
            return STATUS_BLUETOOTH_NOT_AVAILABLE;
        }

        if (!adapter.isEnabled()) {
            return STATUS_BLUETOOTH_DISABLED;
        }

        return STATUS_BLE_ENABLED;
    }

    // Initializes a Bluetooth adapter.
    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return null;
        } else {
            return bluetoothManager.getAdapter();
        }
    }


    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        if (bytes != null) {
            char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        } else return null;
    }


    // TODO: merge all these byteToXXX functions and remove unused ones

    public static String bytesToHex2(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte aByte : bytes) {
            String charString = String.format("%02X", (byte) aByte);

            stringBuffer.append(charString).append(" ");
        }
        return stringBuffer.toString();
    }

    public static @NonNull String bytesToText(byte[] bytes, boolean simplifyNewLine) {
        String text = new String(bytes, Charset.forName("UTF-8"));
        if (simplifyNewLine) {
            text = text.replaceAll("(\\r\\n|\\r)", "\n");
        }
        return text;
    }

    public static String bytesToHexWithSpaces(byte[] bytes) {
        StringBuilder newString = new StringBuilder();
        for (byte aByte : bytes) {
            String byteHex = String.format("%02X", (byte) aByte);
            newString.append(byteHex).append(" ");

        }
        return newString.toString().trim();
    }

    public static String getUuidStringFromByteArray(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();
        for (byte aByte : bytes) {
            buffer.append(String.format("%02x", aByte));
        }
        return buffer.toString();
    }

    // region uuidToString
    public static String uuidToString(UUID uuid) {
        return uuidToString(uuid, true);
    }

    @SuppressWarnings("unused")
    static @Nullable String uuidToString(@Nullable UUID uuid, boolean shorten) {
        if (uuid == null) return null;

        String uppercaseUuidString = uuid.toString().toUpperCase();
        if (shorten && uppercaseUuidString.startsWith("0000") && uppercaseUuidString.endsWith("-0000-1000-8000-00805F9B34FB")) {
            return uppercaseUuidString.substring(4, 8);
        } else {
            return uppercaseUuidString;
        }
    }

    // endregion
}
