package com.pinnisoft.cs50sdkupdate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ctk.sdk.ByteUtil;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * Handles barcode scanner operations
 */
public class ScannerHandler {
    private static final String TAG = "ScannerHandler";

    // Broadcast actions for scanner control
    private static final String ACTION_SCANNER_CONFIG = "ACTION_BAR_SCANCFG";
    private static final String ACTION_SCANNER_TRIGGER = "ACTION_BAR_TRIGSCAN";
    private static final String ACTION_SCANNER_RESULT = "ACTION_BAR_SCAN";

    // Intent extras for scanner configuration
    private static final String EXTRA_SCAN_POWER = "EXTRA_SCAN_POWER";
    private static final String EXTRA_SCAN_MODE = "EXTRA_SCAN_MODE";
    private static final String EXTRA_TRIG_MODE = "EXTRA_TRIG_MODE";
    private static final String EXTRA_SCAN_AUTOENT = "EXTRA_SCAN_AUTOENT";

    // Intent extras for scan results
    private static final String EXTRA_SCAN_DATA = "EXTRA_SCAN_DATA";
    private static final String EXTRA_SCAN_LENGTH = "EXTRA_SCAN_LENGTH";
    private static final String EXTRA_SCAN_ENCODE_MODE = "EXTRA_SCAN_ENCODE_MODE";
    private static final String EXTRA_SCAN_RAW_DATA = "EXTRA_SCAN_RAW_DATA";
    private static final String EXTRA_SCAN_RAW_DATA_LEN = "EXTRA_SCAN_RAW_DATA_LEN";

    // Encoding modes
    public static final int ENCODE_MODE_NONE = 3;

    private final Context context;
    private final MethodChannel channel;
    private final Handler mainHandler;
    private BroadcastReceiver scannerReceiver;
    private boolean isScanning = false;
    private boolean isContinuousMode = false;
    private final Object scanLock = new Object();

    public ScannerHandler(Context context, MethodChannel channel) {
        this.context = context;
        this.channel = channel;
        this.mainHandler = new Handler(Looper.getMainLooper());
        initializeScannerReceiver();
    }

