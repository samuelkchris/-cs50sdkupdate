package com.pinnisoft.cs50sdkupdate;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.os.RemoteException;

import com.ctk.sdk.PosApiHelper;
import com.ctk.sdk.ByteUtil;
import com.ciontek.ciontekposservice.IEmvProcessCallback;
import com.ciontek.ciontekposservice.IInputPinCallback;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * Handles card and payment API operations for CS50 SDK
 */
public class PaymentHandler {
    private static final String TAG = "PaymentHandler";

    private final PosApiHelper posApiHelper;
    private final Context context;
    private final MethodChannel channel;
    private final Handler mainHandler;

    // Callback objects
    private IEmvProcessCallback emvProcessCallback;
    private IInputPinCallback inputPinCallback;

    public PaymentHandler(PosApiHelper posApiHelper, Context context, MethodChannel channel) {
        this.posApiHelper = posApiHelper;
        this.context = context;
        this.channel = channel;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Register all method handlers to the plugin
     */
    public void registerMethodHandlers(Map<String, Cs50sdkupdatePlugin.MethodHandler> methodHandlers) {
        // IC Card / SAM Card methods
        registerIcCardMethods(methodHandlers);

        // Magnetic Card methods
        registerMagneticCardMethods(methodHandlers);

        // Payment general APIs
        registerPaymentGeneralMethods(methodHandlers);

        // PayPass methods
        registerPayPassMethods(methodHandlers);

        // PayWave methods
        registerPayWaveMethods(methodHandlers);

        // EMVCO methods
        registerEmvcoMethods(methodHandlers);

        // Express methods
        registerExpressMethods(methodHandlers);

        // PCI methods
        registerPciMethods(methodHandlers);
    }

    //============================== IC Card / SAM Card Methods =======================================

    private void registerIcCardMethods(Map<String, Cs50sdkupdatePlugin.MethodHandler> methodHandlers) {
        methodHandlers.put("IccOpen", this::openIcCard);
        methodHandlers.put("IccClose", this::closeIcCard);
        methodHandlers.put("IccCommand", this::sendIccCommand);
        methodHandlers.put("IccCheck", this::checkIcCard);
        methodHandlers.put("SC_ApduCmd", this::sendApduCommand);
    }


    private Byte intToByte(Integer value) {
        if (value == null) return null;
        return value.byteValue();
    }



    public void openIcCard(MethodCall call, Result result) {
        Integer slotInt = call.argument("slot");
        Integer vccModeInt = call.argument("vccMode");

        if (slotInt == null || vccModeInt == null) {
            ErrorUtils.invalidArgument(result, "Slot and vccMode parameters must be provided");
            return;
        }

        Byte slot = intToByte(slotInt);
        Byte vccMode = intToByte(vccModeInt);

        // Create a new byte array for the ATR response
        // The ATR is typically 32 bytes, with an extra byte for length
        byte[] atr = new byte[33];  // Allocate enough space for ATR response

        Log.d(TAG, "Opening IC card with slot: " + slot + ", vccMode: " + vccMode);
        int ret = posApiHelper.IccOpen(slot, vccMode, atr);

        if (ret == 0) {
            // Success - return the ATR along with the success message
            // First byte of atr is the length
            int atrLength = atr[0] & 0xFF;
            byte[] atrData = new byte[atrLength];
            System.arraycopy(atr, 1, atrData, 0, atrLength);

            HashMap<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "IC card opened successfully");
            response.put("atr", atrData);

            result.success(response);
        } else {
            ErrorUtils.hardwareError(result, "Failed to open IC card", ret);
        }
    }

    public void closeIcCard(MethodCall call, Result result) {
        Byte slot = call.argument("slot");
        if (slot == null) {
            ErrorUtils.invalidArgument(result, "Slot parameter must be provided");
            return;
        }

        Log.d(TAG, "Closing IC card on slot: " + slot);
        int ret = posApiHelper.IccClose(slot);
        if (ret == 0) {
            result.success("IC card closed successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to close IC card", ret);
        }
    }

