import 'dart:async';

import 'package:cs50sdkupdate/cs50sdkupdate.dart';
import 'package:flutter/services.dart';import 'package:pdf_render/pdf_render.dart' as pdf_render;

enum PrintStatus { pending, printing, printed, failed, cancelled }

class PrintJobManager {
  final Cs50sdkupdate _printPlugin;
  final List<Map<String, dynamic>> _pages = [];
  final Map<String, JobStatus> _activeJobs = {};
  Timer? _statsTimer;

  int _totalPagesPrinted = 0;
  int _totalPagesUnprinted = 0;

  PrintJobManager(this._printPlugin) {
    _startStatsTimer();
  }

  int get pagesCount => _pages.length;

  Cs50sdkupdate get printPlugin => _printPlugin;

  void addPage(String content) {
    _pages.add({
      'content': content,
      'status': PrintStatus.pending,
      'jobId': null,
    });
  }

  Future<void> printAllPages() async {
    for (int i = 0; i < _pages.length; i++) {
      if (_pages[i]['status'] != PrintStatus.printed) {
        await _printPage(i);
      }
    }
  }

  Future<void> _printPage(int index) async {
    try {
      _pages[index]['status'] = PrintStatus.printing;
      String? jobId = await _printPlugin.printPdf(_pages[index]['content']);
      _pages[index]['jobId'] = jobId;
      _activeJobs[jobId!] = _pages[index]['content'];
      notifyListeners();
    } catch (e) {
      _pages[index]['status'] = PrintStatus.failed;
      notifyListeners();
    }
  }

  Future<void> cancelJob(int pageIndex) async {
    if (pageIndex >= 0 && pageIndex < _pages.length) {
      String? jobId = _pages[pageIndex]['jobId'];
      if (jobId != null) {
        try {
          await _printPlugin.cancelJob(jobId);
          _pages[pageIndex]['status'] = PrintStatus.cancelled;
          _activeJobs.remove(jobId);
          notifyListeners();
        } catch (e) {
          print('Error cancelling job: $e');
        }
      }
    }
  }

  Future<void> retryFailedJobs() async {
    for (int i = 0; i < _pages.length; i++) {
      if (_pages[i]['status'] == PrintStatus.failed ||
          _pages[i]['status'] == PrintStatus.cancelled) {
        await _retryJob(i);
      }
    }
  }

  Future<void> _retryJob(int index) async {
    String? oldJobId = _pages[index]['jobId'];
    if (oldJobId != null) {
      try {
        String? newJobId = await _printPlugin.retryPrintJob(oldJobId);
        if (newJobId != null) {
          _pages[index]['status'] = PrintStatus.printing;
          _pages[index]['jobId'] = newJobId;
          _activeJobs[newJobId] = _activeJobs[oldJobId]!;
          _activeJobs.remove(oldJobId);
          notifyListeners();
        } else {
          throw Exception('Failed to retry job');
        }
      } catch (e) {
        print('Error retrying job for page $index: $e');
        _pages[index]['status'] = PrintStatus.failed;
        notifyListeners();
      }
    } else {
      // If there's no job ID, try to print the page again
      await _printPage(index);
    }
  }

  Future<void> printPdf(String pdfPath) async {
    final document = await pdf_render.PdfDocument.openFile(pdfPath);
    final pageCount = document.pageCount;

    for (int i = 0; i < pageCount; i++) {
      try {
        String? jobId = await _printPlugin.printPdf(pdfPath);
        print('Job ID: $jobId');
        addPage('PDF Page ${i + 1}');
        _pages.last['status'] = PrintStatus.printing;
        _pages.last['jobId'] = jobId;
        _activeJobs[jobId!] = JobStatus(
          pages: 1,
          copies: 1,
          creationTime: DateTime.now(),
          isBlocked: false,
          isCancelled: false,
          isCompleted: false,
          isFailed: false,
          isQueued: true,
          isStarted: false,
        );
        notifyListeners();
      } catch (e) {
        addPage('PDF Page ${i + 1}');
        _pages.last['status'] = PrintStatus.failed;
        notifyListeners();
        print('Error printing PDF page ${i + 1}: $e');
      }
    }
  }

