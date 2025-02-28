package com.pinnisoft.cs50sdkupdate;

import android.util.Log;

import com.ctk.sdk.ByteUtil;
import com.ctk.sdk.PosApiHelper;

import java.util.ArrayList;
import java.util.List;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * Handles NFC and PICC card operations
 */
public class NfcHandler {
    private static final String TAG = "NfcHandler";

    private final PosApiHelper posApiHelper;
    private final MethodChannel channel;

    public NfcHandler(PosApiHelper posApiHelper, MethodChannel channel) {
        this.posApiHelper = posApiHelper;
        this.channel = channel;
    }

    /**
     * Initialize and open the PICC hardware
     */
    public void openPicc(MethodCall call, Result result) {
        Log.d(TAG, "Opening PICC");
        int ret = posApiHelper.PiccOpen();
        if (ret == 0) {
            posApiHelper.SysBeep();
            Log.d(TAG, "PICC opened successfully");
            result.success("PICC opened successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to open PICC", ret);
        }
    }

    /**
     * Check for PICC card presence
     */
    public void checkPicc(MethodCall call, Result result) {
        Log.d(TAG, "Checking PICC card presence");
        byte[] cardType = new byte[3];
        byte[] serialNo = new byte[50];

        // Default to type A if not specified
        byte cardTypeParam = (byte) 'A';
        if (call.hasArgument("cardType")) {
            String cardTypeStr = call.argument("cardType");
            if (cardTypeStr != null && !cardTypeStr.isEmpty()) {
                cardTypeParam = (byte) cardTypeStr.charAt(0);
            }
        }

        int ret = posApiHelper.PiccCheck(cardTypeParam, cardType, serialNo);
        if (ret == 0) {
            String cardTypeStr = "Card Type: " + ByteUtil.bytearrayToHexString(cardType, cardType.length);
            String uidStr = "UID: " + ByteUtil.bytearrayToHexString(serialNo, serialNo[0]);
            String resultStr = cardTypeStr + "\n" + uidStr;

            Log.d(TAG, "PICC check success: " + resultStr);
            result.success(resultStr);
        } else {
            ErrorUtils.hardwareError(result, "Failed to check PICC", ret);
        }
    }

    /**
     * Poll for PICC cards
     */
    public void pollPicc(MethodCall call, Result result) {
        Log.d(TAG, "Polling for PICC cards");
        byte[] cardType = new byte[4];
        byte[] uid = new byte[10];
        byte[] uidLen = new byte[1];
        byte[] ats = new byte[40];
        byte[] atsLen = new byte[1];
        byte[] sak = new byte[1];

        int ret = posApiHelper.PiccOpen();
        if (ret != 0) {
            ErrorUtils.hardwareError(result, "Failed to open PICC", ret);
            return;
        }

        ret = posApiHelper.PiccPolling(cardType, uid, uidLen, ats, atsLen, sak);
        if (ret == 0) {
            String cardTypeStr = "Card Type: " + new String(cardType).trim();
            String uidStr = "UID: " + ByteUtil.bytearrayToHexString(uid, uidLen[0]);
            String atsStr = "ATS: " + ByteUtil.bytearrayToHexString(ats, atsLen[0]);
            String sakStr = "SAK: " + ByteUtil.bytearrayToHexString(sak, 1);
            String resultStr = cardTypeStr + "\n" + uidStr + "\n" + atsStr + "\n" + sakStr;

            Log.d(TAG, "PICC polling success: " + resultStr);
            posApiHelper.SysBeep();
            result.success(resultStr);
        } else {
            ErrorUtils.hardwareError(result, "Failed to poll PICC", ret);
        }
    }

    /**
     * Send a raw command to the PICC card
     */
    public void sendCommand(MethodCall call, Result result) {
        List<Integer> list = call.argument("apduSend");
        if (list == null || list.isEmpty()) {
            ErrorUtils.invalidArgument(result, "APDU command data must be provided");
            return;
        }

        byte[] apduSend = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            apduSend[i] = list.get(i).byteValue();
        }

        Log.d(TAG, "Sending PICC command: " + ByteUtil.bytearrayToHexString(apduSend, apduSend.length));
        byte[] apduResp = new byte[256];
        int ret = posApiHelper.PiccCommand(apduSend, apduResp);