    public void sendIccCommand(MethodCall call, Result result) {
        Byte slot = call.argument("slot");
        byte[] apduSend = call.argument("apduSend");
        byte[] apduResp = call.argument("apduResp");

        if (slot == null || apduSend == null || apduResp == null) {
            ErrorUtils.invalidArgument(result, "All IC card command parameters must be provided");
            return;
        }

        Log.d(TAG, "Sending ICC command on slot: " + slot);
        int ret = posApiHelper.IccCommand(slot, apduSend, apduResp);
        if (ret == 0) {
            result.success("ICC command sent successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to send ICC command", ret);
        }
    }

    public void checkIcCard(MethodCall call, Result result) {
        Byte slot = call.argument("slot");
        if (slot == null) {
            ErrorUtils.invalidArgument(result, "Slot parameter must be provided");
            return;
        }

        Log.d(TAG, "Checking IC card on slot: " + slot);
        int ret = posApiHelper.IccCheck(slot);
        if (ret == 0) {
            result.success("IC card detected and inserted");
        } else {
            ErrorUtils.hardwareError(result, "IC card not detected", ret);
        }
    }

    public void sendApduCommand(MethodCall call, Result result) {
        Byte slot = call.argument("slot");
        byte[] pbInApdu = call.argument("pbInApdu");
        Integer usInApduLen = call.argument("usInApduLen");
        byte[] pbOut = call.argument("pbOut");
        byte[] pbOutLen = call.argument("pbOutLen");

        if (slot == null || pbInApdu == null || usInApduLen == null || pbOut == null || pbOutLen == null) {
            ErrorUtils.invalidArgument(result, "All APDU command parameters must be provided");
            return;
        }

        Log.d(TAG, "Sending SC APDU command on slot: " + slot);
        int ret = posApiHelper.SC_ApduCmd(slot, pbInApdu, usInApduLen, pbOut, pbOutLen);
        if (ret == 0) {
            result.success("APDU command sent successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to send APDU command", ret);
        }
    }

    //============================== Magnetic Card Methods =======================================

    private void registerMagneticCardMethods(Map<String, Cs50sdkupdatePlugin.MethodHandler> methodHandlers) {
        methodHandlers.put("McrOpen", this::openMagneticCard);
        methodHandlers.put("McrClose", this::closeMagneticCard);
        methodHandlers.put("McrReset", this::resetMagneticCard);
        methodHandlers.put("McrCheck", this::checkMagneticCard);
        methodHandlers.put("McrRead", this::readMagneticCard);
    }

    public void openMagneticCard(MethodCall call, Result result) {
        Log.d(TAG, "Opening magnetic card reader");
        int ret = posApiHelper.McrOpen();
        if (ret == 0) {
            result.success("Magnetic card reader opened successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to open magnetic card reader", ret);
        }
    }

    public void closeMagneticCard(MethodCall call, Result result) {
        Log.d(TAG, "Closing magnetic card reader");
        int ret = posApiHelper.McrClose();
        if (ret == 0) {
            result.success("Magnetic card reader closed successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to close magnetic card reader", ret);
        }
    }

    public void resetMagneticCard(MethodCall call, Result result) {
        Log.d(TAG, "Resetting magnetic card reader");
        int ret = posApiHelper.McrReset();
        if (ret == 0) {
            result.success("Magnetic card reader reset successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to reset magnetic card reader", ret);
        }
    }

    public void checkMagneticCard(MethodCall call, Result result) {
        Log.d(TAG, "Checking magnetic card");
        int ret = posApiHelper.McrCheck();
        if (ret == 0) {
            result.success("Magnetic card detected");
        } else {
            ErrorUtils.hardwareError(result, "Magnetic card not detected", ret);
        }
    }

    public void readMagneticCard(MethodCall call, Result result) {
        Byte keyNo = call.argument("keyNo");
        Byte mode = call.argument("mode");
        byte[] track1 = call.argument("track1");
        byte[] track2 = call.argument("track2");
        byte[] track3 = call.argument("track3");

        if (keyNo == null || mode == null || track1 == null || track2 == null || track3 == null) {
            ErrorUtils.invalidArgument(result, "All magnetic card parameters must be provided");
            return;
        }

        Log.d(TAG, "Reading magnetic card data");
        int ret = posApiHelper.McrRead(keyNo, mode, track1, track2, track3);
        if (ret > 0) {
            HashMap<String, Object> trackData = new HashMap<>();
            trackData.put("statusCode", ret);
            trackData.put("track1", track1);
            trackData.put("track2", track2);
            trackData.put("track3", track3);

            // Parse status bits
            boolean track1Valid = (ret & 0x01) != 0;
            boolean track2Valid = (ret & 0x02) != 0;
            boolean track3Valid = (ret & 0x04) != 0;
            boolean track1Error = (ret & 0x10) != 0;
            boolean track2Error = (ret & 0x20) != 0;
            boolean track3Error = (ret & 0x40) != 0;

            trackData.put("track1Valid", track1Valid);
            trackData.put("track2Valid", track2Valid);
            trackData.put("track3Valid", track3Valid);
            trackData.put("track1Error", track1Error);
            trackData.put("track2Error", track2Error);
            trackData.put("track3Error", track3Error);

            result.success(trackData);
        } else {
            ErrorUtils.hardwareError(result, "Failed to read magnetic card", ret);
        }
    }

