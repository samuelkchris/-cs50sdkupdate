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
  StreamSubscription<PrintProgress>? _progressSubscription;

  int _currentPage = 0;
  int _totalPages = 0;
  bool _isPrinting = false;
  bool _isInitialized = false;
  String _statusMessage = 'Initializing...';
  String _printJobType = 'processing';
  bool _canCancel = false;
  String? _documentId;
  List<String> _logs = [];

  void _log(String message) {
    debugPrint("PRINT_PROGRESS: $message");
    setState(() {
      _logs.add("[${DateTime.now().toString().split('.').first}] $message");
      // Keep the log from growing too large
      if (_logs.length > 100) _logs.removeAt(0);
    });
  }

  @override
  void initState() {
    super.initState();
    _log("InitState called");
    _initializeSdk();
  }

  Future<void> _initializeSdk() async {
    _log("Initializing SDK");
    try {
      await _cs50sdkupdate.initialize();
      _log("SDK initialized successfully");

      _progressSubscription = _cs50sdkupdate.progressStream.listen(
            (progress) {
          _log("Progress received: ${progress.currentPage}/${progress.totalPages} (${progress.type})");
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
        },
        onError: (error) {
          _log("Progress stream error: $error");
        },
        onDone: () {
          _log("Progress stream closed");
        },
      );

      setState(() {
        _isInitialized = true;
        _statusMessage = 'Ready to print';
      });

      _log("Progress subscription set up");
      // Auto-start printing after initialization
      _startPrinting();
    } catch (e) {
      _log("Error initializing SDK: $e");
      setState(() {
        _statusMessage = 'Error initializing: $e';
      });
    }
  }

  @override
  void dispose() {
    _log("Disposing widget");
    _progressSubscription?.cancel();
    super.dispose();
  }

  Future<void> _startPrinting() async {
    _log("Starting print job with path: ${widget.pdfPath}");
    setState(() {
      _isPrinting = true;
      _statusMessage = 'Starting print job...';
    });

    try {
      _log("Calling printPdf method");
      final result = await _cs50sdkupdate.printPdf(widget.pdfPath);
      _log("Print job completed with status: ${result.status}");

      setState(() {
        _statusMessage = result.message;
        _documentId = result.documentId;
        _log("Document ID: $_documentId");

        if (result.isSuccess) {
          _statusMessage = 'Print job completed successfully';
        } else if (result.isPartialSuccess) {
          _statusMessage = 'Partially completed. Some pages failed: ${result.failedPages}';
          _log("Failed pages: ${result.failedPages}");
        } else if (result.isCancelled) {
          _statusMessage = 'Print job was cancelled';
        } else {
          _statusMessage = 'Error: ${result.message}';
        }
      });

      widget.onPrintComplete(_statusMessage);
    } catch (e) {
      _log("Exception during printing: $e");
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
      _log("Cancelling print job with ID: $_documentId");
      try {
        await _cs50sdkupdate.cancelJob(_documentId!);
        setState(() {
          _statusMessage = 'Print job cancelled';
          _isPrinting = false;
        });
      } catch (e) {
        _log("Error cancelling job: $e");
        setState(() {
          _statusMessage = 'Failed to cancel: $e';
        });
      }
    } else {
      _log("Cannot cancel - documentId is null");
    }
  }

  Future<void> _retryFailedPages() async {
    _log("Retrying failed pages");
    setState(() {
      _statusMessage = 'Retrying failed pages...';
      _isPrinting = true;
    });

    try {
      final result = await _cs50sdkupdate.retryJob();
      _log("Retry completed with status: ${result.status}");

      setState(() {
        _statusMessage = result.message;
        if (result.isSuccess) {
          _statusMessage = 'Retry completed successfully';
        } else if (result.isPartialSuccess) {
          _statusMessage = 'Partially completed. Some pages still failed: ${result.failedPages}';
          _log("Still failed pages: ${result.failedPages}");
        } else {
          _statusMessage = 'Error during retry: ${result.message}';
        }
      });
    } catch (e) {
      _log("Exception during retry: $e");
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
              if (!_isPrinting && _logs.isEmpty)
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
          const SizedBox(height: 20),
          // Display logs in a scrollable area (for debugging)
          if (_logs.isNotEmpty) ...[
            Text("Debug Logs:", style: Theme.of(context).textTheme.titleSmall),
            Container(
              height: 200,
              decoration: BoxDecoration(
                border: Border.all(color: Colors.grey),
                borderRadius: BorderRadius.circular(4),
              ),
              child: ListView.builder(
                itemCount: _logs.length,
                itemBuilder: (context, index) {
                  return Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                    child: Text(
                      _logs[index],
                      style: const TextStyle(
                        fontSize: 12,
                        fontFamily: 'monospace',
                      ),
                    ),
                  );
                },
              ),
            ),
          ],
        ],
      ),
    );
  }
}