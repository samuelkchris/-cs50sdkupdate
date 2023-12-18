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
