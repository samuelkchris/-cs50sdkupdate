import 'dart:async';

import 'package:cs50sdkupdate/cs50sdkupdate.dart';
import 'package:flutter/material.dart';

import 'nfc_converter.dart';
import 'nfc_dialog.dart';

class NFCScanner extends StatefulWidget {
  const NFCScanner({super.key});

  @override
  _NFCScannerState createState() => _NFCScannerState();
}

class _NFCScannerState extends State<NFCScanner> {
  String pollingData = '';
  bool isScanning = false;
  final cs50sdkupdatePlugin = Cs50sdkupdate();

  Future<void> startNFCScanning() async {
    if (isScanning) return;

    setState(() {
      isScanning = true;
      pollingData = 'Scanning...';
    });

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return NFCDialog(
          onCancel: () {
            stopScanning();
            Navigator.of(context).pop();
          },
        );
      },
    );

    await pollForNFC();
  }

  Future<void> pollForNFC() async {
    const Duration timeout = Duration(seconds: 30);
    const Duration pollInterval = Duration(milliseconds: 500);

    Timer? timeoutTimer;
    timeoutTimer = Timer(timeout, () {
      stopScanning();
      showSnackBar('NFC scanning timed out');
    });

    while (isScanning) {
      try {
        final data = await cs50sdkupdatePlugin.piccPolling();
        if (data != null && data.isNotEmpty) {
          timeoutTimer.cancel();
          stopScanning();
          processNFCData(data);
          return;
        }
      } catch (e) {
        // Ignore errors and continue polling
      }

      await Future.delayed(pollInterval);
    }

    timeoutTimer.cancel();
  }

  void stopScanning() {
    if (!isScanning) return;

    setState(() {
      isScanning = false;
    });

    // cs50sdkupdatePlugin.piccPollingStop();
    Navigator.of(context).pop();
  }

  void processNFCData(String data) {
    Map<String, String> parsedData = {};
    List<String> lines = data.split('\n');

    for (String line in lines) {
      if (line.contains(':')) {
        List<String> parts = line.split(':');
        String key = parts[0].trim();
        String value = parts[1].trim();
        parsedData[key] = value;
      }
    }

    String uid = parsedData['UID'] ?? '';
    uid = uid.replaceAll(' ', '').substring(0, 8);

    int decimalUid = NfcUidConverter.hexToDecimal(uid);

    setState(() {
      pollingData = '''
Card Type: ${parsedData['Card Type'] ?? 'N/A'}
UID (Hex): $uid
UID (Decimal): $decimalUid
SAK: ${parsedData['SAK'] ?? 'N/A'}
''';
    });

    print('NFC data received: $pollingData');
    showSnackBar('NFC UID received: $decimalUid');
  }

  void showSnackBar(String message) {
    ScaffoldMessenger.of(context)
        .showSnackBar(SnackBar(content: Text(message)));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('NFC Scanner')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Polling Data: $pollingData'),
            ElevatedButton(
              onPressed: isScanning ? null : startNFCScanning,
              child: Text(isScanning ? 'Scanning...' : 'Start NFC Scan'),
            ),
          ],
        ),
      ),
    );
  }
}
