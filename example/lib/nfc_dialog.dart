import 'package:flutter/material.dart';

class NFCDialog extends StatelessWidget {
  final VoidCallback onCancel;

  const NFCDialog({Key? key, required this.onCancel}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      child: Container(
        padding: const EdgeInsets.all(20),
        height: 300,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              'Ready to Scan',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            Container(
              width: 100,
              height: 100,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                border: Border.all(color: Colors.blue, width: 3),
              ),
              child: const Center(
                child: Icon(Icons.nfc, size: 50, color: Colors.blue),
              ),
            ),
            const Text(
              'Hold your device near the NFC tag',
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 16),
            ),
            TextButton(
              onPressed: onCancel,
              child: const Text('Cancel', style: TextStyle(fontSize: 18)),
            ),
          ],
        ),
      ),
    );
  }
}
