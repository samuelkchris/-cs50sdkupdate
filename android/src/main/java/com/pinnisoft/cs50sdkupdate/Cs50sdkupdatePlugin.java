package com.pinnisoft.cs50sdkupdate;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

import com.ctk.sdk.PosApiHelper;

/**
 * Main plugin class for the CS50 SDK Flutter integration.
 * Handles communication between Flutter and the native Android SDK.
 */
public class Cs50sdkupdatePlugin implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {
    private static final String TAG = "Cs50sdkupdatePlugin";
    private static final String CHANNEL_NAME = "cs50sdkupdate";

    // Core plugin properties
    private MethodChannel channel;
    private Context context;
    private Activity activity;
    private ExecutorService executorService;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Feature handlers
    private PrinterHandler printerHandler;
    private ScannerHandler scannerHandler;
    private NfcHandler nfcHandler;
    private SystemHandler systemHandler;
    private PaymentHandler paymentHandler;

    // Method router
    private final Map<String, MethodHandler> methodHandlers = new HashMap<>();

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        Log.d(TAG, "Plugin attaching to Flutter engine");
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL_NAME);
        channel.setMethodCallHandler(this);
        context = flutterPluginBinding.getApplicationContext();

        // Create thread pool for background operations
        int processors = Runtime.getRuntime().availableProcessors();
        executorService = Executors.newFixedThreadPool(processors);

        // Initialize the POS API helper
        PosApiHelper posApiHelper = PosApiHelper.getInstance();

        // Initialize feature handlers
        systemHandler = new SystemHandler(posApiHelper, context);
        printerHandler = new PrinterHandler(posApiHelper, context, activity, channel, executorService);
        scannerHandler = new ScannerHandler(context, channel);
        nfcHandler = new NfcHandler(posApiHelper, channel);

        // Initialize payment handler
        paymentHandler = new PaymentHandler(posApiHelper, context, channel);

        // Register method handlers
        registerMethodHandlers();

        Log.d(TAG, "Plugin successfully attached to Flutter engine");
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        activity = binding.getActivity();
        if (printerHandler != null) {
            printerHandler.setActivity(activity);
        }
    }

    /**
     * Registers all method handlers to their respective handler classes
     */
    private void registerMethodHandlers() {
        // System methods
        registerSystemMethods();

        // Printer methods
        registerPrinterMethods();

        // Scanner methods
        registerScannerMethods();

        // NFC/PICC methods
        registerNfcMethods();

        // Payment methods
        registerPaymentMethods();

    }

    private void registerPaymentMethods() {
        paymentHandler.registerMethodHandlers(methodHandlers);
    }

    private void registerSystemMethods() {
        methodHandlers.put("getPlatformVersion", systemHandler::getPlatformVersion);
        methodHandlers.put("SysApiVerson", systemHandler::getApiVersion);
        methodHandlers.put("getOSVersion", systemHandler::getOsVersion);
        methodHandlers.put("getDeviceId", systemHandler::getDeviceId);
        methodHandlers.put("SysLogSwitch", systemHandler::setLogSwitch);
        methodHandlers.put("SysGetRand", systemHandler::getRandomNumber);
        methodHandlers.put("SysUpdate", systemHandler::updateFirmware);
        methodHandlers.put("SysGetVersion", systemHandler::getMcuVersion);
        methodHandlers.put("SysReadSN", systemHandler::getSerialNumber);
    }

    private void registerPrinterMethods() {
        methodHandlers.put("PrintInit", printerHandler::initializePrinter);
        methodHandlers.put("PrintInitWithParams", printerHandler::initializeWithParams);
        methodHandlers.put("PrintSetFont", printerHandler::setFont);
        methodHandlers.put("PrintSetGray", printerHandler::setGray);
        methodHandlers.put("PrintSetSpace", printerHandler::setSpace);
        methodHandlers.put("PrintGetFont", printerHandler::getFont);
        methodHandlers.put("PrintStep", printerHandler::setStep);
        methodHandlers.put("PrintSetVoltage", printerHandler::setVoltage);
        methodHandlers.put("PrintIsCharge", printerHandler::setChargeStatus);
        methodHandlers.put("PrintSetLinPixelDis", printerHandler::setLinePixelDistance);
        methodHandlers.put("PrintStr", printerHandler::printString);
        methodHandlers.put("PrintBmp", printerHandler::printBitmap);
        methodHandlers.put("PrintBarcode", printerHandler::printBarcode);
        methodHandlers.put("PrintQrCode_Cut", printerHandler::printQrCode);
        methodHandlers.put("PrintCutQrCode_Str", printerHandler::printQrCodeWithText);
        methodHandlers.put("PrintStart", printerHandler::startPrinting);
        methodHandlers.put("PrintSetLeftIndent", printerHandler::setLeftIndent);
        methodHandlers.put("PrintSetAlign", printerHandler::setAlignment);
        methodHandlers.put("PrintCharSpace", printerHandler::setCharSpace);
        methodHandlers.put("PrintSetLineSpace", printerHandler::setLineSpace);
        methodHandlers.put("PrintSetLeftSpace", printerHandler::setLeftSpace);
        methodHandlers.put("PrintSetSpeed", printerHandler::setSpeed);
        methodHandlers.put("PrintCheckStatus", printerHandler::checkStatus);
        methodHandlers.put("PrintFeedPaper", printerHandler::feedPaper);
        methodHandlers.put("PrintSetMode", printerHandler::setMode);
        methodHandlers.put("PrintSetUnderline", printerHandler::setUnderline);
        methodHandlers.put("PrintSetReverse", printerHandler::setReverse);
        methodHandlers.put("PrintSetBold", printerHandler::setBold);
        methodHandlers.put("PrintLogo", printerHandler::printLogo);
        methodHandlers.put("PrintLabLocate", printerHandler::locateLabel);

        // PDF specific methods
        methodHandlers.put("PrintPdf", printerHandler::printPdf);
        methodHandlers.put("CancelJob", printerHandler::cancelJob);
        methodHandlers.put("RetryJob", printerHandler::retryJob);
        methodHandlers.put("getPrintHistory", printerHandler::getPrintHistory);
        methodHandlers.put("reprintDocument", printerHandler::reprintDocument);
    }

    private void registerScannerMethods() {
        methodHandlers.put("configureScannerSettings", scannerHandler::configureSettings);
        methodHandlers.put("openScanner", scannerHandler::openScanner);
        methodHandlers.put("closeScanner", scannerHandler::closeScanner);
        methodHandlers.put("startScanner", scannerHandler::startScanner);
        methodHandlers.put("stopScanner", scannerHandler::stopScanner);
        methodHandlers.put("setScannerMode", scannerHandler::setMode);
    }

    private void registerNfcMethods() {
        methodHandlers.put("openPicc", nfcHandler::openPicc);
        methodHandlers.put("piccCheck", nfcHandler::checkPicc);
        methodHandlers.put("piccPolling", nfcHandler::pollPicc);
        methodHandlers.put("piccCommand", nfcHandler::sendCommand);
        methodHandlers.put("piccApduCmd", nfcHandler::sendApduCommand);
        methodHandlers.put("piccClose", nfcHandler::closePicc);
        methodHandlers.put("piccRemove", nfcHandler::removePicc);
        methodHandlers.put("piccSamAv2Init", nfcHandler::initializeSamAv2);
        methodHandlers.put("piccHwModeSet", nfcHandler::setHardwareMode);
        methodHandlers.put("piccM1Authority", nfcHandler::verifyM1Authority);
        methodHandlers.put("PiccNfc", nfcHandler::readNfcTag);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        Log.d(TAG, "Method call received: " + call.method);

        MethodHandler handler = methodHandlers.get(call.method);
        if (handler != null) {
            try {
                handler.handle(call, result);
            } catch (Exception e) {
                Log.e(TAG, "Error handling method " + call.method, e);
                ErrorUtils.handleException(call.method, e, result);
            }
        } else {
            Log.w(TAG, "Method not implemented: " + call.method);
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        Log.d(TAG, "Plugin detaching from Flutter engine");

        // Clean up scanner resources
        if (scannerHandler != null) {
            scannerHandler.cleanup();
        }

        // Shut down the executor service gracefully
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        channel.setMethodCallHandler(null);
        channel = null;
        Log.d(TAG, "Plugin successfully detached from Flutter engine");
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
        if (printerHandler != null) {
            printerHandler.setActivity(null);
        }
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        activity = binding.getActivity();
        if (printerHandler != null) {
            printerHandler.setActivity(activity);
        }
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
        if (printerHandler != null) {
            printerHandler.setActivity(null);
        }
    }

    /**
     * Interface for method handlers to standardize handling
     */
    interface MethodHandler {
        void handle(MethodCall call, Result result);
    }
}