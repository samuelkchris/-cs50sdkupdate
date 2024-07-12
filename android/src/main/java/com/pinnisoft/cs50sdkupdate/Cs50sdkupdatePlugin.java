package com.pinnisoft.cs50sdkupdate;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private final Map<String, PrintJob> activeJobs = new HashMap<>();
    private final Map<String, Integer> retryCount = new HashMap<>();
    private final Map<String, String> jobToPdfPath = new HashMap<>(); // New map to store PDF paths
    private static final int MAX_RETRY_ATTEMPTS = 3;

    private int totalPagesPrinted = 0;
    private int totalPagesUnprinted = 0;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "cs50sdkupdate");
        channel.setMethodCallHandler(this);
        context = flutterPluginBinding.getApplicationContext();
    }

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
            if (jobId != null) {
                retryJob(jobId, result);
            } else {
                result.error("INVALID_ARGUMENTS", "Missing jobId", null);
            }
        } else if (call.method.equals("GetPrintStats")) {
            getPrintStats(result);
        } else {
            result.notImplemented();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void printPdf(String pdfPath, Result result) {
        try {
            String jobName = "Document " + System.currentTimeMillis();
            PrintDocumentAdapter pda = new PdfDocumentAdapter(context, pdfPath);

            PrintAttributes attributes = new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build();

            PrintJob printJob = printManager.print(jobName, pda, attributes);
            String jobId = Objects.requireNonNull(printJob.getId()).toString();
            activeJobs.put(jobId, printJob);
            retryCount.put(jobId, 0);
            jobToPdfPath.put(jobId, pdfPath); // Store the PDF path

            result.success(jobId);
        } catch (Exception e) {
            result.error("PRINT_ERROR", "Failed to start print job: " + e.getMessage(), null);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void cancelJob(String jobId, Result result) {
        PrintJob job = activeJobs.get(jobId);
        if (job != null) {
            job.cancel();
            activeJobs.remove(jobId);
            retryCount.remove(jobId);
            jobToPdfPath.remove(jobId);
            result.success("Job cancelled");
        } else {
            result.error("JOB_NOT_FOUND", "No active job found with the given ID", null);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void retryJob(String jobId, Result result) {
        PrintJob oldJob = activeJobs.get(jobId);
        String pdfPath = jobToPdfPath.get(jobId);
        if (oldJob != null && pdfPath != null) {
            if (oldJob.isFailed() || oldJob.isCancelled()) {
                int attempts = retryCount.get(jobId);
                if (attempts < MAX_RETRY_ATTEMPTS) {
                    // Create a new print job with the same parameters
                    PrintJobInfo oldJobInfo = oldJob.getInfo();
                    PrintDocumentAdapter pda = new PdfDocumentAdapter(context, pdfPath);
                    PrintAttributes attributes = oldJobInfo.getAttributes();

                    PrintJob newJob = printManager.print(oldJobInfo.getLabel(), pda, attributes);
                    String newJobId = Objects.requireNonNull(newJob.getId()).toString();

                    // Update job tracking
                    activeJobs.remove(jobId);
                    activeJobs.put(newJobId, newJob);
                    retryCount.put(newJobId, attempts + 1);
                    jobToPdfPath.put(newJobId, pdfPath);
                    jobToPdfPath.remove(jobId);

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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void getPrintStats(Result result) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPagesPrinted", totalPagesPrinted);
        stats.put("totalPagesUnprinted", totalPagesUnprinted);

        Map<String, Object> jobStats = new HashMap<>();
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
            jobDetails.put("retryCount", retryCount.get(jobId));

            jobStats.put(jobId, jobDetails);
        }

        stats.put("jobs", jobStats);
        result.success(stats);
    }



    @TargetApi(Build.VERSION_CODES.KITKAT)
    private class PdfDocumentAdapter extends PrintDocumentAdapter {
        private final String filePath;
        private PrintDocumentInfo pdi;

        PdfDocumentAdapter(Context context, String filePath) {
            this.filePath = filePath;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal, LayoutResultCallback callback,
                             Bundle extras) {
            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            pdi = new PrintDocumentInfo.Builder("file name")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(PrintDocumentInfo.PARCELABLE_WRITE_RETURN_VALUE)
                    .build();

            callback.onLayoutFinished(pdi, !oldAttributes.equals(newAttributes));
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                            CancellationSignal cancellationSignal, WriteResultCallback callback) {
            try (InputStream input = new BufferedInputStream(new FileInputStream(filePath));
                 OutputStream output = new BufferedOutputStream(new FileOutputStream(destination.getFileDescriptor()))) {

                byte[] buffer = new byte[8192]; // Increased buffer size for better performance
                long totalBytesWritten = 0;
                int bytesRead;

                while ((bytesRead = input.read(buffer)) != -1) {
                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        return;
                    }

                    output.write(buffer, 0, bytesRead);
                    totalBytesWritten += bytesRead;

                    // Periodically flush to avoid excessive memory usage
                    if (totalBytesWritten % (1024 * 1024) == 0) { // Flush every 1MB
                        output.flush();
                    }
                }

                output.flush(); // Final flush to ensure all data is written

                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                totalPagesPrinted += pages.length;
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                totalPagesUnprinted += pages.length;
                Log.e("PrintPdf", "Error writing PDF: " + e.getMessage(), e);
            }
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


// version 1:

//private float calculateScaleFactor(PrintAttributes.MediaSize mediaSize, int docWidth, int docHeight) {
//    float scaleX = (float) mediaSize.getWidthMils() / (docWidth * 1000f / 72f);
//    float scaleY = (float) mediaSize.getHeightMils() / (docHeight * 1000f / 72f);
//    return Math.min(scaleX, scaleY);
//}
//
//private boolean isPageBlank(Bitmap bitmap) {
//    int width = bitmap.getWidth();
//    int height = bitmap.getHeight();
//    int totalPixels = width * height;
//    int whitePixels = 0;
//    int blackPixels = 0;
//
//    for (int y = 0; y < height; y++) {
//        for (int x = 0; x < width; x++) {
//            int pixel = bitmap.getPixel(x, y);
//            if (pixel == Color.WHITE) {
//                whitePixels++;
//            } else if (pixel == Color.BLACK) {
//                blackPixels++;
//            }
//        }
//    }
//
//    // Consider the page blank if it's more than 99% white or black
//    return (whitePixels > 0.99 * totalPixels) || (blackPixels > 0.99 * totalPixels);
//}
//
//private Bitmap enhanceBitmapForThermalPrinting(Bitmap original) {
//    Bitmap output = Bitmap.createBitmap(original.getWidth(), original.getHeight(), original.getConfig());
//
//    RenderScript rs = RenderScript.create(context);
//    ScriptIntrinsicConvolve3x3 convolution = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
//    Allocation input = Allocation.createFromBitmap(rs, original);
//    Allocation output_alloc = Allocation.createFromBitmap(rs, output);
//
//    float[] sharpening = {0, -0.2f, 0, -0.2f, 1.8f, -0.2f, 0, -0.2f, 0}; // Reduced sharpening
//    convolution.setCoefficients(sharpening);
//    convolution.setInput(input);
//    convolution.forEach(output_alloc);
//    output_alloc.copyTo(output);
//
//    // Adjust contrast and brightness
//    Paint paint = new Paint();
//    ColorMatrix cm = new ColorMatrix(new float[]
//            {1.2f, 0, 0, 0, -15,
//                    0, 1.2f, 0, 0, -15,
//                    0, 0, 1.2f, 0, -15,
//                    0, 0, 0, 1, 0});
//    paint.setColorFilter(new ColorMatrixColorFilter(cm));
//    Canvas canvas = new Canvas(output);
//    canvas.drawBitmap(output, 0, 0, paint);
//
//    rs.destroy();
//    return output;
//}
//
//private Bitmap floydSteinbergDither(Bitmap src) {
//    int width = src.getWidth();
//    int height = src.getHeight();
//    int[] pixels = new int[width * height];
//    src.getPixels(pixels, 0, width, 0, 0, width, height);
//    int[] newPixels = new int[width * height];
//
//    for (int y = 0; y < height; y++) {
//        for (int x = 0; x < width; x++) {
//            int oldPixel = pixels[y * width + x];
//            int oldR = Color.red(oldPixel);
//            int oldG = Color.green(oldPixel);
//            int oldB = Color.blue(oldPixel);
//
//            int gray = (oldR + oldG + oldB) / 3;
//            int newPixel = gray < 128 ? Color.BLACK : Color.WHITE;
//            newPixels[y * width + x] = newPixel;
//
//            int error = gray - (newPixel == Color.BLACK ? 0 : 255);
//
//            if (x + 1 < width) {
//                addError(pixels, y * width + x + 1, error, 7.0f / 16.0f);
//            }
//            if (x - 1 >= 0 && y + 1 < height) {
//                addError(pixels, (y + 1) * width + x - 1, error, 3.0f / 16.0f);
//            }
//            if (y + 1 < height) {
//                addError(pixels, (y + 1) * width + x, error, 5.0f / 16.0f);
//            }
//            if (x + 1 < width && y + 1 < height) {
//                addError(pixels, (y + 1) * width + x + 1, error, 1.0f / 16.0f);
//            }
//        }
//    }
//
//    Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//    result.setPixels(newPixels, 0, width, 0, 0, width, height);
//    return result;
//}
//
//private void addError(int[] pixels, int idx, int error, float factor) {
//    int color = pixels[idx];
//    int gray = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3;
//    gray = Math.min(255, Math.max(0, gray + (int) (error * factor)));
//    pixels[idx] = Color.rgb(gray, gray, gray);
//}
//
//private Bitmap removePadding(Bitmap src) {
//    int width = src.getWidth();
//    int height = src.getHeight();
//    int left = 0, top = 0, right = width - 1, bottom = height - 1;
//
//    // Find left boundary
//    for (int x = 0; x < width; x++) {
//        boolean found = false;
//        for (int y = 0; y < height; y++) {
//            if (src.getPixel(x, y) == Color.BLACK) {
//                left = x;
//                found = true;
//                break;
//            }
//        }
//        if (found) break;
//    }
//
//    // Find right boundary
//    for (int x = width - 1; x >= 0; x--) {
//        boolean found = false;
//        for (int y = 0; y < height; y++) {
//            if (src.getPixel(x, y) == Color.BLACK) {
//                right = x;
//                found = true;
//                break;
//            }
//        }
//        if (found) break;
//    }
//
//    // Find top boundary
//    for (int y = 0; y < height; y++) {
//        boolean found = false;
//        for (int x = 0; x < width; x++) {
//            if (src.getPixel(x, y) == Color.BLACK) {
//                top = y;
//                found = true;
//                break;
//            }
//        }
//        if (found) break;
//    }
//
//    // Find bottom boundary
//    for (int y = height - 1; y >= 0; y--) {
//        boolean found = false;
//        for (int x = 0; x < width; x++) {
//            if (src.getPixel(x, y) == Color.BLACK) {
//                bottom = y;
//                found = true;
//                break;
//            }
//        }
//        if (found) break;
//    }
//
//    // Add a small margin
//    int margin = 5;
//    left = Math.max(0, left - margin);
//    top = Math.max(0, top - margin);
//    right = Math.min(width - 1, right + margin);
//    bottom = Math.min(height - 1, bottom + margin);
//
//    return Bitmap.createBitmap(src, left, top, right - left + 1, bottom - top + 1);
//}
