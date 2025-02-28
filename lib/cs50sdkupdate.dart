import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/foundation.dart';

import 'cs50sdkupdate_method_channel.dart';
import 'cs50sdkupdate_platform_interface.dart';

/// Result class for print jobs
class PrintJobResult {
  final String status;
  final String message;
  final List<int>? failedPages;
  final String? documentId;

  PrintJobResult({
    required this.status,
    required this.message,
    this.failedPages,
    this.documentId,
  });

  factory PrintJobResult.fromMap(Map<String, dynamic> map) {
    return PrintJobResult(
      status: map['status'] as String,
      message: map['message'] as String,
      failedPages: map['failedPages'] != null
          ? List<int>.from(map['failedPages'])
          : null,
      documentId: map['documentId'] as String?,
    );
  }

  bool get isSuccess => status == 'SUCCESS';
  bool get isPartialSuccess => status == 'PARTIAL_SUCCESS';
  bool get isCancelled => status == 'CANCELLED';
  bool get isError => !isSuccess && !isPartialSuccess && !isCancelled;
}

/// Progress report for print operations
class PrintProgress {
  final int currentPage;
  final int totalPages;
  final String type; // 'processing', 'printing', or 'retry'

  PrintProgress({
    required this.currentPage,
    required this.totalPages,
    required this.type,
  });

  double get progressPercentage =>
      totalPages > 0 ? (currentPage / totalPages) * 100 : 0;
}

/// Main plugin class for CS50 SDK
class Cs50sdkupdate {
  // Stream controllers
  final _progressController = StreamController<PrintProgress>.broadcast();
  final _scanController = StreamController<ScanResult>.broadcast();

  // Streams
  Stream<PrintProgress> get progressStream => _progressController.stream;
  Stream<ScanResult> get scanResultsStream => _scanController.stream;

  // Initialize plugin and listeners
  Future<void> initialize() async {
    await MethodChannelCs50sdkupdate().initialize();

    // Subscribe to internal stream events
    final channelInstance = Cs50sdkupdatePlatform.instance as MethodChannelCs50sdkupdate;

    channelInstance.printProgressStream.listen((progressMap) {
      debugPrint("RECEIVED PROGRESS DATA: $progressMap");
      final type = progressMap['method'] as String? ?? 'printing';
      _progressController.add(PrintProgress(
        currentPage: progressMap['currentPage'] ?? 0,
        totalPages: progressMap['totalPages'] ?? 0,
        type: type.replaceAll('Progress', ''),
      ));
    });

    channelInstance.scanResults.listen((scanResult) {
      _scanController.add(scanResult);
    });
  }

  /// Release resources
  void dispose() {
    _progressController.close();
    _scanController.close();

    // Also dispose channel resources
    final channelInstance = Cs50sdkupdatePlatform.instance as MethodChannelCs50sdkupdate;
    channelInstance.dispose();
  }

  //
  // System Methods
  //

  Future<String?> getPlatformVersion() {
    return Cs50sdkupdatePlatform.instance.getPlatformVersion();
  }

  Future<String?> getOSVersion() {
    return Cs50sdkupdatePlatform.instance.getOSVersion();
  }

  Future<String?> getDeviceId() {
    return Cs50sdkupdatePlatform.instance.getDeviceId();
  }

  Future<String?> sysApiVerson() {
    return Cs50sdkupdatePlatform.instance.sysApiVerson();
  }

  Future<int?> sysLogSwitch(int level) {
    return Cs50sdkupdatePlatform.instance.sysLogSwitch(level);
  }

  Future<int?> sysGetRand(List<int> rnd) {
    return Cs50sdkupdatePlatform.instance.sysGetRand(rnd);
  }

  Future<int?> sysUpdate() {
    return Cs50sdkupdatePlatform.instance.sysUpdate();
  }

  Future<int?> sysGetVersion(List<int> buf) {
    return Cs50sdkupdatePlatform.instance.sysGetVersion(buf);
  }

  Future<int?> sysReadSN(List<int> SN) {
    return Cs50sdkupdatePlatform.instance.sysReadSN(SN);
  }

  //
  // NFC/PICC Card Methods
  //

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

  //
  // Printer Basic Methods
  //

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

  //
  // Printer Content Methods
  //

  Future<String?> printStr(String str) {
    return Cs50sdkupdatePlatform.instance.printStr(str);
  }

  Future<String?> printBmp(Uint8List bmpData) {
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

  Future<String?> printLogo(Uint8List logo) {
    return Cs50sdkupdatePlatform.instance.printLogo(logo);
  }

  //
  // Printer Formatting Methods
  //

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

  Future<String?> printLabLocate(int step) {
    return Cs50sdkupdatePlatform.instance.printLabLocate(step);
  }

  //
  // PDF Printing Methods
  //

  /// Print a PDF file
  Future<PrintJobResult> printPdf(String pdfPath) async {
    final result = await Cs50sdkupdatePlatform.instance.printPdf(pdfPath);
    return PrintJobResult.fromMap(result);
  }

  /// Get history of print jobs
  Future<String?> getPrintHistory() {
    return Cs50sdkupdatePlatform.instance.getPrintHistory();
  }

  /// Cancel an active print job
  Future<void> cancelJob(String jobId) async {
    await Cs50sdkupdatePlatform.instance.cancelJob(jobId);
  }

  /// Retry printing of failed pages
  Future<PrintJobResult> retryJob() async {
    final result = await Cs50sdkupdatePlatform.instance.retryJob();
    return PrintJobResult.fromMap(result);
  }

  /// Reprint a document from history
  Future<PrintJobResult> reprintDocument(String documentId) async {
    final result = await Cs50sdkupdatePlatform.instance.reprintDocument(documentId);
    return PrintJobResult.fromMap(result);
  }

  //
  // Scanner Methods
  //

  /// Configure scanner settings
  static Future<String?> configureScannerSettings({
    int? trigMode,
    int? scanMode,
    int? scanPower,
    int? autoEnter,
  }) {
    return Cs50sdkupdatePlatform.instance.configureScannerSettings(
      trigMode: trigMode,
      scanMode: scanMode,
      scanPower: scanPower,
      autoEnter: autoEnter,
    );
  }

  /// Open the scanner hardware
  static Future<String?> openScanner() {
    return Cs50sdkupdatePlatform.instance.openScanner();
  }

  /// Close the scanner hardware
  static Future<String?> closeScanner() {
    return Cs50sdkupdatePlatform.instance.closeScanner();
  }

  /// Start scanning operation
  static Future<String?> startScanner() {
    return Cs50sdkupdatePlatform.instance.startScanner();
  }

  /// Stop scanning operation
  static Future<String?> stopScanner() {
    return Cs50sdkupdatePlatform.instance.stopScanner();
  }

  /// Set scanner mode (continuous or normal)
  static Future<String?> setScannerMode(int mode) {
    return Cs50sdkupdatePlatform.instance.setScannerMode(mode);
  }
}