    //============================== Payment General Methods =======================================

    private void registerPaymentGeneralMethods(Map<String, Cs50sdkupdatePlugin.MethodHandler> methodHandlers) {
        methodHandlers.put("InitPaySysKernel", this::initPaymentSystem);
        methodHandlers.put("EmvSetKeyPadPrompt", this::setKeypadPrompt);
        methodHandlers.put("EmvSetCurrencyCode", this::setCurrencyCode);
        methodHandlers.put("EmvSetInputPinCallback", this::setInputPinCallback);
        methodHandlers.put("EmvKernelPinInput", this::kernelPinInput);
        methodHandlers.put("InitOnLinePINContext", this::initOnlinePinContext);
        methodHandlers.put("CallContactEmvPinblock", this::callContactEmvPinblock);
    }

    public void initPaymentSystem(MethodCall call, Result result) {
        Log.d(TAG, "Initializing payment system kernel");
        int ret = posApiHelper.InitPaySysKernel();
        if (ret == 0) {
            result.success("Payment system kernel initialized successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to initialize payment system kernel", ret);
        }
    }

    public void setKeypadPrompt(MethodCall call, Result result) {
        String prompt = call.argument("prompt");
        if (prompt == null) {
            ErrorUtils.invalidArgument(result, "Prompt parameter must be provided");
            return;
        }

        Log.d(TAG, "Setting keypad prompt: " + prompt);
        int ret = posApiHelper.EmvSetKeyPadPrompt(prompt);
        if (ret == 0) {
            result.success("Keypad prompt set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set keypad prompt", ret);
        }
    }

    public void setCurrencyCode(MethodCall call, Result result) {
        String code = call.argument("code");
        if (code == null) {
            ErrorUtils.invalidArgument(result, "Currency code parameter must be provided");
            return;
        }

        Log.d(TAG, "Setting currency code: " + code);
        int ret = posApiHelper.EmvSetCurrencyCode(code);
        if (ret == 0) {
            result.success("Currency code set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set currency code", ret);
        }
    }

    public void setInputPinCallback(MethodCall call, Result result) {
        Integer timeout = call.argument("timeout");
        if (timeout == null) {
            ErrorUtils.invalidArgument(result, "Timeout parameter must be provided");
            return;
        }

        Log.d(TAG, "Setting input PIN callback with timeout: " + timeout);


        // Create callback that will communicate with Flutter
        inputPinCallback = new IInputPinCallback.Stub() {

            @Override
            public void onKeyPress(byte keyCode) throws RemoteException {
                // Handle key press events if needed
                // For example:
                mainHandler.post(() -> {
                    Map<String, Object> keyEvent = new HashMap<>();
                    keyEvent.put("type", "keyPress");
                    keyEvent.put("keyCode", keyCode);
                    channel.invokeMethod("onPinKeyPress", keyEvent);
                });
            }

            @Override
            public void onInputResult(int result, byte[] pinBlock) throws RemoteException {
                mainHandler.post(() -> {
                    Map<String, Object> pinResult = new HashMap<>();
                    pinResult.put("type", "pinResult");
                    pinResult.put("result", result);
                    pinResult.put("pinBlock", pinBlock != null ? ByteUtil.bytesToHexString(pinBlock) : null);
                    channel.invokeMethod("onPinInputResult", pinResult);
                });
            }
        };

        int ret = posApiHelper.EmvSetInputPinCallback(timeout, inputPinCallback);
        if (ret == 0) {
            result.success("Input PIN callback set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set input PIN callback", ret);
        }
    }

