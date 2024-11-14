import 'package:cs50sdkupdate_example/scanner_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class ScannerScreen extends StatelessWidget {
  const ScannerScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Consumer<ScannerDataProvider>(
      builder: (context, scannerProvider, child) {
        return Scaffold(
          appBar: AppBar(
            title: const Text('Scanner'),
            actions: [
              IconButton(
                icon: Icon(scannerProvider.isPowered ?
                  Icons.power : Icons.power_off),
                onPressed: () => scannerProvider.isPowered ?
                  scannerProvider.powerOff() :
                  scannerProvider.powerOn(),
              ),
            ],
          ),
          body: Column(
            children: [
              if (scannerProvider.errorMessage != null)
                Container(
                  color: Colors.red[100],
                  padding: const EdgeInsets.all(8),
                  child: Text(
                    scannerProvider.errorMessage!,
                    style: const TextStyle(color: Colors.red),
                  ),
                ),
              if (scannerProvider.isPowered) ...[
                SwitchListTile(
                  title: const Text('Continuous Mode'),
                  value: scannerProvider.isContinuousMode,
                  onChanged: scannerProvider.setScanMode,
                ),
                ElevatedButton.icon(
                  icon: Icon(scannerProvider.isScanning ?
                    Icons.stop : Icons.qr_code_scanner),
                  label: Text(scannerProvider.isScanning ?
                    'Stop Scanning' : 'Start Scanning'),
                  onPressed: () => scannerProvider.isScanning ?
                    scannerProvider.stopScanning() :
                    scannerProvider.startScanning(),
                ),
                Expanded(
                  child: ListView.builder(
                    itemCount: scannerProvider.scanHistory.length,
                    itemBuilder: (context, index) {
                      final scan = scannerProvider.scanHistory[index];
                      return ListTile(
                        title: Text(scan.result),
                        subtitle: Text('Length: ${scan.length}'),
                        trailing: Text('Type: ${scan.encodeType}'),
                      );
                    },
                  ),
                ),
              ],
            ],
          ),
          floatingActionButton: scannerProvider.scanHistory.isNotEmpty
            ? FloatingActionButton(
                onPressed: scannerProvider.clearHistory,
                child: const Icon(Icons.clear_all),
              )
            : null,
        );
      },
    );
  }
}