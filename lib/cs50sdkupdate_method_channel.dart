import 'dart:async';
import 'dart:typed_data' as typed_data;

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'cs50sdkupdate.dart';
import 'cs50sdkupdate_platform_interface.dart';

/// An implementation of [Cs50sdkupdatePlatform] that uses method channels.
class MethodChannelCs50sdkupdate extends Cs50sdkupdatePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('cs50sdkupdate');

  final StreamController<Map<String, int>> _progressController = StreamController<Map<String, int>>.broadcast();

  @override
  Future<void> initialize() async {
    methodChannel.setMethodCallHandler(_handleMethodCall);
  }

  Future<void> _handleMethodCall(MethodCall call) async {
    switch (call.method) {
      case 'updateProgress':
        final Map<String, int> progress = Map<String, int>.from(call.arguments);
        _progressController.add(progress);
        break;
      default:
        throw MissingPluginException('notImplemented');
    }
  }

  Stream<Map<String, int>> get printProgressStream => _progressController.stream;

  @override
  Future<String?> getPlatformVersion() async {
    final version =
    await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<void> openPicc() async {
    await methodChannel.invokeMethod<void>('openPicc');
  }

  @override
  Future<String?> piccCheck() async {
    return await methodChannel.invokeMethod<String>('piccCheck');
  }

  @override
  Future<String?> piccPolling() async {
    return await methodChannel.invokeMethod<String>('piccPolling');
  }

  @override
  Future<String?> piccCommand(List<int> apduSend) async {
    final Map<String, dynamic> args = <String, dynamic>{
      'apduSend': apduSend,
    };
    final List<int>? apduResp =
    await methodChannel.invokeMethod<List<int>>('piccCommand', args);
    return apduResp != null ? String.fromCharCodes(apduResp) : null;
  }

  @override
  Future<String?> piccApduCmd(List<int> pucInput) async {
    return await methodChannel.invokeMethod<String>('piccApduCmd', pucInput);
  }

  @override
  Future<void> piccClose() async {
    await methodChannel.invokeMethod<void>('piccClose');
  }

  @override
  Future<bool> piccRemove() async {
    return await methodChannel.invokeMethod<bool>('piccRemove') ?? false;
  }

  @override
  Future<String?> piccSamAv2Init(int samSlotNo, List<int> samHostKey) async {
    final Map<String, dynamic> args = <String, dynamic>{
      'samSlotNo': samSlotNo,
      'samHostKey': samHostKey,
    };
    return await methodChannel.invokeMethod<String>('piccSamAv2Init', args);
  }

  @override
  Future<String?> piccHwModeSet(int mode) async {
    return await methodChannel.invokeMethod<String>('piccHwModeSet', mode);
  }

  @override
  Future<String?> piccM1Authority(int type, int blkNo, List<int> pwd,
      List<int> serialNo) async {
    final Map<String, dynamic> args = <String, dynamic>{
      'type': type,
      'blkNo': blkNo,
      'pwd': pwd,
      'serialNo': serialNo,
    };
    return await methodChannel.invokeMethod<String>('piccM1Authority', args);
  }

  @override
  Future<String?> piccNfc(List<int> nfcDataLen, List<int> technology,
      List<int> nfcUid, List<int> ndefMessage) async {
    final Map<String, dynamic> args = <String, dynamic>{
      'nfcDataLen': nfcDataLen,
      'technology': technology,
      'nfcUid': nfcUid,
      'ndefMessage': ndefMessage,
    };
    return await methodChannel.invokeMethod<String>('PiccNfc', args);
  }

  @override
  Future<int?> sysLogSwitch(int level) async {
    return await methodChannel.invokeMethod<int>('SysLogSwitch', level);
  }

  //SysApiVerson
  @override
  Future<String?> sysApiVerson() {
    return methodChannel.invokeMethod<String>('SysApiVerson');
  }

  //getDeviceId
  @override
  Future<String?> getDeviceId() {
    return methodChannel.invokeMethod<String>('getDeviceId');
  }

  //SysGetRand
  @override
  Future<int?> sysGetRand(List<int> rnd) {
    final Map<String, dynamic> args = <String, dynamic>{
      'rnd': rnd,
    };
    return methodChannel.invokeMethod<int>('SysGetRand', args);
  }

  //SysUpdate
  @override
  Future<int?> sysUpdate() {
    return methodChannel.invokeMethod<int>('SysUpdate');
  }

  //SysGetVersion
  @override
  Future<int?> sysGetVersion(List<int> buf) {
    final Map<String, dynamic> args = <String, dynamic>{
      'buf': buf,
    };
    return methodChannel.invokeMethod<int>('SysGetVersion', args);
  }

  //SysReadSN
  @override
  Future<int?> sysReadSN(List<int> SN) {
    final Map<String, dynamic> args = <String, dynamic>{
      'SN': SN,
    };
    return methodChannel.invokeMethod<int>('SysReadSN', args);
  }

  @override
  Future<String?> printInit() async {
    return await methodChannel.invokeMethod<String>('PrintInit');
  }

  @override
  Future<String?> printInitWithParams(int gray, int fontHeight, int fontWidth,
      int fontZoom) async {
    final Map<String, dynamic> args = <String, dynamic>{
      'gray': gray,
      'fontHeight': fontHeight,
      'fontWidth': fontWidth,
      'fontZoom': fontZoom,
    };
    return await methodChannel.invokeMethod<String>(
        'PrintInitWithParams', args);
  }

  @override
  Future<String?> printSetFont(int asciiFontHeight, int extendFontHeight,
      int zoom) async {
    final Map<String, dynamic> args = <String, dynamic>{
      'asciiFontHeight': asciiFontHeight,
      'extendFontHeight': extendFontHeight,
      'zoom': zoom,
    };
    return await methodChannel.invokeMethod<String>('PrintSetFont', args);
  }

  @override
  Future<String?> printSetGray(int nLevel) async {
    return await methodChannel
        .invokeMethod<String>('PrintSetGray', {'nLevel': nLevel});
  }

  @override
  Future<String?> printSetSpace(int x, int y) async {
    final Map<String, dynamic> args = <String, dynamic>{
      'x': x,
      'y': y,
    };
    return await methodChannel.invokeMethod<String>('PrintSetSpace', args);
  }

  @override
  Future<String?> printGetFont() async {
    return await methodChannel.invokeMethod<String>('PrintGetFont');
  }

  @override
  Future<String?> printStep(int pixel) async {
    return await methodChannel
        .invokeMethod<String>('PrintStep', {'pixel': pixel});
  }

  @override
  Future<String?> printSetVoltage(int voltage) async {
    return await methodChannel
        .invokeMethod<String>('PrintSetVoltage', {'voltage': voltage});
  }

  @override
  Future<String?> printIsCharge(int ischarge) async {
    return await methodChannel
        .invokeMethod<String>('PrintIsCharge', {'ischarge': ischarge});
  }

  @override
  Future<String?> printSetLinPixelDis(int linDistance) async {
    return await methodChannel.invokeMethod<String>(
        'PrintSetLinPixelDis', {'linDistance': linDistance});
  }

  @override
  Future<String?> printStr(String str) async {
    return await methodChannel.invokeMethod<String>('PrintStr', {'str': str});
  }

  @override
  Future<String?> printBmp(typed_data.Uint8List bmpData) async {
    return await methodChannel
        .invokeMethod<String>('PrintBmp', {'bmpData': bmpData});
  }

  @override
  Future<String?> printBarcode(String contents, int desiredWidth,
      int desiredHeight, String barcodeFormat) async {
    final Map<String, dynamic> args = <String, dynamic>{
      'contents': contents,
      'desiredWidth': desiredWidth,
      'desiredHeight': desiredHeight,
      'barcodeFormat': barcodeFormat,
    };
    return await methodChannel.invokeMethod<String>('PrintBarcode', args);
  }

  @override
  Future<String?> printQrCode(String contents, int desiredWidth,
      int desiredHeight, String barcodeFormat) async {
    final Map<String, dynamic> args = <String, dynamic>{
      'contents': contents,
      'desiredWidth': desiredWidth,
      'desiredHeight': desiredHeight,
      'barcodeFormat': barcodeFormat,
    };
    return await methodChannel.invokeMethod<String>('PrintQrCode_Cut', args);
  }

  @override
  Future<String?> printCutQrCodeStr(String contents,
      String printTxt,
      int distance,
      int desiredWidth,
      int desiredHeight,
      String barcodeFormat) async {
    final Map<String, dynamic> args = <String, dynamic>{
      'contents': contents,
      'printTxt': printTxt,
      'distance': distance,
      'desiredWidth': desiredWidth,
      'desiredHeight': desiredHeight,
      'barcodeFormat': barcodeFormat,
    };
    return await methodChannel.invokeMethod<String>('PrintCutQrCode_Str', args);
  }

  @override
  Future<String?> printStart() async {
    return await methodChannel.invokeMethod<String>('PrintStart');
  }

  @override
  Future<String?> printSetLeftIndent(int x) async {
    return await methodChannel
        .invokeMethod<String>('PrintSetLeftIndent', {'x': x});
  }

  @override
  Future<String?> printSetAlign(int x) async {
    return await methodChannel.invokeMethod<String>('PrintSetAlign', {'x': x});
  }

  @override
  Future<String?> printCharSpace(int x) async {
    return await methodChannel.invokeMethod<String>('PrintCharSpace', {'x': x});
  }

  @override
  Future<String?> printSetLineSpace(int x) async {
    return await methodChannel
        .invokeMethod<String>('PrintSetLineSpace', {'x': x});
  }

  @override
  Future<String?> printSetLeftSpace(int x) async {
    return await methodChannel
        .invokeMethod<String>('PrintSetLeftSpace', {'x': x});
  }

  @override
  Future<String?> printSetSpeed(int iSpeed) async {
    return await methodChannel
        .invokeMethod<String>('PrintSetSpeed', {'iSpeed': iSpeed});
  }

  @override
  Future<String?> printCheckStatus() async {
    return await methodChannel.invokeMethod<String>('PrintCheckStatus');
  }

  @override
  Future<String?> printFeedPaper(int step) async {
    return await methodChannel
        .invokeMethod<String>('PrintFeedPaper', {'step': step});
  }

  @override
  Future<String?> printSetMode(int mode) async {
    return await methodChannel
        .invokeMethod<String>('PrintSetMode', {'mode': mode});
  }

  @override
  Future<String?> printSetUnderline(int x) async {
    return await methodChannel
        .invokeMethod<String>('PrintSetUnderline', {'x': x});
  }

  @override
  Future<String?> printSetReverse(int x) async {
    return await methodChannel
        .invokeMethod<String>('PrintSetReverse', {'x': x});
  }

  @override
  Future<String?> printSetBold(int x) async {
    return await methodChannel.invokeMethod<String>('PrintSetBold', {'x': x});
  }

  @override
  Future<String?> printLogo(typed_data.Uint8List logo) async {
    return await methodChannel
        .invokeMethod<String>('PrintLogo', {'logo': logo});
  }

  @override
  Future<String?> printLabLocate(int step) async {
    return await methodChannel
        .invokeMethod<String>('PrintLabLocate', {'step': step});
  }

  @override
  Future<void> startMonitoringPrintJobs() async {
    await methodChannel.invokeMethod<void>('startMonitoringPrintJobs');
  }

  @override
  Future<List<Map<String, dynamic>>> getAllPrintJobs() async {
    final List<dynamic> jobs =
        await methodChannel.invokeMethod<List<dynamic>>('getAllPrintJobs') ??
            [];
    return jobs.cast<Map<String, dynamic>>();
  }

  @override
  Future<void> cancelPrintJob(String jobId) async {
    await methodChannel.invokeMethod<void>('cancelPrintJob', {'jobId': jobId});
  }

  @override
  Future<void> restartPrintJob(String jobId) async {
    await methodChannel.invokeMethod<void>('restartPrintJob', {'jobId': jobId});
  }

  @override
  Future<String?> printPdf(String pdfPath) async {
    return await methodChannel
        .invokeMethod<String?>('PrintPdf', {'pdfPath': pdfPath});
  }

  @override
  Future<void> cancelJob(String jobId) async {
    return await methodChannel
        .invokeMethod<void>('CancelJob', {'jobId': jobId});
  }

  @override
  Future<String> retryPrintJob(String jobId) async {
    try {
      final String? newJobId = await methodChannel.invokeMethod<String>('RetryJob', {'jobId': jobId});
      return newJobId!;
    } on PlatformException catch (e) {
      print('Error retrying print job: ${e.message}');
      return "null";
    }
  }


  @override
 Future<Map<String, dynamic>?> getPrintStats() async {
  try {
    final Map<dynamic, dynamic>? result = await methodChannel.invokeMethod<Map<dynamic, dynamic>>('GetPrintStats');
    print("Result: $result");

    // Parsing the updated structure
    int totalPagesPrinted = result?['totalPagesPrinted'];
    int totalPagesUnprinted = result?['totalPagesUnprinted'];
    Map<String, dynamic> jobs = result?['jobs'].cast<String, dynamic>();

    // // Logging the basic stats
    // print('Total pages printed: $totalPagesPrinted');
    // print('Total pages unprinted: $totalPagesUnprinted');
    //
    // // Iterating over jobs to log their details
    // jobs.forEach((jobId, jobDetails) {
    //   print('Job ID: $jobId');
    //   print('Pages: ${jobDetails['pages']}');
    //   print('Copies: ${jobDetails['copies']}');
    //   print('Creation Time: ${DateTime.fromMillisecondsSinceEpoch(jobDetails['creationTime'])}');
    //   print('Is Blocked: ${jobDetails['isBlocked']}');
    //   print('Is Cancelled: ${jobDetails['isCancelled']}');
    //   print('Is Completed: ${jobDetails['isCompleted']}');
    //   print('Is Failed: ${jobDetails['isFailed']}');
    //   print('Is Queued: ${jobDetails['isQueued']}');
    //   print('Is Started: ${jobDetails['isStarted']}');
    // });

    // Returning the parsed result
    return result?.map((key, value) => MapEntry(key as String, value));
  } catch (e) {
    print("Error fetching print stats: $e");
    return null;
  }
}

}