    public void kernelPinInput(MethodCall call, Result result) {
        Integer timeout = call.argument("timeout");
        Integer keyId = call.argument("keyId");

        if (timeout == null || keyId == null) {
            ErrorUtils.invalidArgument(result, "Timeout and keyId parameters must be provided");
            return;
        }

        Log.d(TAG, "Starting kernel PIN input with timeout: " + timeout + ", keyId: " + keyId);

        // Create callback if not already created
        if (inputPinCallback == null) {
            inputPinCallback = new IInputPinCallback.Stub() {

                @Override
                public void onKeyPress(byte keyCode) throws RemoteException {
                    mainHandler.post(() -> {
                        Map<String, Object> keyPress = new HashMap<>();
                        keyPress.put("type", "keyPress");
                        keyPress.put("keyCode", (int) keyCode);
                        channel.invokeMethod("onKeyPress", keyPress);
                    });
                }

                @Override
                public void onInputResult(int result, byte[] pinBlock) throws RemoteException {
                    mainHandler.post(() -> {
                        Map<String, Object> pinResult = new HashMap<>();
                        pinResult.put("type", "pinResult");
                        pinResult.put("result", result);
                        pinResult.put("pinBlock", pinBlock != null ? ByteUtil.bytesToHexString(pinBlock) : null);
                        channel.invokeMethod("onPinInputResult", pinResult);
                    });
                }

            };
        }

        int ret = posApiHelper.EmvKernelPinInput(timeout, keyId, inputPinCallback);
        if (ret == 0) {
            result.success("Kernel PIN input started successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to start kernel PIN input", ret);
        }
    }

    public void initOnlinePinContext(MethodCall call, Result result) {
        Log.d(TAG, "Initializing online PIN context");
        int ret = posApiHelper.InitOnLinePINContext();
        if (ret == 0) {
            result.success("Online PIN context initialized successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to initialize online PIN context", ret);
        }
    }

    public void callContactEmvPinblock(MethodCall call, Result result) {
        Integer pinType = call.argument("pinType");
        if (pinType == null) {
            ErrorUtils.invalidArgument(result, "PIN type parameter must be provided");
            return;
        }

        Log.d(TAG, "Calling contact EMV PINBLOCK with type: " + pinType);
        int ret = posApiHelper.CallContactEmvPinblock(pinType);
        if (ret == 0) {
            result.success("Contact EMV PINBLOCK called successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to call contact EMV PINBLOCK", ret);
        }
    }

    //============================== PCI Methods =======================================

    private void registerPciMethods(Map<String, Cs50sdkupdatePlugin.MethodHandler> methodHandlers) {
        methodHandlers.put("PciWritePinMKey", this::writePinMKey);
        methodHandlers.put("PciWriteMacMKey", this::writeMacMKey);
        methodHandlers.put("PciWriteDesMKey", this::writeDesMKey);
        methodHandlers.put("PciWritePinKey", this::writePinKey);
        methodHandlers.put("PciWriteMacKey", this::writeMacKey);
        methodHandlers.put("PciWriteDesKey", this::writeDesKey);
        methodHandlers.put("PciReadKCV", this::readKCV);
        methodHandlers.put("PciGetPin", this::getPin);
        methodHandlers.put("PciGetMac", this::getMac);
        methodHandlers.put("PciGetDes", this::getDes);
        methodHandlers.put("PciWriteDukptIpek", this::writeDukptIpek);
        methodHandlers.put("PciGetDukptMac", this::getDukptMac);
        methodHandlers.put("PciGetDuktDes", this::getDukptDes);
    }

    public void writePinMKey(MethodCall call, Result result) {
        Byte keyNo = call.argument("keyNo");
        Byte keyLen = call.argument("keyLen");
        byte[] keyData = call.argument("keyData");
        Byte mode = call.argument("mode");

        if (keyNo == null || keyLen == null || keyData == null || mode == null) {
            ErrorUtils.invalidArgument(result, "All PIN main key parameters must be provided");
            return;
        }

        Log.d(TAG, "Writing PIN main key with keyNo: " + keyNo);
        int ret = posApiHelper.PciWritePinMKey(keyNo, keyLen, keyData, mode);
        if (ret == 0) {
            result.success("PIN main key written successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to write PIN main key", ret);
        }
    }

