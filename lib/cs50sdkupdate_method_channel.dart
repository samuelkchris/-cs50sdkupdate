import 'dart:async';
import 'dart:typed_data' as typed_data;

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'cs50sdkupdate_platform_interface.dart';

/// Standard error codes that match the native implementation
class Cs50ErrorCodes {
  static const String ioError = 'IO_ERROR';
  static const String hardwareError = 'HARDWARE_ERROR';
  static const String configError = 'CONFIG_ERROR';
  static const String permissionError = 'PERMISSION_ERROR';
  static const String timeoutError = 'TIMEOUT_ERROR';
  static const String invalidArgument = 'INVALID_ARGUMENT';
  static const String notSupported = 'NOT_SUPPORTED';
  static const String resourceBusy = 'RESOURCE_BUSY';
  static const String notFound = 'NOT_FOUND';
  static const String unexpectedError = 'UNEXPECTED_ERROR';
}

/// An implementation of [Cs50sdkupdatePlatform] that uses method channels.
class MethodChannelCs50sdkupdate extends Cs50sdkupdatePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('cs50sdkupdate');

  /// Stream controllers for events
  final StreamController<Map<String, dynamic>> _progressController =
  StreamController<Map<String, dynamic>>.broadcast();
  final StreamController<ScanResult> _scanController =
  StreamController<ScanResult>.broadcast();

  /// Constructor with method channel initialization
  MethodChannelCs50sdkupdate() {
    methodChannel.setMethodCallHandler(_handleMethodCall);
    debugPrint("MethodChannelCs50sdkupdate: Method channel handler set up");
  }

  /// Initialize the method channel handler
  @override
  Future<void> initialize() async {
    debugPrint("MethodChannelCs50sdkupdate: initialize() called");
    // No need to set the handler again, just return a completed future
    return Future.value();
  }

  /// Handle method calls from the native side
  Future<dynamic> _handleMethodCall(MethodCall call) async {
    debugPrint("MethodChannelCs50sdkupdate: Received method call: ${call.method} with args: ${call.arguments}");

    try {
      switch (call.method) {
        case 'processingProgress':
        case 'printingProgress':
        case 'retryProgress':
          final progressMap = Map<String, dynamic>.from(call.arguments);
          debugPrint("MethodChannelCs50sdkupdate: Adding to progress controller: $progressMap");
          _progressController.add(progressMap);
          break;

        case 'onScanResult':
          final resultMap = Map<String, dynamic>.from(call.arguments);
          _scanController.add(ScanResult(
            result: resultMap['result'] as String? ?? '',
            length: resultMap['length'] as int? ?? 0,
            encodeType: resultMap['encodeType'] as int? ?? 0,
          ));
          break;

        default:
          debugPrint("MethodChannelCs50sdkupdate: Unhandled method call: ${call.method}");
      }
    } catch (e) {
      debugPrint('MethodChannelCs50sdkupdate: Error handling method call ${call.method}: $e');
    }
    return null;
  }

  // Access to stream controllers
  Stream<Map<String, dynamic>> get printProgressStream => _progressController.stream;
  Stream<ScanResult> get scanResults => _scanController.stream;

  /// Dispose resources
  void dispose() {
    _progressController.close();
    _scanController.close();
  }

  //
  // System methods
  //

  @override
  Future<String?> getPlatformVersion() async {
    try {
      return await methodChannel.invokeMethod<String>('getPlatformVersion');
    } on PlatformException catch (e) {
      debugPrint('Error getting platform version: $e');
      return null;
    }
  }

  @override
  Future<String?> getOSVersion() async {
    try {
      return await methodChannel.invokeMethod<String>('getOSVersion');
    } on PlatformException catch (e) {
      debugPrint('Error getting OS version: $e');
      return null;
    }
  }

  @override
  Future<String?> getDeviceId() async {
    try {
      return await methodChannel.invokeMethod<String>('getDeviceId');
    } on PlatformException catch (e) {
      debugPrint('Error getting device ID: $e');
      return null;
    }
  }

  @override
  Future<String?> sysApiVerson() async {
    try {
      return await methodChannel.invokeMethod<String>('SysApiVerson');
    } on PlatformException catch (e) {
      debugPrint('Error getting system API version: $e');
      return null;
    }
  }

  @override
  Future<int?> sysLogSwitch(int level) async {
    try {
      return await methodChannel.invokeMethod<int>(
          'SysLogSwitch',
          {'level': level}
      );
    } on PlatformException catch (e) {
      debugPrint('Error setting log switch: $e');
      return null;
    }
  }

  @override
  Future<int?> sysGetRand(List<int> rnd) async {
    try {
      return await methodChannel.invokeMethod<int>(
          'SysGetRand',
          {'rnd': rnd}
      );
    } on PlatformException catch (e) {
      debugPrint('Error getting random number: $e');
      return null;
    }
  }

  @override
  Future<int?> sysUpdate() async {
    try {
      return await methodChannel.invokeMethod<int>('SysUpdate');
    } on PlatformException catch (e) {
      debugPrint('Error updating system: $e');
      return null;
    }
  }

  @override
  Future<int?> sysGetVersion(List<int> buf) async {
    try {
      return await methodChannel.invokeMethod<int>(
          'SysGetVersion',
          {'buf': buf}
      );
    } on PlatformException catch (e) {
      debugPrint('Error getting system version: $e');
      return null;
    }
  }

  @override
  Future<int?> sysReadSN(List<int> SN) async {
    try {
      return await methodChannel.invokeMethod<int>(
          'SysReadSN',
          {'SN': SN}
      );
    } on PlatformException catch (e) {
      debugPrint('Error reading serial number: $e');
      return null;
    }
  }

  //
  // NFC/PICC methods
  //

  @override
  Future<void> openPicc() async {
    try {
      await methodChannel.invokeMethod<void>('openPicc');
    } on PlatformException catch (e) {
      throw Exception('Failed to open PICC: ${e.message}');
    }
  }

  @override
  Future<String?> piccCheck() async {
    try {
      return await methodChannel.invokeMethod<String>('piccCheck');
    } on PlatformException catch (e) {
      debugPrint('Error checking PICC: $e');
      return null;
    }
  }

  @override
  Future<String?> piccPolling() async {
    try {
      return await methodChannel.invokeMethod<String>('piccPolling');
    } on PlatformException catch (e) {
      debugPrint('Error polling PICC: $e');
      return null;
    }
  }

  @override
  Future<String?> piccCommand(List<int> apduSend) async {
    try {
      final Map<String, dynamic> args = <String, dynamic>{
        'apduSend': apduSend,
      };
      final response = await methodChannel.invokeMethod<String>('piccCommand', args);
      return response;
    } on PlatformException catch (e) {
      debugPrint('Error sending PICC command: $e');
      return null;
    }
  }

  @override
  Future<String?> piccApduCmd(List<int> pucInput) async {
    try {
      return await methodChannel.invokeMethod<String>(
          'piccApduCmd',
          {'pucInput': pucInput}
      );
    } on PlatformException catch (e) {
      debugPrint('Error sending PICC APDU command: $e');
      return null;
    }
  }

  @override
  Future<void> piccClose() async {
    try {
      await methodChannel.invokeMethod<void>('piccClose');
    } on PlatformException catch (e) {
      throw Exception('Failed to close PICC: ${e.message}');
    }
  }

  @override
  Future<bool> piccRemove() async {
    try {
      return await methodChannel.invokeMethod<bool>('piccRemove') ?? false;
    } on PlatformException catch (e) {
      debugPrint('Error removing PICC: $e');
      return false;
    }
  }

  @override
  Future<String?> piccSamAv2Init(int samSlotNo, List<int> samHostKey) async {
    try {
      final Map<String, dynamic> args = <String, dynamic>{
        'samSlotNo': samSlotNo,
        'samHostKey': samHostKey,
      };
      return await methodChannel.invokeMethod<String>('piccSamAv2Init', args);
    } on PlatformException catch (e) {
      debugPrint('Error initializing SAM AV2: $e');
      return null;
    }
  }

  @override
  Future<String?> piccHwModeSet(int mode) async {
    try {
      return await methodChannel.invokeMethod<String>(
          'piccHwModeSet',
          {'mode': mode}
      );
    } on PlatformException catch (e) {
      debugPrint('Error setting PICC hardware mode: $e');
      return null;
    }
  }

  @override
  Future<String?> piccM1Authority(
      int type, int blkNo, List<int> pwd, List<int> serialNo) async {
    try {
      final Map<String, dynamic> args = <String, dynamic>{
        'type': type,
        'blkNo': blkNo,
        'pwd': pwd,
        'serialNo': serialNo,
      };
      return await methodChannel.invokeMethod<String>('piccM1Authority', args);
    } on PlatformException catch (e) {
      debugPrint('Error verifying M1 authority: $e');
      return null;
    }
  }

  @override
  Future<String?> piccNfc(List<int> nfcDataLen, List<int> technology,
      List<int> nfcUid, List<int> ndefMessage) async {
    try {
      final Map<String, dynamic> args = <String, dynamic>{
        'nfcDataLen': nfcDataLen,
        'technology': technology,
        'nfcUid': nfcUid,
        'ndefMessage': ndefMessage,
      };
      return await methodChannel.invokeMethod<String>('PiccNfc', args);
    } on PlatformException catch (e) {
      debugPrint('Error reading NFC tag: $e');
      return null;
    }
  }

  //
  // Printer methods
  //

  @override
  Future<String?> printInit() async {
    try {
      return await methodChannel.invokeMethod<String>('PrintInit');
    } on PlatformException catch (e) {
      debugPrint('Error initializing printer: $e');
      return null;
    }
  }

  @override
  Future<String?> printInitWithParams(
      int gray, int fontHeight, int fontWidth, int fontZoom) async {
    try {
      final Map<String, dynamic> args = <String, dynamic>{
        'gray': gray,
        'fontHeight': fontHeight,
        'fontWidth': fontWidth,
        'fontZoom': fontZoom,
      };
      return await methodChannel.invokeMethod<String>('PrintInitWithParams', args);
    } on PlatformException catch (e) {
      debugPrint('Error initializing printer with params: $e');
      return null;
    }
  }

  @override
  Future<String?> printSetFont(
      int asciiFontHeight, int extendFontHeight, int zoom) async {
    try {
      final Map<String, dynamic> args = <String, dynamic>{
        'asciiFontHeight': asciiFontHeight,
        'extendFontHeight': extendFontHeight,
        'zoom': zoom,
      };
      return await methodChannel.invokeMethod<String>('PrintSetFont', args);
    } on PlatformException catch (e) {
      debugPrint('Error setting printer font: $e');
      return null;
    }
  }

  @override
  Future<String?> printSetGray(int nLevel) async {
    try {
      return await methodChannel.invokeMethod<String>(
          'PrintSetGray', {'nLevel': nLevel});
    } on PlatformException catch (e) {
      debugPrint('Error setting printer gray level: $e');
      return null;
    }
  }

  @override
  Future<String?> printSetSpace(int x, int y) async {
    try {
      final Map<String, dynamic> args = <String, dynamic>{
        'x': x,
        'y': y,
      };
      return await methodChannel.invokeMethod<String>('PrintSetSpace', args);
    } on PlatformException catch (e) {
      debugPrint('Error setting printer space: $e');
      return null;
    }
  }

  @override
  Future<String?> printGetFont() async {
    try {
      return await methodChannel.invokeMethod<String>('PrintGetFont');
    } on PlatformException catch (e) {
      debugPrint('Error getting printer font: $e');
      return null;
    }
  }

  @override
  Future<String?> printStep(int pixel) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintStep', {'pixel': pixel});
    } on PlatformException catch (e) {
      debugPrint('Error setting print step: $e');
      return null;
    }
  }

  @override
  Future<String?> printSetVoltage(int voltage) async {
    try {
      return await methodChannel.invokeMethod<String>(
          'PrintSetVoltage', {'voltage': voltage});
    } on PlatformException catch (e) {
      debugPrint('Error setting printer voltage: $e');
      return null;
    }
  }

  @override
  Future<String?> printIsCharge(int ischarge) async {
    try {
      return await methodChannel.invokeMethod<String>(
          'PrintIsCharge', {'ischarge': ischarge});
    } on PlatformException catch (e) {
      debugPrint('Error setting printer charge status: $e');
      return null;
    }
  }

  @override
  Future<String?> printSetLinPixelDis(int linDistance) async {
    try {
      return await methodChannel.invokeMethod<String>(
          'PrintSetLinPixelDis', {'linDistance': linDistance});
    } on PlatformException catch (e) {
      debugPrint('Error setting line pixel distance: $e');
      return null;
    }
  }

  @override
  Future<String?> printStr(String str) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintStr', {'str': str});
    } on PlatformException catch (e) {
      debugPrint('Error printing string: $e');
      return null;
    }
  }

  @override
  Future<String?> printBmp(typed_data.Uint8List bmpData) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintBmp', {'bmpData': bmpData});
    } on PlatformException catch (e) {
      debugPrint('Error printing bitmap: $e');
      return null;
    }
  }

  @override
  Future<String?> printBarcode(String contents, int desiredWidth,
      int desiredHeight, String barcodeFormat) async {
    try {
      final Map<String, dynamic> args = <String, dynamic>{
        'contents': contents,
        'desiredWidth': desiredWidth,
        'desiredHeight': desiredHeight,
        'barcodeFormat': barcodeFormat,
      };
      return await methodChannel.invokeMethod<String>('PrintBarcode', args);
    } on PlatformException catch (e) {
      debugPrint('Error printing barcode: $e');
      return null;
    }
  }

  @override
  Future<String?> printQrCode(String contents, int desiredWidth,
      int desiredHeight, String barcodeFormat) async {
    try {
      final Map<String, dynamic> args = <String, dynamic>{
        'contents': contents,
        'desiredWidth': desiredWidth,
        'desiredHeight': desiredHeight,
        'barcodeFormat': barcodeFormat,
      };
      return await methodChannel.invokeMethod<String>('PrintQrCode_Cut', args);
    } on PlatformException catch (e) {
      debugPrint('Error printing QR code: $e');
      return null;
    }
  }

  @override
  Future<String?> printCutQrCodeStr(
      String contents,
      String printTxt,
      int distance,
      int desiredWidth,
      int desiredHeight,
      String barcodeFormat) async {
    try {
      final Map<String, dynamic> args = <String, dynamic>{
        'contents': contents,
        'printTxt': printTxt,
        'distance': distance,
        'desiredWidth': desiredWidth,
        'desiredHeight': desiredHeight,
        'barcodeFormat': barcodeFormat,
      };
      return await methodChannel.invokeMethod<String>('PrintCutQrCode_Str', args);
    } on PlatformException catch (e) {
      debugPrint('Error printing QR code with text: $e');
      return null;
    }
  }

  @override
  Future<String?> printStart() async {
    try {
      return await methodChannel.invokeMethod<String>('PrintStart');
    } on PlatformException catch (e) {
      debugPrint('Error starting print: $e');
      return null;
    }
  }

  @override
  Future<String?> printSetLeftIndent(int x) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintSetLeftIndent', {'x': x});
    } on PlatformException catch (e) {
      debugPrint('Error setting left indent: $e');
      return null;
    }
  }

  @override
  Future<String?> printSetAlign(int x) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintSetAlign', {'x': x});
    } on PlatformException catch (e) {
      debugPrint('Error setting alignment: $e');
      return null;
    }
  }

  @override
  Future<String?> printCharSpace(int x) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintCharSpace', {'x': x});
    } on PlatformException catch (e) {
      debugPrint('Error setting character space: $e');
      return null;
    }
  }

  @override
  Future<String?> printSetLineSpace(int x) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintSetLineSpace', {'x': x});
    } on PlatformException catch (e) {
      debugPrint('Error setting line space: $e');
      return null;
    }
  }

  @override
  Future<String?> printSetLeftSpace(int x) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintSetLeftSpace', {'x': x});
    } on PlatformException catch (e) {
      debugPrint('Error setting left space: $e');
      return null;
    }
  }

  @override
  Future<String?> printSetSpeed(int iSpeed) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintSetSpeed', {'iSpeed': iSpeed});
    } on PlatformException catch (e) {
      debugPrint('Error setting print speed: $e');
      return null;
    }
  }

  @override
  Future<String?> printCheckStatus() async {
    try {
      return await methodChannel.invokeMethod<String>('PrintCheckStatus');
    } on PlatformException catch (e) {
      debugPrint('Error checking printer status: $e');
      return null;
    }
  }

  @override
  Future<String?> printFeedPaper(int step) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintFeedPaper', {'step': step});
    } on PlatformException catch (e) {
      debugPrint('Error feeding paper: $e');
      return null;
    }
  }

  @override
  Future<String?> printSetMode(int mode) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintSetMode', {'mode': mode});
    } on PlatformException catch (e) {
      debugPrint('Error setting print mode: $e');
      return null;
    }
  }

  @override
  Future<String?> printSetUnderline(int x) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintSetUnderline', {'x': x});
    } on PlatformException catch (e) {
      debugPrint('Error setting underline: $e');
      return null;
    }
  }

  @override
  Future<String?> printSetReverse(int x) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintSetReverse', {'x': x});
    } on PlatformException catch (e) {
      debugPrint('Error setting reverse mode: $e');
      return null;
    }
  }

  @override
  Future<String?> printSetBold(int x) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintSetBold', {'x': x});
    } on PlatformException catch (e) {
      debugPrint('Error setting bold mode: $e');
      return null;
    }
  }

  @override
  Future<String?> printLogo(typed_data.Uint8List logo) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintLogo', {'logo': logo});
    } on PlatformException catch (e) {
      debugPrint('Error printing logo: $e');
      return null;
    }
  }

  @override
  Future<String?> printLabLocate(int step) async {
    try {
      return await methodChannel.invokeMethod<String>('PrintLabLocate', {'step': step});
    } on PlatformException catch (e) {
      debugPrint('Error locating label: $e');
      return null;
    }
  }

  // PDF printing methods

  @override
  Future<Map<String, dynamic>> printPdf(String pdfPath) async {
    try {
      final result = await methodChannel.invokeMethod('PrintPdf', {'pdfPath': pdfPath});
      return Map<String, dynamic>.from(result);
    } on PlatformException catch (e) {
      debugPrint("Failed to print PDF: '${e.message}'.");
      return {
        'status': 'ERROR',
        'message': e.message ?? 'Unknown error occurred while printing PDF'
      };
    }
  }

  @override
  Future<void> cancelJob(String jobId) async {
    try {
      await methodChannel.invokeMethod<void>('CancelJob', {'jobId': jobId});
    } on PlatformException catch (e) {
      debugPrint('Error cancelling job: $e');
      throw Exception('Failed to cancel job: ${e.message}');
    }
  }

  @override
  Future<Map<String, dynamic>> retryJob() async {
    try {
      final result = await methodChannel.invokeMethod<Map<dynamic, dynamic>>('RetryJob');
      return _convertToStringDynamicMap(result);
    } on PlatformException catch (e) {
      debugPrint('Error retrying job: $e');
      return {
        'status': 'ERROR',
        'message': e.message ?? 'Unknown error occurred while retrying'
      };
    }
  }

  @override
  Future<String?> getPrintHistory() async {
    try {
      return await methodChannel.invokeMethod<String>('getPrintHistory');
    } on PlatformException catch (e) {
      debugPrint('Error getting print history: $e');
      return null;
    }
  }

  @override
  Future<Map<String, dynamic>> reprintDocument(String documentId) async {
    try {
      final result = await methodChannel.invokeMethod('reprintDocument', {'documentId': documentId});
      return Map<String, dynamic>.from(result);
    } on PlatformException catch (e) {
      debugPrint("Failed to reprint document: '${e.message}'.");
      return {
        'status': 'ERROR',
        'message': e.message ?? 'Unknown error occurred while reprinting document'
      };
    }
  }

  // Scanner methods

  @override
  Future<String?> configureScannerSettings({
    int? trigMode,
    int? scanMode,
    int? scanPower,
    int? autoEnter,
  }) async {
    try {
      return await methodChannel.invokeMethod<String>('configureScannerSettings', {
        'trigMode': trigMode,
        'scanMode': scanMode,
        'scanPower': scanPower,
        'autoEnter': autoEnter,
      });
    } on PlatformException catch (e) {
      debugPrint('Error configuring scanner: $e');
      throw Exception('Failed to configure scanner: ${e.message}');
    }
  }

  @override
  Future<String?> startScanner() async {
    try {
      return await methodChannel.invokeMethod<String>('startScanner');
    } on PlatformException catch (e) {
      debugPrint('Error starting scanner: $e');
      throw Exception('Failed to start scanner: ${e.message}');
    }
  }

  @override
  Future<String?> stopScanner() async {
    try {
      return await methodChannel.invokeMethod<String>('stopScanner');
    } on PlatformException catch (e) {
      debugPrint('Error stopping scanner: $e');
      throw Exception('Failed to stop scanner: ${e.message}');
    }
  }

  @override
  Future<String?> setScannerMode(int mode) async {
    try {
      return await methodChannel.invokeMethod<String>('setScannerMode', {
        'mode': mode,
      });
    } on PlatformException catch (e) {
      debugPrint('Error setting scanner mode: $e');
      throw Exception('Failed to set scanner mode: ${e.message}');
    }
  }

  @override
  Future<String?> openScanner() async {
    try {
      return await methodChannel.invokeMethod<String>('openScanner');
    } on PlatformException catch (e) {
      debugPrint('Error opening scanner: $e');
      throw Exception('Failed to open scanner: ${e.message}');
    }
  }

  @override
  Future<String?> closeScanner() async {
    try {
      return await methodChannel.invokeMethod<String>('closeScanner');
    } on PlatformException catch (e) {
      debugPrint('Error closing scanner: $e');
      throw Exception('Failed to close scanner: ${e.message}');
    }
  }

  // Add these to cs50sdkupdate_method_channel.dart

