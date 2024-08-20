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
  Timer? inactivityTimer;

  Future<void> startNFCScanning() async {
    if (isScanning) return;

    setState(() {
      isScanning = true;
      pollingData = 'Scanning...';
    });

    showNFCDialog();
    startInactivityTimer();
    await continuousPolling();
  }

  void showNFCDialog() {
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
  }

  void startInactivityTimer() {
    inactivityTimer?.cancel();
    inactivityTimer = Timer(const Duration(seconds: 30), () {
      stopScanning();
      showSnackBar('NFC scanning timed out after 30 seconds of inactivity');
    });
  }

  Future<void> continuousPolling() async {
    const Duration pollInterval = Duration(milliseconds: 500);

    while (isScanning) {
      try {
        final data = await cs50sdkupdatePlugin.piccPolling();
        if (data != null && data.isNotEmpty) {
          processNFCData(data);
          // Reset the inactivity timer after successful read
          startInactivityTimer();
          // Navigate to data screen and wait for it to close
          await navigateToDataScreen();
          if (isScanning) {
            showNFCDialog(); // Reopen the dialog
          }
        }
      } catch (e) {
        // Ignore errors and continue polling
      }

      await Future.delayed(pollInterval);
    }
  }

  Future<void> navigateToDataScreen() async {
    Navigator.of(context).pop(); // Close the NFC dialog
    await Navigator.of(context).push(MaterialPageRoute(
        builder: (context) => NFCDataScreen(data: pollingData)));
  }

  void stopScanning() {
    if (!isScanning) return;

    setState(() {
      isScanning = false;
      pollingData = '';
    });

    inactivityTimer?.cancel();
    // Release resources here
    // cs50sdkupdatePlugin.releaseResources(); // Uncomment and implement if needed

    if (Navigator.of(context).canPop()) {
      Navigator.of(context).pop();
    }
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
  }

  void showSnackBar(String message) {
    ScaffoldMessenger.of(context)
        .showSnackBar(SnackBar(content: Text(message)));
  }

  @override
  void dispose() {
    inactivityTimer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('NFC Scanner')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton(
              onPressed: isScanning ? stopScanning : startNFCScanning,
              child: Text(isScanning ? 'Stop Scanning' : 'Start NFC Scan'),
            ),
          ],
        ),
      ),
    );
  }
}

class NFCDataScreen extends StatelessWidget {
  final String data;

  const NFCDataScreen({Key? key, required this.data}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    //close the screen after 2 seconds
    Future.delayed(const Duration(seconds: 2), () {
      Navigator.of(context).pop();
    });
    return Scaffold(
      appBar: AppBar(title: const Text('NFC Data')),
      body: Center(
        child: Text(
          data,
          style: const TextStyle(fontSize: 18),
          textAlign: TextAlign.center,
        ),
      ),
    );
  }
}
