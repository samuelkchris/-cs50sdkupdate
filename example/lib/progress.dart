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
  late StreamSubscription<Map<String, int>> _progressSubscription;
  int _currentPage = 0;
  int _totalPages = 0;
  bool _isPrinting = false;
  String _statusMessage = 'Ready to print';

  @override
  void initState() {
    super.initState();
    _initializeSdk();
  }

  Future<void> _initializeSdk() async {
    await _cs50sdkupdate.initialize();
    _progressSubscription = _cs50sdkupdate.progressStream.listen((progress) {
      setState(() {
        _currentPage = progress['currentPage']!;
        _totalPages = progress['totalPages']!;
      });
    });
  }

  @override
  void dispose() {
    _progressSubscription.cancel();
    _cs50sdkupdate.dispose();
    super.dispose();
  }

  Future<void> _startPrinting() async {
    setState(() {
      _isPrinting = true;
      _statusMessage = 'Starting print job...';
    });

    try {
      final result = await _cs50sdkupdate.printPdf(widget.pdfPath);
      if (result != 'Print job completed successfully') {
        setState(() {
          _statusMessage = result ?? 'Unknown error occurred';
        });
        widget.onPrintComplete(_statusMessage);
      } else {
        setState(() {
          _statusMessage = 'Print job completed successfully';
        });
        widget.onPrintComplete(_statusMessage);
      }
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

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        if (_isPrinting || _currentPage > 0)
          Text('$_statusMessage $_currentPage of $_totalPages'),
        if (_isPrinting || _currentPage > 0)
          LinearProgressIndicator(
            value: _totalPages > 0 ? _currentPage / _totalPages : 0,
          ),
        ElevatedButton(
          onPressed: _isPrinting ? null : _startPrinting,
          child: Text(_isPrinting ? 'Printing...' : 'Start Printing'),
        ),
      ],
    );
  }
}