// IC Card / SAM Card methods
  @override
  Future<String?> iccOpen(int slot, int vccMode, List<int> atr) async {
    try {
      return await methodChannel.invokeMethod<String>('IccOpen', {
        'slot': slot,
        'vccMode': vccMode,
        'atr': atr,
      });
    } on PlatformException catch (e) {
      debugPrint('Error opening IC card: $e');
      return null;
    }
  }

  @override
  Future<String?> iccClose(int slot) async {
    try {
      return await methodChannel.invokeMethod<String>('IccClose', {
        'slot': slot,
      });
    } on PlatformException catch (e) {
      debugPrint('Error closing IC card: $e');
      return null;
    }
  }

  @override
  Future<String?> iccCommand(int slot, List<int> apduSend, List<int> apduResp) async {
    try {
      return await methodChannel.invokeMethod<String>('IccCommand', {
        'slot': slot,
        'apduSend': apduSend,
        'apduResp': apduResp,
      });
    } on PlatformException catch (e) {
      debugPrint('Error sending ICC command: $e');
      return null;
    }
  }

  @override
  Future<String?> iccCheck(int slot) async {
    try {
      return await methodChannel.invokeMethod<String>('IccCheck', {
        'slot': slot,
      });
    } on PlatformException catch (e) {
      debugPrint('Error checking IC card: $e');
      return null;
    }
  }

  @override
  Future<String?> scApduCmd(int slot, List<int> pbInApdu, int usInApduLen, List<int> pbOut, List<int> pbOutLen) async {
    try {
      return await methodChannel.invokeMethod<String>('SC_ApduCmd', {
        'slot': slot,
        'pbInApdu': pbInApdu,
        'usInApduLen': usInApduLen,
        'pbOut': pbOut,
        'pbOutLen': pbOutLen,
      });
    } on PlatformException catch (e) {
      debugPrint('Error sending SC APDU command: $e');
      return null;
    }
  }

