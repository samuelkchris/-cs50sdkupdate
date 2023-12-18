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

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('getPlatformVersion() has not been implemented.');
  }

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

  Future<String?> piccM1Authority(int type, int blkNo, List<int> pwd, List<int> serialNo) {
    throw UnimplementedError('piccM1Authority() has not been implemented.');
  }

  Future<String?> piccNfc(List<int> nfcDataLen, List<int> technology, List<int> nfcUid, List<int> ndefMessage) {
    throw UnimplementedError('piccNfc() has not been implemented.');
  }
}