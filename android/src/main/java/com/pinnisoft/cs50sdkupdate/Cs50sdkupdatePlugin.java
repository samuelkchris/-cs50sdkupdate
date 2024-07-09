package com.pinnisoft.cs50sdkupdate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ctk.sdk.PosApiHelper;
import com.ctk.sdk.ByteUtil;

import java.util.ArrayList;
import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * A Flutter plugin that provides access to the CS50 SDK for Android.
 *
 * <p>This plugin exposes the following methods to Flutter:</p>
 * <ul>
 * <li>getPlatformVersion: Returns the Android platform version.</li>
 * <li>openPicc: Opens the PICC interface.</li>
 * <li>piccCheck: Checks for a PICC card.</li>
 * <li>piccPolling: Polls for a PICC card.</li>
 * <li>piccCommand: Executes a PICC command.</li>
 * <li>piccApduCmd: Executes a PICC APDU command.</li>
 * <li>piccClose: Closes the PICC interface.</li>
 * <li>piccRemove: Removes a PICC card from the magnetic field.</li>
 * <li>piccSamAv2Init: Initializes the PICC SAM AV2.</li>
 * <li>piccHwModeSet: Sets the NFC work mode.</li>
 * <li>piccM1Authority: Verifies the M1 card authority.</li>
 * <li>PiccNfc: Reads the NFC card.</li>
 * <li>SysApiVerson: Gets the API version.</li>
 * <li>getOSVersion: Gets the Android OS version.</li>
 * <li>getDeviceId: Gets the device ID.</li>
 * <li>SysLogSwitch: Sets the log switch level.</li>
 * <li>SysGetRand: Gets a random number.</li>
 * <li>SysUpdate: Updates the MCU app firmware.</li>
 * <li>SysGetVersion: Gets the MCU firmware version.</li>
 * <li>SysReadSN: Reads the serial number.</li>
 * </ul>
 */
