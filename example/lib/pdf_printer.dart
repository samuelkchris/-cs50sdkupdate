import 'dart:io';
import 'dart:typed_data';
import 'dart:ui' as ui;
import 'package:pdf_render/pdf_render.dart' as pdf_render;
import 'package:image/image.dart' as img;
import 'package:cs50sdkupdate/cs50sdkupdate.dart';
import 'package:cs50sdkupdate_example/print_status_screen.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';
import 'package:pdf/widgets.dart' as pw;
import 'package:pdf/pdf.dart';
import 'package:printing/printing.dart';


class PrintJobManager {
  final Cs50sdkupdate _printPlugin;
  final List<Map<String, dynamic>> _pages = [];

  PrintJobManager(this._printPlugin);

  int get pagesCount => _pages.length;

  Cs50sdkupdate get printPlugin => _printPlugin;

  void addPage(String content) {
    _pages.add({
      'content': content,
      'status': PrintStatus.pending,
    });
  }

  Future<void> printAllPages() async {
    for (int i = 0; i < _pages.length; i++) {
      try {
        await _printPlugin.printStr(_pages[i]['content']);
        await _printPlugin.printStart();
        _pages[i]['status'] = PrintStatus.printed;
      } catch (e) {
        _pages[i]['status'] = PrintStatus.failed;
      }
    }
  }



  Future<void> printPdf(String pdfPath) async {
    final document = await pdf_render.PdfDocument.openFile(pdfPath);
    final pageCount = document.pageCount;

    for (int i = 0; i < pageCount; i++) {
      try {
        final page = await document.getPage(i + 1);
        final pageImage = await page.render(
          width: page.width.toInt(),
          height: page.height.toInt(),
        );

        final pdfRaster = PdfRaster(
          pageImage.width,
          pageImage.height,
          pageImage.pixels,
        );

        final image = await pdfRaster.toImage();
        final byteData = await image.toByteData(format: ui.ImageByteFormat.rawRgba);

        if (byteData != null) {
          final buffer = byteData.buffer;
          final Uint8List rgbaBytes = buffer.asUint8List();

          // Debug: Print some pixel data
          print('First 10 pixels: ${rgbaBytes.sublist(0, 40)}');

          // Convert RGBA to RGB
          final img.Image rgbImage = img.Image.fromBytes(
            width: image.width,
            height: image.height,
            bytes: rgbaBytes.buffer,
            numChannels: 4,
          );

          // Resize image
          final resizedImage = img.copyResize(rgbImage, width: 384, height: 500);

          // Encode as PNG
          final List<int> pngBytes = img.encodePng(resizedImage);

          print('PNG Bytes length: ${pngBytes.length}');
          print('First 10 PNG bytes: ${pngBytes.sublist(0, 10)}');

          // Send PNG data to the printer
          await _printPlugin.printBmp(Uint8List.fromList(pngBytes));
          await _printPlugin.printStart();

          addPage('PDF Page ${i + 1}');
          _pages.last['status'] = PrintStatus.printed;
          print('PDF page ${i + 1} printed successfully');
        } else {
          throw Exception('Failed to get byte data from image');
        }
      } catch (e) {
        addPage('PDF Page ${i + 1}');
        _pages.last['status'] = PrintStatus.failed;
        print('Error printing PDF page ${i + 1}: $e');
      }
    }
  }


  Future<Uint8List> createAndPrintSimplePdf() async {
    final pdf = pw.Document(
      version: PdfVersion.pdf_1_5,
      compress: true,
      pageMode: PdfPageMode.fullscreen,
    );

    for (int i = 0; i < 5; i++) {
      pdf.addPage(
        index: 0,
        pw.Page(
          pageFormat: const PdfPageFormat(42.0, 111),
          margin: const pw.EdgeInsets.only(top: 10),
          build: (pw.Context context) {
            return pw.Center(
              child: pw.Container(
                padding: const pw.EdgeInsets.all(20),
                decoration: pw.BoxDecoration(
                  border: pw.Border.all(color: PdfColors.black, width: 2),
                  borderRadius: pw.BorderRadius.circular(10),
                ),
                child: pw.Column(
                  crossAxisAlignment: pw.CrossAxisAlignment.start,
                  children: [
                    pw.Center(
                      child: pw.Text(
                        'Train Ticket',
                        style: pw.TextStyle(fontSize: 40, fontWeight: pw.FontWeight.bold),
                      ),
                    ),
                    pw.SizedBox(height: 30),
                    pw.Divider(thickness: 2),
                    pw.SizedBox(height: 20),
                    _buildInfoRow('Passenger Name:', 'Samuel Ssekizinvu'),
                    _buildInfoRow('Departure Station:', 'Kampala'),
                    _buildInfoRow('Arrival Station:', 'Nairobi'),
                    _buildInfoRow('Departure Date:', '2022-12-25'),
                    _buildInfoRow('Train Number:', '12345'),
                    _buildInfoRow('Seat Number:', '12A'),
                    _buildInfoRow('Fare:', 'UGX 100,000'),
                    pw.SizedBox(height: 30),
                    if (i == 0) ...[
                      pw.Center(
                        child: pw.BarcodeWidget(
                          barcode: pw.Barcode.qrCode(),
                          data: 'https://www.example.com/ticket/12345',
                          width: 200,
                          height: 200,
                        ),
                      ),
                    ],
                  ],
                ),
              ),
            );
          },
        ),
      );
    }

    return pdf.save();
  }

  pw.Widget _buildInfoRow(String label, String value) {
    return pw.Padding(
      padding: const pw.EdgeInsets.symmetric(vertical: 5),
      child: pw.Row(
        children: [
          pw.Text(
            label,
            style: pw.TextStyle(fontSize: 20, fontWeight: pw.FontWeight.bold),
          ),
          pw.SizedBox(width: 10),
          pw.Text(
            value,
            style: const pw.TextStyle(fontSize: 20),
          ),
        ],
      ),
    );
  }

  Future<void> printImagePdf() async {
    final results = await createAndPrintSimplePdf();
    final output = await getTemporaryDirectory();
    final file = File('${output.path}/example.pdf');
    await file.writeAsBytes(results);
    await printPdf(file.path);
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
  }
}
