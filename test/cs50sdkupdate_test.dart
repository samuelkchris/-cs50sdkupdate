import 'dart:typed_data';

import 'package:flutter_test/flutter_test.dart';
import 'package:cs50sdkupdate/cs50sdkupdate.dart';
import 'package:cs50sdkupdate/cs50sdkupdate_platform_interface.dart';
import 'package:cs50sdkupdate/cs50sdkupdate_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockCs50sdkupdatePlatform
    with MockPlatformInterfaceMixin
    implements Cs50sdkupdatePlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<void> openPicc() {
    // TODO: implement openPicc
    throw UnimplementedError();
  }

  @override
  Future<String?> getDeviceId() {
    // TODO: implement getDeviceId
    throw UnimplementedError();
  }

  @override
  Future<String?> getOSVersion() {
    // TODO: implement getOSVersion
    throw UnimplementedError();
  }

  @override
  Future<String?> piccApduCmd(List<int> pucInput) {
    // TODO: implement piccApduCmd
    throw UnimplementedError();
  }

  @override
  Future<String?> piccCheck() {
    // TODO: implement piccCheck
    throw UnimplementedError();
  }

  @override
  Future<void> piccClose() {
    // TODO: implement piccClose
    throw UnimplementedError();
  }

  @override
  Future<String?> piccCommand(List<int> apduSend) {
    // TODO: implement piccCommand
    throw UnimplementedError();
  }

  @override
  Future<String?> piccHwModeSet(int mode) {
    // TODO: implement piccHwModeSet
    throw UnimplementedError();
  }

  @override
  Future<String?> piccM1Authority(int type, int blkNo, List<int> pwd, List<int> serialNo) {
    // TODO: implement piccM1Authority
    throw UnimplementedError();
  }

  @override
  Future<String?> piccNfc(List<int> nfcDataLen, List<int> technology, List<int> nfcUid, List<int> ndefMessage) {
    // TODO: implement piccNfc
    throw UnimplementedError();
  }

  @override
  Future<String?> piccPolling() {
    // TODO: implement piccPolling
    throw UnimplementedError();
  }

  @override
  Future<bool> piccRemove() {
    // TODO: implement piccRemove
    throw UnimplementedError();
  }

  @override
  Future<String?> piccSamAv2Init(int samSlotNo, List<int> samHostKey) {
    // TODO: implement piccSamAv2Init
    throw UnimplementedError();
  }

  @override
  Future<String?> sysApiVerson() {
    // TODO: implement sysApiVerson
    throw UnimplementedError();
  }

  @override
  Future<int?> sysGetRand(List<int> rnd) {
    // TODO: implement sysGetRand
    throw UnimplementedError();
  }

  @override
  Future<int?> sysGetVersion(List<int> buf) {
    // TODO: implement sysGetVersion
    throw UnimplementedError();
  }

  @override
  Future<int?> sysLogSwitch(int level) {
    // TODO: implement sysLogSwitch
    throw UnimplementedError();
  }

  @override
  Future<int?> sysReadSN(List<int> SN) {
    // TODO: implement sysReadSN
    throw UnimplementedError();
  }

  @override
  Future<int?> sysUpdate() {
    // TODO: implement sysUpdate
    throw UnimplementedError();
  }

  @override
  Future<String?> printBarcode(String contents, int desiredWidth, int desiredHeight, String barcodeFormat) {
    // TODO: implement printBarcode
    throw UnimplementedError();
  }

  @override
  Future<String?> printBmp(Uint8List bmpData) {
    // TODO: implement printBmp
    throw UnimplementedError();
  }

  @override
  Future<String?> printCharSpace(int x) {
    // TODO: implement printCharSpace
    throw UnimplementedError();
  }

  @override
  Future<String?> printCheckStatus() {
    // TODO: implement printCheckStatus
    throw UnimplementedError();
  }

  @override
  Future<String?> printCutQrCodeStr(String contents, String printTxt, int distance, int desiredWidth, int desiredHeight, String barcodeFormat) {
    // TODO: implement printCutQrCodeStr
    throw UnimplementedError();
  }

  @override
  Future<String?> printFeedPaper(int step) {
    // TODO: implement printFeedPaper
    throw UnimplementedError();
  }

  @override
  Future<String?> printGetFont() {
    // TODO: implement printGetFont
    throw UnimplementedError();
  }

  @override
  Future<String?> printInit() {
    // TODO: implement printInit
    throw UnimplementedError();
  }

  @override
  Future<String?> printInitWithParams(int gray, int fontHeight, int fontWidth, int fontZoom) {
    // TODO: implement printInitWithParams
    throw UnimplementedError();
  }

  @override
  Future<String?> printIsCharge(int ischarge) {
    // TODO: implement printIsCharge
    throw UnimplementedError();
  }

  @override
  Future<String?> printLabLocate(int step) {
    // TODO: implement printLabLocate
    throw UnimplementedError();
  }

  @override
  Future<String?> printLogo(Uint8List logo) {
    // TODO: implement printLogo
    throw UnimplementedError();
  }

  @override
  Future<String?> printQrCode(String contents, int desiredWidth, int desiredHeight, String barcodeFormat) {
    // TODO: implement printQrCode
    throw UnimplementedError();
  }

  @override
  Future<String?> printSetAlign(int x) {
    // TODO: implement printSetAlign
    throw UnimplementedError();
  }

  @override
  Future<String?> printSetBold(int x) {
    // TODO: implement printSetBold
    throw UnimplementedError();
  }

  @override
  Future<String?> printSetFont(int asciiFontHeight, int extendFontHeight, int zoom) {
    // TODO: implement printSetFont
    throw UnimplementedError();
  }

  @override
  Future<String?> printSetGray(int nLevel) {
    // TODO: implement printSetGray
    throw UnimplementedError();
  }

  @override
  Future<String?> printSetLeftIndent(int x) {
    // TODO: implement printSetLeftIndent
    throw UnimplementedError();
  }

  @override
  Future<String?> printSetLeftSpace(int x) {
    // TODO: implement printSetLeftSpace
    throw UnimplementedError();
  }

  @override
  Future<String?> printSetLinPixelDis(int linDistance) {
    // TODO: implement printSetLinPixelDis
    throw UnimplementedError();
  }

  @override
  Future<String?> printSetLineSpace(int x) {
    // TODO: implement printSetLineSpace
    throw UnimplementedError();
  }

  @override
  Future<String?> printSetMode(int mode) {
    // TODO: implement printSetMode
    throw UnimplementedError();
  }

  @override
  Future<String?> printSetReverse(int x) {
    // TODO: implement printSetReverse
    throw UnimplementedError();
  }

  @override
  Future<String?> printSetSpace(int x, int y) {
    // TODO: implement printSetSpace
    throw UnimplementedError();
  }

  @override
  Future<String?> printSetSpeed(int iSpeed) {
    // TODO: implement printSetSpeed
    throw UnimplementedError();
  }

  @override
  Future<String?> printSetUnderline(int x) {
    // TODO: implement printSetUnderline
    throw UnimplementedError();
  }

  @override
  Future<String?> printSetVoltage(int voltage) {
    // TODO: implement printSetVoltage
    throw UnimplementedError();
  }

  @override
  Future<String?> printStart() {
    // TODO: implement printStart
    throw UnimplementedError();
  }

  @override
  Future<String?> printStep(int pixel) {
    // TODO: implement printStep
    throw UnimplementedError();
  }

  @override
  Future<String?> printStr(String str) {
    // TODO: implement printStr
    throw UnimplementedError();
  }

  @override
  Future<void> cancelPrintJob(String jobId) {
    // TODO: implement cancelPrintJob
    throw UnimplementedError();
  }

  @override
  Future<List<Map<String, dynamic>>> getAllPrintJobs() {
    // TODO: implement getAllPrintJobs
    throw UnimplementedError();
  }

  @override
  Future<void> restartPrintJob(String jobId) {
    // TODO: implement restartPrintJob
    throw UnimplementedError();
  }

  @override
  Future<void> startMonitoringPrintJobs() {
    // TODO: implement startMonitoringPrintJobs
    throw UnimplementedError();
  }



  @override
  Future<void> cancelJob(String jobId) {
    // TODO: implement cancelJob
    throw UnimplementedError();
  }

  @override
  Future<Map<String, dynamic>?> getPrintStats() {
    // TODO: implement getPrintStats
    throw UnimplementedError();
  }



  @override
  Future<String?> updatePrintProgress(int currentPage, int totalPages) {
    // TODO: implement updatePrintProgress
    throw UnimplementedError();
  }

  @override
  Future<void> initialize() {
    // TODO: implement initialize
    throw UnimplementedError();
  }

  @override
  Future<Map<String, dynamic>> retryPrintJob(String jobId) {
    // TODO: implement retryPrintJob
    throw UnimplementedError();
  }

  @override
  Future<Map<String, dynamic>> printPdf(String pdfPath) {
    // TODO: implement printPdf
    throw UnimplementedError();
  }

  @override
  Future<Map<String, dynamic>> printLastPage() {
    // TODO: implement printLastPage
    throw UnimplementedError();
  }

  @override
  Future<String?> configureScannerSettings({int? trigMode, int? scanMode, int? scanPower, int? autoEnter}) {
    // TODO: implement configureScannerSettings
    throw UnimplementedError();
  }

  @override
  // TODO: implement scanResults
  Stream<ScanResult> get scanResults => throw UnimplementedError();

  @override
  Future<String?> setScannerMode(int mode) {
    // TODO: implement setScannerMode
    throw UnimplementedError();
  }

  @override
  Future<String?> startScanner() {
    // TODO: implement startScanner
    throw UnimplementedError();
  }

  @override
  Future<String?> stopScanner() {
    // TODO: implement stopScanner
    throw UnimplementedError();
  }

  @override
  Future<String?> closeScanner() {
    // TODO: implement closeScanner
    throw UnimplementedError();
  }

  @override
  Future<String?> openScanner() {
    // TODO: implement openScanner
    throw UnimplementedError();
  }


  
}

void main() {
  final Cs50sdkupdatePlatform initialPlatform = Cs50sdkupdatePlatform.instance;

  test('$MethodChannelCs50sdkupdate is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelCs50sdkupdate>());
  });

  test('getPlatformVersion', () async {
    Cs50sdkupdate cs50sdkupdatePlugin = Cs50sdkupdate();
    MockCs50sdkupdatePlatform fakePlatform = MockCs50sdkupdatePlatform();
    Cs50sdkupdatePlatform.instance = fakePlatform;

    expect(await cs50sdkupdatePlugin.getPlatformVersion(), '42');
  });
}
