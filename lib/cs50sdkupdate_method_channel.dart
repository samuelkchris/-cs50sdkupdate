import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'cs50sdkupdate_platform_interface.dart';

/// An implementation of [Cs50sdkupdatePlatform] that uses method channels.
class MethodChannelCs50sdkupdate extends Cs50sdkupdatePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('cs50sdkupdate');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
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
  final List<int>? apduResp = await methodChannel.invokeMethod<List<int>>('piccCommand', args);
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
  Future<String?> piccM1Authority(int type, int blkNo, List<int> pwd, List<int> serialNo) async {
    final Map<String, dynamic> args = <String, dynamic>{
      'type': type,
      'blkNo': blkNo,
      'pwd': pwd,
      'serialNo': serialNo,
    };
    return await methodChannel.invokeMethod<String>('piccM1Authority', args);
  }

  @override
Future<String?> piccNfc(List<int> nfcDataLen, List<int> technology, List<int> nfcUid, List<int> ndefMessage) async {
  final Map<String, dynamic> args = <String, dynamic>{
    'nfcDataLen': nfcDataLen,
    'technology': technology,
    'nfcUid': nfcUid,
    'ndefMessage': ndefMessage,
  };
  return await methodChannel.invokeMethod<String>('PiccNfc', args);
}

}