    public void writeMacMKey(MethodCall call, Result result) {
        Byte keyNo = call.argument("keyNo");
        Byte keyLen = call.argument("keyLen");
        byte[] keyData = call.argument("keyData");
        Byte mode = call.argument("mode");

        if (keyNo == null || keyLen == null || keyData == null || mode == null) {
            ErrorUtils.invalidArgument(result, "All MAC main key parameters must be provided");
            return;
        }

        Log.d(TAG, "Writing MAC main key with keyNo: " + keyNo);
        int ret = posApiHelper.PciWriteMacMKey(keyNo, keyLen, keyData, mode);
        if (ret == 0) {
            result.success("MAC main key written successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to write MAC main key", ret);
        }
    }

    public void writeDesMKey(MethodCall call, Result result) {
        Byte keyNo = call.argument("keyNo");
        Byte keyLen = call.argument("keyLen");
        byte[] keyData = call.argument("keyData");
        Byte mode = call.argument("mode");

        if (keyNo == null || keyLen == null || keyData == null || mode == null) {
            ErrorUtils.invalidArgument(result, "All DES main key parameters must be provided");
            return;
        }

        Log.d(TAG, "Writing DES main key with keyNo: " + keyNo);
        int ret = posApiHelper.PciWriteDesMKey(keyNo, keyLen, keyData, mode);
        if (ret == 0) {
            result.success("DES main key written successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to write DES main key", ret);
        }
    }

    public void writePinKey(MethodCall call, Result result) {
        Byte keyNo = call.argument("keyNo");
        Byte keyLen = call.argument("keyLen");
        byte[] keyData = call.argument("keyData");
        Byte mode = call.argument("mode");
        Byte mkeyNo = call.argument("mkeyNo");

        if (keyNo == null || keyLen == null || keyData == null || mode == null || mkeyNo == null) {
            ErrorUtils.invalidArgument(result, "All PIN key parameters must be provided");
            return;
        }

        Log.d(TAG, "Writing PIN key with keyNo: " + keyNo + ", mKeyNo: " + mkeyNo);
        int ret = posApiHelper.PciWritePinKey(keyNo, keyLen, keyData, mode, mkeyNo);
        if (ret == 0) {
            result.success("PIN key written successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to write PIN key", ret);
        }
    }

    public void writeMacKey(MethodCall call, Result result) {
        Byte keyNo = call.argument("keyNo");
        Byte keyLen = call.argument("keyLen");
        byte[] keyData = call.argument("keyData");
        Byte mode = call.argument("mode");
        Byte mkeyNo = call.argument("mkeyNo");

        if (keyNo == null || keyLen == null || keyData == null || mode == null || mkeyNo == null) {
            ErrorUtils.invalidArgument(result, "All MAC key parameters must be provided");
            return;
        }

        Log.d(TAG, "Writing MAC key with keyNo: " + keyNo + ", mKeyNo: " + mkeyNo);
        int ret = posApiHelper.PciWriteMacKey(keyNo, keyLen, keyData, mode, mkeyNo);
        if (ret == 0) {
            result.success("MAC key written successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to write MAC key", ret);
        }
    }

    public void writeDesKey(MethodCall call, Result result) {
        Byte keyNo = call.argument("keyNo");
        Byte keyLen = call.argument("keyLen");
        byte[] keyData = call.argument("keyData");
        Byte mode = call.argument("mode");
        Byte mkeyNo = call.argument("mkeyNo");

        if (keyNo == null || keyLen == null || keyData == null || mode == null || mkeyNo == null) {
            ErrorUtils.invalidArgument(result, "All DES key parameters must be provided");
            return;
        }

        Log.d(TAG, "Writing DES key with keyNo: " + keyNo + ", mKeyNo: " + mkeyNo);
        int ret = posApiHelper.PciWriteDesKey(keyNo, keyLen, keyData, mode, mkeyNo);
        if (ret == 0) {
            result.success("DES key written successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to write DES key", ret);
        }
    }

