import 'dart:async';
import 'dart:typed_data';
import 'dart:ui' as ui;

import 'package:cs50sdkupdate/cs50sdkupdate.dart';
import 'package:flutter/services.dart';
import 'package:image/image.dart' as img;
import 'package:pdf_render/pdf_render.dart' as pdf_render;
import 'package:printing/printing.dart';

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
        await _printPage(i);
      }
    }
  }

  Future<void> printPdf(String pdfPath) async {
    final document = await pdf_render.PdfDocument.openFile(pdfPath);
    final pageCount = document.pageCount;

    for (int i = 0; i < pageCount; i++) {
      try {
          String? jobId = await _printPlugin.printPdf(pdfPath);
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
    });
  }

  Future<void> _updatePrintStats() async {
    try {
      final result = await _printPlugin.getPrintStats();
      if (result is Map) {
        final Map<String, dynamic> stats = Map<String, dynamic>.from(result!.map((key, value) => MapEntry(key.toString(), value)));
        _totalPagesPrinted = stats['totalPagesPrinted'] as int? ?? 0;
        _totalPagesUnprinted = stats['totalPagesUnprinted'] as int? ?? 0;

        final jobStatsRaw = stats['jobs'];
        if (jobStatsRaw is Map) {
          _activeJobs.clear();
          jobStatsRaw.forEach((key, value) {
            if (value is Map) {
              final jobDetails = Map<String, dynamic>.from(value.map((k, v) => MapEntry(k.toString(), v)));
              _activeJobs[key.toString()] = JobStatus(
                  pages: jobDetails['pages'] as int,
                  copies: jobDetails['copies'] as int,
                  creationTime: DateTime.fromMillisecondsSinceEpoch(jobDetails['creationTime'] as int),
                  isBlocked: jobDetails['isBlocked'] as bool? ?? false,
                  isCancelled: jobDetails['isCancelled'] as bool? ?? false,
                  isCompleted: jobDetails['isCompleted'] as bool? ?? false,
                  isFailed: jobDetails['isFailed'] as bool? ?? false,
                  isQueued: jobDetails['isQueued'] as bool? ?? false,
                  isStarted: jobDetails['isStarted'] as bool? ?? false
              );
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