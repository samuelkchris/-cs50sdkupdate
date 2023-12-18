package com.pinnisoft.cs50sdkupdate;

import androidx.annotation.NonNull;
import com.ctk.sdk.PosApiHelper;
import com.ctk.sdk.ByteUtil;
import java.util.List;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** Cs50sdkupdatePlugin */
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
        int check = posApiHelper.PiccCheck((byte)'A', cardType, serialNo);
        if (check == 0) {
            posApiHelper.SysBeep();
            result.success("Picc checked, Card Type: " + new String(cardType) + ", Serial No: " + new String(serialNo));
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
            result.success("Picc polling successful, Card Type: " + new String(cardType) + ", UID: " + new String(uid));
        } else {
            result.error("ERROR", "Failed to poll picc", null);
        }
    } else if (call.method.equals("piccCommand")) {
        byte[] apduSend = call.argument("apduSend");
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
        int apduCmd = posApiHelper.PiccApduCmd(pucInput, (short)pucInput.length, pucOutput, pusOutputLen);
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
    int init = posApiHelper.PiccSamAv2Init(samSlotNo, samHostKey, samHostMode, samAv2Version, samAv2VerLen);
 if (init == 0) {
     posApiHelper.SysBeep();
            result.success("SAM AV2 initialized successfully");
        } else {
            result.error("ERROR", "Failed to initialize SAM AV2", null);
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
    } else {
        result.notImplemented();
    }
}

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}