// Magnetic Card methods
  @override
  Future<String?> mcrOpen() async {
    try {
      return await methodChannel.invokeMethod<String>('McrOpen');
    } on PlatformException catch (e) {
      debugPrint('Error opening magnetic card reader: $e');
      return null;
    }
  }

  @override
  Future<String?> mcrClose() async {
    try {
      return await methodChannel.invokeMethod<String>('McrClose');
    } on PlatformException catch (e) {
      debugPrint('Error closing magnetic card reader: $e');
      return null;
    }
  }

  @override
  Future<String?> mcrReset() async {
    try {
      return await methodChannel.invokeMethod<String>('McrReset');
    } on PlatformException catch (e) {
      debugPrint('Error resetting magnetic card reader: $e');
      return null;
    }
  }

  @override
  Future<String?> mcrCheck() async {
    try {
      return await methodChannel.invokeMethod<String>('McrCheck');
    } on PlatformException catch (e) {
      debugPrint('Error checking magnetic card: $e');
      return null;
    }
  }

  @override
  Future<Map<String, dynamic>> mcrRead(int keyNo, int mode, List<int> trackBuffers) async {
    try {
      final result = await methodChannel.invokeMethod<Map<dynamic, dynamic>>('McrRead', {
        'keyNo': keyNo,
        'mode': mode,
        'track1': trackBuffers.sublist(0, 256),
        'track2': trackBuffers.sublist(256, 512),
        'track3': trackBuffers.sublist(512, 768),
      });
      return _convertToStringDynamicMap(result);
    } on PlatformException catch (e) {
      debugPrint('Error reading magnetic card: $e');
      return {
        'error': e.message ?? 'Unknown error occurred while reading magnetic card'
      };
    }
  }

