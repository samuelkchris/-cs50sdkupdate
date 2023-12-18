
/**************************************************************************************************
 * Copyright (C) 2021 The Ciontek Source Project
 * ---------------------------------------------
 * Filename: PosApiHelper.java
 *
 * Author: Ciontek.Tao
 * ---------------------------------------------
 * Description:
 * Class PosApiHelper describe APIs for Ciontek CS50,Please call it according to
 * the function of your device
 *
 * ----------------------------------------------
 * APIs list:
 *
 * 1.Android OS Interfaces
 *   1.0.1 -> installRomPackage
 *   1.0.2 -> getOSVersion
 *   1.0.3 -> getDeviceId
 *
 * 2. Generic APIs
 *   2.0.1 -> SysLogSwitch
 *   2.0.2 -> SysGetRand
 *   2.0.3 -> SysUpdate
 *   2.0.4 -> SysGetVersion
 *   2.0.5 -> SysReadSN
 *   2.0.6 -> SysWriteSN
 *   2.0.7 -> SysReadChipID
 *
 * 3. Ic card /SAM card
 *   3.0.1 -> IccOpen
 *   3.0.2 -> IccClose
 *   3.0.3 -> IccCommand
 *   3.0.4 -> IccCheck
 *   3.0.5 -> SC_ApduCmd
 *
 * 4. printer
 *   4.0.1 -> PrintInit
 *   4.0.2 -> PrintInit(paramter)
 *   4.0.3 -> PrintSetFont
 *   4.0.4 -> PrintSetGray
 *   4.0.5 -> PrintSetSpace
 *   4.0.6 -> PrintGetFont
 *   4.0.7 -> PrintStep
 *   4.0.8 -> PrintSetVoltage
 *   4.0.9 -> PrintIsCharge
 *   4.0.10 -> PrintSetLinPixelDis
 *   4.0.11 -> PrintStr
 *   4.0.12 -> PrintBmp
 *   4.0.13 -> PrintBarcode
 *   4.0.14 -> PrintQrCode_Cut
 *   4.0.15 -> PrintCutQrCode_Str
 *   4.0.16 -> PrintStart
 *   4.0.17 -> PrintSetLeftIndent
 *   4.0.18 -> PrintSetAlign
 *   4.0.19 -> PrintCharSpace
 *   4.0.20 -> PrintSetLineSpace
 *   4.0.21 -> PrintSetLeftSpace
 *   4.0.22 -> PrintSetSpeed
 *   4.0.23 -> PrintCheckStatus
 *   4.0.24 -> PrintFeedPaper
 *   4.0.25 -> PrintSetMode
 *   4.0.26 -> PrintSetUnderline
 *   4.0.27 -> PrintSetReverse
 *   4.0.28 -> PrintSetBold
 *   4.0.29 -> PrintLogo
 *   4.0.30 -> PrintLabLocate
 *
 * 5. APP White List
 *   5.0.1 -> enableAppInstallWhiteList
 *   5.0.2 -> disableAppInstallWhiteList
 *   5.0.3 -> addAppToInstallWhiteList
 *   5.0.4 -> delAppFromInstallWhiteList
 *   5.0.5 -> getAppInstallWhiteList
 *   5.0.6 -> enableAppUninstallBlackList
 *   5.0.7 -> disableAppUninstallBlackList
 *   5.0.8 -> addAppToUninstallBlackList
 *   5.0.9 -> delAppFromUninstallBlackList
 *   5.0.10 -> getAppUninstallBlackList
 *
 * 6. fiscal module
 *   6.0.1 -> fiscalOpen
 *   6.0.2 -> fiscalClose
 *   6.0.3 -> fiscalWrite
 *   6.0.4 -> fiscalRead
 *
 * 7. beep
 *   7.0.1 -> SysBeep
 *
 * 8. Magnetic card
 *   8.0.1 -> McrOpen
 *   8.0.2 -> McrClose
 *   8.0.3 -> McrReset
 *   8.0.4 -> McrCheck
 *   8.0.5 -> McrRead
 *
 * 9.  Contactless cards
 *   9.0.1 -> PiccOpen
 *   9.0.2 -> PiccClose
 *   9.0.3 -> PiccCheck
 *   9.0.4 -> PiccPolling
 *   9.0.5 -> PiccCommand
 *   9.0.6 -> PiccApduCmd
 *   9.0.7 -> PiccRemove
 *   9.0.8 -> PiccNfc
 *   9.0.9 -> PiccHalt
 *   9.0.10 -> PiccReset
 *   9.0.11 -> PiccSamAv2Init
 *   9.0.12 -> PiccHwModeSet
 *   9.0.13 -> PiccM1Authority
 *   9.0.14 -> PiccM1ReadBlock
 *   9.0.15 -> PiccM1WriteBlock
 *   9.0.16 -> PiccM1Operate
 *   9.0.17 -> PiccM1WriteValue
 *   9.0.18 -> PiccM1ReadValue
 *   9.0.19 -> PiccM1RestoreTransfer
 *   9.0.20 -> PiccSamMfcAuth
 *   9.0.21 -> PiccWriSl1KeyToAv2
 *   9.0.22 -> PiccMfulActivateCard
 *   9.0.23 -> PiccMfulPwdAuth
 *   9.0.24 -> PiccMfulRead
 *   9.0.25 -> PiccMfulWrite
 *   9.0.26 -> PiccMfulReadSign
 *   9.0.27 -> PiccMfulReadCnt
 *   9.0.28 -> PiccMfulIncrCnt
 *   9.0.29 -> PiccMfpActivateCard
 *   9.0.30 -> PiccMfpWritePerso
 *   9.0.31 -> PiccMfpCommitPerso
 *   9.0.32 -> PiccMfpKeyStore
 *   9.0.33 -> PiccMfpAuthenticateSL
 *   9.0.34 -> PiccMfpAuthenticateClassicSL2
 *   9.0.35 -> PiccMfpMultiBlockRead
 *   9.0.36 -> PiccMfpMultiBlockWrite
 *   9.0.37 -> PiccMfpWrite
 *   9.0.38 -> PiccMfpWriteValue
 *   9.0.39 -> PiccMfpChangeKey
 *   9.0.40 -> PiccMfpRead
 *   9.0.41 -> PiccMfpReadValue
 *   9.4.42 -> PiccMfpProximityCheck
 *   9.0.43 -> PiccMfpResetSecMsgState
 *   9.0.44 -> PiccSamOpen
 *   9.0.45 -> PiccSamClose
 *
 *
 * 1x Payment general APIs
 * 1x.0.1 -> InitPaySysKernel
 * 1x.0.2 -> UninitPaySysKernel
 * 1x.0.3 -> KeyPadInit
 * 1x.0.4 -> SetKeyPadTime
 * 1x.0.5 -> SetKeyPadType
 * 1x.0.6 -> SetPtcCounter
 * 1x.0.7 -> SetExtTransAmount
 * 1x.0.8 -> SetExtPtcCounter
 *
 * 10. Transaction kernel APIs --- Paypass
 *   10.0.1 -> PaypassKernelInit
 *   10.0.2 -> PaypassAidSet
 *   10.0.3 -> PaypassCapkSet
 *   10.0.4 -> PaypassKernelSet
 *   10.0.5 -> PaypassReaderSet
 *   10.0.6 -> PaypassTransSet
 *   10.0.7 -> PaypassTransaction
 *   10.0.8 -> PaypassGetTagValue
 *   10.0.9 -> PayPassShowAmount
 *   10.0.10 -> PaypassFinal
 *
 *
 * 11. Transaction kernel APIs --- Paywave
 *   11.0.1 -> PayWaveKernelInit
 *   11.0.2 -> PayWaveAidSet
 *   11.0.3 -> PayWaveCapkSet
 *   11.0.4 -> PayWaveTermSet
 *   11.0.5 -> PayWaveTransProcess
 *   11.0.6 -> PayWaveSetTransAmount
 *   11.0.7 -> PayWaveSetTransType
 *   11.0.8 -> PayWaveGetTagData
 *   11.0.9 -> PayWaveClearAllCapk
 *   11.0.10 -> PayWaveClearAllTerm
 *   11.0.11 -> PayWaveClearAllAIDS
 *   11.0.12 -> PayWaveFinal
 *
 * 12. Transaction kernel APIs --- EMVCO
 *   12.0.1 -> EmvEnvParaInit
 *   12.0.2 -> EmvKernelInit
 *   12.0.3 -> EmvClearAllAIDS
 *   12.0.4 -> EmvClearOneAIDS
 *   12.0.5 -> EmvAddOneAIDS
 *   12.0.6 -> EmvClearAllCapks
 *   12.0.7 -> EmvClearOneCapks
 *   12.0.8 -> EmvAddOneCAPK
 *   12.0.9 -> EmvSaveTermParas
 *   12.0.10 -> EmvSetTransAmount
 *   12.0.11 -> EmvSetExtAmount
 *   12.0.12 -> EmvSetTransAmountBack
 *   12.0.13 -> EmvSetCardType
 *   12.0.14 -> EmvSetTransType
 *   12.0.15 -> EmvProcess
 *   12.0.16 -> EmvGetTagData
 *   12.0.17 -> EmvSetOnlineResult
 *   12.0.18 -> EmvGetPinBlock
 *   12.0.19 -> EmvGetDukptPinblock_T
 *   12.0.20 -> EmvGetAESDukptPinblock
 *   12.0.21 -> EmvGetMKSKPinblock
 *   12.0.22 -> EmvGetAESMKSKPinblock
 *   12.0.23 -> EmvGetDukptPinblock
 *   12.0.24 -> EmvGetKLKPinBlock
 *   12.0.25 -> GetDukptClearPinblock
 *   12.0.26 -> SetAutoAddKSNDATA
 *   12.0.27 -> EntryPoint
 *   12.0.28 -> SetEntryModeOpen
 *   12.0.29 -> SetEntryModeClose
 *   12.0.30 -> EmvPinbyPass
 *   12.0.31 -> EmvFinal
 *
 * 13. Transaction kernel APIs --- Express
 *   13.0.1 -> ExpressKernelInit
 *   13.0.2 -> ExpressAidSet
 *   13.0.3 -> ExpressCapkSet
 *   13.0.4 -> ExpressGenerlParamSet
 *   13.0.5 -> ExpressDRLParamSet
 *   13.0.6 -> ExpressCRLParamSet
 *   13.0.7 -> ExpressExcepFileParamSet
 *   13.0.8 -> ExpressKernelConfigSet
 *   13.0.9 -> ExpressTransaction
 *   13.0.10 -> ExpressGetTagData
 *   13.0.11 -> ExpressFinal
 *
 * 14. Transaction kernel APIs --- PCI
 *   14.0.1 -> PciWritePinMKey
 *   14.0.2 -> PciWriteMacMKey
 *   14.0.3 -> PciWriteDesMKey
 *   14.0.4 -> PciWritePinKey
 *   14.0.5 -> PciWriteMacKey
 *   14.0.6 -> PciWriteDesKey
 *   14.0.7 -> PciReadKCV
 *   14.0.8 -> PciGetPin
 *   14.0.9 -> PciGetMac
 *   14.0.10 -> PciGetDes
 *   14.0.11 -> PciWriteDukptIpek
 *   14.0.12 -> PciGetDukptMac
 *   14.0.13 -> PciGetDuktDes
 *
 *---------------------
 * Modify: Ciontek.Tao 2021-03-11
 *         The first edition
 *
 * Modify: Ciontek.Tao 2022-04-12
 *        1. Add  PrintLabLocate for support print lable
 *        2. Add  SysBeep
 *        3. Add Magnetic card APIs
 *
 *
 * Modify: Ciontek.Tao 2022-05-19
 *       1. Add Contactless cards APIs
 *       2. Add transaction kernel APIs(common APIs & Paypass & Paywave & Express & EMV & PCI)
 *
 **************************************************************************************************/
package com.ctk.sdk;

import android.os.IBinder;
import java.lang.reflect.Method;
import android.os.RemoteException;
import android.util.Log;
import com.ciontek.ciontekposservice.ICiontekPosService;
import com.ciontek.ciontekposservice.IEmvProcessCallback;
import com.ciontek.ciontekposservice.IInputPinCallback;

import android.graphics.Bitmap;
import java.util.List;

//import test.apidemo.activity.pinkeypad.PinpadLayoutEntity;

//import test.apidemo.activity.PinpadLayoutEntity;


public class PosApiHelper {

    // private static final String TAG = "PosApiHelper";
    private static final String TAG = "KeyPad";
    public static final String POS_SERVICE = "posmanager";

    private ICiontekPosService mPosService;
    private static PosApiHelper mInstance;

    private PosApiHelper() {
        try {
            Class serviceManager = Class.forName("android.os.ServiceManager");
            Method method = serviceManager.getMethod("getService", String.class);
            IBinder b = (IBinder)method.invoke(serviceManager.newInstance(), POS_SERVICE);
            mPosService = ICiontekPosService.Stub.asInterface(b);
            Log.d(TAG, "get pos service success!");
            if(mPosService == null){
                Log.d(TAG, "get pos service null!");
            }
        } catch (Exception e) {
            Log.e(TAG, "get pos service Exception!");
            e.printStackTrace();
        }


    }

    public static PosApiHelper getInstance() {
        if (null == mInstance) {
            mInstance = new PosApiHelper();
        }
        return mInstance;
    }

/*******************************************************************************************************
 ****************************** API start from here ******************************************************
 *******************************************************************************************************/
    /**
     * @Title: SysApiVersion
     * @Description: enable/disable debug log
     * @param: @param
     * @param: @return
     * 0	success
     * !0	fail
     * @return: int
     * @throws
     */
    //0000
    public String SysApiVerson(){
        return "v1.0.5";
    }

