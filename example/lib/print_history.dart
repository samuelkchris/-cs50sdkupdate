import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:iconsax/iconsax.dart';
import 'package:intl/intl.dart';

class PrintHistoryScreen extends StatefulWidget {
  @override
  _PrintHistoryScreenState createState() => _PrintHistoryScreenState();
}

class _PrintHistoryScreenState extends State<PrintHistoryScreen> {
  List<PrintDocument> documents = [];
  bool isLoading = true;
  final platform = const MethodChannel('cs50sdkupdate');
  @override
  void initState() {
    super.initState();

    _loadPrintHistory();
  }

  Future<void> _loadPrintHistory() async {

    setState(() => isLoading = true);
    try {
      final String printHistory = await platform.invokeMethod('getPrintHistory');
      final List<dynamic> historyList = json.decode(printHistory);
      setState(() {
        documents = historyList
            .map((item) => PrintDocument.fromJson(item))
            .toList()
            .reversed
            .toList();
        isLoading = false;
      });
    } catch (e) {
      print('Failed to load print history: $e');
      setState(() => isLoading = false);
    }
  }

  Future<void> _reprintDocument(String documentId) async {
    try {
      final result = await platform.invokeMethod('reprintDocument', {'documentId': documentId});
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Document reprinted successfully')),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Failed to reprint document')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Print History'),
        actions: [
          IconButton(
            icon: const Icon(Iconsax.refresh),
            onPressed: _loadPrintHistory,
          ),
        ],
      ),
      body: isLoading
          ? const Center(child: CircularProgressIndicator())
          : documents.isEmpty
          ? const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Iconsax.document, size: 64, color: Colors.grey),
            SizedBox(height: 16),
            Text('No print history found',
                style: TextStyle(fontSize: 18, color: Colors.grey)),
          ],
        ),
      )
          : ListView.builder(
        itemCount: documents.length,
        itemBuilder: (context, index) {
          final doc = documents[index];
          return Card(
            margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: ListTile(
              leading: const Icon(Iconsax.document_1, color: Colors.blue),
              title: Text(doc.originalPath.split('/').last,
                  style: const TextStyle(fontWeight: FontWeight.bold)),
              subtitle: Text(
                  DateFormat('MMM d, yyyy HH:mm').format(doc.timestamp)),
              trailing: IconButton(
                icon: const Icon(Iconsax.printer),
                onPressed: () => _reprintDocument(doc.id),
              ),
              onTap: () {
                showDialog(
                  context: context,
                  builder: (context) => AlertDialog(
                    title: const Text('Document Details'),
                    content: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Text('ID: ${doc.id}'),
                        const SizedBox(height: 8),
                        Text('Original Path: ${doc.originalPath}'),
                        const SizedBox(height: 8),
                        Text('Saved Path: ${doc.savedPath}'),
                      ],
                    ),
                    actions: [
                      TextButton(
                        child: const Text('Close'),
                        onPressed: () => Navigator.of(context).pop(),
                      ),
                      ElevatedButton(
                        child: const Text('Reprint'),
                        onPressed: () {
                          Navigator.of(context).pop();
                          _reprintDocument(doc.id);
                        },
                      ),
                    ],
                  ),
                );
              },
            ),
          );
        },
      ),
    );
  }
}

class PrintDocument {
  final String id;
  final String originalPath;
  final String savedPath;
  final DateTime timestamp;

  PrintDocument({
    required this.id,
    required this.originalPath,
    required this.savedPath,
    required this.timestamp,
  });

  factory PrintDocument.fromJson(Map<String, dynamic> json) {
    // Print raw timestamp for debugging
    print('Raw timestamp: ${json['timestamp']}');

    // Manually parse the timestamp
    final timestampStr = json['timestamp'] as String;
    final year = int.parse(timestampStr.substring(0, 4));
    final month = int.parse(timestampStr.substring(4, 6));
    final day = int.parse(timestampStr.substring(6, 8));
    final hour = int.parse(timestampStr.substring(9, 11));
    final minute = int.parse(timestampStr.substring(11, 13));
    final second = int.parse(timestampStr.substring(13, 15));

    final timestamp = DateTime(year, month, day, hour, minute, second);

    return PrintDocument(
      id: json['id'],
      originalPath: json['originalPath'],
      savedPath: json['savedPath'],
      timestamp: timestamp,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'originalPath': originalPath,
      'savedPath': savedPath,
      'timestamp': DateFormat('yyyyMMdd_HHmmss').format(timestamp),
    };
  }
}