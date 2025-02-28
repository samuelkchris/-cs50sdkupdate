import 'dart:async';

import 'package:cs50sdkupdate/cs50sdkupdate.dart';
import 'package:flutter/material.dart';
import 'package:pdfx/pdfx.dart' as pdf_render;

class PrintJobManager {
  final Cs50sdkupdate _printPlugin;
  final List<PrintJob> _printJobs = [];
  final StreamController<List<PrintJob>> _jobsController = StreamController<List<PrintJob>>.broadcast();
  StreamSubscription<PrintProgress>? _progressSubscription;

  PrintJobManager(this._printPlugin) {
    _initializePlugin();
  }

  Stream<List<PrintJob>> get jobsStream => _jobsController.stream;

  int get jobsCount => _printJobs.length;

  Cs50sdkupdate get printPlugin => _printPlugin;

  Future<void> _initializePlugin() async {
    await _printPlugin.initialize();
    _progressSubscription = _printPlugin.progressStream.listen(_handleProgressUpdate);
  }

  void _handleProgressUpdate(PrintProgress progress) {
    // Find the active job and update it
    for (var job in _printJobs) {
      if (job.isActive) {
        job.currentPage = progress.currentPage;
        job.totalPages = progress.totalPages;
        job.status = _mapProgressTypeToStatus(progress.type);
        _notifyJobsChanged();
        break;
      }
    }
  }

  PrintJobStatus _mapProgressTypeToStatus(String type) {
    switch (type) {
      case 'processing':
        return PrintJobStatus.processing;
      case 'printing':
        return PrintJobStatus.printing;
      case 'retry':
        return PrintJobStatus.retrying;
      default:
        return PrintJobStatus.processing;
    }
  }

  Future<void> printText(String text) async {
    try {
      // Add a job to the list
      final job = PrintJob(
        id: 'txt-${DateTime.now().millisecondsSinceEpoch}',
        name: 'Text Document',
        content: text,
        createdAt: DateTime.now(),
        status: PrintJobStatus.queued,
      );

      _printJobs.add(job);
      _notifyJobsChanged();

      // Start the print job
      job.status = PrintJobStatus.printing;
      _notifyJobsChanged();

      await _printPlugin.printInit();
      await _printPlugin.printStr(text);
      await _printPlugin.printStart();

      job.status = PrintJobStatus.completed;
      _notifyJobsChanged();
    } catch (e) {
      _addErrorJob('Text Document', e.toString());
    }
  }

  Future<void> printPdf(String pdfPath) async {
    try {
      // Create document name from path
      final pathParts = pdfPath.split('/');
      final docName = pathParts.last;

      // Add a job to the list
      final job = PrintJob(
        id: 'pdf-${DateTime.now().millisecondsSinceEpoch}',
        name: docName,
        content: pdfPath,
        createdAt: DateTime.now(),
        status: PrintJobStatus.queued,
      );

      _printJobs.add(job);
      _notifyJobsChanged();

      // Start the print job
      job.status = PrintJobStatus.processing;
      _notifyJobsChanged();

      // Count pages in the PDF
      final document = await pdf_render.PdfDocument.openFile(pdfPath);
      job.totalPages = document.pagesCount;
      _notifyJobsChanged();

      // Print the PDF
      final result = await _printPlugin.printPdf(pdfPath);

      // Update job with result
      job.documentId = result.documentId;

      if (result.isSuccess) {
        job.status = PrintJobStatus.completed;
      } else if (result.isPartialSuccess) {
        job.status = PrintJobStatus.partiallyCompleted;
        job.failedPages = result.failedPages ?? [];
      } else if (result.isCancelled) {
        job.status = PrintJobStatus.cancelled;
      } else {
        job.status = PrintJobStatus.failed;
        job.error = result.message;
      }

      _notifyJobsChanged();
    } catch (e) {
      _addErrorJob(pdfPath.split('/').last, e.toString());
    }
  }

  Future<void> cancelJob(String jobId) async {
    final job = _findJobById(jobId);
    if (job != null && job.documentId != null) {
      try {
        await _printPlugin.cancelJob(job.documentId!);
        job.status = PrintJobStatus.cancelled;
        _notifyJobsChanged();
      } catch (e) {
        print('Error cancelling job: $e');
      }
    }
  }

  Future<void> retryFailedJobs() async {
    // Find jobs that are partially completed and have a documentId
    final jobsToRetry = _printJobs.where((job) =>
    job.status == PrintJobStatus.partiallyCompleted &&
        job.documentId != null).toList();

    if (jobsToRetry.isEmpty) return;

    for (var job in jobsToRetry) {
      try {
        job.status = PrintJobStatus.retrying;
        _notifyJobsChanged();

        final result = await _printPlugin.retryJob();

        if (result.isSuccess) {
          job.status = PrintJobStatus.completed;
          job.failedPages = [];
        } else if (result.isPartialSuccess) {
          job.status = PrintJobStatus.partiallyCompleted;
          job.failedPages = result.failedPages ?? [];
        } else {
          job.status = PrintJobStatus.failed;
          job.error = result.message;
        }

        _notifyJobsChanged();
      } catch (e) {
        job.status = PrintJobStatus.failed;
        job.error = e.toString();
        _notifyJobsChanged();
      }
    }
  }

