import 'dart:async';
import 'dart:typed_data';

import 'cs50sdkupdate_method_channel.dart';
import 'cs50sdkupdate_platform_interface.dart';

class Cs50sdkupdate {

  final _progressController = StreamController<Map<String, int>>.broadcast();

  Stream<Map<String, int>> get progressStream => _progressController.stream;

  Future<void> initialize() async {
    await MethodChannelCs50sdkupdate().initialize();
  }

  void updateProgress(int currentPage, int totalPages) {
    _progressController.add({
      'currentPage': currentPage,
      'totalPages': totalPages,
    });

    print('Current Page: $currentPage, Total Pages: $totalPages');
  }

  void dispose() {
    _progressController.close();
  }

  Future<String?> getPlatformVersion() {
    return Cs50sdkupdatePlatform.instance.getPlatformVersion();
  }

  Future<void> openPicc() async {
    await Cs50sdkupdatePlatform.instance.openPicc();
  }

  Future<String?> piccCheck() async {
    return await Cs50sdkupdatePlatform.instance.piccCheck();
  }

  Future<String?> piccPolling() async {
    return await Cs50sdkupdatePlatform.instance.piccPolling();
  }

  Future<String?> piccCommand(List<int> apduSend) async {
    return await Cs50sdkupdatePlatform.instance.piccCommand(apduSend);
  }

  Future<String?> piccApduCmd(List<int> pucInput) async {
    return await Cs50sdkupdatePlatform.instance.piccApduCmd(pucInput);
  }

  Future<void> piccClose() async {
    await Cs50sdkupdatePlatform.instance.piccClose();
  }

  Future<bool> piccRemove() async {
    return await Cs50sdkupdatePlatform.instance.piccRemove();
  }

  Future<String?> piccSamAv2Init(int samSlotNo, List<int> samHostKey) async {
    return await Cs50sdkupdatePlatform.instance
        .piccSamAv2Init(samSlotNo, samHostKey);
  }

  Future<String?> piccHwModeSet(int mode) async {
    return await Cs50sdkupdatePlatform.instance.piccHwModeSet(mode);
  }

  Future<String?> piccM1Authority(
      int type, int blkNo, List<int> pwd, List<int> serialNo) async {
    return await Cs50sdkupdatePlatform.instance
        .piccM1Authority(type, blkNo, pwd, serialNo);
  }

  Future<String?> piccNfc(List<int> nfcDataLen, List<int> technology,
      List<int> nfcUid, List<int> ndefMessage) async {
    return await Cs50sdkupdatePlatform.instance
        .piccNfc(nfcDataLen, technology, nfcUid, ndefMessage);
  }

  Future<int?> sysLogSwitch(int level) async {
    return await Cs50sdkupdatePlatform.instance.sysLogSwitch(level);
  }

  Future<int?> sysGetRand(List<int> rnd) async {
    return await Cs50sdkupdatePlatform.instance.sysGetRand(rnd);
  }

  Future<int?> sysUpdate() async {
    return await Cs50sdkupdatePlatform.instance.sysUpdate();
  }

  Future<int?> sysGetVersion(List<int> buf) async {
    return await Cs50sdkupdatePlatform.instance.sysGetVersion(buf);
  }

  Future<int?> sysReadSN(List<int> SN) async {
    return await Cs50sdkupdatePlatform.instance.sysReadSN(SN);
  }

  Future<String?> sysApiVerson() async {
    return await Cs50sdkupdatePlatform.instance.sysApiVerson();
  }

  Future<String?> getOSVersion() async {
    return await Cs50sdkupdatePlatform.instance.getOSVersion();
  }

  Future<String?> getDeviceId() async {
    return await Cs50sdkupdatePlatform.instance.getDeviceId();
  }

// Printing Methods
  Future<String?> printInit() {
    return Cs50sdkupdatePlatform.instance.printInit();
  }

  Future<String?> printInitWithParams(
      int gray, int fontHeight, int fontWidth, int fontZoom) {
    return Cs50sdkupdatePlatform.instance
        .printInitWithParams(gray, fontHeight, fontWidth, fontZoom);
  }

  Future<String?> printSetFont(
      int asciiFontHeight, int extendFontHeight, int zoom) {
    return Cs50sdkupdatePlatform.instance
        .printSetFont(asciiFontHeight, extendFontHeight, zoom);
  }

  Future<String?> printSetGray(int nLevel) {
    return Cs50sdkupdatePlatform.instance.printSetGray(nLevel);
  }

  Future<String?> printSetSpace(int x, int y) {
    return Cs50sdkupdatePlatform.instance.printSetSpace(x, y);
  }

  Future<String?> printGetFont() {
    return Cs50sdkupdatePlatform.instance.printGetFont();
  }

  Future<String?> printStep(int pixel) {
    return Cs50sdkupdatePlatform.instance.printStep(pixel);
  }

  Future<String?> printSetVoltage(int voltage) {
    return Cs50sdkupdatePlatform.instance.printSetVoltage(voltage);
  }

  Future<String?> printIsCharge(int ischarge) {
    return Cs50sdkupdatePlatform.instance.printIsCharge(ischarge);
  }