public class Cs50sdkupdatePlugin implements FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "cs50sdkupdate");
        channel.setMethodCallHandler(this);
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
                result.success("Android " + android.os.Build.VERSION.RELEASE + ", SDK Version: " + new String(version) + ", Picc opened");
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
                String tmpStr = "TYPE: " + new String(technology).substring(0, technologyLength) + "\n"
                        + "UID: " + ByteUtil.bytearrayToHexString(nfcUidData, nfcUidData.length) + "\n"
                        + ndefStr;
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
        }
        else if (call.method.equals("PrintInitWithParams")) {
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
        }
        else if (call.method.equals("PrintSetFont")) {
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
        }
        else if (call.method.equals("PrintSetGray")) {
            int nLevel = call.argument("nLevel");
            int ret = posApiHelper.PrintSetGray(nLevel);
            if (ret == 0) {
                result.success("Printer gray level set successfully");
            } else {
                result.error("ERROR", "Failed to set printer gray level", null);
            }
        }
        else if (call.method.equals("PrintSetSpace")) {
            byte x = call.argument("x");
            byte y = call.argument("y");
            int ret = posApiHelper.PrintSetSpace(x, y);
            if (ret == 0) {
                result.success("Printer space set successfully");
            } else {
                result.error("ERROR", "Failed to set printer space", null);
            }
        }
        else if (call.method.equals("PrintGetFont")) {
            byte[] asciiFontHeight = new byte[1];
            byte[] extendFontHeight = new byte[1];
            byte[] zoom = new byte[1];
            int ret = posApiHelper.PrintGetFont(asciiFontHeight, extendFontHeight, zoom);
            if (ret == 0) {
                result.success("ASCII: " + asciiFontHeight[0] + ", Extend: " + extendFontHeight[0] + ", Zoom: " + zoom[0]);
            } else {
                result.error("ERROR", "Failed to get printer font", null);
            }
        }
        else if (call.method.equals("PrintStep")) {
            int pixel = call.argument("pixel");
            int ret = posApiHelper.PrintStep(pixel);
            if (ret == 0) {
                result.success("Print step set successfully");
            } else {
                result.error("ERROR", "Failed to set print step", null);
            }
        }
        else if (call.method.equals("PrintSetVoltage")) {
            int voltage = call.argument("voltage");
            int ret = posApiHelper.PrintSetVoltage(voltage);
            if (ret == 0) {
                result.success("Printer voltage set successfully");
            } else {
                result.error("ERROR", "Failed to set printer voltage", null);
            }
        }
        else if (call.method.equals("PrintIsCharge")) {
            int ischarge = call.argument("ischarge");
            int ret = posApiHelper.PrintIsCharge(ischarge);
            if (ret == 0) {
                result.success("Printer charge status set successfully");
            } else {
                result.error("ERROR", "Failed to set printer charge status", null);
            }
        }
        else if (call.method.equals("PrintSetLinPixelDis")) {
            char linDistance = call.argument("linDistance");
            int ret = posApiHelper.PrintSetLinPixelDis(linDistance);
            if (ret == 0) {
                result.success("Print line pixel distance set successfully");
            } else {
                result.error("ERROR", "Failed to set print line pixel distance", null);
            }
        }
        else if (call.method.equals("PrintStr")) {
            String str = call.argument("str");
            int ret = posApiHelper.PrintStr(str);
            if (ret == 0) {
                result.success("String printed successfully");
            } else {
                result.error("ERROR", "Failed to print string", null);
            }
        }
        else if (call.method.equals("PrintBmp")) {
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
        }

        else if (call.method.equals("PrintBarcode")) {
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
        }
        else if (call.method.equals("PrintQrCode_Cut")) {
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
        }
        else if (call.method.equals("PrintCutQrCode_Str")) {
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
        }
        else if (call.method.equals("PrintStart")) {
            int ret = posApiHelper.PrintStart();
            if (ret == 0) {
                result.success("Print started successfully");
            } else {
                result.error("ERROR", "Failed to start print", null);
            }
        }
        else if (call.method.equals("PrintSetLeftIndent")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintSetLeftIndent(x);
            if (ret == 0) {
                result.success("Left indent set successfully");
            } else {
                result.error("ERROR", "Failed to set left indent", null);
            }
        }
        else if (call.method.equals("PrintSetAlign")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintSetAlign(x);
            if (ret == 0) {
                result.success("Alignment set successfully");
            } else {
                result.error("ERROR", "Failed to set alignment", null);
            }
        }
        else if (call.method.equals("PrintCharSpace")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintCharSpace(x);
            if (ret == 0) {
                result.success("Character space set successfully");
            } else {
                result.error("ERROR", "Failed to set character space", null);
            }
        }
        else if (call.method.equals("PrintSetLineSpace")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintSetLineSpace(x);
            if (ret == 0) {
                result.success("Line space set successfully");
            } else {
                result.error("ERROR", "Failed to set line space", null);
            }
        }
        else if (call.method.equals("PrintSetLeftSpace")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintSetLeftSpace(x);
            if (ret == 0) {
                result.success("Left space set successfully");
            } else {
                result.error("ERROR", "Failed to set left space", null);
            }
        }
        else if (call.method.equals("PrintSetSpeed")) {
            int iSpeed = call.argument("iSpeed");
            int ret = posApiHelper.PrintSetSpeed(iSpeed);
            if (ret == 0) {
                result.success("Print speed set successfully");
            } else {
                result.error("ERROR", "Failed to set print speed", null);
            }
        }
        else if (call.method.equals("PrintCheckStatus")) {
            int ret = posApiHelper.PrintCheckStatus();
            if (ret == 0) {
                result.success("Printer status checked successfully");
            } else {
                result.error("ERROR", "Failed to check printer status", null);
            }
        }
        else if (call.method.equals("PrintFeedPaper")) {
            int step = call.argument("step");
            int ret = posApiHelper.PrintFeedPaper(step);
            if (ret == 0) {
                result.success("Paper fed successfully");
            } else {
                result.error("ERROR", "Failed to feed paper", null);
            }
        }
        else if (call.method.equals("PrintSetMode")) {
            int mode = call.argument("mode");
            int ret = posApiHelper.PrintSetMode(mode);
            if (ret == 0) {
                result.success("Print mode set successfully");
            } else {
                result.error("ERROR", "Failed to set print mode", null);
            }
        }
        else if (call.method.equals("PrintSetUnderline")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintSetUnderline(x);
            if (ret == 0) {
                result.success("Underline set successfully");
            } else {
                result.error("ERROR", "Failed to set underline", null);
            }
        }
        else if (call.method.equals("PrintSetReverse")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintSetReverse(x);
            if (ret == 0) {
                result.success("Reverse mode set successfully");
            } else {
                result.error("ERROR", "Failed to set reverse mode", null);
            }
        }
        else if (call.method.equals("PrintSetBold")) {
            int x = call.argument("x");
            int ret = posApiHelper.PrintSetBold(x);
            if (ret == 0) {
                result.success("Bold mode set successfully");
            } else {
                result.error("ERROR", "Failed to set bold mode", null);
            }
        }
        else if (call.method.equals("PrintLogo")) {
            byte[] logo = call.argument("logo");
            int ret = posApiHelper.PrintLogo(logo);
            if (ret == 0) {
                result.success("Logo printed successfully");
            } else {
                result.error("ERROR", "Failed to print logo", null);
            }
        }
        else if (call.method.equals("PrintLabLocate")) {
            int step = call.argument("step");
            int ret = posApiHelper.PrintLabLocate(step);
            if (ret == 0) {
                result.success("Label located successfully");
            } else {
                result.error("ERROR", "Failed to locate label", null);
            }
        }
        else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }
}
