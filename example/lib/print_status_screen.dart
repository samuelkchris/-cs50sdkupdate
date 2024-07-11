import 'dart:async';
import 'package:cs50sdkupdate_example/pdf_printer.dart';
import 'package:cs50sdkupdate_example/printer_counter.dart';
import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';

enum PrintStatus { pending, printed, failed }

class ModernPrintStatusScreen extends StatefulWidget {
  final PrintJobManager jobManager;

  const ModernPrintStatusScreen({Key? key, required this.jobManager}) : super(key: key);

  @override
  _ModernPrintStatusScreenState createState() => _ModernPrintStatusScreenState();
}

class _ModernPrintStatusScreenState extends State<ModernPrintStatusScreen> {
  late StreamController<List<Map<String, dynamic>>> _pagesStreamController;

  @override
  void initState() {
    super.initState();
    _pagesStreamController = StreamController<List<Map<String, dynamic>>>.broadcast();
    _initializeStream();
  }

  void _initializeStream() {
    // Initial update
    _updatePages();

    // Set up periodic updates (every 5 seconds)
    Timer.periodic(const Duration(seconds: 1), (_) => _updatePages());
  }

  void _updatePages() {
    List<Map<String, dynamic>> pages = List.generate(
      widget.jobManager.pagesCount,
          (index) => widget.jobManager.getPage(index) ?? {},
    );
    _pagesStreamController.add(pages);
  }

  @override
  void dispose() {
    _pagesStreamController.close();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Print Job Status'),
        elevation: 0,
        backgroundColor: Colors.transparent,
        foregroundColor: Colors.black,
      ),
      backgroundColor: Colors.grey[100],
      body: SafeArea(
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: StreamBuilder<List<Map<String, dynamic>>>(
                stream: _pagesStreamController.stream,
                builder: (context, snapshot) {
                  if (!snapshot.hasData) return const CircularProgressIndicator();
                  int printedPages = snapshot.data!.where((page) => page['status'] == PrintStatus.printed).length;
                  return CircularPrintCounter(
                    totalJobs: widget.jobManager.pagesCount,
                    printedJobs: printedPages,
                  );
                },
              ),
            ),
            const SizedBox(height: 20),
            _buildSummaryCards(),
            Expanded(
              child: _buildPageList(),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSummaryCards() {
    return StreamBuilder<List<Map<String, dynamic>>>(
      stream: _pagesStreamController.stream,
      builder: (context, snapshot) {
        if (!snapshot.hasData) return const SizedBox.shrink();
        int totalPages = snapshot.data!.length;
        int printedPages = snapshot.data!.where((page) => page['status'] == PrintStatus.printed).length;
        int unprintedPages = totalPages - printedPages;

        return SizedBox(
          height: 135,
          child: ListView(
            scrollDirection: Axis.horizontal,
            padding: const EdgeInsets.all(10),
            children: [
              _buildSummaryCard('Total Pages', totalPages.toString(), Colors.blue, Icons.description),
              _buildSummaryCard('Printed', printedPages.toString(), Colors.green, Icons.check_circle),
              _buildSummaryCard('Unprinted', unprintedPages.toString(), Colors.orange, Icons.error),
            ],
          ),
        );
      },
    );
  }

  Widget _buildSummaryCard(String title, String value, Color color, IconData icon) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Container(
        width: 100,
        height: 100,
        padding: const EdgeInsets.all(16),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, size: 20, color: color),
            const SizedBox(height: 8),
            Text(title, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold)),
            Text(value, style: TextStyle(fontSize: 20, fontWeight: FontWeight.w900, color: color)),
          ],
        ),
      ),
    ).animate().fade().scale(delay: 100.ms, duration: 300.ms);
  }

  Widget _buildPageList() {
    return StreamBuilder<List<Map<String, dynamic>>>(
      stream: _pagesStreamController.stream,
      builder: (context, snapshot) {
        if (!snapshot.hasData) return const Center(child: CircularProgressIndicator());
        return ListView.builder(
          itemCount: snapshot.data!.length,
          padding: const EdgeInsets.all(16),
          itemBuilder: (context, index) {
            var page = snapshot.data![index];
            return _buildPageCard(index, page);
          },
        );
      },
    );
  }

  Widget _buildPageCard(int index, Map<String, dynamic> page) {
    Color statusColor;
    IconData statusIcon;

    switch (page['status']) {
      case PrintStatus.printed:
        statusColor = Colors.green;
        statusIcon = Icons.check_circle;
        break;
      case PrintStatus.failed:
        statusColor = Colors.red;
        statusIcon = Icons.error;
        break;
      default:
        statusColor = Colors.orange;
        statusIcon = Icons.hourglass_empty;
    }

    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      margin: const EdgeInsets.only(bottom: 16),
      child: ListTile(
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        leading: CircleAvatar(
          backgroundColor: statusColor.withOpacity(0.2),
          child: Icon(statusIcon, color: statusColor),
        ),
        title: Text(
            page['content'].startsWith('PDF Page') ? page['content'] : 'Page ${index + 1}',
            style: const TextStyle(fontWeight: FontWeight.bold)
        ),
        subtitle: Text('Status: ${page['status'].toString().split('.').last}'),
        trailing: page['status'] == PrintStatus.failed
            ? ElevatedButton.icon(
          icon: const Icon(Icons.refresh),
          label: const Text('Retry'),
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.red,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
          ),
          onPressed: () async {
            try {
              await widget.jobManager.printPlugin.printStr(page['content']);
              await widget.jobManager.printPlugin.printStart();
              _updatePages();
            } catch (e) {
              print('Error retrying print: $e');
            }
          },
        )
            : null,
      ),
    ).animate().fade().slideX(delay: (100 * index).ms, duration: 300.ms);
  }
}