    /**
     * Initialize the broadcast receiver for scanner results
     */
    private void initializeScannerReceiver() {
        if (scannerReceiver == null) {
            scannerReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    synchronized (scanLock) {
                        try {
                            if (!isScanning) {
                                Log.d(TAG, "Received scan result but scanner is not active");
                                return;
                            }

                            String scanResult = "";
                            int length = intent.getIntExtra(EXTRA_SCAN_LENGTH, 0);
                            int encodeType = intent.getIntExtra(EXTRA_SCAN_ENCODE_MODE, 1);

                            if (encodeType == ENCODE_MODE_NONE) {
                                byte[] rawData = intent.getByteArrayExtra(EXTRA_SCAN_RAW_DATA);
                                int rawLength = intent.getIntExtra(EXTRA_SCAN_RAW_DATA_LEN, 0);
                                scanResult = ByteUtil.bytearrayToHexString(rawData, rawLength);
                            } else {
                                scanResult = intent.getStringExtra(EXTRA_SCAN_DATA);
                            }

                            final Map<String, Object> resultMap = new HashMap<>();
                            resultMap.put("result", scanResult);
                            resultMap.put("length", length);
                            resultMap.put("encodeType", encodeType);
                            resultMap.put("method", "onScanResult");

                            Log.d(TAG, "Scan result: " + scanResult);
                            Log.d(TAG, "Scan length: " + length);
                            Log.d(TAG, "Scan encode type: " + encodeType);

                            mainHandler.post(() -> {
                                if (channel != null) {
                                    channel.invokeMethod("onScanResult", resultMap);
                                }
                            });

                            // Stop scanning after receiving result in normal mode
                            if (!isContinuousMode) {
                                stopScanningInternal();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing scan result", e);
                        }
                    }
                }
            };

            IntentFilter filter = new IntentFilter(ACTION_SCANNER_RESULT);
            context.registerReceiver(scannerReceiver, filter);
            Log.d(TAG, "Scanner receiver registered");
        }
    }

    /**
     * Configure scanner settings
     */
    public void configureSettings(MethodCall call, Result result) {
        try {
            Intent intent = new Intent(ACTION_SCANNER_CONFIG);

            Integer trigMode = call.argument("trigMode");
            Integer scanMode = call.argument("scanMode");
            Integer scanPower = call.argument("scanPower");
            Integer autoEnter = call.argument("autoEnter");

            if (trigMode != null) intent.putExtra(EXTRA_TRIG_MODE, trigMode);
            if (scanMode != null) intent.putExtra(EXTRA_SCAN_MODE, scanMode);
            if (scanPower != null) intent.putExtra(EXTRA_SCAN_POWER, scanPower);
            if (autoEnter != null) intent.putExtra(EXTRA_SCAN_AUTOENT, autoEnter);

            Log.d(TAG, "Configuring scanner with settings: " +
                    "trigMode=" + trigMode +
                    ", scanMode=" + scanMode +
                    ", scanPower=" + scanPower +
                    ", autoEnter=" + autoEnter);

            context.sendBroadcast(intent);
            result.success("Scanner configured successfully");
        } catch (Exception e) {
            ErrorUtils.handleException("configureScannerSettings", e, result);
        }
    }

    /**
     * Open the scanner hardware
     */
    public void openScanner(MethodCall call, Result result) {
        try {
            Intent intent = new Intent(ACTION_SCANNER_CONFIG);
            intent.putExtra(EXTRA_SCAN_POWER, 1);  // 1 for power on
            intent.putExtra(EXTRA_SCAN_MODE, 3);   // API mode

            Log.d(TAG, "Opening scanner in API mode");
            context.sendBroadcast(intent);
            result.success("Scanner opened successfully");
        } catch (Exception e) {
            ErrorUtils.handleException("openScanner", e, result);
        }
    }

    /**
     * Close the scanner hardware
     */
    public void closeScanner(MethodCall call, Result result) {
        try {
            Intent intent = new Intent(ACTION_SCANNER_CONFIG);
            intent.putExtra(EXTRA_SCAN_POWER, 0);  // 0 for power off

            Log.d(TAG, "Closing scanner");
            context.sendBroadcast(intent);

            // Ensure scanning is stopped
            synchronized (scanLock) {
                isScanning = false;
            }

            result.success("Scanner closed successfully");
        } catch (Exception e) {
            ErrorUtils.handleException("closeScanner", e, result);
        }
    }

    /**
     * Start scanner operation
     */
    public void startScanner(MethodCall call, Result result) {
        synchronized (scanLock) {
            try {
                if (!isScanning) {
                    isScanning = true;
                    Intent intent = new Intent(ACTION_SCANNER_TRIGGER);

                    Log.d(TAG, "Starting scanner");
                    context.sendBroadcast(intent);
                    result.success("Scanner started");
                } else {
                    Log.w(TAG, "Scanner is already active");
                    result.error(ErrorUtils.ERROR_RESOURCE_BUSY, "Scanner is already running", null);
                }
            } catch (Exception e) {
                ErrorUtils.handleException("startScanner", e, result);
            }
        }
    }

    /**
     * Stop scanner operation
     */
    public void stopScanner(MethodCall call, Result result) {
        try {
            stopScanningInternal();
            result.success("Scanner stopped");
        } catch (Exception e) {
            ErrorUtils.handleException("stopScanner", e, result);
        }
    }

    /**
     * Internal method to stop scanning
     */
    private void stopScanningInternal() {
        synchronized (scanLock) {
            if (isScanning) {
                isScanning = false;
                Intent intent = new Intent(ACTION_SCANNER_TRIGGER);
                Log.d(TAG, "Stopping scanner");
                context.sendBroadcast(intent);
            }
        }
    }

    /**
     * Set scanner mode (continuous or normal)
     */
    public void setMode(MethodCall call, Result result) {
        try {
            Integer mode = call.argument("mode");
            if (mode == null) {
                ErrorUtils.invalidArgument(result, "Mode cannot be null");
                return;
            }

            isContinuousMode = (mode == 1);
            Intent intent = new Intent(ACTION_SCANNER_CONFIG);
            intent.putExtra(EXTRA_TRIG_MODE, mode);

            Log.d(TAG, "Setting scanner mode to: " + (isContinuousMode ? "continuous" : "normal"));
            context.sendBroadcast(intent);
            result.success("Scanner mode set to: " + (isContinuousMode ? "continuous" : "normal"));
        } catch (Exception e) {
            ErrorUtils.handleException("setScannerMode", e, result);
        }
    }

    /**
     * Clean up scanner resources
     */
    public void cleanup() {
        try {
            // Ensure scanner is powered off
            Intent intent = new Intent(ACTION_SCANNER_CONFIG);
            intent.putExtra(EXTRA_SCAN_POWER, 0);
            context.sendBroadcast(intent);

            // Unregister the broadcast receiver
            if (scannerReceiver != null) {
                context.unregisterReceiver(scannerReceiver);
                scannerReceiver = null;
                Log.d(TAG, "Scanner receiver unregistered");
            }

            synchronized (scanLock) {
                isScanning = false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error cleaning up scanner resources", e);
        }
    }
}