// Payment general APIs
  @override
  Future<String?> initPaySysKernel() async {
    try {
      return await methodChannel.invokeMethod<String>('InitPaySysKernel');
    } on PlatformException catch (e) {
      debugPrint('Error initializing payment system kernel: $e');
      return null;
    }
  }

  @override
  Future<String?> emvSetKeyPadPrompt(String prompt) async {
    try {
      return await methodChannel.invokeMethod<String>('EmvSetKeyPadPrompt', {
        'prompt': prompt,
      });
    } on PlatformException catch (e) {
      debugPrint('Error setting keypad prompt: $e');
      return null;
    }
  }

  @override
  Future<String?> emvSetCurrencyCode(String code) async {
    try {
      return await methodChannel.invokeMethod<String>('EmvSetCurrencyCode', {
        'code': code,
      });
    } on PlatformException catch (e) {
      debugPrint('Error setting currency code: $e');
      return null;
    }
  }

  @override
  Future<String?> emvSetInputPinCallback(int timeout) async {
    try {
      return await methodChannel.invokeMethod<String>('EmvSetInputPinCallback', {
        'timeout': timeout,
      });
    } on PlatformException catch (e) {
      debugPrint('Error setting input PIN callback: $e');
      return null;
    }
  }

  @override
  Future<String?> emvKernelPinInput(int timeout, int keyId) async {
    try {
      return await methodChannel.invokeMethod<String>('EmvKernelPinInput', {
        'timeout': timeout,
        'keyId': keyId,
      });
    } on PlatformException catch (e) {
      debugPrint('Error starting kernel PIN input: $e');
      return null;
    }
  }

  @override
  Future<String?> initOnLinePINContext() async {
    try {
      return await methodChannel.invokeMethod<String>('InitOnLinePINContext');
    } on PlatformException catch (e) {
      debugPrint('Error initializing online PIN context: $e');
      return null;
    }
  }

  @override
  Future<String?> callContactEmvPinblock(int pinType) async {
    try {
      return await methodChannel.invokeMethod<String>('CallContactEmvPinblock', {
        'pinType': pinType,
      });
    } on PlatformException catch (e) {
      debugPrint('Error calling contact EMV PINBLOCK: $e');
      return null;
    }
  }

