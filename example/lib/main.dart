import 'package:flutter/material.dart';
import 'package:cs50sdkupdate/cs50sdkupdate.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: HomeScreen(),
    );
  }
}

class HomeScreen extends StatefulWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final _cs50sdkupdatePlugin = Cs50sdkupdate();
  String _platformVersion = 'Unknown';
  String _pollingData = 'No data';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    try {
      _platformVersion = await _cs50sdkupdatePlugin.getPlatformVersion() ?? 'Unknown platform version';
      await _cs50sdkupdatePlugin.openPicc();
      _showDialog('PICC opened.');
    } catch (e) {
      _showDialog('Failed to get platform version.');
      _platformVersion = 'Failed to get platform version.';
    }
  }

  Future<void> _showDialog(String message) async {
    return showDialog<void>(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('Message'),
          content: Text(message),
          actions: <Widget>[
            TextButton(
              child: const Text('OK'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }

  Future<void> executeCommand(String commandName, Future Function() command) async {
    try {
      await command();
      _showDialog('$commandName executed successfully.');
    } catch (e) {
      _showDialog('Failed to execute $commandName.');
    }
  }

  Future<void> executeCommands() async {
  await executeCommand('Execute Commands', () async {
    List<int> apduSend = [0x00, 0x84, 0x00, 0x00, 0x08];
    String? commandResponse = await _cs50sdkupdatePlugin.piccCommand(apduSend);
    String apduCmdResponse = await _cs50sdkupdatePlugin.piccApduCmd(apduSend) ?? 'No data';
    await _cs50sdkupdatePlugin.piccClose();
    bool isRemoved = await _cs50sdkupdatePlugin.piccRemove();

    print('Command Response: $commandResponse');
    print('APDU Command Response: $apduCmdResponse');
    print('Is Card Removed: $isRemoved');
  });
}

Future<void> executeSamAv2Init() async {
  await executeCommand('Execute SAM AV2 Init', () async {
    List<int> samHostKey = [0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00];
    String? samInitResponse = await _cs50sdkupdatePlugin.piccSamAv2Init(1, samHostKey);
    String? hwModeSetResponse = await _cs50sdkupdatePlugin.piccHwModeSet(1);
    List<int> pwd = [0xff, 0xff, 0xff, 0xff, 0xff, 0xff];
    List<int> serialNo = [0x00, 0x00, 0x00, 0x00]; // assuming a 4-byte serial number
    String? m1AuthorityResponse = await _cs50sdkupdatePlugin.piccM1Authority(0x0B, 0x01, pwd, serialNo);

    print('SAM AV2 Init Response: $samInitResponse');
    print('HW Mode Set Response: $hwModeSetResponse');
    print('M1 Authority Response: $m1AuthorityResponse');
  });
}

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('CS50 SDK Update Plugin'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
              onPressed: () => executeCommand('Open PICC', _cs50sdkupdatePlugin.openPicc),
              child: const Text('Open PICC'),
            ),
            Text('Running on: $_platformVersion\n'),
            ElevatedButton(
              onPressed: () => executeCommand('Start Polling', _cs50sdkupdatePlugin.piccPolling),
              child: const Text('Start Polling'),
            ),
            Text('Polling Data: $_pollingData'),
            ElevatedButton(
              onPressed: () => executeCommand('Close PICC', _cs50sdkupdatePlugin.piccClose),
              child: const Text('Close PICC'),
            ),
            ElevatedButton(
              onPressed: () => executeCommand('Remove PICC', _cs50sdkupdatePlugin.piccRemove),
              child: const Text('Remove PICC'),
            ),
            ElevatedButton(
              onPressed: () => executeCommand('Check PICC', _cs50sdkupdatePlugin.piccCheck),
              child: const Text('Check PICC'),
            ),
          ],
        ),
      ),
    );
  }
}