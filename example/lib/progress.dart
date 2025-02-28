import 'dart:async';

import 'package:flutter/material.dart';
import 'package:cs50sdkupdate/cs50sdkupdate.dart';

class PrintProgressWidget extends StatefulWidget {
  final String pdfPath;
  final Function(String) onPrintComplete;

  const PrintProgressWidget({
    Key? key,
    required this.pdfPath,
    required this.onPrintComplete,
  }) : super(key: key);

  @override
  _PrintProgressWidgetState createState() => _PrintProgressWidgetState();
}

class _PrintProgressWidgetState extends State<PrintProgressWidget> {
  final Cs50sdkupdate _cs50sdkupdate = Cs50sdkupdate();
  late StreamSubscription<PrintProgress> _progressSubscription;

  int _currentPage = 0;
  int _totalPages = 0;
  bool _isPrinting = false;
  String _statusMessage = 'Ready to print';
  String _printJobType = 'processing';
  bool _canCancel = false;
  String? _documentId;

  @override
  void initState() {
    super.initState();
    _initializeSdk();
  }

  Future<void> _initializeSdk() async {
    await _cs50sdkupdate.initialize();

    _progressSubscription = _cs50sdkupdate.progressStream.listen((progress) {
      setState(() {
        _currentPage = progress.currentPage;
        _totalPages = progress.totalPages;
        _printJobType = progress.type;

        if (_printJobType == 'processing') {
          _statusMessage = 'Processing page $_currentPage of $_totalPages';
        } else if (_printJobType == 'printing') {
          _statusMessage = 'Printing page $_currentPage of $_totalPages';
          _canCancel = true;
        } else if (_printJobType == 'retry') {
          _statusMessage = 'Retrying page $_currentPage of $_totalPages';
        }
      });
    });
  }

  @override
  void dispose() {
    _progressSubscription.cancel();
    super.dispose();
  }

  Future<void> _startPrinting() async {
    setState(() {
      _isPrinting = true;
      _statusMessage = 'Starting print job...';
    });

    try {
      final result = await _cs50sdkupdate.printPdf(widget.pdfPath);
      setState(() {
        _statusMessage = result.message;
        _documentId = result.documentId;

        if (result.isSuccess) {
          _statusMessage = 'Print job completed successfully';
        } else if (result.isPartialSuccess) {
          _statusMessage = 'Partially completed. Some pages failed.';
        } else if (result.isCancelled) {
          _statusMessage = 'Print job was cancelled';
        } else {
          _statusMessage = 'Error: ${result.message}';
        }
      });

      widget.onPrintComplete(_statusMessage);
    } catch (e) {
      setState(() {
        _statusMessage = 'Failed to start print job: $e';
      });
      widget.onPrintComplete(_statusMessage);
    } finally {
      setState(() {
        _isPrinting = false;
      });
    }
  }

  Future<void> _cancelPrinting() async {
    if (_documentId != null) {
      try {
        await _cs50sdkupdate.cancelJob(_documentId!);
        setState(() {
          _statusMessage = 'Print job cancelled';
          _isPrinting = false;
        });
      } catch (e) {
        setState(() {
          _statusMessage = 'Failed to cancel: $e';
        });
      }
    }
  }

  Future<void> _retryFailedPages() async {
    setState(() {
      _statusMessage = 'Retrying failed pages...';
      _isPrinting = true;
    });

    try {
      final result = await _cs50sdkupdate.retryJob();
      setState(() {
        _statusMessage = result.message;
        if (result.isSuccess) {
          _statusMessage = 'Retry completed successfully';
        } else if (result.isPartialSuccess) {
          _statusMessage = 'Partially completed. Some pages still failed.';
        } else {
          _statusMessage = 'Error during retry: ${result.message}';
        }
      });
    } catch (e) {
      setState(() {
        _statusMessage = 'Failed to retry: $e';
      });
    } finally {
      setState(() {
        _isPrinting = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    double progress = _totalPages > 0 ? _currentPage / _totalPages : 0;

    return Container(
      width: 400,
      padding: const EdgeInsets.all(16),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            'Print Status',
            style: Theme.of(context).textTheme.titleLarge,
          ),
          const SizedBox(height: 16),
          Text(_statusMessage),
          const SizedBox(height: 8),
          if (_isPrinting || _currentPage > 0) ...[
            LinearProgressIndicator(
              value: progress,
              minHeight: 10,
            ),
            const SizedBox(height: 8),
            Text('$_currentPage of $_totalPages pages'),
            const SizedBox(height: 16),
          ],
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              if (!_isPrinting)
                ElevatedButton(
                  onPressed: _startPrinting,
                  child: const Text('Start Printing'),
                ),
              if (_isPrinting && _canCancel)
                ElevatedButton(
                  onPressed: _cancelPrinting,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.red,
                  ),
                  child: const Text('Cancel'),
                ),
              if (!_isPrinting && _currentPage > 0 && _currentPage < _totalPages)
                ElevatedButton(
                  onPressed: _retryFailedPages,
                  child: const Text('Retry Failed Pages'),
                ),
            ],
          ),
        ],
      ),
    );
  }
}