// EMVCO methods
  @override
  Future<String?> emvGetPinBlock(int type, int pinkeyN, List<int> cardNo, List<int> mode, List<int> pinBlock, int timeout) async {
    try {
      return await methodChannel.invokeMethod<String>('EmvGetPinBlock', {
        'type': type,
        'pinkeyN': pinkeyN,
        'cardNo': cardNo,
        'mode': mode,
        'pinBlock': pinBlock,
        'timeout': timeout,
      });
    } on PlatformException catch (e) {
      debugPrint('Error getting EMV PIN block: $e');
      return null;
    }
  }

  @override
  Future<String?> emvGetDukptPinblock(int type, int pinkeyN, List<int> cardNo, List<int> pinBlock, List<int> outKsn, List<int> pinKcv, int timeout) async {
    try {
      return await methodChannel.invokeMethod<String>('EmvGetDukptPinblock', {
        'type': type,
        'pinkeyN': pinkeyN,
        'cardNo': cardNo,
        'pinBlock': pinBlock,
        'outKsn': outKsn,
        'pinKcv': pinKcv,
        'timeout': timeout,
      });
    } on PlatformException catch (e) {
      debugPrint('Error getting EMV DUKPT PIN block: $e');
      return null;
    }
  }

// PCI methods
  @override
  Future<String?> pciWritePinMKey(int keyNo, int keyLen, List<int> keyData, int mode) async {
    try {
      return await methodChannel.invokeMethod<String>('PciWritePinMKey', {
        'keyNo': keyNo,
        'keyLen': keyLen,
        'keyData': keyData,
        'mode': mode,
      });
    } on PlatformException catch (e) {
      debugPrint('Error writing PIN main key: $e');
      return null;
    }
  }

  @override
  Future<String?> pciWriteMacMKey(int keyNo, int keyLen, List<int> keyData, int mode) async {
    try {
      return await methodChannel.invokeMethod<String>('PciWriteMacMKey', {
        'keyNo': keyNo,
        'keyLen': keyLen,
        'keyData': keyData,
        'mode': mode,
      });
    } on PlatformException catch (e) {
      debugPrint('Error writing MAC main key: $e');
      return null;
    }
  }

  @override
  Future<String?> pciWriteDesMKey(int keyNo, int keyLen, List<int> keyData, int mode) async {
    try {
      return await methodChannel.invokeMethod<String>('PciWriteDesMKey', {
        'keyNo': keyNo,
        'keyLen': keyLen,
        'keyData': keyData,
        'mode': mode,
      });
    } on PlatformException catch (e) {
      debugPrint('Error writing DES main key: $e');
      return null;
    }
  }

  @override
  Future<String?> pciWritePinKey(int keyNo, int keyLen, List<int> keyData, int mode, int mkeyNo) async {
    try {
      return await methodChannel.invokeMethod<String>('PciWritePinKey', {
        'keyNo': keyNo,
        'keyLen': keyLen,
        'keyData': keyData,
        'mode': mode,
        'mkeyNo': mkeyNo,
      });
    } on PlatformException catch (e) {
      debugPrint('Error writing PIN key: $e');
      return null;
    }
  }

  @override
  Future<String?> pciWriteMacKey(int keyNo, int keyLen, List<int> keyData, int mode, int mkeyNo) async {
    try {
      return await methodChannel.invokeMethod<String>('PciWriteMacKey', {
        'keyNo': keyNo,
        'keyLen': keyLen,
        'keyData': keyData,
        'mode': mode,
        'mkeyNo': mkeyNo,
      });
    } on PlatformException catch (e) {
      debugPrint('Error writing MAC key: $e');
      return null;
    }
  }

  @override
  Future<String?> pciWriteDesKey(int keyNo, int keyLen, List<int> keyData, int mode, int mkeyNo) async {
    try {
      return await methodChannel.invokeMethod<String>('PciWriteDesKey', {
        'keyNo': keyNo,
        'keyLen': keyLen,
        'keyData': keyData,
        'mode': mode,
        'mkeyNo': mkeyNo,
      });
    } on PlatformException catch (e) {
      debugPrint('Error writing DES key: $e');
      return null;
    }
  }

  @override
  Future<String?> pciReadKCV(int mKeyNo, int keyType, List<int> mKeyKcv) async {
    try {
      return await methodChannel.invokeMethod<String>('PciReadKCV', {
        'mKeyNo': mKeyNo,
        'keyType': keyType,
        'mKeyKcv': mKeyKcv,
      });
    } on PlatformException catch (e) {
      debugPrint('Error reading KCV: $e');
      return null;
    }
  }

  @override
  Future<String?> pciGetPin(int keyNo, int minLen, int maxLen, int mode, List<int> cardNo, List<int> pinBlock, List<int> pinPasswd, int pinLen, int mark, List<int> iAmount, int waitTimeSec) async {
    try {
      return await methodChannel.invokeMethod<String>('PciGetPin', {
        'keyNo': keyNo,
        'minLen': minLen,
        'maxLen': maxLen,
        'mode': mode,
        'cardNo': cardNo,
        'pinBlock': pinBlock,
        'pinPasswd': pinPasswd,
        'pinLen': pinLen,
        'mark': mark,
        'iAmount': iAmount,
        'waitTimeSec': waitTimeSec,
      });
    } on PlatformException catch (e) {
      debugPrint('Error getting PIN: $e');
      return null;
    }
  }

  @override
  Future<String?> pciGetMac(int keyNo, int inLen, List<int> inData, List<int> macOut, int mode) async {
    try {
      return await methodChannel.invokeMethod<String>('PciGetMac', {
        'keyNo': keyNo,
        'inLen': inLen,
        'inData': inData,
        'macOut': macOut,
        'mode': mode,
      });
    } on PlatformException catch (e) {
      debugPrint('Error getting MAC: $e');
      return null;
    }
  }

  @override
  Future<String?> pciGetDes(int keyNo, int inLen, List<int> inData, List<int> desOut, int mode) async {
    try {
      return await methodChannel.invokeMethod<String>('PciGetDes', {
        'keyNo': keyNo,
        'inLen': inLen,
        'inData': inData,
        'desOut': desOut,
        'mode': mode,
      });
    } on PlatformException catch (e) {
      debugPrint('Error performing DES operation: $e');
      return null;
    }
  }

  @override
  Future<String?> pciWriteDukptIpek(int keyId, int ipekLen, List<int> ipek, int ksnLen, List<int> ksn) async {
    try {
      return await methodChannel.invokeMethod<String>('PciWriteDukptIpek', {
        'keyId': keyId,
        'ipekLen': ipekLen,
        'ipek': ipek,
        'ksnLen': ksnLen,
        'ksn': ksn,
      });
    } on PlatformException catch (e) {
      debugPrint('Error writing DUKPT IPEK: $e');
      return null;
    }
  }

  @override
  Future<String?> pciGetDukptMac(int keyId, int mode, int macDataLen, List<int> macDataIn, List<int> macOut, List<int> outKsn, List<int> macKcv) async {
    try {
      return await methodChannel.invokeMethod<String>('PciGetDukptMac', {
        'keyId': keyId,
        'mode': mode,
        'macDataLen': macDataLen,
        'macDataIn': macDataIn,
        'macOut': macOut,
        'outKsn': outKsn,
        'macKcv': macKcv,
      });
    } on PlatformException catch (e) {
      debugPrint('Error getting DUKPT MAC: $e');
      return null;
    }
  }

  @override
  Future<String?> pciGetDukptDes(int keyId, int mode, int desMode, int desDataLen, List<int> desDataIn, List<int> iv, List<int> desOut, List<int> outKsn, List<int> desKcv) async {
    try {
      return await methodChannel.invokeMethod<String>('PciGetDukptDes', {
        'keyId': keyId,
        'mode': mode,
        'desMode': desMode,
        'desDataLen': desDataLen,
        'desDataIn': desDataIn,
        'iv': iv,
        'desOut': desOut,
        'outKsn': outKsn,
        'desKcv': desKcv,
      });
    } on PlatformException catch (e) {
      debugPrint('Error performing DUKPT DES operation: $e');
      return null;
    }
  }

  // Helper methods

  Map<String, dynamic> _convertToStringDynamicMap(Map<dynamic, dynamic>? input) {
    if (input == null) return {};
    return Map<String, dynamic>.fromEntries(input.entries.map((entry) =>
        MapEntry(
            entry.key.toString(),
            entry.value is Map
                ? _convertToStringDynamicMap(entry.value as Map<dynamic, dynamic>)
                : entry.value)));
  }
}

/// Model class for scan results
class ScanResult {
  final String result;
  final int length;
  final int encodeType;

  ScanResult({
    required this.result,
    required this.length,
    required this.encodeType,
  });

  @override
  String toString() =>
      'ScanResult(result: $result, length: $length, encodeType: $encodeType)';
}