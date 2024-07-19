package com.pinnisoft.cs50sdkupdate;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ctk.sdk.ByteUtil;
import com.ctk.sdk.PosApiHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;


public class Cs50sdkupdatePlugin implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {
    private MethodChannel channel;
    private Context context;
    private Activity activity;
    private PrintManager printManager;
    private final Map<String, PrintJob> activeJobs = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> retryCount = new ConcurrentHashMap<>();
    private final Map<String, String> jobToPdfPath = new ConcurrentHashMap<>();
    private final AtomicInteger totalPagesPrinted = new AtomicInteger(0);
    private final AtomicInteger totalPagesUnprinted = new AtomicInteger(0);
    private static final String TAG = "PdfPrintPlugin";
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int BUFFER_SIZE = 8192;
    private final Map<String, PdfDocumentAdapter> activeAdapters = new HashMap<>();
    private PosApiHelper posApiHelper;
    private int currentPage = 0;
    private int totalPages = 0;
    private List<Integer> failedPages = new ArrayList<>();
    private String currentPdfPath;
    private String lastPrintedPdfPath;
    private int lastPrintedPageIndex = -1;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "cs50sdkupdate");
        channel.setMethodCallHandler(this);
        context = flutterPluginBinding.getApplicationContext();
        posApiHelper = PosApiHelper.getInstance();


    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        activity = binding.getActivity();
        printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);


    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        PosApiHelper posApiHelper = PosApiHelper.getInstance();

        if (call.method.equals("getPlatformVersion")) {

            byte[] version = new byte[10];
            int ret = posApiHelper.SysGetVersion(version);
            int pic = posApiHelper.PiccOpen();
            if (ret == 0 && pic == 0) {
                posApiHelper.SysBeep();
                result.success("Android " + Build.VERSION.RELEASE + ", SDK Version: " + new String(version) + ", Picc opened");
            } else {
                result.error("ERROR", "Failed to get SDK version or open picc", null);
            }
        } else if (call.method.equals("printProgress")) {
            int currentPage = call.argument("currentPage");
            int totalPages = call.argument("totalPages");
            result.success("Current Page: " + currentPage + ", Total Pages: " + totalPages);
        } else if (call.method.equals("openPicc")) {

            int pic = posApiHelper.PiccOpen();
            if (pic == 0) {
                posApiHelper.SysBeep();
                result.success("Picc opened");
            } else {
                result.error("ERROR", "Failed to open picc", null);
            }
        } else if (call.method.equals("piccCheck")) {
            byte[] cardType = new byte[3];
            byte[] serialNo = new byte[50];
            int check = posApiHelper.PiccCheck((byte) 'A', cardType, serialNo);
            if (check == 0) {
                String cardTypeStr = "Card Type: " + ByteUtil.bytearrayToHexString(cardType, cardType.length);
                String uidStr = "UID: " + ByteUtil.bytearrayToHexString(serialNo, serialNo[0]);
                String resultStr = cardTypeStr + "\n" + uidStr;
                result.success(resultStr);
            } else {
                result.error("ERROR", "Failed to check picc", null);
            }
        } else if (call.method.equals("piccPolling")) {
            byte[] cardType = new byte[2];
            byte[] uid = new byte[50];
            byte[] uidLen = new byte[1];
            byte[] ats = new byte[50];
            byte[] atsLen = new byte[1];
            byte[] sak = new byte[1];
            int poll = posApiHelper.PiccPolling(cardType, uid, uidLen, ats, atsLen, sak);
            if (poll == 0) {
                String cardTypeStr = "Card Type: " + ByteUtil.bytearrayToHexString(cardType, cardType.length);
                String uidStr = "UID: " + ByteUtil.bytearrayToHexString(uid, uid[0]);
                String atsStr = "ATS: " + ByteUtil.bytearrayToHexString(ats, ats[0]);
                String sakStr = "SAK: " + ByteUtil.bytearrayToHexString(sak, sak.length);
                String resultStr = cardTypeStr + "\n" + uidStr + "\n" + atsStr + "\n" + sakStr;
                result.success(resultStr);
            } else {
                result.error("ERROR", "Failed to poll picc", null);
            }
        } else if (call.method.equals("piccCommand")) {
            ArrayList<Integer> list = call.argument("apduSend");
            byte[] apduSend = new byte[list.size()];
            for (int i = 0; i < list.size(); i++) {
                apduSend[i] = list.get(i).byteValue();
            }
            byte[] apduResp = new byte[256];
            int command = posApiHelper.PiccCommand(apduSend, apduResp);
            if (command == 0) {
                posApiHelper.SysBeep();
                result.success(new String(apduResp));
            } else {
                result.error("ERROR", "Failed to execute PiccCommand", null);
            }
        } else if (call.method.equals("piccApduCmd")) {
            byte[] pucInput = call.argument("pucInput");
            byte[] pucOutput = new byte[256];
            byte[] pusOutputLen = new byte[1];
            int apduCmd = posApiHelper.PiccApduCmd(pucInput, (short) pucInput.length, pucOutput, pusOutputLen);
            if (apduCmd == 0) {
                posApiHelper.SysBeep();
                result.success(new String(pucOutput));
            } else {
                result.error("ERROR", "Failed to execute PiccApduCmd", null);
            }
        } else if (call.method.equals("piccClose")) {
            int close = posApiHelper.PiccClose();
            if (close == 0) {
                posApiHelper.SysBeep();
                result.success("Picc closed successfully");
            } else {
                result.error("ERROR", "Failed to close Picc", null);
            }
        } else if (call.method.equals("piccRemove")) {
            int remove = posApiHelper.PiccRemove();
            if (remove == 0) {
                posApiHelper.SysBeep();
                result.success("Card is still in the magnetic field");
            } else {
                result.error("ERROR", "Card has left the magnetic field", null);
            }
        } else if (call.method.equals("piccSamAv2Init")) {
            int samSlotNo = call.argument("samSlotNo");
            List<Integer> samHostKeyList = call.argument("samHostKey");
            byte[] samHostKey = new byte[samHostKeyList.size()];
            for (int i = 0; i < samHostKeyList.size(); i++) {
                samHostKey[i] = samHostKeyList.get(i).byteValue();
            }
            byte[] samHostMode = new byte[2];
            byte[] samAv2Version = new byte[2];
            byte[] samAv2VerLen = new byte[1];
            try {
                int init = posApiHelper.PiccSamAv2Init(samSlotNo, samHostKey, samHostMode, samAv2Version, samAv2VerLen);
                if (init == 0) {
                    posApiHelper.SysBeep();
                    result.success("SAM AV2 initialized successfully");
                } else {
                    result.error("ERROR", "Failed to initialize SAM AV2", null);
                }
            } catch (Exception e) {
                result.error("ERROR", "Exception occurred: " + e.getMessage(), null);
            }

        } else if (call.method.equals("piccHwModeSet")) {
            int mode = call.argument("mode");
            int set = posApiHelper.PiccHwModeSet(mode);
            if (set == 0) {
                result.success("NFC work mode set successfully");
            } else {
                result.error("ERROR", "Failed to set NFC work mode", null);
            }
        } else if (call.method.equals("piccM1Authority")) {
            byte type = call.argument("type");
            byte blkNo = call.argument("blkNo");
            byte[] pwd = call.argument("pwd");
            byte[] serialNo = new byte[4]; // assuming a 4-byte serial number
            int authority = posApiHelper.PiccM1Authority(type, blkNo, pwd, serialNo);
            if (authority == 0) {
                posApiHelper.SysBeep();
                result.success("M1 card authority verified successfully");
            } else {
                result.error("ERROR", "Failed to verify M1 card authority", null);
            }
        } else if (call.method.equals("PiccNfc")) {
            byte[] nfcDataLen = new byte[5];
            byte[] technology = new byte[25];
            byte[] nfcUid = new byte[56];
            byte[] ndefMessage = new byte[500];

            int ret = posApiHelper.PiccNfc(nfcDataLen, technology, nfcUid, ndefMessage);

            int technologyLength = nfcDataLen[0] & 0xFF;
            int nfcUidLength = nfcDataLen[1] & 0xFF;
            int ndefMessageLength = (nfcDataLen[3] & 0xFF) + (nfcDataLen[4] & 0xFF);

            byte[] ndefMessageData = new byte[ndefMessageLength];
            byte[] nfcUidData = new byte[nfcUidLength];

            System.arraycopy(nfcUid, 0, nfcUidData, 0, nfcUidLength);
            System.arraycopy(ndefMessage, 0, ndefMessageData, 0, ndefMessageLength);

            String ndefMessageDataStr = new String(ndefMessageData);
            String ndefStr = null;
            if (ndefMessageDataStr.contains("http://")) {
                ndefStr = "NDEF: " + ndefMessageDataStr.substring(ndefMessageDataStr.indexOf("http://"));
            } else if (ndefMessageDataStr.contains("https://")) {
                ndefStr = "NDEF: " + ndefMessageDataStr.substring(ndefMessageDataStr.indexOf("https://"));
            } else if (ndefMessageDataStr.contains("www.")) {
                ndefStr = "NDEF: " + ndefMessageDataStr.substring(ndefMessageDataStr.indexOf("www."));
            } else {
                ndefStr = "NDEF: " + ndefMessageDataStr;
            }


            if (ret == 0) {
                posApiHelper.SysBeep();
                String tmpStr = "TYPE: " + new String(technology).substring(0, technologyLength) + "\n" + "UID: " + ByteUtil.bytearrayToHexString(nfcUidData, nfcUidData.length) + "\n" + ndefStr;
                result.success(tmpStr);
            } else {
                result.error("ERROR", "Read Card Failed !..", null);
            }
        } else if (call.method.equals("SysApiVerson")) {
            String version = posApiHelper.SysApiVerson();
            result.success(version);
        } else if (call.method.equals("getOSVersion")) {
            String osVersion = posApiHelper.getOSVersion();
            if (osVersion != null) {
                result.success(osVersion);
            } else {
                result.error("ERROR", "Failed to get OS version", null);
            }
        } else if (call.method.equals("getDeviceId")) {
            try {
                String deviceId = posApiHelper.getDeviceId();
                if (deviceId != null) {
                    result.success(deviceId);
                } else {
                    result.error("ERROR", "Failed to get device ID", null);
                }
            } catch (Exception e) {
                result.error("ERROR", "Exception occurred: " + e.getMessage(), null);
            }
        } else if (call.method.equals("SysLogSwitch")) {
            int level = call.argument("level");
            int logSwitch = posApiHelper.SysLogSwitch(level);
            if (logSwitch == 0) {
                result.success("Log switch set successfully");
            } else {
                result.error("ERROR", "Failed to set log switch", null);
            }
        } else if (call.method.equals("SysGetRand")) {
            byte[] rnd = new byte[16]; // assuming a 16-byte random number
            int getRand = posApiHelper.SysGetRand(rnd);
            if (getRand == 0) {
                result.success(rnd);
            } else {
                result.error("ERROR", "Failed to get random number", null);
            }
        } else if (call.method.equals("SysUpdate")) {
            int update = posApiHelper.SysUpdate();
            if (update == 0) {
                result.success("MCU app firmware updated successfully");
            } else {
                result.error("ERROR", "Failed to update MCU app firmware", null);
            }
        } else if (call.method.equals("SysGetVersion")) {
            byte[] buf = new byte[10]; // assuming a 10-byte buffer
            int getVersion = posApiHelper.SysGetVersion(buf);
            if (getVersion == 0) {
                result.success(buf);
            } else {
                result.error("ERROR", "Failed to get MCU firmware version", null);
            }
        } else if (call.method.equals("SysReadSN")) {
            byte[] SN = new byte[16]; // assuming a 16-byte serial number
            int readSN = posApiHelper.SysReadSN(SN);
            if (readSN == 0) {
                result.success(SN);
            } else {
                result.error("ERROR", "Failed to read serial number", null);
            }
        } // Printer methods
        else if (call.method.equals("PrintInit")) {
            int ret = posApiHelper.PrintInit();
            if (ret == 0) {
                result.success("Printer initialized successfully");
            } else {
                result.error("ERROR", "Failed to initialize printer", null);
            }
        } else if (call.method.equals("PrintInitWithParams")) {
            int gray = call.argument("gray");
            int fontHeight = call.argument("fontHeight");
            int fontWidth = call.argument("fontWidth");
            int fontZoom = call.argument("fontZoom");
            int ret = posApiHelper.PrintInit(gray, fontHeight, fontWidth, fontZoom);
            if (ret == 0) {
                result.success("Printer initialized with parameters successfully");
            } else {
                result.error("ERROR", "Failed to initialize printer with parameters", null);
            }
        } else if (call.method.equals("PrintSetFont")) {
            Number asciiFontHeightNumber = call.argument("asciiFontHeight");
            byte asciiFontHeight = asciiFontHeightNumber.byteValue();

            Number extendFontHeightNumber = call.argument("extendFontHeight");
            byte extendFontHeight = extendFontHeightNumber.byteValue();

            Number zoomNumber = call.argument("zoom");
            byte zoom = zoomNumber.byteValue();

            int ret = posApiHelper.PrintSetFont(asciiFontHeight, extendFontHeight, zoom);
            if (ret == 0) {
                result.success("Printer font set successfully");
            } else {
                result.error("ERROR", "Failed to set printer font", null);
            }
        } else if (call.method.equals("PrintSetGray")) {
            int nLevel = call.argument("nLevel");
            int ret = posApiHelper.PrintSetGray(nLevel);
            if (ret == 0) {
                result.success("Printer gray level set successfully");
            } else {
                result.error("ERROR", "Failed to set printer gray level", null);
            }
        } else if (call.method.equals("PrintSetSpace")) {
            byte x = call.argument("x");
            byte y = call.argument("y");
            int ret = posApiHelper.PrintSetSpace(x, y);
            if (ret == 0) {
                result.success("Printer space set successfully");
            } else {
                result.error("ERROR", "Failed to set printer space", null);
            }
        } else if (call.method.equals("PrintGetFont")) {
            byte[] asciiFontHeight = new byte[1];
            byte[] extendFontHeight = new byte[1];
            byte[] zoom = new byte[1];
            int ret = posApiHelper.PrintGetFont(asciiFontHeight, extendFontHeight, zoom);
            if (ret == 0) {
                result.success("ASCII: " + asciiFontHeight[0] + ", Extend: " + extendFontHeight[0] + ", Zoom: " + zoom[0]);
            } else {
                result.error("ERROR", "Failed to get printer font", null);
            }
        } else if (call.method.equals("PrintStep")) {
            int pixel = call.argument("pixel");
            int ret = posApiHelper.PrintStep(pixel);
            if (ret == 0) {
                result.success("Print step set successfully");
            } else {
                result.error("ERROR", "Failed to set print step", null);
            }
        } else if (call.method.equals("PrintSetVoltage")) {
            int voltage = call.argument("voltage");
            int ret = posApiHelper.PrintSetVoltage(voltage);
            if (ret == 0) {
                result.success("Printer voltage set successfully");
            } else {
                result.error("ERROR", "Failed to set printer voltage", null);
            }
        } else if (call.method.equals("PrintIsCharge")) {
            int ischarge = call.argument("ischarge");
            int ret = posApiHelper.PrintIsCharge(ischarge);
            if (ret == 0) {
                result.success("Printer charge status set successfully");
            } else {
                result.error("ERROR", "Failed to set printer charge status", null);
            }
        } else if (call.method.equals("PrintSetLinPixelDis")) {
            char linDistance = call.argument("linDistance");
            int ret = posApiHelper.PrintSetLinPixelDis(linDistance);
            if (ret == 0) {
                result.success("Print line pixel distance set successfully");
            } else {
                result.error("ERROR", "Failed to set print line pixel distance", null);
            }
        } else if (call.method.equals("PrintStr")) {
            String str = call.argument("str");
            int ret = posApiHelper.PrintStr(str);
            if (ret == 0) {
                result.success("String printed successfully");
            } else {
                result.error("ERROR", "Failed to print string", null);
            }
        } else if (call.method.equals("PrintBmp")) {
            byte[] bmpData = call.argument("bmpData");
            Log.d("PrintBmp", "Received bmpData length: " + (bmpData != null ? bmpData.length : "null"));

            Bitmap bitmap = BitmapFactory.decodeByteArray(bmpData, 0, bmpData.length);

            if (bitmap != null) {
                Log.d("PrintBmp", "Bitmap decoded successfully. Width: " + bitmap.getWidth() + ", Height: " + bitmap.getHeight());
                int ret = posApiHelper.PrintBmp(bitmap);
                if (ret == 0) {
                    result.success("Bitmap printed successfully");
                } else {
                    Log.e("PrintBmp", "Failed to print bitmap. Error code: " + ret);
                    result.error("ERROR", "Failed to print bitmap. Error code: " + ret, null);
                }
            } else {
                Log.e("PrintBmp", "Failed to decode bitmap from byte array");
                result.error("ERROR", "Invalid bitmap data", null);
            }
        } else if (call.method.equals("PrintBarcode")) {
            String contents = call.argument("contents");
            int desiredWidth = call.argument("desiredWidth");
            int desiredHeight = call.argument("desiredHeight");
            String barcodeFormat = call.argument("barcodeFormat");
            int ret = posApiHelper.PrintBarcode(contents, desiredWidth, desiredHeight, barcodeFormat);
            if (ret == 0) {
                result.success("Barcode printed successfully");
            } else {
                result.error("ERROR", "Failed to print barcode", null);
            }
        } else if (call.method.equals("PrintQrCode_Cut")) {
            String contents = call.argument("contents");
            int desiredWidth = call.argument("desiredWidth");
            int desiredHeight = call.argument("desiredHeight");
            String barcodeFormat = call.argument("barcodeFormat");
            int ret = posApiHelper.PrintQrCode_Cut(contents, desiredWidth, desiredHeight, barcodeFormat);
            if (ret == 0) {
                result.success("QR code printed successfully");
            } else {
                result.error("ERROR", "Failed to print QR code", null);
            }
        } else if (call.method.equals("PrintCutQrCode_Str")) {
            String contents = call.argument("contents");
            String printTxt = call.argument("printTxt");
            int distance = call.argument("distance");
            int desiredWidth = call.argument("desiredWidth");
            int desiredHeight = call.argument("desiredHeight");
            String barcodeFormat = call.argument("barcodeFormat");
            int ret = posApiHelper.PrintCutQrCode_Str(contents, printTxt, distance, desiredWidth, desiredHeight, barcodeFormat);
            if (ret == 0) {
                result.success("QR code with text printed successfully");
            } else {
                result.error("ERROR", "Failed to print QR code with text", null);
            }
        } else if (call.method.equals("PrintStart")) {
            int ret = posApiHelper.PrintStart();
            if (ret == 0) {
                result.success("Print started successfully");
            } else {
                result.error("ERROR", "Failed to start print", null);
            }
        } else if (call.method.equals("PrintSetLeftIndent")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintSetLeftIndent(x);
            if (ret == 0) {
                result.success("Left indent set successfully");
            } else {
                result.error("ERROR", "Failed to set left indent", null);
            }
        } else if (call.method.equals("PrintSetAlign")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintSetAlign(x);
            if (ret == 0) {
                result.success("Alignment set successfully");
            } else {
                result.error("ERROR", "Failed to set alignment", null);
            }
        } else if (call.method.equals("PrintCharSpace")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintCharSpace(x);
            if (ret == 0) {
                result.success("Character space set successfully");
            } else {
                result.error("ERROR", "Failed to set character space", null);
            }
        } else if (call.method.equals("PrintSetLineSpace")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintSetLineSpace(x);
            if (ret == 0) {
                result.success("Line space set successfully");
            } else {
                result.error("ERROR", "Failed to set line space", null);
            }
        } else if (call.method.equals("PrintSetLeftSpace")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintSetLeftSpace(x);
            if (ret == 0) {
                result.success("Left space set successfully");
            } else {
                result.error("ERROR", "Failed to set left space", null);
            }
        } else if (call.method.equals("PrintSetSpeed")) {
            int iSpeed = call.argument("iSpeed");
            int ret = posApiHelper.PrintSetSpeed(iSpeed);
            if (ret == 0) {
                result.success("Print speed set successfully");
            } else {
                result.error("ERROR", "Failed to set print speed", null);
            }
        } else if (call.method.equals("PrintCheckStatus")) {
            int ret = posApiHelper.PrintCheckStatus();
            if (ret == 0) {
                result.success("Printer status checked successfully");
            } else {
                result.error("ERROR", "Failed to check printer status", null);
            }
        } else if (call.method.equals("PrintFeedPaper")) {
            int step = call.argument("step");
            int ret = posApiHelper.PrintFeedPaper(step);
            if (ret == 0) {
                result.success("Paper fed successfully");
            } else {
                result.error("ERROR", "Failed to feed paper", null);
            }
        } else if (call.method.equals("PrintSetMode")) {
            int mode = call.argument("mode");
            int ret = posApiHelper.PrintSetMode(mode);
            if (ret == 0) {
                result.success("Print mode set successfully");
            } else {
                result.error("ERROR", "Failed to set print mode", null);
            }
        } else if (call.method.equals("PrintSetUnderline")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintSetUnderline(x);
            if (ret == 0) {
                result.success("Underline set successfully");
            } else {
                result.error("ERROR", "Failed to set underline", null);
            }
        } else if (call.method.equals("PrintSetReverse")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintSetReverse(x);
            if (ret == 0) {
                result.success("Reverse mode set successfully");
            } else {
                result.error("ERROR", "Failed to set reverse mode", null);
            }
        } else if (call.method.equals("PrintSetBold")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintSetBold(x);
            if (ret == 0) {
                result.success("Bold mode set successfully");
            } else {
                result.error("ERROR", "Failed to set bold mode", null);
            }
        } else if (call.method.equals("PrintLogo")) {
            byte[] logo = call.argument("logo");
            int ret = posApiHelper.PrintLogo(logo);
            if (ret == 0) {
                result.success("Logo printed successfully");
            } else {
                result.error("ERROR", "Failed to print logo", null);
            }
        } else if (call.method.equals("PrintLabLocate")) {
            int step = call.argument("step");
            int ret = posApiHelper.PrintLabLocate(step);
        } else if (call.method.equals("PrintPdf")) {
            String pdfPath = call.argument("pdfPath");
            if (pdfPath != null) {
                if (activity != null) {
                    printPdf(pdfPath, result);
                } else {
                    result.error("NO_ACTIVITY", "Cannot print without an activity context", null);
                }
            } else {
                result.error("INVALID_ARGUMENTS", "Missing pdfPath", null);
            }
        } else if (call.method.equals("CancelJob")) {
            String jobId = call.argument("jobId");
            if (jobId != null) {
                cancelJob(jobId, result);
            } else {
                result.error("INVALID_ARGUMENTS", "Missing jobId", null);
            }
        } else if (call.method.equals("RetryJob")) {
            String jobId = call.argument("jobId");

            retryFailedPages(result);
//            if (jobId != null) {
//                retryJob(jobId, result);
//            } else {
//                result.error("INVALID_ARGUMENTS", "Missing jobId", null);
//            }
        } else if (call.method.equals("GetPrintStats")) {
            getPrintStats(result);
        } else {
            result.notImplemented();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void printPdf(String pdfPath, Result result) {
        Log.d(TAG, "Starting printPdf with path: " + pdfPath);
        currentPdfPath = pdfPath;
        failedPages.clear();
        try {
            File file = new File(pdfPath);
            if (!file.exists()) {
                String errorMsg = "PDF file does not exist: " + pdfPath;
                Log.e(TAG, errorMsg);
                result.error("FILE_NOT_FOUND", errorMsg, null);
                return;
            }

            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(fileDescriptor);
            totalPages = renderer.getPageCount();
            Log.d(TAG, "PdfRenderer created successfully. Page count: " + totalPages);

            // Set print settings for clearer output
            setPrintSettings();

            // Process and print all pages
            boolean allPagesPrinted = processAndPrintPages(renderer, 0, totalPages - 1);

            renderer.close();
            fileDescriptor.close();

            if (!failedPages.isEmpty()) {
                String warningMsg = "Some pages failed to print: " + failedPages;
                Log.w(TAG, warningMsg);
                result.success(new HashMap<String, Object>() {{
                    put("status", "PARTIAL_SUCCESS");
                    put("failedPages", failedPages);
                    put("message", warningMsg);
                }});
            } else if (allPagesPrinted) {
                Log.d(TAG, "PDF processed and printed successfully");
                result.success(new HashMap<String, Object>() {{
                    put("status", "SUCCESS");
                    put("message", "PDF processed and printed successfully");
                }});
            } else {
                String errorMsg = "Failed to print all pages. Failed pages: " + failedPages;
                Log.e(TAG, errorMsg);
                result.error("PRINT_FAILED", errorMsg, null);
            }

        } catch (IOException e) {
            String errorMsg = "IOException occurred: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            result.error("IO_EXCEPTION", errorMsg, Arrays.toString(e.getStackTrace()));
        } catch (Exception e) {
            String errorMsg = "Unexpected error occurred: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            result.error("UNEXPECTED_ERROR", errorMsg, Arrays.toString(e.getStackTrace()));
        }
    }

    private boolean processAndPrintPages(PdfRenderer renderer, int startPage, int endPage) {
        int tileWidth = 384;
        int tileHeight = 984;

        boolean allPagesPrinted = true;

        for (int i = startPage; i <= endPage; i++) {
            if (!processAndPrintPage(renderer, i, tileWidth, tileHeight)) {
                failedPages.add(i);
                allPagesPrinted = false;
                Log.e(TAG, "Failed to process and print page " + (i + 1));
            } else {
                sendPrintingProgressUpdate(i + 1, totalPages);
            }
        }

        return allPagesPrinted;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean processAndPrintPage(PdfRenderer renderer, int pageIndex, int tileWidth, int tileHeight) {
        PdfRenderer.Page page = renderer.openPage(pageIndex);
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();

        boolean pageSuccessful = true;

        // Initialize printer for this page
        int ret = posApiHelper.PrintInit();
        if (ret != 0) {
            String errorMsg = "Failed to initialize printer for page " + (pageIndex + 1) + ". Error code: " + ret;
            Log.e(TAG, errorMsg);
            return false;
        }

        for (int y = 0; y < pageHeight; y += tileHeight) {
            for (int x = 0; x < pageWidth; x += tileWidth) {
                int currentTileWidth = Math.min(tileWidth, pageWidth - x);
                int currentTileHeight = Math.min(tileHeight, pageHeight - y);

                Bitmap tileBitmap = Bitmap.createBitmap(currentTileWidth, currentTileHeight, Bitmap.Config.ARGB_8888);
                Matrix matrix = new Matrix();

                Rect srcRect = new Rect(x, y, x + currentTileWidth, y + currentTileHeight);
                Rect dstRect = new Rect(0, 0, currentTileWidth, currentTileHeight);
                float scaleX = (float) dstRect.width() / srcRect.width();
                float scaleY = (float) dstRect.height() / srcRect.height();

                matrix.setScale(scaleX, scaleY);

                page.render(tileBitmap, dstRect, matrix, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);

                tileBitmap = enhanceBitmapForThermalPrinting(tileBitmap);

                ret = posApiHelper.PrintBmp(tileBitmap);
                if (ret != 0) {
                    String errorMsg = "Failed to queue tile at page " + (pageIndex + 1) + ", x=" + x + ", y=" + y + ". Error code: " + ret;
                    Log.e(TAG, errorMsg);
                    pageSuccessful = false;
                    break;
                }

                tileBitmap.recycle();
            }

            if (!pageSuccessful) break;

            posApiHelper.PrintStep(1);
        }

        page.close();

        if (pageSuccessful) {
            // Start printing for this page
            ret = posApiHelper.PrintStart();
            if (ret != 0) {
                String errorMsg = "Failed to start printing for page " + (pageIndex + 1) + ". Error code: " + ret;
                Log.e(TAG, errorMsg);
                pageSuccessful = false;
            }
        }

        sendProcessingProgressUpdate(pageIndex + 1, totalPages);
        lastPrintedPageIndex = pageIndex;
        return pageSuccessful;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void printLastPage(Result result) {
        Log.d(TAG, "Starting printLastPage");
        if (lastPrintedPdfPath == null || lastPrintedPdfPath.isEmpty() || lastPrintedPageIndex == -1) {
            Log.e(TAG, "No previous print job found");
            result.error("NO_PREVIOUS_JOB", "No previous print job found", null);
            return;
        }

        try {
            File file = new File(lastPrintedPdfPath);
            if (!file.exists()) {
                Log.e(TAG, "PDF file does not exist: " + lastPrintedPdfPath);
                result.error("FILE_NOT_FOUND", "PDF file does not exist", null);
                return;
            }

            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(fileDescriptor);

            // Initialize printer
            int ret = posApiHelper.PrintInit();
            if (ret != 0) {
                Log.e(TAG, "Failed to initialize printer: " + ret);
                result.error("PRINTER_INIT_FAILED", "Failed to initialize printer", null);
                return;
            }

            // Set print settings for clearer output
            setPrintSettings();

            // Print the last page
            boolean success = printPage(renderer, lastPrintedPageIndex, 384, 984);

            // Start printing
            ret = posApiHelper.PrintStart();
            if (ret != 0) {
                Log.e(TAG, "Failed to start printing: " + ret);
                result.error("PRINT_START_FAILED", "Failed to start printing", null);
                return;
            }

            renderer.close();
            fileDescriptor.close();

            if (success) {
                Log.d(TAG, "Last page printed successfully");
                result.success(new HashMap<String, Object>() {{
                    put("status", "SUCCESS");
                    put("message", "Last page printed successfully");
                }});
            } else {
                Log.w(TAG, "Failed to print last page");
                result.success(new HashMap<String, Object>() {{
                    put("status", "FAILED");
                    put("message", "Failed to print last page");
                }});
            }

        } catch (IOException e) {
            Log.e(TAG, "IOException occurred", e);
            result.error("IO_EXCEPTION", "An IO error occurred", e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error occurred", e);
            result.error("UNEXPECTED_ERROR", "An unexpected error occurred", e.getMessage());
        }
    }

    private void sendProcessingProgressUpdate(int currentPage, int totalPages) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        if (channel != null) {
            Map<String, Object> progressMap = new HashMap<>();
            progressMap.put("currentPage", currentPage);
            progressMap.put("totalPages", totalPages);
            progressMap.put("method", "processingProgress");

            Log.d(TAG, "Sending processing progress update: " + currentPage + "/" + totalPages);

            new Handler(Looper.getMainLooper()).post(() -> channel.invokeMethod("processingProgress", progressMap));
        }
    }

    private void sendPrintingProgressUpdate(int currentPage, int totalPages) {
        if (channel != null) {
            Map<String, Object> progressMap = new HashMap<>();
            progressMap.put("currentPage", currentPage);
            progressMap.put("totalPages", totalPages);
            progressMap.put("method", "printingProgress");

            Log.d(TAG, "Sending printing progress update: " + currentPage + "/" + totalPages);

            new Handler(Looper.getMainLooper()).post(() -> channel.invokeMethod("printingProgress", progressMap));
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    public void retryFailedPages(Result result) {
        Log.d(TAG, "Starting retryFailedPages");
        if (failedPages.isEmpty()) {
            Log.d(TAG, "No failed pages to retry");
            result.success(new HashMap<String, Object>() {{
                put("status", "NO_RETRY_NEEDED");
                put("message", "No failed pages to retry");
            }});
            return;
        }

        try {
            File file = new File(currentPdfPath);
            if (!file.exists()) {
                String errorMsg = "PDF file does not exist: " + currentPdfPath;
                Log.e(TAG, errorMsg);
                result.error("FILE_NOT_FOUND", errorMsg, null);
                return;
            }

            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(fileDescriptor);

            List<Integer> stillFailedPages = new ArrayList<>();
            int totalRetryPages = failedPages.size();
            int currentRetryPage = 0;

            // Set print settings for clearer output
            setPrintSettings();

            for (int pageIndex : failedPages) {
                currentRetryPage++;
                if (!processAndPrintPage(renderer, pageIndex, 384, 984)) {
                    stillFailedPages.add(pageIndex);
                    Log.e(TAG, "Failed to reprint page " + (pageIndex + 1));
                } else {
                    Log.d(TAG, "Successfully reprinted page " + (pageIndex + 1));
                }
                sendRetryProgressUpdate(currentRetryPage, totalRetryPages);
            }

            renderer.close();
            fileDescriptor.close();

            failedPages = stillFailedPages;
            if (!failedPages.isEmpty()) {
                String warningMsg = "Some pages still failed after retry: " + failedPages;
                Log.w(TAG, warningMsg);
                result.success(new HashMap<String, Object>() {{
                    put("status", "PARTIAL_RETRY_SUCCESS");
                    put("failedPages", failedPages);
                    put("message", warningMsg);
                }});
            } else {
                Log.d(TAG, "All failed pages printed successfully on retry");
                result.success(new HashMap<String, Object>() {{
                    put("status", "RETRY_SUCCESS");
                    put("message", "All failed pages printed successfully on retry");
                }});
            }

        } catch (IOException e) {
            String errorMsg = "IOException occurred during retry: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            result.error("IO_EXCEPTION", errorMsg, e.getStackTrace().toString());
        } catch (Exception e) {
            String errorMsg = "Unexpected error occurred during retry: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            result.error("UNEXPECTED_ERROR", errorMsg, e.getStackTrace().toString());
        }
    }


    private void sendRetryProgressUpdate(int currentPage, int totalPages) {
        if (channel != null) {
            Map<String, Object> progressMap = new HashMap<>();
            progressMap.put("currentPage", currentPage);
            progressMap.put("totalPages", totalPages);
            progressMap.put("method", "retryProgress");

            Log.d(TAG, "Sending retry progress update: " + currentPage + "/" + totalPages);

            new Handler(Looper.getMainLooper()).post(() -> channel.invokeMethod("retryProgress", progressMap));
        }
    }
    private void setPrintSettings() {
        posApiHelper.PrintSetGray(5);
        posApiHelper.PrintSetMode(0);
        posApiHelper.PrintSetSpeed(1);
        posApiHelper.PrintSetAlign(0);
        posApiHelper.PrintSetFont((byte) 24, (byte) 24, (byte) 0x33);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void getPrintStats(Result result) {
        Map<String, Object> stats = new HashMap<>();

        Map<String, Object> progressMap = new HashMap<>();
        progressMap.put("currentPage", this.currentPage);
        progressMap.put("totalPages", this.totalPages);
        progressMap.put("method", "printProgress");
        stats.put("progress", progressMap);

        Map<String, Object> jobStats = new HashMap<>();
        if (activeJobs != null) {
            for (Map.Entry<String, PrintJob> entry : activeJobs.entrySet()) {
                String jobId = entry.getKey();
                PrintJob job = entry.getValue();
                PrintJobInfo jobInfo = job.getInfo();

                Map<String, Object> jobDetails = new HashMap<>();
                jobDetails.put("pages", jobInfo.getPages() != null ? jobInfo.getPages().length : 0);
                jobDetails.put("copies", jobInfo.getCopies());
                jobDetails.put("creationTime", jobInfo.getCreationTime());
                jobDetails.put("isBlocked", job.isBlocked());
                jobDetails.put("isCancelled", job.isCancelled());
                jobDetails.put("isCompleted", job.isCompleted());
                jobDetails.put("isFailed", job.isFailed());
                jobDetails.put("isQueued", job.isQueued());
                jobDetails.put("isStarted", job.isStarted());
                // Safely handle potential null values for retryCount
                AtomicInteger retryCounter = retryCount.get(jobId);
                jobDetails.put("retryCount", retryCounter != null ? retryCounter.get() : 0);

                jobStats.put(jobId, jobDetails);
            }
        }

        stats.put("jobs", jobStats);
        result.success(stats);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Bitmap enhanceBitmapForThermalPrinting(Bitmap original) {
        Bitmap output = Bitmap.createBitmap(original.getWidth(), original.getHeight(), original.getConfig());

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicConvolve3x3 convolution = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        Allocation input = Allocation.createFromBitmap(rs, original);
        Allocation output_alloc = Allocation.createFromBitmap(rs, output);

        float[] sharpening = {
                -1, -1, -1,
                -1, 9, -1,
                -1, -1, -1
        };
        convolution.setCoefficients(sharpening);
        convolution.setInput(input);
        convolution.forEach(output_alloc);
        output_alloc.copyTo(output);

        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix(new float[]
                {1.5f, 0, 0, 0, -20,
                        0, 1.5f, 0, 0, -20,
                        0, 0, 1.5f, 0, -20,
                        0, 0, 0, 1, 0});
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(output, 0, 0, paint);

        // Optional Thresholding
        // Bitmap thresholded = Bitmap.createBitmap(output.getWidth(), output.getHeight(), Bitmap.Config.ARGB_8888);


        rs.destroy();
        return output;
    }


//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    private void printPdf(String pdfPath, Result result) {
//        try {
//            String jobName = "Document " + System.currentTimeMillis();
//            PdfDocumentAdapter pda = new PdfDocumentAdapter(pdfPath);
//
//            PrintAttributes attributes = new PrintAttributes.Builder()
//                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
//                    .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 300, 300))
//                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
//                    .build();
//
//            PrintJob printJob = printManager.print(jobName, pda, attributes);
//            String jobId = Objects.requireNonNull(printJob.getId()).toString();
//            activeJobs.put(jobId, printJob);
//
//            result.success(jobId);
//        } catch (Exception e) {
//            Log.e(TAG, "Failed to start print job", e);
//            result.error("PRINT_ERROR", "Failed to start print job: " + e.getMessage(), null);
//        }
//    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void cancelJob(String jobId, Result result) {
        PrintJob job = activeJobs.get(jobId);
        if (job != null) {
            job.cancel();
            activeJobs.remove(jobId);
            result.success("Job cancelled");
        } else {
            result.error("JOB_NOT_FOUND", "No active job found with the given ID", null);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void retryJob(String jobId, Result result) {
        PrintJob oldJob = activeJobs.get(jobId);
        PdfDocumentAdapter adapter = activeAdapters.get(jobId);
        String pdfPath = jobToPdfPath.get(jobId);
        if (oldJob != null && adapter != null && pdfPath != null) {
            if (oldJob.isFailed() || oldJob.isCancelled()) {
                int attempts = Objects.requireNonNull(retryCount.get(jobId)).getAndIncrement();
                if (attempts < MAX_RETRY_ATTEMPTS) {
                    PrintJobInfo oldJobInfo = oldJob.getInfo();
                    adapter.setResumeWriting(true);
                    PrintAttributes attributes = oldJobInfo.getAttributes();

                    PrintJob newJob = printManager.print(oldJobInfo.getLabel(), adapter, attributes);
                    String newJobId = Objects.requireNonNull(newJob.getId()).toString();

                    cleanupJob(jobId);
                    activeJobs.put(newJobId, newJob);
                    activeAdapters.put(newJobId, adapter);
                    retryCount.put(newJobId, new AtomicInteger(attempts + 1));
                    jobToPdfPath.put(newJobId, pdfPath);

                    result.success(newJobId);
                } else {
                    result.error("MAX_RETRIES_REACHED", "Maximum retry attempts reached for this job", null);
                }
            } else {
                result.error("JOB_NOT_FAILED", "Cannot retry a job that hasn't failed or been cancelled", null);
            }
        } else {
            result.error("JOB_NOT_FOUND", "No active job found with the given ID", null);
        }
    }




    private void cleanupJob(String jobId) {
        activeJobs.remove(jobId);
        activeAdapters.remove(jobId);
        retryCount.remove(jobId);
        jobToPdfPath.remove(jobId);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private class PdfDocumentAdapter extends PrintDocumentAdapter {
        private final String filePath;
        private long totalBytesWritten;
        private boolean resumeWriting;

        PdfDocumentAdapter(String filePath) {
            this.filePath = filePath;
            this.totalBytesWritten = 0;
            this.resumeWriting = false;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal, LayoutResultCallback callback,
                             Bundle extras) {
            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("file name")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    .build();

            callback.onLayoutFinished(pdi, !oldAttributes.equals(newAttributes));
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                            CancellationSignal cancellationSignal, WriteResultCallback callback) {
            try (InputStream input = new BufferedInputStream(new FileInputStream(filePath));
                 OutputStream output = new BufferedOutputStream(new FileOutputStream(destination.getFileDescriptor()))) {
                ParcelFileDescriptor inputPfd = ParcelFileDescriptor.open(new File(filePath), ParcelFileDescriptor.MODE_READ_ONLY);
                PdfRenderer renderer = new PdfRenderer(inputPfd);
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                int pagesPrinted = 0;
                int totalPages = renderer.getPageCount();

                renderer.close();
                inputPfd.close();
                if (resumeWriting) {
                    input.skip(totalBytesWritten);
                }

                while ((bytesRead = input.read(buffer)) != -1) {
                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        return;
                    }

                    output.write(buffer, 0, bytesRead);
                    totalBytesWritten += bytesRead;

                    // Assume each page is approximately 100KB (adjust as needed)
                    int currentPage = (int) (totalBytesWritten / (100 * 1024)) + 1;
                    if (currentPage > pagesPrinted) {
                        pagesPrinted = currentPage;
                        // Update UI with current page being printed
                        updatePrintProgress(pagesPrinted, totalPages);
                    }

                    if (totalBytesWritten % (1024 * 1024) == 0) {
                        output.flush();
                    }
                }

                output.flush();
                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                totalPagesPrinted.addAndGet(pagesPrinted);
                resetPrintProgress();
            } catch (IOException e) {
                Log.e(TAG, "Error writing PDF", e);
                callback.onWriteFailed(e.toString());
                totalPagesUnprinted.addAndGet(pages.length - (int) (totalBytesWritten / (100 * 1024)));
            }
        }

        private void updatePrintProgress(int currentPage, int totalPages) {
            // Send progress update to Flutter
            if (channel != null) {
                Map<String, Object> progressData = new HashMap<>();
                progressData.put("currentPage", currentPage);
                progressData.put("totalPages", totalPages);
                progressData.put("totalBytesWritten", totalBytesWritten);
                channel.invokeMethod("onPrintProgress", progressData);
            }
        }

        private void resetPrintProgress() {
            totalBytesWritten = 0;
            resumeWriting = false;
            // Send reset event to Flutter
            if (channel != null) {
                channel.invokeMethod("onPrintReset", null);
            }
        }

        public void setResumeWriting(boolean resume) {
            this.resumeWriting = resume;
        }

        public long getTotalBytesWritten() {
            return totalBytesWritten;
        }
    }


    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
    }
}
