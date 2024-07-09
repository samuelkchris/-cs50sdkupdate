
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
  Future<String?> sysApiVerson() {
    throw UnimplementedError('SysApiVerson() has not been implemented.');
  }

  Future<String?> getOSVersion() {
    throw UnimplementedError('getOSVersion() has not been implemented.');
  }

  Future<String?> getDeviceId() {
    throw UnimplementedError('getDeviceId() has not been implemented.');
  }

  Future<String?> printInit() {
    throw UnimplementedError('printInit() has not been implemented.');
  }

  Future<String?> printInitWithParams(int gray, int fontHeight, int fontWidth, int fontZoom) {
    throw UnimplementedError('printInitWithParams() has not been implemented.');
  }

  Future<String?> printSetFont(int asciiFontHeight, int extendFontHeight, int zoom) {
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

  Future<String?> printStr(String str) {
    throw UnimplementedError('printStr() has not been implemented.');
  }

  Future<String?> printBmp(Uint8List bmpData) {
    throw UnimplementedError('printBmp() has not been implemented.');
  }

  Future<String?> printBarcode(String contents, int desiredWidth, int desiredHeight, String barcodeFormat) {
    throw UnimplementedError('printBarcode() has not been implemented.');
  }

  Future<String?> printQrCode(String contents, int desiredWidth, int desiredHeight, String barcodeFormat) {
    throw UnimplementedError('printQrCode() has not been implemented.');
  }

  Future<String?> printCutQrCodeStr(String contents, String printTxt, int distance, int desiredWidth, int desiredHeight, String barcodeFormat) {
    throw UnimplementedError('printCutQrCodeStr() has not been implemented.');
  }

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

  Future<String?> printLogo(Uint8List logo) {
    throw UnimplementedError('printLogo() has not been implemented.');
  }

  Future<String?> printLabLocate(int step) {
    throw UnimplementedError('printLabLocate() has not been implemented.');
  }
}