    public void readKCV(MethodCall call, Result result) {
        Byte mKeyNo = call.argument("mKeyNo");
        Byte keyType = call.argument("keyType");
        byte[] mKeyKcv = call.argument("mKeyKcv");

        if (mKeyNo == null || keyType == null || mKeyKcv == null) {
            ErrorUtils.invalidArgument(result, "All KCV parameters must be provided");
            return;
        }

        Log.d(TAG, "Reading KCV with mKeyNo: " + mKeyNo + ", keyType: " + keyType);
        int ret = posApiHelper.PciReadKCV(mKeyNo, keyType, mKeyKcv);
        if (ret == 0) {
            result.success("KCV read successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to read KCV", ret);
        }
    }

    public void getPin(MethodCall call, Result result) {
        Byte keyNo = call.argument("keyNo");
        Byte minLen = call.argument("minLen");
        Byte maxLen = call.argument("maxLen");
        Byte mode = call.argument("mode");
        byte[] cardNo = call.argument("cardNo");
        byte[] pinBlock = call.argument("pinBlock");
        byte[] pinPasswd = call.argument("pinPasswd");
        Byte pinLen = call.argument("pinLen");
        Byte mark = call.argument("mark");
        byte[] iAmount = call.argument("iAmount");
        Byte waitTimeSec = call.argument("waitTimeSec");

        if (keyNo == null || minLen == null || maxLen == null || mode == null ||
                cardNo == null || pinBlock == null || pinPasswd == null || pinLen == null ||
                mark == null || iAmount == null || waitTimeSec == null) {

            ErrorUtils.invalidArgument(result, "All PIN parameters must be provided");
            return;
        }

        Log.d(TAG, "Getting PIN with keyNo: " + keyNo);
        int ret = posApiHelper.PciGetPin(keyNo, minLen, maxLen, mode, cardNo, pinBlock,
                pinPasswd, pinLen, mark, iAmount, waitTimeSec);
        if (ret == 0) {
            result.success("PIN obtained successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to get PIN", ret);
        }
    }

    public void getMac(MethodCall call, Result result) {
        Byte keyNo = call.argument("keyNo");
        Integer inLen = call.argument("inLen");
        byte[] inData = call.argument("inData");
        byte[] macOut = call.argument("macOut");
        Byte mode = call.argument("mode");

        if (keyNo == null || inLen == null || inData == null || macOut == null || mode == null) {
            ErrorUtils.invalidArgument(result, "All MAC parameters must be provided");
            return;
        }

        Log.d(TAG, "Getting MAC with keyNo: " + keyNo);
        int ret = posApiHelper.PciGetMac(keyNo, inLen, inData, macOut, mode);
        if (ret == 0) {
            result.success("MAC obtained successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to get MAC", ret);
        }
    }

    public void getDes(MethodCall call, Result result) {
        Byte keyNo = call.argument("keyNo");
        Integer inLen = call.argument("inLen");
        byte[] inData = call.argument("inData");
        byte[] desOut = call.argument("desOut");
        Byte mode = call.argument("mode");

        if (keyNo == null || inLen == null || inData == null || desOut == null || mode == null) {
            ErrorUtils.invalidArgument(result, "All DES parameters must be provided");
            return;
        }

        Log.d(TAG, "Getting DES with keyNo: " + keyNo);
        int ret = posApiHelper.PciGetDes(keyNo, inLen, inData, desOut, mode);
        if (ret == 0) {
            result.success("DES operation completed successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to perform DES operation", ret);
        }
    }

    public void writeDukptIpek(MethodCall call, Result result) {
        Integer keyId = call.argument("keyId");
        Integer ipekLen = call.argument("ipekLen");
        byte[] ipek = call.argument("ipek");
        Integer ksnLen = call.argument("ksnLen");
        byte[] ksn = call.argument("ksn");

        if (keyId == null || ipekLen == null || ipek == null || ksnLen == null || ksn == null) {
            ErrorUtils.invalidArgument(result, "All DUKPT IPEK parameters must be provided");
            return;
        }

        Log.d(TAG, "Writing DUKPT IPEK with keyId: " + keyId);
        int ret = posApiHelper.PciWriteDukptIpek(keyId, ipekLen, ipek, ksnLen, ksn);
        if (ret == 0) {
            result.success("DUKPT IPEK written successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to write DUKPT IPEK", ret);
        }
    }

