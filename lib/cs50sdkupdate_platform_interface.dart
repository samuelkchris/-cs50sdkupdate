import 'dart:typed_data';

import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'cs50sdkupdate_method_channel.dart';

abstract class Cs50sdkupdatePlatform extends PlatformInterface {
  /// Constructs a Cs50sdkupdatePlatform.
  Cs50sdkupdatePlatform() : super(token: _token);

  static final Object _token = Object();

  static Cs50sdkupdatePlatform _instance = MethodChannelCs50sdkupdate();

  /// The default instance of [Cs50sdkupdatePlatform] to use.
  ///
  /// Defaults to [MethodChannelCs50sdkupdate].
  static Cs50sdkupdatePlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [Cs50sdkupdatePlatform] when
  /// they register themselves.
  static set instance(Cs50sdkupdatePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  // Initialize method channel handler
  Future<void> initialize() {
    throw UnimplementedError('initialize() has not been implemented.');
  }

  // System methods
  Future<String?> getPlatformVersion() {
    throw UnimplementedError('getPlatformVersion() has not been implemented.');
  }

  Future<String?> getOSVersion() {
    throw UnimplementedError('getOSVersion() has not been implemented.');
  }

  Future<String?> getDeviceId() {
    throw UnimplementedError('getDeviceId() has not been implemented.');
  }

  Future<String?> sysApiVerson() {
    throw UnimplementedError('sysApiVerson() has not been implemented.');
  }

  Future<int?> sysLogSwitch(int level) {
    throw UnimplementedError('SysLogSwitch() has not been implemented.');
  }

  Future<int?> sysGetRand(List<int> rnd) {
    throw UnimplementedError('SysGetRand() has not been implemented.');
  }

  Future<int?> sysUpdate() {
    throw UnimplementedError('SysUpdate() has not been implemented.');
  }

  Future<int?> sysGetVersion(List<int> buf) {
    throw UnimplementedError('SysGetVersion() has not been implemented.');
  }

  Future<int?> sysReadSN(List<int> SN) {
    throw UnimplementedError('SysReadSN() has not been implemented.');
  }

  // NFC/PICC methods
  Future<void> openPicc() {
    throw UnimplementedError('openPicc() has not been implemented.');
  }

  Future<String?> piccCheck() {
    throw UnimplementedError('piccCheck() has not been implemented.');
  }

  Future<String?> piccPolling() {
    throw UnimplementedError('piccPolling() has not been implemented.');
  }

  Future<String?> piccCommand(List<int> apduSend) {
    throw UnimplementedError('piccCommand() has not been implemented.');
  }

  Future<String?> piccApduCmd(List<int> pucInput) {
    throw UnimplementedError('piccApduCmd() has not been implemented.');
  }

  Future<void> piccClose() {
    throw UnimplementedError('piccClose() has not been implemented.');
  }

  Future<bool> piccRemove() {
    throw UnimplementedError('piccRemove() has not been implemented.');
  }

  Future<String?> piccSamAv2Init(int samSlotNo, List<int> samHostKey) {
    throw UnimplementedError('piccSamAv2Init() has not been implemented.');
  }

  Future<String?> piccHwModeSet(int mode) {
    throw UnimplementedError('piccHwModeSet() has not been implemented.');
  }

  Future<String?> piccM1Authority(int type, int blkNo, List<int> pwd,
      List<int> serialNo) {
    throw UnimplementedError('piccM1Authority() has not been implemented.');
  }

  Future<String?> piccNfc(List<int> nfcDataLen, List<int> technology,
      List<int> nfcUid, List<int> ndefMessage) {
    throw UnimplementedError('piccNfc() has not been implemented.');
  }

  // Printer basic methods
  Future<String?> printInit() {
    throw UnimplementedError('printInit() has not been implemented.');
  }

  Future<String?> printInitWithParams(int gray, int fontHeight, int fontWidth,
      int fontZoom) {
    throw UnimplementedError('printInitWithParams() has not been implemented.');
  }

  Future<String?> printSetFont(int asciiFontHeight, int extendFontHeight,
      int zoom) {
    throw UnimplementedError('printSetFont() has not been implemented.');
  }

  Future<String?> printSetGray(int nLevel) {
    throw UnimplementedError('printSetGray() has not been implemented.');
  }

  Future<String?> printSetSpace(int x, int y) {
    throw UnimplementedError('printSetSpace() has not been implemented.');
  }

  Future<String?> printGetFont() {
    throw UnimplementedError('printGetFont() has not been implemented.');
  }

  Future<String?> printStep(int pixel) {
    throw UnimplementedError('printStep() has not been implemented.');
  }

  Future<String?> printSetVoltage(int voltage) {
    throw UnimplementedError('printSetVoltage() has not been implemented.');
  }

  Future<String?> printIsCharge(int ischarge) {
    throw UnimplementedError('printIsCharge() has not been implemented.');
  }

  Future<String?> printSetLinPixelDis(int linDistance) {
    throw UnimplementedError('printSetLinPixelDis() has not been implemented.');
  }

  // Printer content methods
  Future<String?> printStr(String str) {
    throw UnimplementedError('printStr() has not been implemented.');
  }

  Future<String?> printBmp(Uint8List bmpData) {
    throw UnimplementedError('printBmp() has not been implemented.');
  }

  Future<String?> printBarcode(String contents, int desiredWidth,
      int desiredHeight, String barcodeFormat) {
    throw UnimplementedError('printBarcode() has not been implemented.');
  }

  Future<String?> printQrCode(String contents, int desiredWidth,
      int desiredHeight, String barcodeFormat) {
    throw UnimplementedError('printQrCode() has not been implemented.');
  }

  Future<String?> printCutQrCodeStr(String contents, String printTxt,
      int distance, int desiredWidth, int desiredHeight, String barcodeFormat) {
    throw UnimplementedError('printCutQrCodeStr() has not been implemented.');
  }

  Future<String?> printLogo(Uint8List logo) {
    throw UnimplementedError('printLogo() has not been implemented.');
  }

  // Printer formatting methods
  Future<String?> printStart() {
    throw UnimplementedError('printStart() has not been implemented.');
  }

  Future<String?> printSetLeftIndent(int x) {
    throw UnimplementedError('printSetLeftIndent() has not been implemented.');
  }

  Future<String?> printSetAlign(int x) {
    throw UnimplementedError('printSetAlign() has not been implemented.');
  }

  Future<String?> printCharSpace(int x) {
    throw UnimplementedError('printCharSpace() has not been implemented.');
  }

  Future<String?> printSetLineSpace(int x) {
    throw UnimplementedError('printSetLineSpace() has not been implemented.');
  }

  Future<String?> printSetLeftSpace(int x) {
    throw UnimplementedError('printSetLeftSpace() has not been implemented.');
  }

  Future<String?> printSetSpeed(int iSpeed) {
    throw UnimplementedError('printSetSpeed() has not been implemented.');
  }

  Future<String?> printCheckStatus() {
    throw UnimplementedError('printCheckStatus() has not been implemented.');
  }

  Future<String?> printFeedPaper(int step) {
    throw UnimplementedError('printFeedPaper() has not been implemented.');
  }

  Future<String?> printSetMode(int mode) {
    throw UnimplementedError('printSetMode() has not been implemented.');
  }

  Future<String?> printSetUnderline(int x) {
    throw UnimplementedError('printSetUnderline() has not been implemented.');
  }

  Future<String?> printSetReverse(int x) {
    throw UnimplementedError('printSetReverse() has not been implemented.');
  }

  Future<String?> printSetBold(int x) {
    throw UnimplementedError('printSetBold() has not been implemented.');
  }

  Future<String?> printLabLocate(int step) {
    throw UnimplementedError('printLabLocate() has not been implemented.');
  }

  // PDF printing methods
  Future<Map<String, dynamic>> printPdf(String pdfPath) {
    throw UnimplementedError('printPdf() has not been implemented.');
  }

  Future<String?> getPrintHistory() {
    throw UnimplementedError('getPrintHistory() has not been implemented.');
  }

  Future<void> cancelJob(String jobId) {
    throw UnimplementedError('cancelJob() has not been implemented.');
  }

  Future<Map<String, dynamic>> retryJob() {
    throw UnimplementedError('retryJob() has not been implemented.');
  }

  Future<Map<String, dynamic>> reprintDocument(String documentId) {
    throw UnimplementedError('reprintDocument() has not been implemented.');
  }

  // Scanner methods
  Future<String?> configureScannerSettings({
    int? trigMode,
    int? scanMode,
    int? scanPower,
    int? autoEnter,
  }) {
    throw UnimplementedError('configureScannerSettings() has not been implemented.');
  }

  Future<String?> startScanner() {
    throw UnimplementedError('startScanner() has not been implemented.');
  }

  Future<String?> stopScanner() {
    throw UnimplementedError('stopScanner() has not been implemented.');
  }

  Future<String?> setScannerMode(int mode) {
    throw UnimplementedError('setScannerMode() has not been implemented.');
  }

  Future<String?> openScanner() {
    throw UnimplementedError('openScanner() has not been implemented.');
  }

  Future<String?> closeScanner() {
    throw UnimplementedError('closeScanner() has not been implemented.');
  }

  // Add these to cs50sdkupdate_platform_interface.dart
// IC Card / SAM Card methods
  Future<String?> iccOpen(int slot, int vccMode, List<int> atr) {
    throw UnimplementedError('iccOpen() has not been implemented.');
  }

  Future<String?> iccClose(int slot) {
    throw UnimplementedError('iccClose() has not been implemented.');
  }

  Future<String?> iccCommand(int slot, List<int> apduSend, List<int> apduResp) {
    throw UnimplementedError('iccCommand() has not been implemented.');
  }

  Future<String?> iccCheck(int slot) {
    throw UnimplementedError('iccCheck() has not been implemented.');
  }

  Future<String?> scApduCmd(int slot, List<int> pbInApdu, int usInApduLen, List<int> pbOut, List<int> pbOutLen) {
    throw UnimplementedError('scApduCmd() has not been implemented.');
  }

// Magnetic Card methods
  Future<String?> mcrOpen() {
    throw UnimplementedError('mcrOpen() has not been implemented.');
  }

  Future<String?> mcrClose() {
    throw UnimplementedError('mcrClose() has not been implemented.');
  }

  Future<String?> mcrReset() {
    throw UnimplementedError('mcrReset() has not been implemented.');
  }

  Future<String?> mcrCheck() {
    throw UnimplementedError('mcrCheck() has not been implemented.');
  }

  Future<Map<String, dynamic>> mcrRead(int keyNo, int mode, List<int> trackBuffers) {
    throw UnimplementedError('mcrRead() has not been implemented.');
  }

// Payment general APIs
  Future<String?> initPaySysKernel() {
    throw UnimplementedError('initPaySysKernel() has not been implemented.');
  }

  Future<String?> emvSetKeyPadPrompt(String prompt) {
    throw UnimplementedError('emvSetKeyPadPrompt() has not been implemented.');
  }

  Future<String?> emvSetCurrencyCode(String code) {
    throw UnimplementedError('emvSetCurrencyCode() has not been implemented.');
  }

  Future<String?> emvSetInputPinCallback(int timeout) {
    throw UnimplementedError('emvSetInputPinCallback() has not been implemented.');
  }

  Future<String?> emvKernelPinInput(int timeout, int keyId) {
    throw UnimplementedError('emvKernelPinInput() has not been implemented.');
  }

  Future<String?> initOnLinePINContext() {
    throw UnimplementedError('initOnLinePINContext() has not been implemented.');
  }

  Future<String?> callContactEmvPinblock(int pinType) {
    throw UnimplementedError('callContactEmvPinblock() has not been implemented.');
  }

// EMVCO methods
  Future<String?> emvGetPinBlock(int type, int pinkeyN, List<int> cardNo, List<int> mode, List<int> pinBlock, int timeout) {
    throw UnimplementedError('emvGetPinBlock() has not been implemented.');
  }

  Future<String?> emvGetDukptPinblock(int type, int pinkeyN, List<int> cardNo, List<int> pinBlock, List<int> outKsn, List<int> pinKcv, int timeout) {
    throw UnimplementedError('emvGetDukptPinblock() has not been implemented.');
  }

// PCI methods
  Future<String?> pciWritePinMKey(int keyNo, int keyLen, List<int> keyData, int mode) {
    throw UnimplementedError('pciWritePinMKey() has not been implemented.');
  }

  Future<String?> pciWriteMacMKey(int keyNo, int keyLen, List<int> keyData, int mode) {
    throw UnimplementedError('pciWriteMacMKey() has not been implemented.');
  }

  Future<String?> pciWriteDesMKey(int keyNo, int keyLen, List<int> keyData, int mode) {
    throw UnimplementedError('pciWriteDesMKey() has not been implemented.');
  }

  Future<String?> pciWritePinKey(int keyNo, int keyLen, List<int> keyData, int mode, int mkeyNo) {
    throw UnimplementedError('pciWritePinKey() has not been implemented.');
  }

  Future<String?> pciWriteMacKey(int keyNo, int keyLen, List<int> keyData, int mode, int mkeyNo) {
    throw UnimplementedError('pciWriteMacKey() has not been implemented.');
  }

  Future<String?> pciWriteDesKey(int keyNo, int keyLen, List<int> keyData, int mode, int mkeyNo) {
    throw UnimplementedError('pciWriteDesKey() has not been implemented.');
  }

  Future<String?> pciReadKCV(int mKeyNo, int keyType, List<int> mKeyKcv) {
    throw UnimplementedError('pciReadKCV() has not been implemented.');
  }

  Future<String?> pciGetPin(int keyNo, int minLen, int maxLen, int mode, List<int> cardNo, List<int> pinBlock, List<int> pinPasswd, int pinLen, int mark, List<int> iAmount, int waitTimeSec) {
    throw UnimplementedError('pciGetPin() has not been implemented.');
  }

  Future<String?> pciGetMac(int keyNo, int inLen, List<int> inData, List<int> macOut, int mode) {
    throw UnimplementedError('pciGetMac() has not been implemented.');
  }

  Future<String?> pciGetDes(int keyNo, int inLen, List<int> inData, List<int> desOut, int mode) {
    throw UnimplementedError('pciGetDes() has not been implemented.');
  }

  Future<String?> pciWriteDukptIpek(int keyId, int ipekLen, List<int> ipek, int ksnLen, List<int> ksn) {
    throw UnimplementedError('pciWriteDukptIpek() has not been implemented.');
  }

  Future<String?> pciGetDukptMac(int keyId, int mode, int macDataLen, List<int> macDataIn, List<int> macOut, List<int> outKsn, List<int> macKcv) {
    throw UnimplementedError('pciGetDukptMac() has not been implemented.');
  }

  Future<String?> pciGetDukptDes(int keyId, int mode, int desMode, int desDataLen, List<int> desDataIn, List<int> iv, List<int> desOut, List<int> outKsn, List<int> desKcv) {
    throw UnimplementedError('pciGetDukptDes() has not been implemented.');
  }

  // No longer needed methods - kept for backwards compatibility
  Future<void> startMonitoringPrintJobs() async {}
  Future<List<Map<String, dynamic>>> getAllPrintJobs() async => [];
  Future<void> cancelPrintJob(String jobId) async {}
  Future<void> restartPrintJob(String jobId) async {}
  Future<Map<String, dynamic>?> getPrintStats() async => {};
  Future<Map<String, dynamic>> printLastPage() async => {'status': 'NOT_SUPPORTED'};
  Future<String?> updatePrintProgress(int currentPage, int totalPages) async => null;
}