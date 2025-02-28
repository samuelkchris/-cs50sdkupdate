package com.pinnisoft.cs50sdkupdate;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.ctk.sdk.ByteUtil;
import com.ctk.sdk.PosApiHelper;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * Handles system-related operations for the POS device
 */
public class SystemHandler {
    private static final String TAG = "SystemHandler";
    private final PosApiHelper posApiHelper;
    private final Context context;

    public SystemHandler(PosApiHelper posApiHelper, Context context) {
        this.posApiHelper = posApiHelper;
        this.context = context;
    }

    /**
     * Get platform version and SDK information
     */
    public void getPlatformVersion(MethodCall call, Result result) {
        Log.d(TAG, "Getting platform version");
        byte[] version = new byte[10];
        int ret = posApiHelper.SysGetVersion(version);
        int pic = posApiHelper.PiccOpen();

        if (ret == 0 && pic == 0) {
            posApiHelper.SysBeep();
            String versionStr = "Android " + Build.VERSION.RELEASE +
                    ", SDK Version: " + new String(version).trim() +
                    ", Picc opened";
            Log.d(TAG, "Platform version: " + versionStr);
            result.success(versionStr);
        } else {
            ErrorUtils.hardwareError(result,
                    "Failed to get SDK version or open PICC", ret);
        }
    }

    /**
     * Get the API version
     */
    public void getApiVersion(MethodCall call, Result result) {
        Log.d(TAG, "Getting API version");
        try {
            String version = posApiHelper.SysApiVerson();
            if (version != null) {
                Log.d(TAG, "API version: " + version);
                result.success(version);
            } else {
                ErrorUtils.hardwareError(result, "Failed to get API version", -1);
            }
        } catch (Exception e) {
            ErrorUtils.handleException("SysApiVerson", e, result);
        }
    }

    /**
     * Get the OS version
     */
    public void getOsVersion(MethodCall call, Result result) {
        Log.d(TAG, "Getting OS version");
        try {
            String osVersion = posApiHelper.getOSVersion();
            if (osVersion != null) {
                Log.d(TAG, "OS version: " + osVersion);
                result.success(osVersion);
            } else {
                ErrorUtils.hardwareError(result, "Failed to get OS version", -1);
            }
        } catch (Exception e) {
            ErrorUtils.handleException("getOSVersion", e, result);
        }
    }

    /**
     * Get the device ID
     */
    public void getDeviceId(MethodCall call, Result result) {
        Log.d(TAG, "Getting device ID");
        try {
            String deviceId = posApiHelper.getDeviceId();
            if (deviceId != null) {
                Log.d(TAG, "Device ID: " + deviceId);
                result.success(deviceId);
            } else {
                ErrorUtils.hardwareError(result, "Failed to get device ID", -1);
            }
        } catch (Exception e) {
            ErrorUtils.handleException("getDeviceId", e, result);
        }
    }

    /**
     * Set the log level
     */
    public void setLogSwitch(MethodCall call, Result result) {
        Integer level = call.argument("level");
        if (level == null) {
            ErrorUtils.invalidArgument(result, "Log level must be provided");
            return;
        }

        Log.d(TAG, "Setting log level to: " + level);
        int logSwitch = posApiHelper.SysLogSwitch(level);
        if (logSwitch == 0) {
            Log.d(TAG, "Log switch set successfully");
            result.success("Log switch set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set log switch", logSwitch);
        }
    }

    /**
     * Get a random number from the device
     */
    public void getRandomNumber(MethodCall call, Result result) {
        Log.d(TAG, "Getting random number");
        byte[] rnd = new byte[16];
        int getRand = posApiHelper.SysGetRand(rnd);
        if (getRand == 0) {
            String randomHex = ByteUtil.bytearrayToHexString(rnd, rnd.length);
            Log.d(TAG, "Random number: " + randomHex);
            result.success(randomHex);
        } else {
            ErrorUtils.hardwareError(result, "Failed to get random number", getRand);
        }
    }

    /**
     * Update the MCU firmware
     */
    public void updateFirmware(MethodCall call, Result result) {
        Log.d(TAG, "Updating MCU firmware");
        int update = posApiHelper.SysUpdate();
        if (update == 0) {
            Log.d(TAG, "MCU firmware updated successfully");
            result.success("MCU app firmware updated successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to update MCU firmware", update);
        }
    }

    /**
     * Get the MCU firmware version
     */
    public void getMcuVersion(MethodCall call, Result result) {
        Log.d(TAG, "Getting MCU firmware version");
        byte[] buf = new byte[10];
        int getVersion = posApiHelper.SysGetVersion(buf);
        if (getVersion == 0) {
            String version = new String(buf).trim();
            Log.d(TAG, "MCU firmware version: " + version);
            result.success(version);
        } else {
            ErrorUtils.hardwareError(result, "Failed to get MCU firmware version", getVersion);
        }
    }

    /**
     * Read the device serial number
     */
    public void getSerialNumber(MethodCall call, Result result) {
        Log.d(TAG, "Reading device serial number");
        byte[] sn = new byte[16];
        int readSN = posApiHelper.SysReadSN(sn);
        if (readSN == 0) {
            String serialNumber = new String(sn).trim();
            Log.d(TAG, "Serial number: " + serialNumber);
            result.success(serialNumber);
        } else {
            ErrorUtils.hardwareError(result, "Failed to read serial number", readSN);
        }
    }
}