    public void getDukptMac(MethodCall call, Result result) {
        Integer keyId = call.argument("keyId");
        Byte mode = call.argument("mode");
        Byte macDataLen = call.argument("macDataLen");
        byte[] macDataIn = call.argument("macDataIn");
        byte[] macOut = call.argument("macOut");
        byte[] outKsn = call.argument("outKsn");
        byte[] macKcv = call.argument("macKcv");

        if (keyId == null || mode == null || macDataLen == null || macDataIn == null ||
                macOut == null || outKsn == null || macKcv == null) {
            ErrorUtils.invalidArgument(result, "All DUKPT MAC parameters must be provided");
            return;
        }

        Log.d(TAG, "Getting DUKPT MAC with keyId: " + keyId);
        int ret = posApiHelper.PciGetDukptMac(keyId, mode, macDataLen, macDataIn, macOut, outKsn, macKcv);
        if (ret == 0) {
            result.success("DUKPT MAC obtained successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to get DUKPT MAC", ret);
        }
    }

    public void getDukptDes(MethodCall call, Result result) {
        Integer keyId = call.argument("keyId");
        Byte mode = call.argument("mode");
        Byte desMode = call.argument("desMode");
        Integer desDataLen = call.argument("desDataLen");
        byte[] desDataIn = call.argument("desDataIn");
        byte[] iv = call.argument("iv");
        byte[] desOut = call.argument("desOut");
        byte[] outKsn = call.argument("outKsn");
        byte[] desKcv = call.argument("desKcv");

        if (keyId == null || mode == null || desMode == null || desDataLen == null ||
                desDataIn == null || iv == null || desOut == null || outKsn == null || desKcv == null) {
            ErrorUtils.invalidArgument(result, "All DUKPT DES parameters must be provided");
            return;
        }

        Log.d(TAG, "Getting DUKPT DES with keyId: " + keyId);
        int ret = posApiHelper.PciGetDuktDes(keyId, mode, desMode, desDataLen, desDataIn, iv, desOut, outKsn, desKcv);
        if (ret == 0) {
            result.success("DUKPT DES operation completed successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to perform DUKPT DES operation", ret);
        }
    }

    //============================== EMVCO Methods =======================================

    private void registerEmvcoMethods(Map<String, Cs50sdkupdatePlugin.MethodHandler> methodHandlers) {
        methodHandlers.put("EmvGetPinBlock", this::getEmvPinBlock);
        methodHandlers.put("EmvGetDukptPinblock", this::getEmvDukptPinblock);
    }

    public void getEmvPinBlock(MethodCall call, Result result) {
        Integer type = call.argument("type");
        Integer pinkeyN = call.argument("pinkeyN");
        byte[] cardNo = call.argument("cardNo");
        byte[] mode = call.argument("mode");
        byte[] pinBlock = call.argument("pinBlock");
        Integer timeout = call.argument("timeout");

        if (type == null || pinkeyN == null || cardNo == null || mode == null ||
                pinBlock == null || timeout == null) {
            ErrorUtils.invalidArgument(result, "All EMV PIN block parameters must be provided");
            return;
        }

        Log.d(TAG, "Getting EMV PIN block with type: " + type + ", pinkeyN: " + pinkeyN);
        int ret = posApiHelper.EmvGetPinBlock(type, pinkeyN, cardNo, mode, pinBlock, timeout);
        if (ret == 0) {
            result.success("EMV PIN block obtained successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to get EMV PIN block", ret);
        }
    }

    public void getEmvDukptPinblock(MethodCall call, Result result) {
        Integer type = call.argument("type");
        Integer pinkeyN = call.argument("pinkeyN");
        byte[] cardNo = call.argument("cardNo");
        byte[] pinBlock = call.argument("pinBlock");
        byte[] outKsn = call.argument("outKsn");
        byte[] pinKcv = call.argument("pinKcv");
        Integer timeout = call.argument("timeout");

        if (type == null || pinkeyN == null || cardNo == null || pinBlock == null ||
                outKsn == null || pinKcv == null || timeout == null) {
            ErrorUtils.invalidArgument(result, "All EMV DUKPT PIN block parameters must be provided");
            return;
        }

        Log.d(TAG, "Getting EMV DUKPT PIN block with type: " + type + ", pinkeyN: " + pinkeyN);
        int ret = posApiHelper.EmvGetDukptPinblock(type, pinkeyN, cardNo, pinBlock, outKsn, pinKcv, timeout);
        if (ret == 0) {
            result.success("EMV DUKPT PIN block obtained successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to get EMV DUKPT PIN block", ret);
        }
    }