  void _addErrorJob(String name, String error) {
    final job = PrintJob(
      id: 'err-${DateTime.now().millisecondsSinceEpoch}',
      name: name,
      content: '',
      createdAt: DateTime.now(),
      status: PrintJobStatus.failed,
      error: error,
    );

    _printJobs.add(job);
    _notifyJobsChanged();
  }

  PrintJob? _findJobById(String jobId) {
    try {
      return _printJobs.firstWhere((job) => job.id == jobId);
    } catch (e) {
      return null;
    }
  }

  void _notifyJobsChanged() {
    _jobsController.add(List.unmodifiable(_printJobs));
  }

  List<PrintJob> getCompletedJobs() {
    return _printJobs.where((job) =>
    job.status == PrintJobStatus.completed ||
        job.status == PrintJobStatus.partiallyCompleted).toList();
  }

  List<PrintJob> getActiveJobs() {
    return _printJobs.where((job) =>
    job.status == PrintJobStatus.queued ||
        job.status == PrintJobStatus.processing ||
        job.status == PrintJobStatus.printing ||
        job.status == PrintJobStatus.retrying).toList();
  }

  List<PrintJob> getFailedJobs() {
    return _printJobs.where((job) =>
    job.status == PrintJobStatus.failed).toList();
  }

  void reset() {
    _printJobs.clear();
    _notifyJobsChanged();
  }

  void dispose() {
    _progressSubscription?.cancel();
    _jobsController.close();
  }
}

/// Status of a print job
enum PrintJobStatus {
  queued,
  processing,
  printing,
  retrying,
  completed,
  partiallyCompleted,
  failed,
  cancelled
}

/// Represents a single print job
class PrintJob {
  final String id;
  final String name;
  final String content;
  final DateTime createdAt;
  PrintJobStatus status;
  String? documentId;
  String? error;
  int currentPage = 0;
  int totalPages = 0;
  List<int> failedPages = [];

  PrintJob({
    required this.id,
    required this.name,
    required this.content,
    required this.createdAt,
    required this.status,
    this.documentId,
    this.error,
  });

  /// Check if this job is currently being processed or printed
  bool get isActive =>
      status == PrintJobStatus.queued ||
          status == PrintJobStatus.processing ||
          status == PrintJobStatus.printing ||
          status == PrintJobStatus.retrying;

  /// Get the progress percentage (0.0 to 1.0)
  double get progress {
    if (totalPages <= 0) return 0.0;
    return currentPage / totalPages;
  }

  /// Get a human-readable status message
  String get statusMessage {
    switch (status) {
      case PrintJobStatus.queued:
        return 'Queued';
      case PrintJobStatus.processing:
        return 'Processing';
      case PrintJobStatus.printing:
        return 'Printing';
      case PrintJobStatus.retrying:
        return 'Retrying';
      case PrintJobStatus.completed:
        return 'Completed';
      case PrintJobStatus.partiallyCompleted:
        return 'Partially Completed';
      case PrintJobStatus.failed:
        return error ?? 'Failed';
      case PrintJobStatus.cancelled:
        return 'Cancelled';
    }
  }

  /// Get a color associated with the current status
  Color get statusColor {
    switch (status) {
      case PrintJobStatus.queued:
        return Colors.grey;
      case PrintJobStatus.processing:
      case PrintJobStatus.printing:
      case PrintJobStatus.retrying:
        return Colors.blue;
      case PrintJobStatus.completed:
        return Colors.green;
      case PrintJobStatus.partiallyCompleted:
        return Colors.orange;
      case PrintJobStatus.failed:
      case PrintJobStatus.cancelled:
        return Colors.red;
    }
  }

  /// Get an icon associated with the current status
  IconData get statusIcon {
    switch (status) {
      case PrintJobStatus.queued:
        return Icons.hourglass_empty;
      case PrintJobStatus.processing:
        return Icons.settings;
      case PrintJobStatus.printing:
        return Icons.print;
      case PrintJobStatus.retrying:
        return Icons.refresh;
      case PrintJobStatus.completed:
        return Icons.check_circle;
      case PrintJobStatus.partiallyCompleted:
        return Icons.warning;
      case PrintJobStatus.failed:
        return Icons.error;
      case PrintJobStatus.cancelled:
        return Icons.cancel;
    }
  }
}