  Future<String?> printSetLinPixelDis(int linDistance) {
    return Cs50sdkupdatePlatform.instance.printSetLinPixelDis(linDistance);
  }

  Future<String?> printStr(String str) {
    return Cs50sdkupdatePlatform.instance.printStr(str);
  }

  Future<String?> printBmp(Uint8List bmpData) {
    print('printBmpDAta: $bmpData');
    return Cs50sdkupdatePlatform.instance.printBmp(bmpData);
  }

  Future<String?> printBarcode(String contents, int desiredWidth,
      int desiredHeight, String barcodeFormat) {
    return Cs50sdkupdatePlatform.instance
        .printBarcode(contents, desiredWidth, desiredHeight, barcodeFormat);
  }

  Future<String?> printQrCode(String contents, int desiredWidth,
      int desiredHeight, String barcodeFormat) {
    return Cs50sdkupdatePlatform.instance
        .printQrCode(contents, desiredWidth, desiredHeight, barcodeFormat);
  }

  Future<String?> printCutQrCodeStr(String contents, String printTxt,
      int distance, int desiredWidth, int desiredHeight, String barcodeFormat) {
    return Cs50sdkupdatePlatform.instance.printCutQrCodeStr(contents, printTxt,
        distance, desiredWidth, desiredHeight, barcodeFormat);
  }

  Future<String?> printStart() {
    return Cs50sdkupdatePlatform.instance.printStart();
  }

  Future<String?> printSetLeftIndent(int x) {
    return Cs50sdkupdatePlatform.instance.printSetLeftIndent(x);
  }

  Future<String?> printSetAlign(int x) {
    return Cs50sdkupdatePlatform.instance.printSetAlign(x);
  }

  Future<String?> printCharSpace(int x) {
    return Cs50sdkupdatePlatform.instance.printCharSpace(x);
  }

  Future<String?> printSetLineSpace(int x) {
    return Cs50sdkupdatePlatform.instance.printSetLineSpace(x);
  }

  Future<String?> printSetLeftSpace(int x) {
    return Cs50sdkupdatePlatform.instance.printSetLeftSpace(x);
  }

  Future<String?> printSetSpeed(int iSpeed) {
    return Cs50sdkupdatePlatform.instance.printSetSpeed(iSpeed);
  }

  Future<String?> printCheckStatus() {
    return Cs50sdkupdatePlatform.instance.printCheckStatus();
  }

  Future<String?> printFeedPaper(int step) {
    return Cs50sdkupdatePlatform.instance.printFeedPaper(step);
  }

  Future<String?> printSetMode(int mode) {
    return Cs50sdkupdatePlatform.instance.printSetMode(mode);
  }

  Future<String?> printSetUnderline(int x) {
    return Cs50sdkupdatePlatform.instance.printSetUnderline(x);
  }

  Future<String?> printSetReverse(int x) {
    return Cs50sdkupdatePlatform.instance.printSetReverse(x);
  }

  Future<String?> printSetBold(int x) {
    return Cs50sdkupdatePlatform.instance.printSetBold(x);
  }

  Future<String?> printLogo(Uint8List logo) {
    return Cs50sdkupdatePlatform.instance.printLogo(logo);
  }

  Future<String?> printLabLocate(int step) {
    return Cs50sdkupdatePlatform.instance.printLabLocate(step);
  }

  Future<void> startMonitoringPrintJobs() {
    return Cs50sdkupdatePlatform.instance.startMonitoringPrintJobs();
  }

  Future<List<Map<String, dynamic>>> getAllPrintJobs() {
    return Cs50sdkupdatePlatform.instance.getAllPrintJobs();
  }

  Future<void> cancelPrintJob(String jobId) {
    return Cs50sdkupdatePlatform.instance.cancelPrintJob(jobId);
  }

  Future<void> restartPrintJob(String jobId) {
    return Cs50sdkupdatePlatform.instance.restartPrintJob(jobId);
  }

  Future<Map<String, dynamic>>  printPdf(String pdfPath) {
    return Cs50sdkupdatePlatform.instance.printPdf(pdfPath);
  }

  Future<Map<String, dynamic>?> getPrintStats() async {
    return await Cs50sdkupdatePlatform.instance.getPrintStats();
  }

  Future<void> cancelJob(String jobId) async {
    await Cs50sdkupdatePlatform.instance.cancelJob(jobId);
  }

  Future<Map<String, dynamic>> retryPrintJob(String jobId) async {
    return await Cs50sdkupdatePlatform.instance.retryPrintJob(jobId);
  }

  Future<Map<String, dynamic>> printLastPage() async {
    return await Cs50sdkupdatePlatform.instance.printLastPage();
  }

  Future<String?> updatePrintProgress(int currentPage, int totalPages) async {
    try {
      final String? result = await Cs50sdkupdatePlatform.instance
          .updatePrintProgress(currentPage, totalPages);
      return result;
    } catch (e) {
      // print('Failed to update print progress: $e');
      return null;
    }
  }

  Stream<Map<String, dynamic>> get printProgressStream {
    return (Cs50sdkupdatePlatform.instance as MethodChannelCs50sdkupdate).printProgressStream;
  }
}