    //============================== PayPass Methods =======================================

    private void registerPayPassMethods(Map<String, Cs50sdkupdatePlugin.MethodHandler> methodHandlers) {
        // Since the PayPass functionality is not fully implemented in the provided SDK,
        // we'll define placeholders and map them to the appropriate Flutter method calls,
        // even though they may return "not implemented" for now
        methodHandlers.put("PaypassKernelInit", this::notImplementedHandler);
        methodHandlers.put("PaypassAidSet", this::notImplementedHandler);
        methodHandlers.put("PaypassCapkSet", this::notImplementedHandler);
        methodHandlers.put("PaypassKernelSet", this::notImplementedHandler);
        methodHandlers.put("PaypassReaderSet", this::notImplementedHandler);
        methodHandlers.put("PaypassTransSet", this::notImplementedHandler);
        methodHandlers.put("PaypassTransaction", this::notImplementedHandler);
        methodHandlers.put("PaypassGetTagValue", this::notImplementedHandler);
        methodHandlers.put("PayPassShowAmount", this::notImplementedHandler);
        methodHandlers.put("PaypassFinal", this::notImplementedHandler);
    }

    //============================== PayWave Methods =======================================

    private void registerPayWaveMethods(Map<String, Cs50sdkupdatePlugin.MethodHandler> methodHandlers) {
        // Since the PayWave functionality is not fully implemented in the provided SDK,
        // we'll define placeholders and map them to the appropriate Flutter method calls,
        // even though they may return "not implemented" for now
        methodHandlers.put("PayWaveKernelInit", this::notImplementedHandler);
        methodHandlers.put("PayWaveAidSet", this::notImplementedHandler);
        methodHandlers.put("PayWaveCapkSet", this::notImplementedHandler);
        methodHandlers.put("PayWaveTermSet", this::notImplementedHandler);
        methodHandlers.put("PayWaveTransProcess", this::notImplementedHandler);
        methodHandlers.put("PayWaveSetTransAmount", this::notImplementedHandler);
        methodHandlers.put("PayWaveSetTransType", this::notImplementedHandler);
        methodHandlers.put("PayWaveGetTagData", this::notImplementedHandler);
        methodHandlers.put("PayWaveClearAllCapk", this::notImplementedHandler);
        methodHandlers.put("PayWaveClearAllTerm", this::notImplementedHandler);
        methodHandlers.put("PayWaveClearAllAIDS", this::notImplementedHandler);
        methodHandlers.put("PayWaveFinal", this::notImplementedHandler);
    }

    //============================== Express Methods =======================================

    private void registerExpressMethods(Map<String, Cs50sdkupdatePlugin.MethodHandler> methodHandlers) {
        // Since the Express functionality is not fully implemented in the provided SDK,
        // we'll define placeholders and map them to the appropriate Flutter method calls,
        // even though they may return "not implemented" for now
        methodHandlers.put("ExpressKernelInit", this::notImplementedHandler);
        methodHandlers.put("ExpressAidSet", this::notImplementedHandler);
        methodHandlers.put("ExpressCapkSet", this::notImplementedHandler);
        methodHandlers.put("ExpressGenerlParamSet", this::notImplementedHandler);
        methodHandlers.put("ExpressDRLParamSet", this::notImplementedHandler);
        methodHandlers.put("ExpressCRLParamSet", this::notImplementedHandler);
        methodHandlers.put("ExpressExcepFileParamSet", this::notImplementedHandler);
        methodHandlers.put("ExpressKernelConfigSet", this::notImplementedHandler);
        methodHandlers.put("ExpressTransaction", this::notImplementedHandler);
        methodHandlers.put("ExpressGetTagData", this::notImplementedHandler);
        methodHandlers.put("ExpressFinal", this::notImplementedHandler);
    }

    /**
     * Generic handler for methods that are not yet implemented.
     * This allows us to provide consistent feedback to Flutter for APIs that
     * exist but aren't fully implemented in the SDK yet.
     */
    private void notImplementedHandler(MethodCall call, Result result) {
        Log.w(TAG, "Method not fully implemented yet: " + call.method);
        result.error(
                "NOT_IMPLEMENTED",
                "The method " + call.method + " is not fully implemented in the current SDK version",
                null
        );
    }
}