        if (ret == 0) {
            posApiHelper.SysBeep();
            String response = ByteUtil.bytearrayToHexString(apduResp, apduResp.length).trim();
            Log.d(TAG, "PICC command success, response: " + response);
            result.success(response);
        } else {
            ErrorUtils.hardwareError(result, "Failed to execute PICC command", ret);
        }
    }

    /**
     * Send an APDU command to the PICC card
     */
    public void sendApduCommand(MethodCall call, Result result) {
        byte[] pucInput = call.argument("pucInput");
        if (pucInput == null || pucInput.length == 0) {
            ErrorUtils.invalidArgument(result, "APDU command data must be provided");
            return;
        }

        Log.d(TAG, "Sending PICC APDU command: " + ByteUtil.bytearrayToHexString(pucInput, pucInput.length));
        byte[] pucOutput = new byte[256];
        byte[] pusOutputLen = new byte[1];

        int ret = posApiHelper.PiccApduCmd(pucInput, (short) pucInput.length, pucOutput, pusOutputLen);
        if (ret == 0) {
            posApiHelper.SysBeep();

            // Extract only the valid part of the response based on the output length
            byte[] validOutput = new byte[pusOutputLen[0] & 0xFF];
            System.arraycopy(pucOutput, 0, validOutput, 0, validOutput.length);

            String response = ByteUtil.bytearrayToHexString(validOutput, validOutput.length);
            Log.d(TAG, "PICC APDU command success, response: " + response);
            result.success(response);
        } else {
            ErrorUtils.hardwareError(result, "Failed to execute PICC APDU command", ret);
        }
    }

    /**
     * Close the PICC hardware
     */
    public void closePicc(MethodCall call, Result result) {
        Log.d(TAG, "Closing PICC");
        int ret = posApiHelper.PiccClose();
        if (ret == 0) {
            posApiHelper.SysBeep();
            Log.d(TAG, "PICC closed successfully");
            result.success("PICC closed successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to close PICC", ret);
        }
    }

    /**
     * Check if card is still in the field
     */
    public void removePicc(MethodCall call, Result result) {
        Log.d(TAG, "Checking if card is still in field");
        int ret = posApiHelper.PiccRemove();
        if (ret == 0) {
            posApiHelper.SysBeep();
            Log.d(TAG, "Card is still in the magnetic field");
            result.success("Card is still in the magnetic field");
        } else {
            Log.d(TAG, "Card has left the magnetic field");
            result.success("Card has left the magnetic field");
        }
    }

    /**
     * Initialize SAM AV2
     */
    public void initializeSamAv2(MethodCall call, Result result) {
        Integer samSlotNo = call.argument("samSlotNo");
        List<Integer> samHostKeyList = call.argument("samHostKey");

        if (samSlotNo == null) {
            ErrorUtils.invalidArgument(result, "SAM slot number must be provided");
            return;
        }

        if (samHostKeyList == null || samHostKeyList.isEmpty()) {
            ErrorUtils.invalidArgument(result, "SAM host key must be provided");
            return;
        }

        byte[] samHostKey = new byte[samHostKeyList.size()];
        for (int i = 0; i < samHostKeyList.size(); i++) {
            samHostKey[i] = samHostKeyList.get(i).byteValue();
        }

        byte[] samHostMode = new byte[2];
        byte[] samAv2Version = new byte[2];
        byte[] samAv2VerLen = new byte[1];

        Log.d(TAG, "Initializing SAM AV2 in slot " + samSlotNo);
        try {
            int ret = posApiHelper.PiccSamAv2Init(samSlotNo, samHostKey, samHostMode, samAv2Version, samAv2VerLen);
            if (ret == 0) {
                posApiHelper.SysBeep();
                Log.d(TAG, "SAM AV2 initialized successfully");
                result.success("SAM AV2 initialized successfully");
            } else {
                ErrorUtils.hardwareError(result, "Failed to initialize SAM AV2", ret);
            }
        } catch (Exception e) {
            ErrorUtils.handleException("piccSamAv2Init", e, result);
        }
    }

    /**
     * Set PICC hardware mode
     */
    public void setHardwareMode(MethodCall call, Result result) {
        Integer mode = call.argument("mode");
        if (mode == null) {
            ErrorUtils.invalidArgument(result, "Hardware mode must be provided");
            return;
        }

        Log.d(TAG, "Setting PICC hardware mode to " + mode);
        int ret = posApiHelper.PiccHwModeSet(mode);
        if (ret == 0) {
            Log.d(TAG, "NFC work mode set successfully");
            result.success("NFC work mode set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set NFC work mode", ret);
        }
    }

    /**
     * Verify M1 card authority
     */
    public void verifyM1Authority(MethodCall call, Result result) {
        Number typeNumber = call.argument("type");
        Number blkNoNumber = call.argument("blkNo");
        byte[] pwd = call.argument("pwd");

        if (typeNumber == null) {
            ErrorUtils.invalidArgument(result, "Authority type must be provided");
            return;
        }

        if (blkNoNumber == null) {
            ErrorUtils.invalidArgument(result, "Block number must be provided");
            return;
        }

        if (pwd == null || pwd.length == 0) {
            ErrorUtils.invalidArgument(result, "Password must be provided");
            return;
        }

        byte type = typeNumber.byteValue();
        byte blkNo = blkNoNumber.byteValue();
        byte[] serialNo = new byte[4];

        Log.d(TAG, "Verifying M1 card authority for block " + blkNo);
        int ret = posApiHelper.PiccM1Authority(type, blkNo, pwd, serialNo);
        if (ret == 0) {
            posApiHelper.SysBeep();
            String serialStr = ByteUtil.bytearrayToHexString(serialNo, serialNo.length);
            Log.d(TAG, "M1 card authority verified successfully, serial: " + serialStr);
            result.success("M1 card authority verified successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to verify M1 card authority", ret);
        }
    }

    /**
     * Read NFC tag
     */
    public void readNfcTag(MethodCall call, Result result) {
        Log.d(TAG, "Reading NFC tag");
        byte[] nfcDataLen = new byte[5];
        byte[] technology = new byte[25];
        byte[] nfcUid = new byte[56];
        byte[] ndefMessage = new byte[500];

        int ret = posApiHelper.PiccNfc(nfcDataLen, technology, nfcUid, ndefMessage);

        if (ret == 0) {
            // Extract lengths from nfcDataLen
            int technologyLength = nfcDataLen[0] & 0xFF;
            int nfcUidLength = nfcDataLen[1] & 0xFF;
            int ndefMessageLength = ((nfcDataLen[3] & 0xFF) << 8) | (nfcDataLen[4] & 0xFF);

            // Extract valid data
            byte[] ndefMessageData = new byte[ndefMessageLength];
            byte[] nfcUidData = new byte[nfcUidLength];

            System.arraycopy(nfcUid, 0, nfcUidData, 0, nfcUidLength);
            System.arraycopy(ndefMessage, 0, ndefMessageData, 0, ndefMessageLength);

            // Convert to strings
            String ndefMessageDataStr = new String(ndefMessageData);
            String ndefStr;

            // Extract URL from NDEF message if present
            if (ndefMessageDataStr.contains("http://")) {
                ndefStr = "NDEF: " + ndefMessageDataStr.substring(ndefMessageDataStr.indexOf("http://"));
            } else if (ndefMessageDataStr.contains("https://")) {
                ndefStr = "NDEF: " + ndefMessageDataStr.substring(ndefMessageDataStr.indexOf("https://"));
            } else if (ndefMessageDataStr.contains("www.")) {
                ndefStr = "NDEF: " + ndefMessageDataStr.substring(ndefMessageDataStr.indexOf("www."));
            } else {
                ndefStr = "NDEF: " + ndefMessageDataStr;
            }

            String technologyStr = new String(technology, 0, technologyLength);
            String uidStr = ByteUtil.bytearrayToHexString(nfcUidData, nfcUidData.length);

            String resultStr = "TYPE: " + technologyStr + "\n" +
                    "UID: " + uidStr + "\n" +
                    ndefStr;

            posApiHelper.SysBeep();
            Log.d(TAG, "Read NFC tag successfully: " + resultStr);
            result.success(resultStr);
        } else {
            ErrorUtils.hardwareError(result, "Failed to read NFC tag", ret);
        }
    }
}