import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:cs50sdkupdate/cs50sdkupdate_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelCs50sdkupdate platform = MethodChannelCs50sdkupdate();
  const MethodChannel channel = MethodChannel('cs50sdkupdate');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