    /*--------------------------------Android OS Interface ----------------------------------------*/
    /**
     * @Title: installRomPackage
     * @Description: to update Android OS firmware
     * @param:
    romFilePath: rom file path
    example:
    String path = "/storage/emulated/0/update.zip";
    PosApiHelper.getInstance().installRomPackage(path);
     * @return:
     * 0	success
     * !0	fail
     */
    //1.0.1
    synchronized public int installRomPackage(String romFilePath){
        if (mPosService != null) {
            try {
                return mPosService.installRomPackage(romFilePath);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;

    }

    /**
     * @Title: getOSVersion
     * @Description:  get Android OS firmware version
     * @param:
     * @return:
     */
    //1.0.2
    public String getOSVersion(){
        if (mPosService != null) {
            try {
                return mPosService.getOSVersion();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    /**
     * @Title: getDeviceId
     * @Description:  get the device Serial Number
     * @param:
     * @return:
     */
    //1.0.3
    public String getDeviceId(){
        if (mPosService != null) {
            try {
                return mPosService.getDeviceId();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;

    }
    /*--------------------------------Peripheral Interface ----------------------------------------*/
    /**
     * @Title: SysLogSwitch
     * @Description: enable/disable debug log
     * @param: @param
     * @param: @return
     * 0	success
     * !0	fail
     * @return: int
     * @throws
     */
    //2.0.1
    synchronized public int SysLogSwitch(int level){
        if(mPosService != null){
            try {
                return  mPosService.Lib_LogSwitch(level);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: SysGetRand
     * @Description:obtain random number 
     * @param: rnd [out]
     * @param: @return
     * 0	success
     * !0	fail
     * @return: int
     * @throws
     */
    //2.0.2
    synchronized public int SysGetRand(byte[] rnd){
        if (mPosService != null) {
            try {
                return mPosService.Lib_GetRand(rnd);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: SysUpdate
     * @Description: to update the mcu app firmware
     * @param: @param
     * @param: @return
     * 0	success
     * !0	fail
     * @return: int
     * @throws
     */
    //2.0.3
    synchronized public int SysUpdate(){
        if (mPosService != null) {
            try {
                return mPosService.Lib_Update_32550();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: SysGetVersion
     * @Description: get the mcu firmware version array
     * @param: @param [out] byte[] buf
     *                buf[0~2] : mcu Version number
     *                buf[3~5] : lib Version number
     *                buf[6~9] : sb Version number , 0xff mean have no sb Version number,ignore it
     * @param: @return
     * 0	success
     * !0	fail
     * @return: int
     * @throws
     */
    //2.0.4
    synchronized public int SysGetVersion(byte[] buf) {
        if (mPosService != null) {
            try {
                return mPosService.Lib_GetVersion(buf);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: SysReadSN
     * @Description: read a 16 bytes serial number
     * @param: 1.SN [out]
     *
     *
     * @param: @return
     * 0	success
     * !0	fail
     * @return: int
     * @throws
     */
    //2.0.5
    synchronized public int SysReadSN(byte[] SN){
        if (mPosService != null) {
            try {
                return mPosService.Lib_ReadSN(SN);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: SysWriteSN
     * @Description: to write a 16 bytes SN
     * @param: 1.SN [out]
     *
     *
     * @param: @return
     * 0	success
     * !0	fail
     * @return: int
     * @throws
     */
    //2.0.6
    synchronized public int SysWriteSN(byte[] SN){
        if (mPosService != null) {
            try {
                return mPosService.Lib_WriteSN(SN);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: SysReadChipID
     * @Description: Get IC card ID no.
     * @param:
     * buf: IC card ID no.
     * len:length
     * @return:
     * 0	success
     * !0	fail
     */
    //2.0.7
    synchronized public int SysReadChipID(byte[] buf,int len){
        if (mPosService != null) {
            try {
                return mPosService.Lib_ReadChipID(buf,len);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /*---------------------------------   Ic card   --------------------------------------------*/

    /**
     * @Title: IccOpen
     * @Description: Initialize the IC card and return the response content of the card
     * @param: @param slot
     * 0x00－IC card Channel;
     * 0x01－PSAM1 card Channel;
     * 0x02－PSAM2 card Channel;
     * @param: @param VCC_Mode
     * 1---5V;
     * 2---3V;
     * 3---1.8V;
     * @param: @param ATR	Card reset response. (at least 32+1bytes of space). The contents are length (1 byte) + reset response content
     * @return:
     * 0	success.
     * (-2403)	Channel Error
     * (-2405)	The card is pulled out or not
     * (-2404)	Protocol error
     * (-2500)	Voltage mode error of IC card reset
     * (-2503)	Communication failure.
     */
    //3.0.1
    synchronized public int IccOpen(byte slot, byte vccMode, byte[] atr){
        if (mPosService != null) {
            try {
                return mPosService.Lib_IccOpen(slot,vccMode,atr);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: IccClose
     * @Description: Close IC Card
     * @param: @param slot: Channel
    Slotcassette No.：
    0x00－IC cand Channel
    0x01－PSAM1 cand Channel
    0x02－PSAM2 cand Channel
     * @return:
     * 0	success
     * !0	fail
     */
    //3.0.2
    synchronized public int IccClose(byte slot){
        if (mPosService != null) {
            try {
                Log.d("PosManagerService","Lib_IccClose");
                return mPosService.Lib_IccClose(slot);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: IccCommand
     * @Description: Read and Write IC Card
     *               If both LC and LE exist, you should set "LC = X; LE = 0x01"
     * @param: @param slot: Card Channel
     * 0x00－IC Card Channel
     * 0x01－PSAM1 Card Channel
     * 0x02－PSAM2 Card Channel
     * @param: @param ApduSend:  sent to the card’s apdu
     * @param: @param ApduResp:  Receive the card's  apdu of returned
     * @return:
    0	    successfully
    (-2503)	Communication timeout
    (-2405)	The cards are put out in the transaction
    (-2401)	Parity error
    (-2403)	Select Channel error
    (-2400)	Sending data too long（LC）
    (-2404)	The Protocol error（is Not T = 0 or T = 1）
    (-2406)	No reset card
     */
    //3.0.3
    synchronized public int IccCommand(byte slot, byte[] apduSend, byte[]  apduResp){
        if (mPosService != null) {
            try {
                return mPosService.Lib_IccCommand(slot,apduSend,apduResp);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: IccCheck
     * @Description: Check if there is a card in the specified cassette。
     * @param: @param slot: Card Channel
     * 0x00－IC Card Channel
     * @return:
     * 0	 - The IC card was detected and inserted
     * !0 - fail
     */
    //3.0.4
    synchronized public int IccCheck(byte slot){
        if (mPosService != null) {
            try {
                return mPosService.Lib_IccCheck(slot);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    //3.0.5
    synchronized public int SC_ApduCmd(byte bslot, byte[] pbInApdu, int usInApduLen, byte[]pbOut,byte[] pbOutLen){
        if (mPosService != null) {
            try {
                return mPosService.SC_ApduCmd(bslot,pbInApdu,usInApduLen,pbOut,pbOutLen);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    synchronized public int SleOpenAt24(byte slot){
        if (mPosService != null) {
            try {
                return mPosService.SleOpenAt24(slot);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;

    }

    synchronized public int SleCloseAt24(byte slot){
        if (mPosService != null) {
            try {
                return mPosService.SleCloseAt24(slot);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;

    }

    synchronized public int SleCheckAt24(byte slot){
        if (mPosService != null) {
            try {
                return mPosService.SleCheckAt24(slot);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;

    }

    synchronized public int SleReadMemAt24(byte slot, int startAddress, int length,  byte []outData){
        if (mPosService != null) {
            try {

                return mPosService.SleReadMemAt24(slot,startAddress,length,outData);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;

    }

    synchronized public int SleWriteMemAt24(byte slot, int startAddress, int length,  byte []inData){
        if (mPosService != null) {
            try {
                return mPosService.SleWriteMemAt24(slot,startAddress,length,inData);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;

    }

    /*--------------------------------    printer      --------------------------------------------*/

    /**
     * @Title: PrintInit
     * @Description: Printer initialization
     * @param:
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.1
    synchronized public int PrintInit(){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnInit();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintInit
     * @Description: Printer initialization with gray and font
     * @param:
     * Gray: the grad density. 1-high density, 2-medium,3-low

     * Fontheight: font height. 16 or 24

     * Fontwidth: font width. 16 or 24

     * Fontzoom: bolt font, 0x00 or 0x33

     * @param:
    0	success
    !0	fail
    -4001 : PRINT BUSY
    -4002 : PRINT NOPAPER
    -4003 : PRINT DATAERR
    -4004 : PRINT FAULT
    -4005 : PRINT TOOHEAT
    -4006 : PRINT UNFINISHED
    -4007 : PRINT NOFONTLIB
    -4008 : PRINT BUFFOVERFLOW
    -4009 : PRINT SETFONTERR
    -4010 : PRINT GETFONTERR
     */
    //4.0.2
    synchronized public int PrintInit(int gray,int fontHeight, int fontWidth, int fontZoom) {
        if (mPosService != null) {
            try {
                int ret = -1;
                ret =  mPosService.Lib_PrnInit();
                if(ret!=0){
                    return  ret;
                }

                //setGray
                ret = mPosService.Lib_PrnSetGray(gray);
                if(ret!=0){
                    return  ret;
                }


                //setFont
                ret = mPosService.Lib_PrnSetFont((byte)fontHeight,(byte)fontWidth,(byte)fontZoom);
                if(ret!=0){
                    return  ret;
                }

                return  ret;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;

    }


    /**
     * @Title: PrintSetFont
     * @Description: Set font for the Printer
     * @param:
     *          1.AsciiFontHeight : font height. 16 or 24
     *          2.ExtendFontHeight: font width. 16 or 24
     *          3.Zoom            :  bolt font, 0x00 or 0x33
     * @return :
     * 0	success
     * !0	fail
     */
    //4.0.3
    synchronized public int PrintSetFont(byte AsciiFontHeight, byte ExtendFontHeight, byte Zoom){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnSetFont( AsciiFontHeight,  ExtendFontHeight,  Zoom);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintSetGray
     * @Description: Set print density
     * @param:
     * nLevel: density level, value 1~5
     *          1:Lowest 3：medium 5：Highest
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.4
    synchronized public int PrintSetGray(int nLevel){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnSetGray( nLevel);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: PrintSetSpace
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.5
    synchronized public int PrintSetSpace(byte x, byte y){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnSetSpace( x,  y);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }



    /**
     * @Title: PrintGetFont
     * @Description: get current font 
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.6
    synchronized public int PrintGetFont( byte[] AsciiFontHeight,  byte[] ExtendFontHeight,  byte[] Zoom){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnGetFont( AsciiFontHeight,  ExtendFontHeight,  Zoom);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintStep
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.7
    synchronized public int PrintStep(int pixel){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnStep( pixel);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: PrintSetVoltage
     * @Description:
     * @param:
     *      voltage： current battery voltage*10
     *
     *      example, we Set current voltage as 7.5V
     *      PrintSetVoltage(75);
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.8
    synchronized public int PrintSetVoltage(int voltage){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnSetVoltage( voltage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: PrintIsCharge
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.9
    synchronized public int PrintIsCharge(int ischarge){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnIsCharge( ischarge);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintSetLinPixelDis
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.10
    synchronized public int PrintSetLinPixelDis(char LinDistance){
        if (mPosService != null) {
            try {
                return mPosService.Lib_SetLinPixelDis( LinDistance);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintStr
     * @Description: set a String to print
     * @param: String str:
     *
     *
     * @return:
     * 0	success
     * !0	fail
     * -4002 –need paper
     * -4003 –data error
     */
    //4.0.11
    synchronized public int PrintStr(String str){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnStr(str);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintBmp
     * @Description: Set BMP photo as print content ( size requirement width <=384,height <=500)
     * @param: bitmap
     *
     *
     * @return:
    0 –successfully
    Other -failure
    Such as:
    -4003 PRN_DATAERR
    -4004 PRN_FAULT
    -4008 PRN_BUFFOVERFLOW
     */
    //4.0.12
    synchronized public int PrintBmp(Bitmap bitmap){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnBmp(bitmap);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintBarcode
     * @Description: print a Barcode with contents
     * @param:
     *  contents: barcode content
     *  desiredWidth: barcode width
     *  desiredHeight: barcode height
     *  barcodeFormat: barcode Format -->
     *                 "CODE_128" "CODE_39" "EAN_8" "QR_CODE" "PDF_417" "ITF"
     *
     * @return:
    0 –successfully
    Other -failure
     */
    //4.0.13
    synchronized public int PrintBarcode(String contents, int desiredWidth,int desiredHeight,String barcodeFormat){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnBarcode(contents,desiredWidth,desiredHeight,barcodeFormat);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;

    }


    /**
     * @Title: PrintQrCode_Cut
     * @Description: Print QR code
     * @param:
     *  contents: barcode content
     *  desiredWidth: barcode width
     *  desiredHeight: barcode height
     *  barcodeFormat: barcode Format -->
     *                 "CODE_128" "CODE_39" "EAN_8" "QR_CODE" "PDF_417" "ITF"
     *
     * @return:
    0 –successfully
    Other -failure
     */
    //4.0.14
    synchronized public int PrintQrCode_Cut(String contents, int desiredWidth,int desiredHeight, String barcodeFormat){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrintCutQrCode(contents,desiredWidth,desiredHeight,barcodeFormat);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;

    }


    /**
     * @Title: PrintCutQrCode_Str
     * @Description:
     * @param:
     *  contents: barcode content
     *  desiredWidth: barcode width
     *  desiredHeight: barcode height
     *  barcodeFormat: barcode Format -->
     *                 "CODE_128" "CODE_39" "EAN_8" "QR_CODE" "PDF_417" "ITF"
     *
     * @return:
    0 –successfully
    Other -failure
     */
    //4.0.15
    synchronized public int PrintCutQrCode_Str(String contents, String printTxt ,int distance,
                                               int desiredWidth,int desiredHeight, String barcodeFormat){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrintCutQrCodeStr(contents,printTxt,distance,desiredWidth,desiredHeight,barcodeFormat);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: PrintStart
     * @Description: start to print
     * @param:
     * @return:
    0	success
    !0	fail
    -1001/1001：send fail；
    -1002/1002：receive timeout；
    -1：Short of paper；
    -2：The temperature is too high；
    -3: The voltage is too low；
    8/9:Instruction reply disorder；
    -1023：status error；
    -1021：Short of paper；
    -1000/-1016/-1001/-1002/-1003/-1004/-1019/-1017/-1018/-1020：print timeout；
    -1007/-1008/-1009/-1010/-1011/-1012：Print times exceeds limit；
    -1022：heat error；
    -1015/-1014;Short of paper；
     */
    //4.0.16
    synchronized public int PrintStart(){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnStart();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintSetLeftIndent
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.17
    synchronized public int PrintSetLeftIndent(int x){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnSetLeftIndent( x);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintSetAlign
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.18
    synchronized public int PrintSetAlign(int X){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnSetAlign( X);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintCharSpace
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.19
    synchronized public int PrintCharSpace(int X){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnSetCharSpace( X);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintSetLineSpace
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.20
    synchronized public int PrintSetLineSpace(int x){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnSetLineSpace( x);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintSetLeftSpace
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.21
    synchronized public int PrintSetLeftSpace(int x){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnSetLeftSpace( x);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: PrintSetSpeed
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.22
    synchronized public int PrintSetSpeed(int iSpeed){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnSetSpeed( iSpeed);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintCheckStatus
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //7.0.23
    synchronized public int PrintCheckStatus( ){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnCheckStatus( );
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintFeedPaper
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.24
    synchronized public int PrintFeedPaper(int step){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnFeedPaper( step);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintSetMode
     * @Description: set print mode for receipt or lable
     * @param:
     *       mode:
     *       0(default) -> print a receipt
     *       1          -> print a lable
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.25
    synchronized public int PrintSetMode(int mode){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnSetMode(mode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: PrintSetUnderline
     * @Description: Set the lines and width of underline
     * @param:
     *       x:
     *     The value is in HEX format,
     *     The upper four digits are the number of underlined lines, greater than 2 is 2 lines, and less than 2 is 1 line
     *     The lower four bits are the width
     *
     *
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.26
    synchronized public int PrintSetUnderline(int x){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnSetUnderline(x);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: PrintSetReverse
     * @Description: Set the font display reverse mode
     * @param:
     *       mode:
     *       0(default) -> normal
     *       1          -> reverse
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.27
    synchronized public int PrintSetReverse(int x){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnSetReverse(x);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: PrintSetBold
     * @Description: Set the font display Bold mode
     * @param:
     *       mode:
     *       0(default) -> normal
     *       1          -> Bold
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.28
    synchronized public int PrintSetBold(int x){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnSetBold(x);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: PrintLogo
     * @Description: print a picture by a byte[]
     * @param:
     *       byte[] logo: the byte[] for a picture
     *
     *
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.29
    synchronized public int PrintLogo(byte[] logo){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnLogo(logo);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PrintLabLocate
     * @Description: Determine the print position for print a lable
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //4.0.30
    synchronized public int PrintLabLocate (int step){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PrnFeedPaper( step);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /*---------------------------  APP White List  -----------------------------------------------
     *
     * To config app whitelist for restrict APK install
     *
     * ------------------------------------------------------------------------------------------*/

    /**
     * @Title: enableAppInstallWhiteList
     * @Description: enable the function of App White list 
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //5.0.1
    synchronized  public boolean enableAppInstallWhiteList(){

        if (mPosService != null) {
            try {
                return mPosService.enableAppInstallWhiteList();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;

    }


    /**
     * @Title: disableAppInstallWhiteList
     * @Description: disable the function of App White list 
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //5.0.2
    synchronized  public boolean disableAppInstallWhiteList(){

        if (mPosService != null) {
            try {
                return mPosService.disableAppInstallWhiteList();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }



    /**
     * @Title: addAppToInstallWhiteList
     * @Description: add a apk to white list
     * @param:
     *
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //5.0.3
    synchronized public boolean addAppToInstallWhiteList(String pkgName){

        if (mPosService != null) {
            try {
                return mPosService.addAppToInstallWhiteList(pkgName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * @Title: delAppFromInstallWhiteList
     * @Description: delect a apk from white list
     * @param:
     *
     * @return: ArrayList<String>
     *
     * example:
     *
     *
     *
     */
    //5.0.4
    synchronized public boolean delAppFromInstallWhiteList(String pkgName){

        if (mPosService != null) {
            try {
                return mPosService.delAppFromInstallWhiteList(pkgName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;

    }

    /**
     * @Title: getAppInstallWhiteList
     * @Description: get the APP white list
     * @param:
     *
     * @return: ArrayList<String>
     *
     * example:
     *
     *
     *
     *
     *
     */
    //5.0.5
    synchronized public List<String> getAppInstallWhiteList(){

        if (mPosService != null) {
            try {
                return mPosService.getAppInstallWhiteList();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;

    }


    /**
     * @Title: enableAppUninstallBlackList
     * @Description:
     * @param:
     *
     * @return:
     *
     * example:
     *
     *
     *
     */
    //5.0.6
    synchronized public boolean enableAppUninstallBlackList(){

        if (mPosService != null) {
            try {
                return mPosService.enableAppUninstallBlackList();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;

    }


    /**
     * @Title: disableAppUninstallBlackList
     * @Description:
     * @param:
     *
     * @return:
     *
     * example:
     *
     */
    //5.0.7
    synchronized public boolean disableAppUninstallBlackList(){

        if (mPosService != null) {
            try {
                return mPosService.disableAppUninstallBlackList();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;

    }



    /**
     * @Title: addAppToUninstallBlackList
     * @Description:
     * @param:
     *
     * @return:
     *
     * example:
     *
     */
    //5.0.8
    synchronized public boolean addAppToUninstallBlackList(String pkgName){

        if (mPosService != null) {
            try {
                return mPosService.addAppToUninstallBlackList(pkgName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;

    }


    /**
     * @Title: delAppFromUninstallBlackList
     * @Description:
     * @param:
     *
     * @return:
     *
     * example:
     *
     */
    //5.0.9
    synchronized public boolean delAppFromUninstallBlackList(String pkgName){

        if (mPosService != null) {
            try {
                return mPosService.delAppFromUninstallBlackList(pkgName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;

    }



    /**
     * @Title: getAppUninstallBlackList
     * @Description:
     * @param:
     *
     * @return:
     *
     * example:
     *
     */
    //5.0.10
    synchronized public List<String> getAppUninstallBlackList(){

        if (mPosService != null) {
            try {
                return mPosService.getAppUninstallBlackList();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    /*---------------------------  Fiscal  APIs---------------------------------------------------*/

    /**
     * @Title: fiscalOpen
     * @Description: power on the fiscal and open the uart port
     * @param:
     * baudrate: the baudrate of uart port
     *     size:
     *     stop:
     *   parity:
     *    cflow:
     *
     * @return:
     *       0: success
     *      -1: fail
     *      -2: uninitialized
     *      -3: paramter error
     *      -4: timeout
     *      -5: init uart port error
     *      -6: read error
     *      -7: write error
     *
     */
    //6.0.1
    synchronized public int fiscalOpen(int baudrate,int size, int stop, char parity, char cflow){

        if (mPosService != null) {
            try {
                return mPosService.fiscalOpen(baudrate,size,stop,parity,cflow);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;

    }

    /**
     * @Title: fiscalClose
     * @Description: power off the fiscal and close the uart port
     * @param:
     *
     * @return:
     *       0: success
     *      -1: fail
     *      -2: uninitialized
     *      -3: paramter error
     *      -4: timeout
     *      -5: init uart port error
     *      -6: read error
     *      -7: write error
     *
     */
    //6.0.2
    synchronized public int fiscalClose(){

        if (mPosService != null) {
            try {
                return mPosService.fiscalClose();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;

    }

    /**
     * @Title: fiscalWrite
     * @Description: send data to fiscal
     * @param:
     *   data: the data you want to send
     *
     * @return:
     *       0: success
     *      -1: fail
     *      -2: uninitialized
     *      -3: paramter error
     *      -4: timeout
     *      -5: init uart port error
     *      -6: read error
     *      -7: write error
     *
     */
    //6.0.3
    synchronized public int fiscalWrite(byte[] data){
        if (mPosService != null) {
            try {
                return mPosService.fiscalWrite(data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;

    }

    /**
     * @Title: fiscalRead
     * @Description: send data to fiscal
     * @param:
     *   buffer: the buffer read from fiscal
     *   bufLen: the length of the buffer
     *  timeout: timeout for read, unit: ms
     * @return:
     *       0: success
     *      -1: fail
     *      -2: uninitialized
     *      -3: paramter error
     *      -4: timeout
     *      -5: init uart port error
     *      -6: read error
     *      -7: write error
     *
     */
    //6.0.4
    synchronized public int fiscalRead(byte[] buffer,int bufLen,int timeout){
        if (mPosService != null) {
            try {
                return mPosService.fiscalRead(buffer,bufLen,timeout);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;

    }

    /*-----------------------------------  Beep api   -----------------------------------------*/
    /**
     * @Title: SysBeep
     * @Description: for Beep
     * @param:
     * @return:
     * 0    success
     * !0   fail
     */
    //7.0.1
    synchronized public int SysBeep(){
        if (mPosService != null) {
            try {
                return mPosService.Lib_Beep();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /*-----------------------------------  Magnetic card   -----------------------------------------*/
    /**
     * @Title: McrOpen
     * @Description: To open the Magnetic card reader
     * @param: 1.
     *
     *
     * @return:
     * 0   success
     * !0  fail
     */
    //8.0.1
    synchronized public int McrOpen(){
        if (mPosService != null) {
            try {
                return mPosService.Lib_McrOpen();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: McrClose
     * @Description: Close the Magnetic card reader 
     * @param: 1.
     * @return:
     * 0   success
     * !0  fail
     */
    //8.0.2
    synchronized public int McrClose(){
        if (mPosService != null) {
            try {
                return mPosService.Lib_McrClose();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: McrReset
     * @Description: Magnetic head restoration,clear magnetic relief data
     * @param:
     * @return:
     * 0   success
     * !0  fail
     */
    //8.0.3
    synchronized public int McrReset(){
        if (mPosService != null) {
            try {
                return mPosService.Lib_McrReset();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: McrCheck
     * @Description: Check have Magnetic card swiped
     * @param:
     * @return:
     * 0   success
     * !0  fail
     */
    //8.0.4
    synchronized public int McrCheck(){
        if (mPosService != null) {
            try {
                return mPosService.Lib_McrCheck();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: McrRead
     * @Description: Read magnetic card track 1,2,3 data from relief area
     * @param:
    1.keyNo:
    DES key index no., value from 0~4, should be the same AUTHDESK
    key index no in authentication
    2.mode: Magnetic head mode
    mode -> 0：Unencrypted
    mode -> 1：encrypted
    3. track1:
    store track 1 data’s pointer [Application layer relief area should set as 256]
    4. track2:
    store track 2 data’s pointer [Application layer relief area should set as 256]
    5. track3:
    store track 3 data’s pointer [Application layer relief area should set as 256]
     * @param: @return
    0 swipe card error
    （＞0）：
    bit0 = 1 read track 1 data correctly
    bit1 = 1 read track 2 data correctly
    bit2 = 1 read track 3 data correctly
    bit4 = 1 checkout wrong in track 1 data
    bit5 = 1 checkout wrong in track 2 data
    bit6 = 1 checkout wrong in track 3 data
    other value\
     */
    //8.0.5
    synchronized public int McrRead(byte keyNo, byte mode, byte[] track1, byte[] track2, byte[] track3){
        if (mPosService != null) {
            try {
                return mPosService.Lib_McrRead(keyNo,mode,track1,track2,track3);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /*---------------------------------   Contactless  Card   --------------------------------------------*/
    /**
     * @Title: PiccOpen
     * @Description: Check the power and reset on the non-contact card module
     *               to check whether the initial state of the module is normal after reset
     * @return:
     * 0   success
     * !0  fail
     */
    //9.0.1
    synchronized public int PiccOpen(){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccOpen();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PiccClose
     * @Description: Close the non-contact card module to make the module close
     * @return:
     * 0   success
     * !0  fail
     */
    //9.0.2
    synchronized public int PiccClose(){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccClose();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: PiccCheck
     * @Description: Search for PICC CARDS by specified mode; After searching the card, select and activate it
     * @param: @param mode - Specify the card finder protocol --‘A’/‘B’/‘M’ cardType：

     * @param: @param cardType - Card type byte buffer;The type value of the 2 bytes is currently returned
     * CardType[0] ：
     *     ‘A’ —搜寻到A型卡
     *     ‘B’ —搜寻到B型卡
     * CardType[1] ：
     *     ‘C’ —搜寻到CPU型卡
     *     ‘M’ —搜寻到M1型卡
     * @param :serialNo: it is the card UID
    SerialNo[0]: the length of the UID;
    SerialNo[1~N] :UID ;
     * @return:
    0      successfully
    (-3503)    Parameter error(Invalid Mode value)
    (-3502)    Module not opened
    (-3526)    conflict
    Other  operation failure

     */
    //9.0.3
    synchronized public int PiccCheck(byte mode, byte[] cardType,byte[] serialNo){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccCheck(mode,cardType,serialNo);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PiccPolling
     * @Description: Polling card(type A)
     * @param : CardType => 2 Byte length
     * 'A' 'C':type A card
     * 0x00,0x00:no card
     * UID:
     * ucUIDLen:
     * ATS:
     * 11
     * ucATSLen:
     * SAK:
     * Return: 0 => success;
    other => error;
     */
    //9.0.4
    synchronized public int PiccPolling(byte[] CardType,byte[] UID,byte[] ucUIDLen,byte[] ATS,byte[] ucATSLen,byte[] SAK){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccPolling(CardType,UID,ucUIDLen,ATS,ucATSLen,SAK);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PiccCommand
     * @Description: Handle the APDU card command
     *               Note:If both LC and LE exist, you should set "LC = X; LE = 0x01"
     * @param :
     * [Input] apduSend：sent to the card’s apdu
     * [Output]:apduResp: Receive the card's apdu of returned
     * @return
     *  0 successfully
     *  (-3503) Parameter error
     *  (-3502) Module not opened
     *  (-3524) Data exchange error
     */
    //9.0.5
    synchronized public int PiccCommand(byte[] apduSend,byte[] apduResp){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccCommand(apduSend,apduResp);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PiccApduCmd
     * @Description: Send apdu command to the card
     *
     * @param :
     * * Input:
     * 		 pucInput =>Data to be sent;
     *       usInputLen =>the length of the “pucInput ”;
     * * Output:
     *      pucOutput =>Data output ;
     *      pusOutputLen =>output length
     * @return
     * 0 :successful
     * Other: fail
     */
    //9.0.6
    synchronized public int PiccApduCmd(byte[] jpucInput,int usInputLen, byte[] jpucOutput,byte[] jpusOutputLen){
        if (mPosService != null) {
            try {
                return mPosService.Picc_ApduCmd(jpucInput,usInputLen,jpucOutput,jpusOutputLen);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccNfc
     * @Description:
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     */
    //9.0.7
    synchronized public int PiccNfc(byte[] NfcData_Len, byte[] Technology, byte[] UID, byte[] NDEF_message){
        if (mPosService != null) {
            try {
                return mPosService.Lib_Nfc(NfcData_Len,Technology,UID,NDEF_message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccRemove
     * @Description: Check if the activated card is still in the magnetic field
     *
     * @param :None
     * @return
     * 0    : the card that has been activated is still in the magnetic field area;
     * other: card leaves the magnetic field
     *
     */
    //9.0.8
    synchronized public int PiccRemove(){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccRemove();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccHalt
     * @Description:
     *
     * @param :None
     * @return
     *
     */
    //9.0.9
    synchronized public int PiccHalt(){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccHalt();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccReset
     * @Description:
     *
     * @param :None
     * @return
     *
     */
    //9.0.10
    synchronized public int PiccReset(){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccReset();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccSamAv2Init
     * @Description: Initialize the SAMAV2 card
     *
     * @param :
     *  SamSlotNo: 1-sam1; 2-sam2;
     * 	SamHostKey: HosKey;
     *  SAMHostMode:2Byte(Little endian mode);
     * 	SAMAV2Version:
     * 	jSamAv2VerLen:
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.11
    synchronized public int PiccSamAv2Init(int SamSlotNo , byte[] SamHostKey, byte[] SAMHostMode, byte[]  SAMAV2Version, byte[] jSamAv2VerLen){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccSamAv2Init( SamSlotNo , SamHostKey,SAMHostMode, SAMAV2Version,  jSamAv2VerLen);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccHwModeSet
     * @Description: Switch nfc work mode
     *
     * @param :Mode
     *        1=>samav2 mode;
     *        0=>not sam mode;
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.12
    synchronized public int PiccHwModeSet(int Mode){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccHwModeSet(Mode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title:  PiccM1Authority
     * @Description: Verify password A or B when M1 card visit related block
     *
     * @param :
     * type：
     *        Specifies the type of password to be submitted：
     *        ‘A’or‘a’ or0x0a ——Submit ‘A’ password
     *        ‘B’or‘b’ or0x0b ——Submit ‘B’ password
     * blkNo：
     *       The block number used to specify access. For the M1 card with 1K
     *       capacity, its valid range is 0~63
     * pwd：
     *       Point to the submitted password buffer
     * SerialNo：
     *       For the future.
     * @return
     *     0:   successfully
     * -3502:	Module not opened (-3503)	Parameter error
     * -3523:	Password authentication failure
     *
     */
    //9.0.13
    synchronized public int PiccM1Authority(byte type, byte blkNo, byte[] pwd, byte[] serialNo){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccM1Authority(type,blkNo, pwd, serialNo);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     *
     * @Title:  PiccM1ReadBlock
     * @Description: Read appointed block content from M1 card(16 byte in total)
     *
     * @param :
     * blkNo：
     *       The block number used to specify access. For the M1 card with 1K capacity, its valid
     *       range is 0~63
     * blkValue：
     *       point to the head address of buffer of block to be accessed;this buffer
     *       should assign at least 16 bytes
     * @return
     *    0 :   successful
     * -3502:	Module not opened: fail
     * -3522:	Read block data failure
     */
    //9.0.14
    synchronized public int PiccM1ReadBlock(byte blkNo, byte[] blkValue){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccM1ReadBlock(blkNo, blkValue);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     *
     * @Title:  PiccM1WriteBlock
     * @Description: Write content to appointed ablock to M1card(16 byte in total)
     *
     * @param :
     * blkNo：
     *       The block number used to specify access. For the M1 card with 1K capacity, its valid
     *       range is 0~63
     * blkValue：
     *       point to the head address of buffer of block to be written
     *
     * @return
     *    0 :   successful
     * -3502:	Module not opened: fail
     * -3522:	write  block data failure
     */
    //9.0.15
    synchronized public int PiccM1WriteBlock(byte blkNo, byte[] blkValue){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccM1WriteBlock( blkNo,  blkValue);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     *
     * @Title:  PiccM1Operate
     * @Description: Add, subtract, store operations
     *              Annotation: you must firstly call the Lib_PiccM1WriteValue function before you operate the PiccM1Operate function.
     *              The data must comply with the data format of the block.
     *
     * @param :
     *type：
     *      To appoint operate type for wallet
     *      ‘+’ →top up, plus sign
     *      ‘-’ →decrease value , minus sign
     *      ‘=’ → transfer and recover from wallet to wallet backup block, equal sign
     *blkNo：For  the  M1 card  with 1K capacity, its valid range is 0~63
     *Value：point to the head address of buffer of amount to top up and
     *       decreased;amount is 4 bytes,lower byte is in front
     *updateBlkNo：For  the  M1 card  with 1K capacity, its valid range is 0~63
     *
     * @return
     * 0 :successful
     * (-3503)	Parameter error(Invalid Type value) : fail
     * (-3502)	Module not opened
     * (-3520)	Top up failure
     */
    //9.0.16
    synchronized public int PiccM1Operate(byte type, byte blkNo, byte [] value, byte updateBlkNo){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccM1Operate( type,  blkNo,  value,  updateBlkNo);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     *
     * @Title:  PiccM1WriteValue
     * @Description: Write 4 bytes of data (block data format)
     *
     * @param :
     * blkNo => Block number(For the M1 card with 1K capacity, its valid range is 0~63 )
     * Value => 4 bytes of data that you want to write;
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.17
    synchronized public int PiccM1WriteValue(int blkNo,byte[] value){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccM1WriteValue(blkNo, value);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     *
     * @Title:  PiccM1ReadValue
     * @Description: Read the data from the specified blkNo
     *
     * @param :
     * blkNo => Block number to be readed (For the M1 card with 1K capacity, its valid range is 0~63 )
     * Value => the 4 bytes data that you readed from blkNo
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.18
    synchronized public int PiccM1ReadValue(int blkNo,byte[] value){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccM1ReadValue(blkNo,value);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PiccM1RestoreTransfer
     * @Description: Mifare class restoreTransfer
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.19
    synchronized public int PiccM1RestoreTransfer(byte SrcBlock,byte DstBlock){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccM1RestoreTransfer(SrcBlock, DstBlock);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title:  PiccSamMfcAuth
     * @Description:
     *
     * @param :
     *  KeyType :
     *          'A' or 0x0a - Submit typeA KEY
     *          Other - Submit typeB KEY
     * BlkNo:
     * KeyNumber
     * KeyVersion
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.20
    synchronized public int PiccSamMfcAuth(byte KeyType, byte BlkNo, byte KeyNumber, byte KeyVersion){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccSamMfcAuth(KeyType, BlkNo, KeyNumber, KeyVersion);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccWriSl1KeyToAv2
     * @Description:
     *
     * @param :
     *     key(6 bytes for key A + 6 bytes for key B );
     * 	   KeyNumber
     * 	   KeyPos
     * 	   KeyVersion
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.21
    synchronized public int PiccWriSl1KeyToAv2(byte[] key ,byte KeyNumber,byte KeyPos,byte KeyVersion){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PiccWriSl1KeyToAv2(key , KeyNumber, KeyPos, KeyVersion);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title:  PiccMfulActivateCard
     * @Description: Activate ultralight Card
     *
     * @param :None
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.22
    synchronized public int PiccMfulActivateCard(){
        if (mPosService != null) {
            try {
                return mPosService.Mful_ActivateCard();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfulPwdAuth
     * @Description: Check  ultralight Card password
     *
     * @param :
     * Key :4Byte key;
     * Pack:2Byte;
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.23
    synchronized public int PiccMfulPwdAuth(byte[] Key,byte[] Pack ){
        if (mPosService != null) {
            try {
                return mPosService.Mful_PwdAuth( Key, Pack );
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfulRead
     * @Description: Read data for ultralight Card
     *
     * @param :
     * PageNum :  the page munber that would be read
     * Data    :  16Byte data;
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.24
    synchronized public int PiccMfulRead(int PageNum,byte[] Data){
        if (mPosService != null) {
            try {
                return mPosService.Mful_Read(PageNum, Data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfulWrite
     * @Description: write data to ultralight Card
     *
     * @param :
     * PageNum :  the page munber that would be written
     * Data    :  16Byte data;
     * note:Write single page (4 bytes), 12 bytes ignored
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.25
    synchronized public int PiccMfulWrite(int PageNum,byte[] Data){
        if (mPosService != null) {
            try {
                return mPosService.Mful_Write(PageNum, Data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfulReadSign
     * @Description: read sign from ultralight Card
     *
     * @param :
     * Addr : 0
     * Signature : the 32Byte signature data readed from ultralight Card
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.26
    synchronized public int PiccMfulReadSign(int Addr,byte[] Signature){
        if (mPosService != null) {
            try {
                return mPosService.Mful_ReadSign( Addr, Signature);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfulReadCnt
     * @Description: read counter from ultralight Card
     *
     * @param :
     * CntNum :  the counter number that would be readed
     * Counter : the 4Byte counter data readed from ultralight Card
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.27
    synchronized public int PiccMfulReadCnt(int CntNum,byte[] Counter){
        if (mPosService != null) {
            try {
                return mPosService.Mful_ReadCnt(CntNum, Counter);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfulIncrCnt
     * @Description: Increase counter to ultralight Card
     *
     * @param :
     * CntNum :   the number that to be increased
     * IncValue : the 4Byte data (Little endian mode)
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.28
    synchronized public int PiccMfulIncrCnt(int CntNum, byte[] IncValue){
        if (mPosService != null) {
            try {
                return mPosService.Mful_IncrCnt(CntNum, IncValue);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title:  PiccMfpActivateCard
     * @Description: Activate mifare plus card
     *
     * @param :
     * None
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.29
    synchronized public int PiccMfpActivateCard(){
        if (mPosService != null) {
            try {
                return mPosService.Mfp_ActivateCard();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title:  PiccMfpWritePerso
     * @Description: Performs a MIFARE Plus Write Perso command.
     *
     * @param :
     * wBlockNr:	MIFARE Plus Block number.
     * pValue : 	Value (16 bytes).
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.30
    synchronized public int PiccMfpWritePerso(short wBlockNr,byte[] jaValue){
        if (mPosService != null) {
            try {
                return mPosService.Mfp_WritePerso(wBlockNr,jaValue);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title:  PiccMfpCommitPerso
     * @Description: Performs a MIFARE Plus Commit Perso command.
     *
     * @param :
     * None
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.31
    synchronized public int PiccMfpCommitPerso(){
        if (mPosService != null) {
            try {
                return mPosService.Mfp_CommitPerso();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PiccMfpKeyStore
     * @Description: Performs a MIFARE Plus key store
     *
     * @param :
     * pbNewKey: key data
     * ucNewKeyLen : 6 bytes for key A + 6 bytes for key B
     *               AES128:16Bytes
     *               DES:8Bytes
     *               2K3DES:16Bytes
     *               3K3DES:24Bytes
     *
     * ucKeyStoreKeyType:
     *              AES128=>0x00
     *              DES=>0x03
     *              2K3DES=> 0x04
     *              3K3DES=> 0x05
     *              MIFAREKEY=>0x06;
     *
     * ucKeyNumber :the index of key
     * ucKeyVersion:the version of key
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.32
    synchronized public int PiccMfpKeyStore(byte[] pbNewKey,byte ucNewKeyLen,byte ucKeyStoreKeyType,byte ucKeyNumber,byte ucKeyVersion){
        if (mPosService != null) {
            try {
                return mPosService.Mfp_KeyStore(pbNewKey,ucNewKeyLen,ucKeyStoreKeyType,ucKeyNumber,ucKeyVersion);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfpAuthenticateSL
     * @Description: Performs a complete MIFARE Plus Authentication for Security Leve
     *
     * @param :
     * bSLNum		0/1/2/3 corresponds to sl0/sl1/sl2/sl3
     * bFirstAuth	0: Following Authentication; 1: First Authentication;
     * wBlockNr	Key Block number.
     * wKeyNumber	Key Storage number.
     * wKeyVersion	Key Storage version.
     * bLenDivInput	Length of diversification input used to diversify the key. If 0, no diversification is performed.
     * pDivInput	Diversification Input used to diversify the key.
     * bLenPcdCap2	Length of the supplied PCDCaps.
     * pPcdCap2In	Pointer to PCDCaps (bLenPcdCap2 bytes), ignored if bLenPcdCap2 == 0.
     * pPcdCap2Out	Pointer to PCD Caps sent from the card (6 bytes).
     * pPdCap2	Pointer to PDCaps sent from the card (6 bytes).
     * pKmf	MIFARE Plus Sector Key Modifier (6 bytes).it is only suitable for sl2
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.33
    synchronized public int PiccMfpAuthenticateSL(byte bSLNum,byte bFirstAuth,int wBlockNr,int wKeyNumber,int wKeyVersion,byte bLenDivInput,
                                                  byte[] jaDivInput,
                                                  byte bLenPcdCap2,
                                                  byte[] jaPcdCap2In,
                                                  byte[] jaPcdCap2Out,
                                                  byte[] jaPdCap2,
                                                  byte[] jaKmf){
        if (mPosService != null) {
            try {
                return mPosService.Mfp_AuthenticateSL(bSLNum,bFirstAuth,wBlockNr,wKeyNumber,wKeyVersion,bLenDivInput,jaDivInput,bLenPcdCap2,jaPcdCap2In,jaPcdCap2Out,jaPdCap2,jaKmf);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfpAuthenticateClassicSL2
     * @Description: Performs a complete MIFARE Plus Authentication for Security Leve
     *
     * @param :
     * bBlockNo:	Block number on Card to authenticate to.
     * bKeyType:	Either /ref PHHAL_HW_MFC_KEYA or /ref PHHAL_HW_MFC_KEYB.
     * wKeyNumber:	Key number to be used in authentication.
     * wKeyVersion:	Key version to be used in authentication
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.34
    synchronized public int PiccMfpAuthenticateClassicSL2(byte bBlockNo,byte bKeyType,int wKeyNumber,int wKeyVersion) {
        if (mPosService != null) {
            try {
                return mPosService.Mfp_AuthenticateClassicSL2(bBlockNo, bKeyType, wKeyNumber, wKeyVersion);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfpMultiBlockRead
     * @Description: Performs a Multi Block Read command
     *
     * @param :
     * bBlockNr:	MIFARE Plus block number.
     * bNumBlocks:	Number of blocks to read (must not be more than 3).
     * pBlocks: 	Block(s) (16u*bNumBlocks bytes).
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.35
    synchronized public int PiccMfpMultiBlockRead(byte bBlockNr,byte bNumBlocks,byte[] jaBlocks) {
        if (mPosService != null) {
            try {
                return mPosService.Mfp_MultiBlockRead(bBlockNr, bNumBlocks, jaBlocks);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfpMultiBlockWrite
     * @Description: Performs a Multi Block write command
     *
     * @param :
     * bBlockNr:	MIFARE Plus block number.
     * bNumBlocks:	Number of blocks to read (must not be more than 3).
     * pBlocks: 	Block(s) (16u*bNumBlocks bytes).
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.36
    synchronized public int PiccMfpMultiBlockWrite(byte bBlockNr,byte bNumBlocks,byte[] jaBlocks) {
        if (mPosService != null) {
            try {
                return mPosService.Mfp_MultiBlockWrite(bBlockNr, bNumBlocks, jaBlocks);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfpWrite
     * @Description: Performs a Write / Write MACed command.
     *
     * @param :
     * bEncrypted	0: Plain communication; 1: Encrypted communication;
     * bWriteMaced	0: No MAC on response; 1: MAC on response;
     * wBlockNr	MIFARE Plus block number.
     * bNumBlocks	Number of blocks to write (must not be more than 3).
     * pBlocks	Block(s) (16u*bNumBlocks bytes).
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.37
    synchronized  public int PiccMfpWrite(byte bEncrypted,byte	bWriteMaced,int	wBlockNr,byte bNumBlocks,byte[] jaBlocks){
        if (mPosService != null) {
            try {
                return mPosService.Mfp_Write(bEncrypted, bWriteMaced, wBlockNr,bNumBlocks,jaBlocks);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfpWriteValue
     * @Description: Performs a Write / Write MACed command of a value.
     *
     * @param :
     * bEncrypted	0: Plain communication; 1: Encrypted communication;
     * bWriteMaced	0: No MAC on response; 1: MAC on response;
     * wBlockNr	MIFARE Plus block number.
     * pValue	pValue[4] containing value (LSB first) read from the MIFARE Plus card
     * bAddrData	bAddrData containing address written to the MIFARE Plus card value block.
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.38
    synchronized  public int PiccMfpWriteValue(byte bEncrypted,byte	bWriteMaced,int	wBlockNr,byte[] jaBlocks,byte bAddrData){
        if (mPosService != null) {
            try {
                return mPosService.Mfp_WriteValue(bEncrypted, bWriteMaced, wBlockNr,jaBlocks,bAddrData);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfpChangeKey
     * @Description: Performs a Key Change of a MIFARE Plus key.
     *
     * @param :
     * bChangeKeyMaced	0: No MAC on response; 1: MAC on response;
     * wBlockNr	MIFARE Plus block number.
     * wKeyNumber	Key number of key to be written to the card.
     * wKeyVersion	Key version of key to be written to the card.
     * bLenDivInput	Length of diversification input used to diversify the key. If 0, no diversification is performed.
     * pDivInput	Diversification Input used to diversify the key.
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.39
    synchronized  public int PiccMfpChangeKey(byte bChangeKeyMaced,int wBlockNr,int	wKeyNumber,int wKeyVersion,byte bLenDivInput,byte[] jaDivInput){
        if (mPosService != null) {
            try {
                return mPosService.Mfp_ChangeKey(bChangeKeyMaced, wBlockNr, wKeyNumber,wKeyVersion,bLenDivInput,jaDivInput);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfpRead
     * @Description: Performs a Read / Read MACed command.
     *
     * @param :
     * bEncrypted	0: Plain communication; 1: Encrypted communication;
     * bReadMaced	0: No MAC on response; 1: MAC on response;
     * bMacOnCmd	0: No MAC on command; 1: MAC on command;
     * wBlockNr	MIFARE Plus block number.
     * bNumBlocks	Number of blocks to read.
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.40
    synchronized  public int PiccMfpRead(byte bEncrypted,byte bReadMaced,byte bMacOnCmd,int	wBlockNr,byte bNumBlocks,byte[]	jaBlocks){
        if (mPosService != null) {
            try {
                return mPosService.Mfp_Read(bEncrypted, bReadMaced, bMacOnCmd,wBlockNr,bNumBlocks,jaBlocks);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfpReadValue
     * @Description: Performs a Read / Read MACed Value command.
     *
     * @param :
     * bEncrypted	0: Plain communication; 1: Encrypted communication;
     * bReadMaced	0: No MAC on response; 1: MAC on response;
     * bMacOnCmd	0: No MAC on command; 1: MAC on command;
     * wBlockNr	MIFARE Plus block number.
     * pValue	pValue[4] containing value (LSB first) read from the MIFARE Plus card
     * pAddrData	bAddrData containing address read from the MIFARE Plus card value  block
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.41
    synchronized  public int PiccMfpReadValue(int bEncrypted,byte bReadMaced,byte bMacOnCmd,int	wBlockNr,byte[] jaValue,byte[] jaAddrData){
        if (mPosService != null) {
            try {
                return mPosService.Mfp_ReadValue(bEncrypted, bReadMaced, bMacOnCmd,wBlockNr,jaValue,jaAddrData);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfpResetAuth
     * @Description: Performs a Reset Auth command.
     *
     * @param : None
     *
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.42
    synchronized  public int PiccMfpResetAuth(){
        if (mPosService != null) {
            try {
                return mPosService.Mfp_ResetAuth();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfpProximityCheck
     * @Description: Performs the whole Proximity Check command chain.
     *
     * @param :
     *     * Input:
     *         [in]	bGenerateRndC	0: RndC provided; 1: generate RndC;
     *         [in]	pRndC	Provided RndC (7 bytes), ignored if bGenerateRndC == 1.
     *         [in]	bPps1	Communication Speed byte (PPS1).
     *         [in]	bNumSteps	Number of ProximityCheck cycles; RFU, must be 1.
     *      * Output:
     *          [out]	pUsedRndC	Used RndC (7 bytes), can be NULL.
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.42
    synchronized  public int PiccMfpProximityCheck(int bGenerateRndC,byte[] jaRndC,byte bPps1,byte bNumSteps,byte[] jaUsedRndC){
        if (mPosService != null) {
            try {
                return mPosService.Mfp_ProximityCheck(bGenerateRndC,jaRndC,bPps1,bNumSteps,jaUsedRndC);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccMfpResetSecMsgState
     * @Description: Reset the libraries internal secure messaging state.
     *
     * @param : None
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.43
    synchronized  public int PiccMfpResetSecMsgState(){
        if (mPosService != null) {
            try {
                return mPosService.Mfp_ResetSecMsgState();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccSamOpen
     * @Description: Activate psam and obtain atr(you should in samav2 mode)
     *
     * @param :
     *      * Input: 	SamSlotNo :1-sam1,2-sam2;
     *      * Output:
     *                 SamAtr:	ATR[0] => ATR length;
     *                          ATR[1] => ATR data;
     *                          ATR[2] => ATR data;
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.44
    synchronized  public int PiccSamOpen(int SamSlotNo,byte[] SamAtr){
        if (mPosService != null) {
            try {
                return mPosService.Picc_SamOpen(SamSlotNo,SamAtr);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  PiccSamClose
     * @Description:
     *
     * @param :
     *      * Input: 	SamSlotNo :1-sam1,2-sam2;
     * @return
     * 0 :successful
     * Other: fail
     *
     */
    //9.0.45
    synchronized  public int PiccSamClose(int SamSlotNo){
        if (mPosService != null) {
            try {
                return mPosService.Picc_SamClose(SamSlotNo);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /*---------------------------  EMVCO APIs -------------------------------------------------------*/
    /**
     * @Title:  EmvEnvParaInit
     * @Description: Initialize EMV operating environment parameter
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.1
//    synchronized public int EmvEnvParaInit(){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvParaInit();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvKernelInit
     * @Description:  Intialize EMV kernel parameters,Through EmvAddOneAIDS and EmvSaveTermParas,
     *  combine the AID and the terminal parameters in a pair, which EmvKernelInit loads,
     *  Because the customer needs the AID and the terminal parameters to be matched into a combination,
     *  so that different combination functions can be realized
     * @param: None
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.2
//    synchronized public int EmvKernelInit(){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvContextInit(54, 3);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvClearAllAIDS
     * @Description: Clear all AIDs from emv kernel
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.3
//    synchronized public int EmvClearAllAIDS(){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvClearAllAIDS();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvClearOneAIDS
     * @Description: Clear an AID from emv kernel
     * @param:
     * 1. Input: TAG 9F06 ’s Value
     * 2. InLen: Value’s length
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.4
//    synchronized public int EmvClearOneAIDS(byte[] Input, int InLen){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvClearOneAIDS(Input, InLen);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvAddOneAIDS
     * @Description: add an AID to emv kernel
     * @param:
     * Input: AID TLV formatted data
     * InLen: AID TLV formatted data length
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.5
//    synchronized public int EmvAddOneAIDS( byte[] Input, int InLen){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvAddOneAIDS(Input, InLen);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvClearAllCapks
     * @Description: clear all Capks from emv kernel
     * @param: None
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.6
//    synchronized public int EmvClearAllCapks(){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvClearAllCapks();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvClearOneCapks
     * @Description: Clear a public key from kernel
     * @param:
     * Input: Input CAPK identifier by TLV format
     * InLen: Input length
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.7
//    synchronized public int EmvClearOneCapks( byte[] Input, int InLen){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvClearOneCapks(Input,  InLen);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvAddOneCAPK
     * @Description: add an AID to emv kernel
     * @param:
     * Input: Input CAPK identifier by TLV format
     * InLen: Input length
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.8
//    synchronized public int EmvAddOneCAPK( byte[] Input, int InLen){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvAddOneCAPK(Input,  InLen);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvSaveTermParas
     * @Description: Save terminal parameters after process
     * Each set of AIDs and terminal parameters forms a combination
     *
     * @param:
     * Input: input data by TLV format
     * InLen: Input length
     * Aid_index: 0---Alternate parameter, default is 0
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.9
//    synchronized public int EmvSaveTermParas( byte[] Input, int InLen,int index){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvSaveTermParas(Input,  InLen,index);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvSetTransAmount
     * @Description: Set transfer amount
     * @param: Amount,minimum unit is cent
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.10
//    synchronized public int EmvSetTransAmount(int amount){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvSetTransAmount( amount);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvSetTransAmount
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.11
//    synchronized public int EmvSetExtAmount(int[] amount){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvSetExtAmount( amount);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvSetTransAmountBack
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.12
//    synchronized public int EmvSetTransAmountBack(int amountBack){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvSetTransAmountBack( amountBack);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvSetCardType
     * @Description: Set card type of the EMV transfer
     * @param: Card type:
     *                  1－Contact IC card
     *                  2－Contactless IC card
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.13
//    synchronized public int EmvSetCardType(int cardtype) {
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvSetCardType(cardtype) ;
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvSetTransType
     * @Description: Set transfer type
     * @param: Transfer type:
     *          1  － Goods and service
     *          2  － Balance inquiry
     *          3  － Cardholder account transfer
     *          5  － read transaction logs(NonEMV&PBOC protocol)
     *          6  － digital cash consume
     *          7  － digital cash balance inquiry
     *          8  － balance inquiry - amount can be used offline
     *          9  － digital cash credit for load
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.14
//    synchronized public int EmvSetTransType(int TransTypeNum ){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvSetTransType( TransTypeNum ) ;
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvProcess
     * @Description: Emv transfer Process
     * @param:  KernelType：
     *          1  － Contact EMV KERNEL
     *          4  － Contactless VISA Quics KERNEL
     *          FlowType：
     *          0  －  Full program =>default option
     *          1  －  Simplify program
     *          2  －  Initialize APP program
     *
     * @return:
     *      -1  －  EMV terminate
     *      1   －  transaction approved
     *      2   －  Transaction denied DENIAL
     *      3   －  Request for Online EMV_GOONLINE
     *      12  －  Offline transaction success EMV_OFFLINE_SUCCESS
     *
     */
    //12.0.15
//    synchronized public int EmvProcess(int KernelType, int FlowType){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvProcess(KernelType, FlowType);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvGetTagData
     * @Description: Get TAG Data
     * @param:
     * OutPut： Return the buffer of TAG data content
     * OutputBufSize： the buffer size of OutPut
     * tagname：       Set TAG to be obtained
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.16
//    synchronized public int EmvGetTagData(byte[]  OutPut, int OutputBufSize, int tagname){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvGetTagData(OutPut, OutputBufSize,tagname );
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvSetOnlineResult
     * @Description: Process data package returned from online flow, Parameter values are returned from the transaction server
     * @param:
     *      result： Authorization code returned to background
     *      IsSuerRespData： script data returned to background
     *      IsSuerRespDataLength：length
     *
     * @return:
     *      -1 － EMV terminate
     *      1  － transaction approved
     *      2  － Transaction denied
     *      3  － Request for Online
     *      12 － Offline transaction success
     *
     */
    //12.0.17
//    synchronized public int EmvSetOnlineResult(byte[] result, byte[] IsSuerRespData, int IsSuerRespDataLength){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvSetOnlineResult(result, IsSuerRespData,IsSuerRespDataLength );
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvGetPinBlock
     * @Description: Inject the keypad and get Pinblock
     * @param:
     *      type:
     *              0--Traditional keyboard
     *              1--Sberbank fixed keyboard
     *              2--Sberbank random keyboard
     *      pinkey_n :
     *      card_no :  Card No.
     *      mode:
     *              1: Independent keyboard PIN input mode,there is a pop-up Pin password dialog
     *              0:Transaction Kernel PIN Input Mode, Read PIN values that have been entered in the transaction kernel to calculate PINBLOCK,no password dialog.
     *      pin_block:byte[]
     *      timeout: PIN input timeout setting, unit - seconds
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.18
    synchronized public int EmvGetPinBlock(int type, int pinkey_n, byte[] card_no, byte[] mode, byte[] pin_block, int timeout){
        if (mPosService != null) {
            try {
                return mPosService.EmvGetPinBlock(type,pinkey_n,card_no,mode,pin_block,timeout);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  EmvGetDukptPinblock_T
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.19
//    synchronized public int EmvGetDukptPinblock_T(int pinkey_n, byte[] card_no, byte[] pin_block, byte[] OutKsn, byte[] PinKcv, int timeout){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvGetDukptPinblock_T(pinkey_n,card_no,pin_block,OutKsn,PinKcv,timeout);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvGetAESDukptPinblock
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.20
//    synchronized public int EmvGetAESDukptPinblock(int pinkey_n, byte[] card_no, byte[] pin_block, byte[] OutKsn, byte[] PinKcv, int timeout){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvGetAESDukptPinblock(pinkey_n,card_no,pin_block,OutKsn,PinKcv,timeout);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvGetMKSKPinblock
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.21
//    synchronized public int EmvGetMKSKPinblock(int pinkey_n, byte[] card_no, byte[] pin_block, byte[] PinKcv, int timeout){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvGetMKSKPinblock(pinkey_n,card_no,pin_block,PinKcv,timeout);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvGetAESMKSKPinblock
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.22
//    synchronized public int EmvGetAESMKSKPinblock(int pinkey_n, byte[] card_no, byte[] pin_block, byte[] PinKcv, int timeout){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvGetAESMKSKPinblock(pinkey_n,card_no,pin_block,PinKcv,timeout);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvGetDukptPinblock
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.23
    synchronized public int EmvGetDukptPinblock(int type ,int pinkey_n , byte[] card_no, byte[] pin_block, byte[] OutKsn, byte[] PinKcv, int timeout){
        if (mPosService != null) {
            try {
                return mPosService.EmvGetDukptPinblock(type,pinkey_n,card_no,pin_block,OutKsn,PinKcv,timeout);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title:  EmvGetKLKPinBlock
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.24
//    synchronized public int EmvGetKLKPinBlock(int type , int pinkey_n, byte[] card_no, byte[] mode,  byte[] pin_block, int timeout){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvGetKLKPinBlock(type,pinkey_n,card_no,mode,pin_block,timeout);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  GetDukptClearPinblock
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.25
//    synchronized public int GetDukptClearPinblock(int pinkey_n , byte[] card_no, byte[] pinclear, byte[] pin_block, byte[] OutKsn, byte[] PinKcv){
//        if (mPosService != null) {
//            try {
//                return mPosService.GetDukptClearPinblock(pinkey_n, card_no,pinclear,pin_block,OutKsn,PinKcv);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  SetAutoAddKSNDATA
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.26
//    synchronized public int SetAutoAddKSNDATA(int mode, int key_index){
//        if (mPosService != null) {
//            try {
//                return mPosService.SetAddKSNDATA( mode,  key_index);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: EntryPoint
     * @Description: Decect card kernel entry point type
     * @param: None
     * @return:
     * 0: magnetic stripe card
     * 1: Contact card-EMV kernel
     * 2: contactless card-paypass kernel
     * 3: contactless card-paywave/unionpay kernel
     * 8: contactless card-mir kernel
     *
     * @throws
     */
    //12.0.27
//    synchronized public int EntryPoint(){
//        if (mPosService != null) {
//            try {
//                return mPosService.Lib_EntryPoint();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: SetEntryModeOpen
     * @Description: Initialize card kernel entry point detection
     * @param: None
     * @return:
     *      * 0	 success
     *      * !0 fail
     * @throws
     */
    //12.0.28
//    synchronized public int SetEntryModeOpen(){
//        if (mPosService != null) {
//            try {
//                return mPosService.Lib_SetEntryModeOpen();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: SetEntryModeClose
     * @Description: Close card kernel entry point detection
     * @param: None
     * @return:
     *      * 0	 success
     *      * !0 fail
     * @throws
     */
    //12.0.29
//    synchronized public int SetEntryModeClose(){
//        if (mPosService != null) {
//            try {
//                return mPosService.Lib_SetEntryModeClose();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvPinbyPass
     * @Description:
     * @param:
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.30
//    synchronized public int EmvPinbyPass(){
//        if (mPosService != null) {
//            try {
//                short TagPinbyPass = 0x95;
//                int len;
//                byte TagPinbyPass_Data[] = new byte[56];
//                len = mPosService.EmvGetTagData(TagPinbyPass_Data, 56, TagPinbyPass);
//
//                if((TagPinbyPass_Data[2] & 0x08) == 1)
//                    return 1;
//                else
//                    return 0;
//
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//
//    }

    /**
     * @Title:  EmvFinal
     * @Description: Accomplish the transaction and power off
     * @param: None
     *
     * @return:
     * 0	success
     * !0	fail
     */
    //12.0.31
//    synchronized public int EmvFinal(){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvFinal();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title:  EmvPrePare55Field
     * @Description: 55 field data packet
     * @param:
     *
     * @return:
     * 55 domain data length
     */
    //12.0.46
//    synchronized public int EmvPrePare55Field(byte[] OutBuf, int OutBufSize){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvPrePare55Field(OutBuf,  OutBufSize);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /*---------------------------  Paypass APIs ---------------------------------------------------*/
    /**
     * @Title: PaypassKernelInit
     * @Description: Initialize Paypass operating environment paramete
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     */
    //10.0.1
//    synchronized public int PaypassKernelInit(){
//        if (mPosService != null) {
//            try {
//                return mPosService.PaypassKernelInit();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PaypassAidSet
     * @Description: Add an Aid to Paypass kernel
     *
     * @param : Aid parameter Input
     * @return
     * 0 :successful
     * Other: fail
     */
    //10.0.2
//    synchronized public int PaypassAidSet(String Aid_Input){
//        if (mPosService != null) {
//            try {
//                return mPosService.PaypassAidSet(Aid_Input);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PaypassCapkSet
     * @Description: Set the paypass Capk parameter
     *
     * @param : Capk parameter Input
     * @return
     * 0 :successful
     * Other: fail
     */
    //10.0.3
//    synchronized public int PaypassCapkSet(String Capk_Input){
//        if (mPosService != null) {
//            try {
//                return mPosService.PaypassCapkSet(Capk_Input);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PaypassKernelSet
     * @Description: Set the paypass Kernel parameter
     *
     * @param : Kernel parameter Input
     * @return
     * 0 :successful
     * Other: fail
     */
    //10.0.4
//    synchronized public int PaypassKernelSet(String Kernel_Input){
//        if (mPosService != null) {
//            try {
//                return mPosService.PaypassKernelSet(Kernel_Input);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PaypassReaderSet
     * @Description: Set the paypass Reader parameter
     *
     * @param :Reader parameter Input
     * @return
     * 0 :successful
     * Other: fail
     */
    //10.0.5
//    synchronized public int PaypassReaderSet(String Reader_Input){
//        if (mPosService != null) {
//            try {
//                return mPosService.PaypassReaderSet(Reader_Input);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PaypassTransSet
     * @Description: Input transaction content and data
     *
     * @param : Transaction content data
     * @return
     * 0 :successful
     * Other: fail
     */
    //10.0.6
//    synchronized public int PaypassTransSet(String Trans_Input){
//        if (mPosService != null) {
//            try {
//                return mPosService.PaypassTransSet(Trans_Input, 9);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PaypassTransaction
     * @Description: Processing transaction, return processing results
     *
     * @param :none
     *
     * @return:
     *      APPROVED                 0x01
     *      DECLINED                 0x02
     *      ONLINE_REQ               0x03
     *      END_APPLICATION          0x04
     *      SELECT_NEXT              0x05
     *      TRY_ANOTHER_INTERFACE    0x06
     *      TRY_AGAIN                0x07
     */
    //10.0.7
//    synchronized public int PaypassTransaction(){
//        if (mPosService != null) {
//            try {
//                return mPosService.PaypassTransaction();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PaypassGetTagValue
     * @Description: Read the Tag data
     *
     * @param :
     * 1. Output          Tag content data
     * 2. OutputBufSize   The length of the output array
     * 3. Tagname         The tag name
     *
     * @return  Return the length of the Tag content data
     *
     */
    //10.0.8
//    synchronized public int PaypassGetTagValue(byte[] OutPut, int OutputBufSize, int tagname){
//        if (mPosService != null) {
//            try {
//                return mPosService.PaypassGetTagValue(OutPut,OutputBufSize,tagname);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }


    /**
     * @Title: PayPassShowAmount
     * @Description: Paypass kernel needs to call this function to display the transaction amount in keyboard mode;
     *
     * @param : None
     * @return
     * 0 :successful
     * Other: fail
     */
    //10.0.9
//    synchronized public int PayPassShowAmount() {
//        if (mPosService != null) {
//            try {
//                String PaypssTag_data = "";
//                byte PaypassTagBuff[] = new byte[1024];
//                int TagName = 0x9F02;
//                int ExtAmount = 0;
//
//                int Data_len  = mPosService.PaypassGetTagValue(PaypassTagBuff, 1024, TagName );
//                if(Data_len >0)
//                {
//                    for (int i = 0; i < Data_len; i++) {
//                        PaypssTag_data += ByteUtil.byteToHexString(PaypassTagBuff[i] /*& 0xFF*/);
//                    }
//
//                    if (Data_len / 2 != 0) {
//                        PaypssTag_data = PaypssTag_data.substring(0, Data_len * 2);
//                    }
//                    ExtAmount = Integer.parseInt(PaypssTag_data);
//                }else
//                {
//                    ExtAmount = 0;
//                }
//                return ExtAmount;
//
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return 0;
//    }

    /**
     * @Title: PaypassFinal
     * @Description: The kernel handles the end of the transaction
     *
     * @param : None
     * @return
     * 0 :successful
     * Other: fail
     */
    //10.0.10
//    synchronized public int PaypassFinal(){
//        if (mPosService != null) {
//            try {
//                return mPosService.PaypassFinal();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /*---------------------------  Paywave APIs ---------------------------------------------------*/
    /**
     * @Title: PayWaveKernelInit
     * @Description: Initialize Paywave operating environment parameter
     *
     * @param : None
     * @return
     * 0 :successful
     * Other: fail
     */
    //11.0.1
//    synchronized public int PayWaveKernelInit(){
//        if (mPosService != null) {
//            try {
//                return mPosService.PayWaveKernelInit();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PayWaveAidSet
     * @Description: Add an AID to PayWave Kernel
     *
     * @param : Input: AID TLV formatted data
     * @return
     * 0 :successful
     * Other: fail
     */
    //11.0.2
//    synchronized public int PayWaveAidSet(String Aid_Data){
//        if (mPosService != null) {
//            try {
//                return mPosService.PayWaveDownloadAIDS(Aid_Data);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PayWaveCapkSet
     * @Description: Add an CAPK to PayWave kernel
     *
     * @param : Input: input data by TLV format
     * @return
     * 0 :successful
     * Other: fail
     */
    //11.0.3
//    synchronized public int PayWaveCapkSet(String Capk_Data){
//        if (mPosService != null) {
//            try {
//                return mPosService.PayWaveDownloadCapks(Capk_Data);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PayWaveTermSet
     * @Description: Add an Term to PayWave kernel
     *
     * @param : Input: input data by TLV format
     * @return
     * 0 :successful
     * Other: fail
     */
    //11.0.4
//    synchronized public int PayWaveTermSet(String Term_Data){
//        if (mPosService != null) {
//            try {
//                return mPosService.PayWaveDownloadTerm(Term_Data);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PayWaveTransProcess
     * @Description: Paywave transaction processing
     *
     * @param : None
     * @return
     * Transaction status code:
     *     EMV_COMPLETED = 100,                           //!< Transaction complete
     *     EMV_ACCEPTED_OFFLINE = 101,                    //!< Transaction offline accepted
     *     EMV_DENIALED_OFFLINE = 102,                    //!< Transaction offline denialed
     *     EMV_ACCEPTED_ONLINE = 103,                    //!< Transaction online accepted
     *     EMV_DENIALED_ONLINE = 104,                    //!< Transaction online denialed
     *     MV_FALLBACK = 105,                            //!< Transaction fallback
     *      EMV_FORCE_ACCEPTED = 106,                    //!< Transaction force accepted
     *
     */
    //11.0.5
//    synchronized public int PayWaveTransProcess(){
//        if (mPosService != null) {
//            try {
//                return mPosService.PayWaveTrans();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PayWaveSetTransAmount
     * @Description: Set transfer amount
     *
     * @param : Amount,minimum unit is cent
     * @return
     * 0 :successful
     * Other: fail
     */
    //11.0.6
//    synchronized public int PayWaveSetTransAmount(int Amount){
//        if (mPosService != null) {
//            try {
//                return mPosService.PayWaveSetTransAmount(Amount);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }



//    synchronized public int PayWaveSetOtherTransAmount(int Amount){
//        if (mPosService != null) {
//            try {
//                return mPosService.PayWaveSetOtherTransAmount(Amount);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }
    /**
     * @Title: PayWaveSetTransType
     * @Description: Set transfer type
     *
     * @param : Type
     ******************  Debit *********************
     *     EMV_TRANSTYPE_GOODS_SERVICES = 0x00,
     *     EMV_TRANSTYPE_CASH = 0x01,
     *     EMV_TRANSTYPE_ADJUSTMENT_DEBIT = 0x02,
     *     EMV_TRANSTYPE_CHEQUE_GUARANTEE = 0x03,
     *     EMV_TRANSTYPE_CHEQUE_VERIFICATION = 0x04,
     *     EMV_TRANSTYPE_EUROCHEQUE = 0x05,
     *     EMV_TRANSTYPE_TRAVELLER_CHEQUE = 0x06,
     *     EMV_TRANSTYPE_LETTER_OF_CREDIT = 0x07,
     *     EMV_TRANSTYPE_GLRO = 0x08,
     *     EMV_TRANSTYPE_GOODS_SERVICES_WITH_CASH = 0x09,
     *     EMV_TRANSTYPE_CASHBACK = 0x09,
     *
     ******************  Credit  *********************
     *     EMV_TRANSTYPE_RETURNS = 0x20,
     *     EMV_TRANSTYPE_DEPOSITS = 0x21,
     *     EMV_TRANSTYPE_ADJUSTMENT_CREDIT = 0x22,
     *     EMV_TRANSTYPE_CHEQUE_DEPOSIT_GUARANTEE = 0x23,
     *     EMV_TRANSTYPE_CHEQUE_DEPOSIT = 0x24,
     *
     ******************  Query service  ***************
     *     EMV_TRANSTYPE_AVAILABLE_FUNDS_INQUIRY = 0x30,
     *     EMV_TRANSTYPE_BALANCE_INQUIRY = 0x31,
     *
     ******************  Transfer service **************
     *     EMV_TRANSTYPE_TRANFER = 0x40,
     *     EMV_TRANSTYPE_PAYMENT = 0x50,
     *     EMV_TRANSTYPE_ADMINISTRATIVE = 0x66,
     *
     * @return
     * 0 :successful
     * Other: fail
     */
    //11.0.7
//    synchronized public int PayWaveSetTransType(int TransType){
//        if (mPosService != null) {
//            try {
//                return mPosService.PayWaveSetTransType(TransType);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PayWaveGetTagData
     * @Description: Get TAG Data
     *
     * @param :
     * OutPut：         the return buff for TAG data content
     * OutputBufSize:   Size of the OutPut
     * tagname:         Set TAG to be obtained
     *
     * @return: Length of the tag data
     */
    //11.0.8
//    synchronized public int PayWaveGetTagData(byte[] OutPut, int OutputBufSize, int tagname){
//        if (mPosService != null) {
//            try {
//                return mPosService.PayWaveGetTagData(OutPut,OutputBufSize,tagname);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PayWaveClearAllCapk
     * @Description: Clear all the capks data from paywave kernel
     *
     * @param : None
     * @return
     * 0 :successful
     * Other: fail
     */
    //11.0.9
//    synchronized public int PayWaveClearAllCapk(){
//        if (mPosService != null) {
//            try {
//                return mPosService.PayWaveClearAllCapk();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PayWaveClearAllTerm
     * @Description: Clear all the terminals data from paywave kernel
     *
     * @param : None
     * @return
     * 0 :successful
     * Other: fail
     */
    //11.0.10
//    synchronized public int PayWaveClearAllTerm(){
//        if (mPosService != null) {
//            try {
//                return mPosService.PayWaveClearAllTerm();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PayWaveClearAllAIDS
     * @Description: Clear all the AIDs from paywave kernel
     *
     * @param : None
     * @return
     * 0 :successful
     * Other: fail
     */
    //11.0.11
//    synchronized public int PayWaveClearAllAIDS(){
//        if (mPosService != null) {
//            try {
//                return mPosService.PayWaveClearAllAIDS();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: PayWaveFinal
     * @Description:  Accomplish the transaction and power off
     *
     * @param : None
     * @return
     * 0 :successful
     * Other: fail
     */
    //11.0.12
//    synchronized public int PayWaveFinal(){
//        if (mPosService != null) {
//            try {
//                return mPosService.PayWaveFinal();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /*---------------------------  Express APIs ----------------------------------------------------*/
    /**
     * @Title: ExpressKernelInit
     * @Description: Initialize Express operating environment parameter
     *
     * @param : None
     * @return
     * 0 :successful
     * Other: fail
     */
    //13.0.1
//    synchronized public int ExpressKernelInit(){
//        if (mPosService != null) {
//            try {
//                return mPosService.ExpressKernelInit();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: ExpressAidSet
     * @Description: Add an AID to Express Kernel
     *
     * @param : Input: AID TLV formatted data
     * @return
     * 0 :successful
     * Other: fail
     */
    //13.0.2
//    synchronized public int ExpressAidSet(String jString){
//        if (mPosService != null) {
//            try {
//                return mPosService.ExpressAidSet(jString);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: ExpressCapkSet
     * @Description: Add an CAPK to Express kernel
     *
     * @param : Input: input data by TLV format
     * @return
     * 0 :successful
     * Other: fail
     */
    //13.0.3
//    synchronized public int ExpressCapkSet(String jString){
//        if (mPosService != null) {
//            try {
//                return mPosService.ExpressCapkSet(jString);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: ExpressGenerlParamSet
     * @Description:
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     */
    //13.0.4
//    synchronized public int ExpressTerminalParamSet(String jString){
//        if (mPosService != null) {
//            try {
//                return mPosService.ExpressTerminalParamSet(jString);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: ExpressDRLParamSet
     * @Description:
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     */
    //13.0.5
//    synchronized public int ExpressDRLParamSet(String jString){
//        if (mPosService != null) {
//            try {
//                return mPosService.ExpressDRLParamSet(jString);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: ExpressCRLParamSet
     * @Description:
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     */
    //13.0.6
//    synchronized public int ExpressCRLParamSet(String jString){
//        if (mPosService != null) {
//            try {
//                return mPosService.ExpressCRLParamSet(jString);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: ExpressExcepFileParamSet
     * @Description:
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     */
    //13.0.7
//    synchronized public int ExpressExcepFileParamSet(String jString){
//        if (mPosService != null) {
//            try {
//                return mPosService.ExpressExcepFileParamSet(jString);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: ExpressKernelConfigSet
     * @Description:
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     */
//    //13.0.8
//    synchronized public int ExpressKernelConfigSet(String jString){
//        if (mPosService != null) {
//            try {
//                return mPosService.ExpressKernelConfigSet(jString);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: ExpressTransaction
     * @Description:
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     */
    //13.0.9
//    synchronized public int ExpressTransaction( ){
//        if (mPosService != null) {
//            try {
//                return mPosService.ExpressTransaction();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: ExpressSetTransactionType
     * @Description:
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     */
    //13.0.10
//    synchronized public int ExpressSetTransactionType(byte TransType){
//        if (mPosService != null) {
//            try {
//                return mPosService.ExpressSetTransactionType(TransType);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: ExpressSetTransactionAmount
     * @Description:
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     */
    //13.0.11
//    synchronized public int ExpressSetTransactionAmount(int  uiAmount, int  uiAmountOther){
//        if (mPosService != null) {
//            try {
//                return mPosService.ExpressSetTransactionAmount(uiAmount, uiAmountOther);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }


    /**
     * @Title: ExpressGetTagData
     * @Description: Get TAG Data
     *
     * @param :
     * OutPut：         the return buff for TAG data content
     * tagname:         Set TAG to be obtained
     *
     * @return: Length of the tag data
     */
    //13.0.12
//    synchronized public int ExpressGetTagData(int tagname, byte[]  OutPut){
//        if (mPosService != null) {
//            try {
//                return mPosService.ExpressGetTagData(tagname,OutPut);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: ExpressFinal
     * @Description:  Accomplish the transaction and power off
     *
     * @param : None
     * @return
     * 0 :successful
     * Other: fail
     */
    //13.0.13
//    synchronized public int ExpressFinal(){
//        if (mPosService != null) {
//            try {
//                return mPosService.ExpressOff();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }


    /*---------------------------  PCI APIs -------------------------------------------------------*/
    /**
     * @Title: PciWritePinMKey
     * @Description: Write PIN main key
     *
     * @param: @param keyNo: Key serial no., from 0~9, other value is illegal
     * @param: @param keyLen:ley length,can only be8,  16,  24,others are illegal
     * @param: @param keyData: key data
     * @param: @param mode: 0 -decrypt PINK by PIN_MK ;1 - encrypt PINK by PIN_MK
     * @return:
     * (0)	successful
     * (-7000)	keyboard Is locked
     * (-7001)	key type is wrong
     * (-7002)	Wrong Key check value
     * (-7003)	Illegal key index
     * (-7004)	Illegal key length
     * (-7005)	wrong mode
     * (-7011)	key do not exist
     * (-7012)	input key fail
     * @throws
     */
    //14.0.1
    synchronized public int PciWritePinMKey(byte keyNo, byte keyLen, byte[] keyData, byte mode){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PciWritePinMKey(keyNo, keyLen, keyData, mode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PciWriteMacMKey
     * @Description: Write MAC main key
     *
     * @param: @param keyNo: Key serial no., from 0~9, other value is illegal
     * @param: @param keyLen: ley length,can only be8,  16,  24,others are illegal
     * @param: @param keyData: key data
     * @param: @param mode: 0 -decrypt MACK  by MAC_MK ;1 - encrypt MACK  by MAC_MK
     *
     * @return:
     * (0)	successful
     * (-7000)	keyboard Is locked
     * (-7001)	key type is wrong
     * (-7002)	Wrong Key check value
     * (-7003)	Illegal key index
     * (-7004)	Illegal key length
     * (-7005)	wrong mode
     * (-7011)	key do not exist
     * (-7012)	input key fail
     * @throws
     */
    //14.0.2
    synchronized public int PciWriteMacMKey(byte keyNo, byte keyLen, byte[] keyData, byte mode){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PciWriteMacMKey(keyNo, keyLen, keyData, mode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PciWriteDesMKey
     * @Description: Write DES main key
     *
     * @param: @param keyNo: Key serial no., from 0~9, other value is illegal
     * @param: @param keyLen: ley length,can only be8,  16,  24,others are illegal
     * @param: @param keyData: key data
     * @param: @param mode: 0 -decrypt DESK  by DES_MK ;1 - encrypt DESK  by DES_MK
     *
     * @return:
     * (0)	successful
     * (-7000)	keyboard Is locked
     * (-7001)	key type is wrong
     * (-7002)	Wrong Key check value
     * (-7003)	Illegal key index
     * (-7004)	Illegal key length
     * (-7005)	wrong mode
     * (-7011)	key do not exist
     * (-7012)	input key fail
     * @throws
     */
    //14.0.3
    synchronized public int PciWriteDesMKey(byte keyNo, byte keyLen, byte[] keyData, byte mode){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PciWriteDesMKey(keyNo, keyLen, keyData, mode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PciWritePinKey
     * @Description: Write PIN exclusive key
     *
     * @param: @param keyNo: Key serial no., from 0~9, other value is illegal
     * @param: @param keyLen: ley length,can only be8,  16,  24,others are illegal
     * @param: @param keyData: key data
     * @param: @param mode: 0 -decrypt PINK by PIN_MK ;1 - encrypt PINK by PIN_MK
     * @param: @param mkeyNo: main key no., from 0~9,others is illegal
     *
     * @return:
     * (0)	successful
     * (-7000)	keyboard Is locked
     * (-7001)	key type is wrong
     * (-7002)	Wrong Key check value
     * (-7003)	Illegal key index
     * (-7004)	Illegal key length
     * (-7005)	wrong mode
     * (-7011)	key do not exist
     * (-7012)	input key fail
     * @throws
     */
    //14.0.4
    synchronized public int PciWritePinKey(byte keyNo, byte keyLen, byte[] keyData, byte mode, byte mkeyNo){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PciWritePinKey(keyNo, keyLen, keyData, mode,mkeyNo);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PciWriteMacKey
     * @Description: Write MAC exclusive key
     *
     * @param: @param keyNo: Key serial no., from 0~9, other value is illegal
     * @param: @param keyLen: ley length,can only be8,  16,  24,others are illegal
     * @param: @param keyData: key data
     * @param: @param mode: 0 -decrypt MACK by MAC_MK ;1 - encrypt MACK by MAC_MK
     * @param: @param mkeyNo: main key no., from 0~9,others is illegal
     *
     * @return:
     * (0)	successful
     * (-7000)	keyboard Is locked
     * (-7001)	key type is wrong
     * (-7002)	Wrong Key check value
     * (-7003)	Illegal key index
     * (-7004)	Illegal key length
     * (-7005)	wrong mode
     * (-7011)	key do not exist
     * (-7012)	input key fail
     * @throws
     */
    //14.0.5
    synchronized public int PciWriteMacKey(byte keyNo, byte keyLen, byte[] keyData, byte mode, byte mkeyNo){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PciWriteMacKey(keyNo, keyLen, keyData, mode,mkeyNo);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PciWriteDesKey
     * @Description: Write DES exclusive key
     *
     * @param: @param keyNo: Key serial no., from 0~9, other value is illegal
     * @param: @param keyLen: ley length,can only be8,  16,  24,others are illegal
     * @param: @param keyData: key data
     * @param: @param mode: 0 -decrypt DESK by DES_MK ;1 - encrypt DESK by DES_MK
     * @param: @param mkeyNo: main key no., from 0~9,others is illegal
     *
     * @return:
     * (0)	successful
     * (-7000)	keyboard Is locked
     * (-7001)	key type is wrong
     * (-7002)	Wrong Key check value
     * (-7003)	Illegal key index
     * (-7004)	Illegal key length
     * (-7005)	wrong mode
     * (-7011)	key do not exist
     * (-7012)	input key fail
     * @throws
     */
    //14.0.6
    synchronized public int PciWriteDesKey(byte keyNo, byte keyLen, byte[] keyData, byte mode, byte mkeyNo){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PciWriteDesKey(keyNo, keyLen, keyData, mode,mkeyNo);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PciReadKCV
     * @Description: Use key to get kcv data
     *
     * @param: @param mkey_no: mkey_no from 0~9, others are illegal
     * @param: @param key_type:
     *                          MAIN_PIN_Type = 0x0A;
     *                          MAIN_MAC_Type = 0x0B;
     *                          MAIN_DES_Type = 0x0C;
     *                          WORKKey_PIN_Type = 0x0D;
     *                          WORKKey_MAC_Type = 0x0E;
     *                          WORKKey_DES_Type = 0x0F;
     * @param: @param mkey_kcv: Kcv data
     *
     * @return:
     *   0  -- success：
     * 1010 -- No key
     * 1013 -- No decryption key
     * 1014 -- No decryption key
     * 1015 -- kcv check error
     * @throws
     */
    //14.0.7
    synchronized public int PciReadKCV(byte mkey_no, byte key_type,byte[] mkey_kcv){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PciReadKCV( mkey_no, key_type, mkey_kcv);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PciGetPin
     * @Description: Get encrypted PIN－BLOCK
     * @param: @param keyNo : PIN key index no., from 0~9, should be same AUTHPINK key index no. as it’s in authentication
     * @param: @param minLen: indicate shortest length of input data,from 4~12
     * @param: @param maxLen: indicate longest length of input data,from 4~12
     * @param: @param mode  : encryption mode：[this parameter is not used yet, support X9.8 only]
     *                        0=X9.8
     *                        1=X3.92
     * @param: @param cardNo     : indicate related card or transaction serial no
     * @param: @param pinBlock   : Return encrypted PIN block
     * @param: @param pinPasswd  : Pin returned by pin entry
     * @param: @param pin_len    : pin’s length
     * @param: @param mark       : Amount info display sign(0x00:no;0x01:yes)
     * @param: @param iAmount    : amount
     *                             Eg:(123456.00)  If amount length is less than 14,then add 0 in the end
     * @param: @param waitTimeSec: timeout waiting time, means if user do not click any button after some seconds,
     *                             then exit. (second), if it is 0, then default time is 120s
     * @param: @return
     *   (0)	success：
     * (-7000)	keyboard is locked
     * (-7001)	Wrong key type
     * (-7002)	Wrong Key check value
     * (-7003)	Illegal key index
     * (-7004)	Illegal key length
     * (-7006)	Input illegal variable length
     * (-7007)	Input cancel
     * (-7008)	Input different Keys two times
     * (-7009)	input timeout
     * (-7010)	input interval less than 30 s
     * (-7011)	Key do not exist
     * (-7015)	input wrong length
     * (-7016)	No input
     * @throws
     */
    //14.0.8
    synchronized public int PciGetPin(byte keyNo, byte minLen, byte maxLen, byte mode, byte[] cardNo,  byte[] pinBlock,  byte[] pinPasswd,byte pin_len, byte mark,  byte[] iAmount, byte waitTimeSec){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PciGetPin(keyNo, minLen,  maxLen,  mode,cardNo,  pinBlock, pinPasswd, pin_len,  mark, iAmount,  waitTimeSec);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PciGetMac
     * @Description: Get encrypted MAC message
     * @param: @param keyNo: appoint MAC key serial no., from 0~9,others are illegal
     * @param: @param inLen: the length of inData
     * @param: @param inData: count MAC message data content
     * @param: @param macOut: ReturnMAC message encryption result
     * @param: @param mode :
     *                      0＝algorithm 1
     *                      1＝algorithm 2
     *                      2＝algorithm 3
     * @param: @return
     *   (0)	success：
     * (-7000)	keyboard is locked
     * (-7001)	Wrong password type
     * (-7002)	Wrong Key check value
     * (-7003)	Illegal key index
     * (-7004)	Illegal key length
     * (-7005)	Illegal input mode
     * (-7011)	Key do not exist
     * (-7015)	input wrong length
     * @throws
     */
    //14.0.9
    synchronized public int PciGetMac(byte keyNo, int inLen,  byte[] inData,  byte[] macout, byte mode){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PciGetMac( keyNo, inLen,  inData, macout, mode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PciGetDes
     * @Description: Use des key to encrypt & decrypt data
     * @param: @param keyNo: DES key serial no., from 0~9, others are illegal
     * @param: @param inLen: the length of inData
     * @param: @param inData: data needs to be encrypted/decrypted
     * @param: @param macOut: encryption/decryption result
     * @param: @param mode :
     *                      1 –encryption
     *                      0 –decryption
     * @param: @return
     *   (0)	success：
     * (-7000)	keyboard is locked
     * (-7001)	Wrong password type
     * (-7002)	Wrong Key check value
     * (-7003)	Illegal key index
     * (-7004)	Illegal key length
     * (-7005)	Illegal input mode
     * (-7011)	Key do not exist
     * (-7015)	input wrong length
     * @throws
     */
    //14.0.10
    synchronized public int PciGetDes(byte keyNo, int inLen,  byte[] inData,  byte[] desout, byte mode){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PciGetDes( keyNo, inLen,  inData, desout, mode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: PciWriteDukptIpek
     * @Description: Write the IPEK to the secure storage area
     * @param:
     *       KeyId  : key index
     *       IpekLen: Ipek  length
     *       Ipek   : Ipek  data  input
     *       KsnLen : Ksn  length
     *       Ksn    : The  KSN  corresponding  to  the current  key
     * @return
     *   (0)	success
     * (-1001)	SEND_PACKER_ERROR
     * (-1002)	RECV_PACKER_ERROR
     * @throws
     */
    //14.0.11
    synchronized public int PciWriteDukptIpek(int KeyId,  int IpekLen,  byte[] Ipek, int KsnLen,  byte[] Ksn){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PciWriteDukptIpek(KeyId,  IpekLen,   Ipek,  KsnLen, Ksn);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: PciGetDukptMac
     * @Description: DUKPT algorithm calculates MAC data
     * @param:
     *       KeyId: Mac key index
     *       Mode    :
     *                  0 –MAC1 mode
     *                  1 –MAC2 mode
     *                  2 –MAC3 mode
     *                  3 –MAC4 mode
     *       Mac_data_len: Mac_data_in length
     *       Mac_data_in : MAC data input
     *       Mac_out     : Data returned after MAC calculation
     *       OutKsn      : The  KSN  corresponding  to  the current  MAC key
     *       MacKcv      :The  KCV value of  the current  MAC key
     * @return
     *   (0)	success
     * (-1001)	SEND_PACKER_ERROR
     * (-1002)	RECV_PACKER_ERROR
     * @throws
     */
    //14.0.12
    synchronized public int PciGetDukptMac(int KeyId, byte  mode, byte  Mac_data_len, byte[]  Mac_data_in, byte[] Mac_out, byte[] OutKsn, byte[] MacKcv){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PciGetDukptMac(KeyId, mode, Mac_data_len, Mac_data_in, Mac_out, OutKsn, MacKcv);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: PciGetDuktDes
     * @Description: DUKPT algorithm calculates Des data
     * @param:
     *       KeyId: Mac key index
     *       Mode    :
     *                  0 –Ksn increments by 1 after each function call
     *                  1 –Ksn does not change after each function call
     *       DesMode :  0 –encryption
     *                  1 –decrypt
     *
     *       Des_data_len: Des_data_in length
     *       Des_data_in : Des data input
     *       jIV         : 8 bytes IV value
     *       Des_out     : Data returned after Des calculation
     *       OutKsn      : The  KSN  corresponding  to  the current  Des key
     *       DesKcv      :The  KCV value of  the current  Des key
     * @return
     *   (0)	success
     * (-1001)	SEND_PACKER_ERROR
     * (-1002)	RECV_PACKER_ERROR
     * @throws
     */
    //14.0.13
    synchronized public int PciGetDuktDes(int KeyId, byte mode, byte DesMode, int  Des_data_len, byte[] Des_data_in, byte[] jIV,  byte[]  Des_out, byte[]  OutKsn, byte[] DesKcv){
        if (mPosService != null) {
            try {
                return mPosService.Lib_PciGetDuktDes(KeyId, mode, DesMode, Des_data_len, Des_data_in, jIV, Des_out,  OutKsn, DesKcv);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }
    /*--------------------------------- Payment general APIs ----------------------------------------------------*/
    /**
     * @Title: InitPaySysKernel
     * @Description: it should be called before some payment kernel APIs called
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     */
    //1x.0.1
    synchronized public int InitPaySysKernel() {
        if (mPosService != null) {
            try {
                return mPosService.InitPaySysKernel();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    /**
     * @Title: UninitPaySysKernel
     * @Description: it should be called when payment finish
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     */
    //When the paypass kernel enters an infinite loop, it can exit in the application
    //1x.0.2
//    public int UninitPaySysKernel() {
//
//        if (mPosService != null) {
//            try {
//                return mPosService.UninitPaySysKernel();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: KeyPadInit
     * @Description: initial keypad logo, the image size should greater than 600X120,
     *               use a defualt logo if this api not be called
     *
     * @param :  logo: the logo showed on the keypad, image size should greater than 600X120
     * @return
     * 0 :successful
     * Other: fail
     */
    //1x.0.3
//    synchronized public int KeyPadInit(Bitmap logo){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvKeyPadInit(logo);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: SetKeyPadTime
     * @Description: set keypad show time, unit:seconds
     *               use a default 30s if this api not be called
     *
     * @param : time_s -> keypad will gone after 'time_s' seconds
     * @return
     * 0 :successful
     * Other: fail
     */
    //1x.0.4
//    synchronized public int SetKeyPadTime(int time_s){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvSetKeyPadTime(time_s);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: SetKeyPadType
     * @Description: set keypad type, random keypad or normal keypad
     *               show a normal keypad if this api not be called
     *
     * @param : Type -> 1: show an random keypad; 0:show a normal keypad
     * @return
     * 0 :successful
     * Other: fail
     */
    //1x.0.5
//    synchronized public int SetKeyPadType(int Type){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvSetKeyPadType(Type);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: SetPinblockType
     * @Description: set PinblockType
     *
     * @param : Type -> 1: dukpt; 0:mksk
     * @return
     * 0 :successful
     * Other: fail
     */
    //1x.0.5
//    synchronized public int EmvSetPinblockType(int Type, int keyindex){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvSetPinblockType(Type, keyindex);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }



    /**
     * @Title: EmvSetKeyPadPrompt
     * @Description: set keypad Prompt
     *
     * @param : Prompt
     * @return
     * 0 :successful
     * Other: fail
     */
    //1x.0.5
    synchronized public int EmvSetKeyPadPrompt(String Prompt){
        if (mPosService != null) {
            try {
                mPosService.EmvSetKeyPadPrompt(Prompt);
                return 0;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }


    /**
     * @Title: EmvSetCurrencyCode
     * @Description: set Currency Code
     *
     * @param : Currency Code
     * @return
     * 0 :successful
     * Other: fail
     */
    //1x.0.5
    synchronized public int EmvSetCurrencyCode(String Code){
        if (mPosService != null) {
            try {
                mPosService.EmvSetCurrencyCode(Code);
                return 0;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }
    /**
     * @Title: SetPtcCounter
     * @Description:
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     */
    //1x.0.6
//    synchronized public int SetPtcCounter(int counter){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvSetPtcCounter(counter);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: SetExtTransAmount
     * @Description:
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     */
    //1x.0.7
//    synchronized public int SetExtTransAmount(){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvSetExtTransAmount();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    /**
     * @Title: SetExtPtcCounter
     * @Description:
     *
     * @param :
     * @return
     * 0 :successful
     * Other: fail
     */
    //1x.0.8
//    synchronized public int SetExtPtcCounter(){
//        if (mPosService != null) {
//            try {
//                return mPosService.EmvSetExtPtcCounter();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        return -5555;
//    }

    public int setEmvProcessCallback(IEmvProcessCallback emvProcessCallback){
        if (mPosService != null) {
            try {
                mPosService.EmvSetCallback(emvProcessCallback);
                return 0;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

//    public byte[] setPinpadLayout(PinpadLayoutEntity var1) {
//        Log.d(TAG, "setPinpadLayout ");
//        try {
//            if (var1 == null) {
//                return null;
//            } else {
//                int[] var2 = new int[60];
//                byte var3 = 0;
//                int var8 = var3 + 1;
//                var2[var3] = var1.getKey1().left;
//                var2[var8++] = var1.getKey1().top;
//                var2[var8++] = var1.getKey1().right;
//                var2[var8++] = var1.getKey1().bottom;
//                var2[var8++] = var1.getKey2().left;
//                var2[var8++] = var1.getKey2().top;
//                var2[var8++] = var1.getKey2().right;
//                var2[var8++] = var1.getKey2().bottom;
//                var2[var8++] = var1.getKey3().left;
//                var2[var8++] = var1.getKey3().top;
//                var2[var8++] = var1.getKey3().right;
//                var2[var8++] = var1.getKey3().bottom;
//                var2[var8++] = var1.getKeyCancel().left;
//                var2[var8++] = var1.getKeyCancel().top;
//                var2[var8++] = var1.getKeyCancel().right;
//                var2[var8++] = var1.getKeyCancel().bottom;
//                var2[var8++] = var1.getKey4().left;
//                var2[var8++] = var1.getKey4().top;
//                var2[var8++] = var1.getKey4().right;
//                var2[var8++] = var1.getKey4().bottom;
//                var2[var8++] = var1.getKey5().left;
//                var2[var8++] = var1.getKey5().top;
//                var2[var8++] = var1.getKey5().right;
//                var2[var8++] = var1.getKey5().bottom;
//                var2[var8++] = var1.getKey6().left;
//                var2[var8++] = var1.getKey6().top;
//                var2[var8++] = var1.getKey6().right;
//                var2[var8++] = var1.getKey6().bottom;
//                var2[var8++] = var1.getKeyClear().left;
//                var2[var8++] = var1.getKeyClear().top;
//                var2[var8++] = var1.getKeyClear().right;
//                var2[var8++] = var1.getKeyClear().bottom;
//                var2[var8++] = var1.getKey7().left;
//                var2[var8++] = var1.getKey7().top;
//                var2[var8++] = var1.getKey7().right;
//                var2[var8++] = var1.getKey7().bottom;
//                var2[var8++] = var1.getKey8().left;
//                var2[var8++] = var1.getKey8().top;
//                var2[var8++] = var1.getKey8().right;
//                var2[var8++] = var1.getKey8().bottom;
//                var2[var8++] = var1.getKey9().left;
//                var2[var8++] = var1.getKey9().top;
//                var2[var8++] = var1.getKey9().right;
//                var2[var8++] = var1.getKey9().bottom;
//                var2[var8++] = -1;
//                var2[var8++] = -1;
//                var2[var8++] = -1;
//                var2[var8++] = -1;
//                var2[var8++] = var1.getKey10().left;
//                var2[var8++] = var1.getKey10().top;
//                var2[var8++] = var1.getKey10().right;
//                var2[var8++] = var1.getKey10().bottom;
//                var2[var8++] = -1;
//                var2[var8++] = -1;
//                var2[var8++] = -1;
//                var2[var8++] = -1;
//                var2[var8++] = var1.getKeyConfirm().left;
//                var2[var8++] = var1.getKeyConfirm().top;
//                var2[var8++] = var1.getKeyConfirm().right;
//                var2[var8++] = var1.getKeyConfirm().bottom;
//
//                String result=" ";
//                for (int i = 0; i < var2.length; i++)
//                {
//                    result += " "+ var2[i];
//
//                }
//
//                if (mPosService != null) {
//                    int retCode = mPosService.KeyPad_setCustomLayout(var2);
//                    if(retCode !=0){
//                        return null;
//                    }else {
//                        byte[] digits = new byte[10];
//                        retCode = mPosService.KeyPad_getPinKeys(digits);
//                        if (retCode != 0) {
//                            return null;
//                        } else {
//                            return digits;
//                        }
//                    }
//                }
//            }
//        } catch (RemoteException var7) {
//            var7.printStackTrace();
//        }
//        return null;
//    }

    synchronized public int EmvSetInputPinCallback(int timeout, IInputPinCallback callback){
        if (mPosService != null) {
            try {
                return mPosService.EmvSetInputPinCallback(timeout,callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    synchronized public int EmvKernelPinInput(int timeout, int keyId, IInputPinCallback callback){
        if (mPosService != null) {
            try {
                return mPosService.EmvKernelPinInput(timeout, keyId, callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

//    /**
//     * Allows to confirm the PIn response
//     *
//     * @param confirm Confirm PIN
//     * @param isBypassPin is bypass pin
//     */
//    public void onSetPinInputResponse(boolean confirm, boolean isBypassPin) {
//        //todo: implement here!!
//        if (mPosService != null) {
//            try {
//                mPosService.EmvSetPinInputResponse(confirm, isBypassPin);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    public void DoOnlineOfflinePIN(boolean confirm) {
//        //todo: implement here!!
//        if (mPosService != null) {
//            try {
//                mPosService.EmvDoOnlineOfflinePIN(confirm);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    public void onSetSelAppResponse(int selection) {
//        //todo: implement here!!
//        Log.d(TAG, "app onSetSelAppResponse selection-- "+selection);
//        if (mPosService != null) {
//            try {
//                Log.d(TAG, "app onSetSelAppResponse selection---- "+selection);
//                mPosService.EmvSetSelAppResponse(selection);
//                Log.d(TAG, "app onSetSelAppResponse selection--ok ");
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    synchronized public int InitOnLinePINContext() {
        if (mPosService != null) {
            try {
                return mPosService.InitPaySysKernel();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }

    synchronized public int CallContactEmvPinblock(int pinType){
        if (mPosService != null) {
            try {
                return mPosService.ServicesCallContactEmvPinblock(pinType);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -5555;
    }
}
