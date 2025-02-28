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
  bool _isInitializing = false;

  @override
  void initState() {
    super.initState();
    _initializeNfc();
  }

  Future<void> _initializeNfc() async {
    setState(() {
      _isInitializing = true;
    });

    try {
      await cs50sdkupdatePlugin.initialize();
      await cs50sdkupdatePlugin.openPicc();
      setState(() {
        _isInitializing = false;
      });
    } catch (e) {
      setState(() {
        _isInitializing = false;
        pollingData = 'Failed to initialize NFC: $e';
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Failed to initialize NFC: $e')),
      );
    }
  }

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
    // Close PICC gracefully
    cs50sdkupdatePlugin.piccClose().catchError((e) {
      print('Error closing PICC: $e');
    });

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

    // Handle different UID formats
    if (uid.isNotEmpty) {
      uid = uid.replaceAll(' ', '');
      // Make sure we have at least 8 characters for hex conversion
      if (uid.length >= 8) {
        uid = uid.substring(0, 8);
        // Try to convert to decimal
        try {
          int decimalUid = NfcUidConverter.hexToDecimal(uid);

          setState(() {
            pollingData = '''
Card Type: ${parsedData['Card Type'] ?? 'N/A'}
UID (Hex): $uid
UID (Decimal): $decimalUid
SAK: ${parsedData['SAK'] ?? 'N/A'}
''';
          });
        } catch (e) {
          setState(() {
            pollingData = '''
Card Type: ${parsedData['Card Type'] ?? 'N/A'}
UID (Hex): $uid
SAK: ${parsedData['SAK'] ?? 'N/A'}
Error: Could not convert UID to decimal
''';
          });
        }
      } else {
        setState(() {
          pollingData = '''
Card Type: ${parsedData['Card Type'] ?? 'N/A'}
UID: $uid
SAK: ${parsedData['SAK'] ?? 'N/A'}
''';
        });
      }
    } else {
      setState(() {
        pollingData = 'Could not parse NFC data: $data';
      });
    }

    print('NFC data received: $pollingData');
  }

  void showSnackBar(String message) {
    ScaffoldMessenger.of(context)
        .showSnackBar(SnackBar(content: Text(message)));
  }

  @override
  void dispose() {
    inactivityTimer?.cancel();

    // Ensure PICC is closed when navigating away
    if (isScanning) {
      cs50sdkupdatePlugin.piccClose().catchError((e) {
        print('Error closing PICC on dispose: $e');
      });
    }

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('NFC Scanner')),
      body: Center(
        child: _isInitializing
            ? const CircularProgressIndicator()
            : Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton(
              onPressed: isScanning ? stopScanning : startNFCScanning,
              child: Text(isScanning ? 'Stop Scanning' : 'Start NFC Scan'),
            ),
            const SizedBox(height: 20),
            if (pollingData.isNotEmpty && !isScanning)
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: Card(
                  elevation: 4,
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    child: Column(
                      children: [
                        const Text(
                          'Last Scan Result:',
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: 16,
                          ),
                        ),
                        const SizedBox(height: 8),
                        Text(
                          pollingData,
                          style: const TextStyle(fontSize: 14),
                        ),
                      ],
                    ),
                  ),
                ),
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
        child: Card(
          margin: const EdgeInsets.all(16),
          elevation: 4,
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Text(
              data,
              style: const TextStyle(fontSize: 18),
              textAlign: TextAlign.center,
            ),
          ),
        ),
      ),
    );
  }
}