  void _startStatsTimer() {
    _statsTimer = Timer.periodic(const Duration(seconds: 5), (timer) async {
      await _updatePrintStats();
      await PrintProgressListener().startListening();
    });
  }

  Future<void> _updatePrintStats() async {
    try {
      final result = await _printPlugin.getPrintStats();
      if (result is Map) {
        final Map<String, dynamic> stats = Map<String, dynamic>.from(
            result!.map((key, value) => MapEntry(key.toString(), value)));
        _totalPagesPrinted = stats['totalPagesPrinted'] as int? ?? 0;
        _totalPagesUnprinted = stats['totalPagesUnprinted'] as int? ?? 0;

        final jobStatsRaw = stats['jobs'];
        if (jobStatsRaw is Map) {
          _activeJobs.clear();
          jobStatsRaw.forEach((key, value) {
            if (value is Map) {
              final jobDetails = Map<String, dynamic>.from(
                  value.map((k, v) => MapEntry(k.toString(), v)));
              _activeJobs[key.toString()] = JobStatus(
                  pages: jobDetails['pages'] as int,
                  copies: jobDetails['copies'] as int,
                  creationTime: DateTime.fromMillisecondsSinceEpoch(
                      jobDetails['creationTime'] as int),
                  isBlocked: jobDetails['isBlocked'] as bool? ?? false,
                  isCancelled: jobDetails['isCancelled'] as bool? ?? false,
                  isCompleted: jobDetails['isCompleted'] as bool? ?? false,
                  isFailed: jobDetails['isFailed'] as bool? ?? false,
                  isQueued: jobDetails['isQueued'] as bool? ?? false,
                  isStarted: jobDetails['isStarted'] as bool? ?? false);
            }
          });
        }
      }
      notifyListeners();
    } catch (e) {
      print('Error updating print stats: $e');
    }
  }

  List<Map<String, dynamic>> get printedPages {
    return _pages
        .where((page) => page['status'] == PrintStatus.printed)
        .toList();
  }

  List<Map<String, dynamic>> get unprintedPages {
    return _pages
        .where((page) => page['status'] != PrintStatus.printed)
        .toList();
  }

  Map<String, dynamic>? getPage(int index) {
    if (index >= 0 && index < _pages.length) {
      return Map.from(_pages[index]);
    }
    return null;
  }

  void reset() {
    _pages.clear();
    _activeJobs.clear();
    _totalPagesPrinted = 0;
    _totalPagesUnprinted = 0;
    notifyListeners();
  }

  void dispose() {
    _statsTimer?.cancel();
  }

  void notifyListeners() {
    // Notify listeners of state changes
  }

  int get totalPagesPrinted => _totalPagesPrinted;

  int get totalPagesUnprinted => _totalPagesUnprinted;
}

class JobStatus {
  final int pages;
  final int copies;
  final DateTime creationTime;
  final bool isBlocked;
  final bool isCancelled;
  final bool isCompleted;
  final bool isFailed;
  final bool isQueued;
  final bool isStarted;

  JobStatus({
    required this.pages,
    required this.copies,
    required this.creationTime,
    required this.isBlocked,
    required this.isCancelled,
    required this.isCompleted,
    required this.isFailed,
    required this.isQueued,
    required this.isStarted,
  });
}


class PrintProgressListener {
  static const MethodChannel _channel = MethodChannel('cs50sdkupdate');

   startListening() {
    _channel.setMethodCallHandler(_handleMethodCall);
  }

  Future<dynamic> _handleMethodCall(MethodCall call) async {
    switch (call.method) {
      case 'onPrintProgress':
        final int currentPage = call.arguments['currentPage'];
        final int totalPages = call.arguments['totalPages'];
        final int totalBytesWritten = call.arguments['totalBytesWritten'];
        // Use these values to update your UI or logic
        print('Current page: $currentPage, Total pages: $totalPages, Bytes written: $totalBytesWritten');
        break;
      default:
        print('Unhandled method call: ${call.method}');
        break;
    }
  }
}
