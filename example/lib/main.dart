import 'dart:async';
import 'dart:io';

import 'package:cs50sdkupdate/cs50sdkupdate.dart';
import 'package:cs50sdkupdate/cs50sdkupdate_method_channel.dart';
import 'package:cs50sdkupdate_example/pdf_printer.dart';
import 'package:cs50sdkupdate_example/print_history.dart';
import 'package:cs50sdkupdate_example/progress.dart';
import 'package:cs50sdkupdate_example/scanner_provider.dart';
import 'package:cs50sdkupdate_example/scannner_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';
import 'package:pdf/pdf.dart';
import 'package:pdf/widgets.dart' as pw;
import 'package:printing/printing.dart';
import 'package:provider/provider.dart';

import 'nfc_scanner.dart';

void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => ScannerDataProvider()),
      ],
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'CS50 SDK Update Demo',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primarySwatch: Colors.blue,
        brightness: Brightness.light,
        fontFamily: 'Roboto',
      ),
      home: const HomePage(),
    );
  }
}

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  String _platformVersion = 'Unknown';
  String _pollingData = 'No data';
  String _printStatus = 'Printer not initialized';
  String _piccStatus = 'PICC not initialized';
  final _cs50sdkupdatePlugin = Cs50sdkupdate();
  late PrintJobManager _printJobManager;
  List<PrintJob> _printJobs = [];
  String _lastScanResult = '';
  bool _isScanning = false;
  StreamSubscription? _scanSubscription;
  bool _isScannerPowered = false;
  bool _isContinuousMode = false;

  @override
  void initState() {
    super.initState();
    _initializePlugin();
    _printJobManager = PrintJobManager(_cs50sdkupdatePlugin);
  }

  Future<void> _initializePlugin() async {
    await _cs50sdkupdatePlugin.initialize();

    // Subscribe to scanner results
    _scanSubscription = _cs50sdkupdatePlugin.scanResultsStream.listen(
          (ScanResult result) {
        setState(() {
          _lastScanResult = result.result;
          _isScanning = false;
        });
        _showSnackBar('Scanned: ${result.result}');
      },
      onError: (error) {
        setState(() => _isScanning = false);
        _showSnackBar('Scan error: $error');
      },
    );

    // Subscribe to print jobs from print manager
    _printJobManager.jobsStream.listen((jobs) {
      setState(() {
        _printJobs = jobs;
      });
    });
  }

  @override
  void dispose() {
    if (_isScanning) {
      Cs50sdkupdate.stopScanner();
    }
    _scanSubscription?.cancel();
    _printJobManager.dispose();
    _cs50sdkupdatePlugin.dispose();
    super.dispose();
  }

  Future<void> _toggleScanning() async {
    try {
      if (_isScanning) {
        await Cs50sdkupdate.stopScanner();
        setState(() => _isScanning = false);
      } else {
        setState(() => _isScanning = true);
        await Cs50sdkupdate.startScanner();
      }
    } catch (e) {
      setState(() => _isScanning = false);
      _showSnackBar('Error toggling scanner: $e');
    }
  }

  Future<void> _setScannerMode(bool continuous) async {
    try {
      await Cs50sdkupdate.setScannerMode(continuous ? 1 : 0);
      setState(() => _isContinuousMode = continuous);
      _showSnackBar('Scanner set to ${continuous ? 'continuous' : 'normal'} mode');
    } catch (e) {
      _showSnackBar('Error setting scanner mode: $e');
    }
  }

  Future<void> _toggleScannerPower() async {
    try {
      if (_isScannerPowered) {
        await Cs50sdkupdate.closeScanner();
        setState(() {
          _isScannerPowered = false;
          _isScanning = false;
        });
        _showSnackBar('Scanner powered off');
      } else {
        await Cs50sdkupdate.openScanner();
        setState(() {
          _isScannerPowered = true;
        });
        _showSnackBar('Scanner powered on');
      }
    } catch (e) {
      _showSnackBar('Error toggling scanner power: $e');
    }
  }

  Future<void> _refreshPrintJobs() async {
    // The jobs are now managed by PrintJobManager and updated via the stream
    _showSnackBar('Print jobs refreshed');
  }

  Future<void> _cancelJob(String jobId) async {
    try {
      await _printJobManager.cancelJob(jobId);
      _showSnackBar('Job cancelled successfully');
    } catch (e) {
      _showSnackBar('Failed to cancel job: $e');
    }
  }

  Future<void> _retryFailedJobs() async {
    try {
      await _printJobManager.retryFailedJobs();
      _showSnackBar('Retrying failed jobs');
    } catch (e) {
      _showSnackBar('Failed to retry jobs: $e');
    }
  }

  Future<void> _getPlatformVersion() async {
    String platformVersion;
    try {
      platformVersion = await _cs50sdkupdatePlugin.getPlatformVersion() ??
          'Unknown platform version';
      _showSnackBar('Platform version: $platformVersion');
    } on PlatformException {
      _showSnackBar('Failed to get platform version.');
      platformVersion = 'Failed to get platform version.';
    }

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  Future<void> initializePicc() async {
    try {
      await _cs50sdkupdatePlugin.openPicc();
      setState(() {
        _piccStatus = 'PICC initialized';
      });
      _showSnackBar('PICC initialized successfully.');
    } on PlatformException {
      setState(() {
        _piccStatus = 'Failed to initialize PICC';
      });
      _showSnackBar('Failed to initialize PICC.');
    }
  }

  Future<void> closePicc() async {
    try {
      await _cs50sdkupdatePlugin.piccClose();
      setState(() {
        _piccStatus = 'PICC closed';
      });
      _showSnackBar('PICC closed.');
    } on PlatformException {
      _showSnackBar('Failed to close PICC.');
    }
  }

  Future<void> removePicc() async {
    try {
      await _cs50sdkupdatePlugin.piccRemove();
      setState(() {
        _piccStatus = 'PICC removed';
      });
      _showSnackBar('PICC removed.');
    } on PlatformException {
      _showSnackBar('Failed to remove PICC.');
    }
  }

  Future<void> piccCheck() async {
    String piccCheckData;
    try {
      piccCheckData = await _cs50sdkupdatePlugin.piccCheck() ?? 'No data';
      _showSnackBar('PICC Check: $piccCheckData');
    } on PlatformException {
      _showSnackBar('Failed to check PICC.');
      piccCheckData = 'Failed to check PICC.';
    }

    setState(() {
      _pollingData = piccCheckData;
    });
  }

  Future<void> startNFCScanning() async {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => NFCScanner()),
    );
  }

  // Update the existing startPolling method to use the new startNFCScanning method
  Future<void> startPolling() async {
    await startNFCScanning();
  }


  Future<void> executeCommands() async {
    String? apiVersion = await _cs50sdkupdatePlugin.sysApiVerson();
    String? deviceId = await _cs50sdkupdatePlugin.getDeviceId();
    _showSnackBar('API version: $apiVersion\nDevice ID: $deviceId');
  }

  Future<void> executePiccSamAv2Init() async {
    int samSlotNo = 1;
    List<int> samHostKey = List.filled(16, 0);
    try {
      String? samInitResponse =
      await _cs50sdkupdatePlugin.piccSamAv2Init(samSlotNo, samHostKey);
      _showSnackBar('SAM AV2 Init Response: $samInitResponse');
    } catch (e) {
      _showSnackBar('Failed to initialize SAM AV2: $e');
    }
  }

  Future<void> executeNfc() async {
    List<int> nfcDataLen = List.filled(5, 0);
    List<int> technology = List.filled(25, 0);
    List<int> nfcUid = List.filled(56, 0);
    List<int> ndefMessage = List.filled(500, 0);

    try {
      String? nfcResponse = await _cs50sdkupdatePlugin.piccNfc(
          nfcDataLen, technology, nfcUid, ndefMessage);
      _showSnackBar('NFC Response: $nfcResponse');
    } catch (e) {
      _showSnackBar('Failed to execute NFC: $e');
    }
  }

  // Printing functions
  Future<void> _initPrinter() async {
    try {
      await _cs50sdkupdatePlugin.printInit();
      setState(() {
        _printStatus = 'Printer initialized';
      });
      _showSnackBar('Printer initialized');
    } catch (e) {
      setState(() {
        _printStatus = 'Failed to initialize printer: $e';
      });
      _showSnackBar('Failed to initialize printer: $e');
    }
  }

  Future<void> _printText() async {
    try {
      await _cs50sdkupdatePlugin.printInit();
      await _cs50sdkupdatePlugin.printStr('Hello, World!\n');
      await _cs50sdkupdatePlugin.printStr('This is a test print.\n');
      await _cs50sdkupdatePlugin.printStart();

      _showSnackBar('Text printed successfully');
    } catch (e) {
      _showSnackBar('Failed to print text: $e');
    }
  }

  Future<void> _printBarcode() async {
    try {
      await _cs50sdkupdatePlugin.printBarcode(
          '1234567890', 300, 100, 'CODE_128');
      await _cs50sdkupdatePlugin.printStart();
      _showSnackBar('Barcode printed successfully');
    } catch (e) {
      _showSnackBar('Failed to print barcode: $e');
    }
  }

  Future<void> _printQRCode() async {
    try {
      await _cs50sdkupdatePlugin.printQrCode(
          'https://example.com', 200, 200, 'QR_CODE');
      await _cs50sdkupdatePlugin.printStart();
      _showSnackBar('QR Code printed successfully');
    } catch (e) {
      _showSnackBar('Failed to print QR Code: $e');
    }
  }

  Future<void> _initPrinterWithParams() async {
    try {
      await _cs50sdkupdatePlugin.printInitWithParams(50, 24, 24, 0);
      _showSnackBar('Printer initialized with parameters');
    } catch (e) {
      _showSnackBar('Failed to initialize printer with parameters: $e');
    }
  }

  Future<void> _printSetFont() async {
    try {
      await _cs50sdkupdatePlugin.printSetFont(24, 24, 0);
      _showSnackBar('Font set successfully');
    } catch (e) {
      _showSnackBar('Failed to set font: $e');
    }
  }

  Future<void> _printSetGray() async {
    try {
      await _cs50sdkupdatePlugin.printSetGray(50);
      _showSnackBar('Gray level set successfully');
    } catch (e) {
      _showSnackBar('Failed to set gray level: $e');
    }
  }

  Future<void> _printSetSpace() async {
    try {
      await _cs50sdkupdatePlugin.printSetSpace(0, 0);
      _showSnackBar('Space set successfully');
    } catch (e) {
      _showSnackBar('Failed to set space: $e');
    }
  }

  Future<void> _printGetFont() async {
    try {
      String? font = await _cs50sdkupdatePlugin.printGetFont();
      _showSnackBar('Current font: $font');
    } catch (e) {
      _showSnackBar('Failed to get font: $e');
    }
  }

  Future<void> _printStep() async {
    try {
      await _cs50sdkupdatePlugin.printStep(100);
      _showSnackBar('Step printed successfully');
    } catch (e) {
      _showSnackBar('Failed to print step: $e');
    }
  }

  Future<void> _printSetVoltage() async {
    try {
      await _cs50sdkupdatePlugin.printSetVoltage(7);
      _showSnackBar('Voltage set successfully');
    } catch (e) {
      _showSnackBar('Failed to set voltage: $e');
    }
  }

  Future<void> _printIsCharge() async {
    try {
      await _cs50sdkupdatePlugin.printIsCharge(1);
      _showSnackBar('Charge status set successfully');
    } catch (e) {
      _showSnackBar('Failed to set charge status: $e');
    }
  }

  Future<void> _printSetLinPixelDis() async {
    try {
      await _cs50sdkupdatePlugin.printSetLinPixelDis(384);
      _showSnackBar('Line pixel distance set successfully');
    } catch (e) {
      _showSnackBar('Failed to set line pixel distance: $e');
    }
  }

  Future<void> _printStr() async {
    try {
      await _cs50sdkupdatePlugin.printStr('Hello, World!\n');
      _showSnackBar('Text printed successfully');
    } catch (e) {
      _showSnackBar('Failed to print text: $e');
    }
  }

  Future<void> _printBmp() async {
    // Note: You'll need to provide actual bitmap data
    try {
      Uint8List bmpData = Uint8List(0); // Replace with actual bitmap data
      await _cs50sdkupdatePlugin.printBmp(bmpData);
      _showSnackBar('Bitmap printed successfully');
    } catch (e) {
      _showSnackBar('Failed to print bitmap: $e');
    }
  }

  Future<void> _printCutQRCodeStr() async {
    try {
      await _cs50sdkupdatePlugin.printCutQrCodeStr(
          'https://example.com', 'Scan me!', 10, 200, 200, 'QR_CODE');
      _showSnackBar('QR Code with text printed successfully');
    } catch (e) {
      _showSnackBar('Failed to print QR Code with text: $e');
    }
  }

  Future<void> _printStart() async {
    try {
      await _cs50sdkupdatePlugin.printStart();
      _showSnackBar('Print started successfully');
    } catch (e) {
      _showSnackBar('Failed to start print: $e');
    }
  }

  Future<void> _printSetLeftIndent() async {
    try {
      await _cs50sdkupdatePlugin.printSetLeftIndent(10);
      _showSnackBar('Left indent set successfully');
    } catch (e) {
      _showSnackBar('Failed to set left indent: $e');
    }
  }

  Future<void> _printSetAlign() async {
    try {
      await _cs50sdkupdatePlugin
          .printSetAlign(1); // 0: left, 1: center, 2: right
      _showSnackBar('Alignment set successfully');
    } catch (e) {
      _showSnackBar('Failed to set alignment: $e');
    }
  }

  Future<void> _printCharSpace() async {
    try {
      await _cs50sdkupdatePlugin.printCharSpace(1);
      _showSnackBar('Character space set successfully');
    } catch (e) {
      _showSnackBar('Failed to set character space: $e');
    }
  }

  Future<void> _printSetLineSpace() async {
    try {
      await _cs50sdkupdatePlugin.printSetLineSpace(50);
      _showSnackBar('Line space set successfully');
    } catch (e) {
      _showSnackBar('Failed to set line space: $e');
    }
  }

  Future<void> _printSetLeftSpace() async {
    try {
      await _cs50sdkupdatePlugin.printSetLeftSpace(10);
      _showSnackBar('Left space set successfully');
    } catch (e) {
      _showSnackBar('Failed to set left space: $e');
    }
  }

  Future<void> _printSetSpeed() async {
    try {
      await _cs50sdkupdatePlugin.printSetSpeed(5);
      _showSnackBar('Print speed set successfully');
    } catch (e) {
      _showSnackBar('Failed to set print speed: $e');
    }
  }

  Future<void> _printCheckStatus() async {
    try {
      String? status = await _cs50sdkupdatePlugin.printCheckStatus();
      _showSnackBar('Printer status: $status');
    } catch (e) {
      _showSnackBar('Failed to check printer status: $e');
    }
  }

  Future<void> _printFeedPaper() async {
    try {
      await _cs50sdkupdatePlugin.printFeedPaper(100);
      _showSnackBar('Paper fed successfully');
    } catch (e) {
      _showSnackBar('Failed to feed paper: $e');
    }
  }

  Future<void> _printSetMode() async {
    try {
      await _cs50sdkupdatePlugin
          .printSetMode(0); // 0: normal, 1: white on black
      _showSnackBar('Print mode set successfully');
    } catch (e) {
      _showSnackBar('Failed to set print mode: $e');
    }
  }

  Future<void> _printSetUnderline() async {
    try {
      await _cs50sdkupdatePlugin.printSetUnderline(1); // 0: off, 1: on
      _showSnackBar('Underline set successfully');
    } catch (e) {
      _showSnackBar('Failed to set underline: $e');
    }
  }

  Future<void> _printSetReverse() async {
    try {
      await _cs50sdkupdatePlugin.printSetReverse(1); // 0: off, 1: on
      _showSnackBar('Reverse print set successfully');
    } catch (e) {
      _showSnackBar('Failed to set reverse print: $e');
    }
  }

  Future<void> _printSetBold() async {
    try {
      await _cs50sdkupdatePlugin.printSetBold(1); // 0: off, 1: on
      _showSnackBar('Bold print set successfully');
    } catch (e) {
      _showSnackBar('Failed to set bold print: $e');
    }
  }

  Future<void> _printLogo() async {
    // Note: You'll need to provide actual logo data
    try {
      Uint8List logoData = Uint8List.fromList([
        0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0x00, // Row 1
        0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x00, // Row 2
        0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, // Row 3
        0xFF, 0xFF, 0x00, 0x00, 0xFF, 0x00, 0x00, 0xFF, 0xFF, // Row 4
        0xFF, 0xFF, 0x00, 0x00, 0xFF, 0x00, 0x00, 0xFF, 0xFF, // Row 5
        0xFF, 0xFF, 0x00, 0x00, 0xFF, 0x00, 0x00, 0xFF, 0xFF, // Row 6
        0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, // Row 7
        0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x00, 0x00, // Row 8
        0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0x00, 0x00, 0x00, // Row 9
      ]);

      await _cs50sdkupdatePlugin.printLogo(logoData);
      _showSnackBar('Logo printed successfully');
    } catch (e) {
      _showSnackBar('Failed to print logo: $e');
    }
  }

  Future<void> _printLabLocate() async {
    try {
      await _cs50sdkupdatePlugin.printLabLocate(100);
      _showSnackBar('Label located successfully');
    } catch (e) {
      _showSnackBar('Failed to locate label: $e');
    }
  }

  void _showSnackBar(String message) {
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(
      content: Text(message),
      duration: const Duration(seconds: 3),
    ));
  }

  // Add scanner initialization method
  Future<void> _initializeScanner() async {
    try {
      await Cs50sdkupdate.closeScanner(); // Ensure scanner is off initially
      setState(() => _isScannerPowered = false);
      _showSnackBar('Scanner initialized');
    } catch (e) {
      _showSnackBar('Failed to initialize scanner: $e');
    }
  }

  // Add scanner control methods
  Future<void> _startScanning() async {
    try {
      setState(() => _isScanning = true);
      await Cs50sdkupdate.startScanner();
    } catch (e) {
      setState(() => _isScanning = false);
      _showSnackBar('Failed to start scanning: $e');
    }
  }

  Future<void> _stopScanning() async {
    try {
      await Cs50sdkupdate.stopScanner();
      setState(() => _isScanning = false);
    } catch (e) {
      _showSnackBar('Failed to stop scanning: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
        title: const Text('CS50 SDK Update Demo'),
    elevation: 0,
    ),
    body: Container(
    decoration: BoxDecoration(
    gradient: LinearGradient(
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    colors: [Colors.blue.shade100, Colors.blue.shade300],
    ),
    ),
    child: SingleChildScrollView(
    padding: const EdgeInsets.all(16.0),
    child: Column(
    crossAxisAlignment: CrossAxisAlignment.stretch,
    children: <Widget>[
    Card(
    elevation: 5,
    shape: RoundedRectangleBorder(
    borderRadius: BorderRadius.circular(15)),
    child: Padding(
    padding: const EdgeInsets.all(16.0),
    child: Column(
    children: [
    Text('Platform: $_platformVersion',
    style: const TextStyle(fontSize: 16)),
    const SizedBox(height: 8),
    Text('PICC Status: $_piccStatus',
    style: const TextStyle(fontSize: 16)),
    const SizedBox(height: 8),
    Text('Polling: $_pollingData',
    style: const TextStyle(fontSize: 16)),
    const SizedBox(height: 8),
    Text('Printer: $_printStatus',
    style: const TextStyle(fontSize: 16)),
    ],
    ),
    ),
    ),
    const SizedBox(height: 16),
    _buildSection('Initialization', [
    _buildButton(Icons.power_settings_new, 'Get Platform Version',
    _getPlatformVersion),
    _buildButton(Icons.nfc, 'Initialize PICC', initializePicc),
    _buildButton(Icons.scanner, 'Initialize Scanner', _initializeScanner),
    _buildButton(Icons.power_settings_new, 'Toggle Scanner Power',
    _toggleScannerPower),
    ]),

    // Add scanner section after initialization section
    const SizedBox(height: 16),
    _buildScannerSection(),
    const SizedBox(height: 16),
    _buildSection('PICC Operations', [
    _buildButton(Icons.close, 'Close PICC', closePicc),
    _buildButton(Icons.remove_circle, 'Remove PICC', removePicc),
    _buildButton(Icons.check_circle, 'PICC Check', piccCheck),
    _buildButton(Icons.loop, 'Start Polling', startNFCScanning),
    ]),
    const SizedBox(height: 16),
    _buildSection('Other Operations', [
    _buildButton(Icons.code, 'Execute Commands', executeCommands),
    _buildButton(
    Icons.memory, 'SAM AV2 Init', executePiccSamAv2Init),
    _buildButton(Icons.nfc, 'Execute NFC', executeNfc),
    ]),
    const SizedBox(height: 16),
    _buildSection('Printer Initialization', [
    _buildButton(Icons.print, 'Initialize Printer', _initPrinter),
    _buildButton(
    Icons.settings, 'Init with Params', _initPrinterWithParams),
    ]),
    const SizedBox(height: 16),
    _buildSection('Printer Settings', [
    _buildButton(Icons.font_download, 'Set Font', _printSetFont),
    _buildButton(
    Icons.format_color_fill, 'Set Gray', _printSetGray),
    _buildButton(Icons.space_bar, 'Set Space', _printSetSpace),
    _buildButton(
    Icons.font_download_outlined, 'Get Font', _printGetFont),
    _buildButton(Icons.height, 'Set Step', _printStep),
    _buildButton(Icons.battery_charging_full, 'Set Voltage',
    _printSetVoltage),
    _buildButton(Icons.power, 'Set Charge', _printIsCharge),
    _buildButton(
    Icons.linear_scale, 'Set Line Pixel', _printSetLinPixelDis),
    _buildButton(Icons.format_align_left, 'Set Left Indent',
    _printSetLeftIndent),
    _buildButton(
    Icons.format_align_center, 'Set Alignment', _printSetAlign),
    _buildButton(Icons.space_bar_outlined, 'Set Char Space',
    _printCharSpace),
    _buildButton(Icons.format_line_spacing, 'Set Line Space',
    _printSetLineSpace),
    _buildButton(Icons.format_indent_increase, 'Set Left Space',
    _printSetLeftSpace),
    _buildButton(Icons.speed, 'Set Speed', _printSetSpeed),
    _buildButton(Icons.mode, 'Set Mode', _printSetMode),
    _buildButton(Icons.format_underlined, 'Set Underline',
    _printSetUnderline),
    _buildButton(Icons.flip, 'Set Reverse', _printSetReverse),
    _buildButton(Icons.format_bold, 'Set Bold', _printSetBold),
    ]),
    const SizedBox(height: 16),
    _buildSection('Printing Operations', [
    _buildButton(Icons.text_fields, 'Print Text', _printStr),
    _buildButton(Icons.image, 'Print Bitmap', _printBmp),
    _buildButton(Icons.qr_code, 'Print Barcode', _printBarcode),
    _buildButton(Icons.qr_code_2, 'Print QR Code', _printQRCode),
    _buildButton(Icons.qr_code_scanner, 'Print QR with Text',
    _printCutQRCodeStr),
    _buildButton(Icons.play_arrow, 'Start Print', _printStart),
    _buildButton(Icons.logo_dev, 'Print Logo', _printLogo),
    _buildButton(
    Icons.text_snippet_rounded, 'Print All Text', _printText),
    _buildButton(Icons.picture_as_pdf, 'Print PDF', _printPdf),
    _buildButton(Icons.picture_as_pdf, 'Print Existing PDF',
    _printExistingPdf),
    _buildButton(Icons.add_to_photos, 'Create & Print Simple PDF',
    _createAndPrintSimplePdf),
    ]),
    const SizedBox(height: 16),
      _buildSection('Printer Utilities', [
        _buildButton(Icons.check_circle_outline, 'Check Status',
            _printCheckStatus),
        _buildButton(
            Icons.vertical_align_bottom, 'Feed Paper', _printFeedPaper),
        _buildButton(
            Icons.label_outline, 'Locate Label', _printLabLocate),
        _buildButton(
            Icons.list, 'Print Status', _viewPrintStatus),
      ]),
      const SizedBox(height: 16),
      _buildSection('Print Job Operations', [
        _buildButton(
            Icons.refresh, 'Refresh Print Jobs', _refreshPrintJobs),
        _buildButton(Icons.list, 'View Print History', _viewPrintJobs),
        _buildButton(Icons.refresh, 'Retry Failed Jobs', _retryFailedJobs),
      ]),
    ],
    ),
    ),
    ),
    );
  }

  void _viewPrintStatus() {
    // Show a dialog with active print jobs
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Print Status'),
        content: SizedBox(
          width: double.maxFinite,
          height: 300,
          child: ListView(
            children: _printJobs.map((job) => ListTile(
              title: Text(job.name),
              subtitle: Text(job.statusMessage),
              leading: Icon(job.statusIcon, color: job.statusColor),
              trailing: job.isActive ?
              IconButton(
                icon: const Icon(Icons.cancel),
                onPressed: () => _cancelJob(job.id),
              ) : null,
            )).toList(),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Close'),
          ),
        ],
      ),
    );
  }

  void _viewPrintJobs() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) =>  PrintHistoryScreen()),
    );
  }

  Widget _buildSection(String title, List<Widget> children) {
    return Card(
      elevation: 3,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(title,
                style:
                const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            const SizedBox(height: 12),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: children,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildButton(IconData icon, String label, VoidCallback onPressed) {
    return ElevatedButton.icon(
      icon: Icon(icon),
      label: Text(label),
      onPressed: onPressed,
      style: ElevatedButton.styleFrom(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(30)),
      ),
    );
  }

  // Add this to your build method, in the Column of sections
  Widget _buildScannerSection() {
    return _buildSection(
      'Scanner Operations',
      [
        _buildButton(
          _isScannerPowered ? Icons.power : Icons.power_off,
          _isScannerPowered ? 'Power Off Scanner' : 'Power On Scanner',
          _toggleScannerPower,
        ),
        _buildButton(
          Icons.scanner,
          'Scanner Screen',
              (){
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => ScannerScreen()),
            );
          },
        ),
        if (_isScannerPowered) ...[
          _buildButton(
            Icons.qr_code_scanner,
            _isScanning ? 'Stop Scanning' : 'Start Scanning',
            _isScanning ? _stopScanning : _startScanning,
          ),
          Card(
            elevation: 2,
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text(
                        'Last Scan Result:',
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      if (_isScanning)
                        const SizedBox(
                          width: 20,
                          height: 20,
                          child: CircularProgressIndicator(
                            strokeWidth: 2,
                          ),
                        ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Container(
                    width: double.infinity,
                    padding: const EdgeInsets.all(8),
                    decoration: BoxDecoration(
                      color: Colors.grey[200],
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Text(
                      _lastScanResult.isEmpty ? 'No scan yet' : _lastScanResult,
                      style: TextStyle(
                        fontSize: 14,
                        color: _lastScanResult.isEmpty ? Colors.grey : Colors.black,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
          _buildButton(
            Icons.settings,
            'Configure Scanner',
            _showScannerSettings,
          ),
        ],
      ],
    );
  }

  void _showScannerSettings() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Scanner Settings'),
        content: StatefulBuilder(
          builder: (context, setState) => Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              SwitchListTile(
                title: const Text('Continuous Mode'),
                subtitle: const Text('Scan multiple items without restarting'),
                value: _isContinuousMode,
                onChanged: (value) async {
                  await _setScannerMode(value);
                  setState(() => _isContinuousMode = value);
                  Navigator.pop(context);
                },
              ),
              const Divider(),
              ListTile(
                title: const Text('Reset Scanner'),
                subtitle: const Text('Reset to default settings'),
                onTap: () async {
                  await Cs50sdkupdate.configureScannerSettings(
                    trigMode: 0,
                    scanMode: 3,
                    scanPower: 1,
                    autoEnter: 1,
                  );
                  setState(() => _isContinuousMode = false);
                  Navigator.pop(context);
                  _showSnackBar('Scanner reset to default settings');
                },
              ),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Close'),
          ),
        ],
      ),
    );
  }

  Future<void> _printExistingPdf() async {
    try {
      // For this example, we'll use a PDF file from the assets folder
      // Make sure to add a PDF file to your assets and update the pubspec.yaml accordingly
      final byteData =
      await rootBundle.load('assets/KAT_03_07_2024_18-Batches.pdf');
      final tempDir = await getTemporaryDirectory();
      final tempFile = File('${tempDir.path}/temp.pdf');
      await tempFile.writeAsBytes(byteData.buffer.asUint8List());
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return Dialog(
            child: StatefulBuilder(
              builder: (BuildContext context, StateSetter setState) {
                return PrintProgressWidget(
                  pdfPath: tempFile.path,
                  onPrintComplete: (String message) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text(message)),
                    );
                  },
                );
              },
            ),
          );
        },
      );
    } catch (e) {
      _showSnackBar('Failed to print PDF: $e');
    }
  }

  Future<void> _createAndPrintSimplePdf() async {
    try {
      _showSnackBar('Creating simple PDF...');
      final results = await createAndPrintSimplePdf();
      final output = await getTemporaryDirectory();
      final file = File('${output.path}/example.pdf');
      await file.writeAsBytes(results);

      _showSnackBar('Simple PDF created, starting print job...');
      await _printJobManager.printPdf(file.path);
    } catch (e) {
      _showSnackBar('Failed to create and print simple PDF: $e');
    }
  }

  Future<Uint8List> createAndPrintSimplePdf() async {
    final pdf = pw.Document();

    pdf.addPage(
      pw.Page(
        pageFormat: const PdfPageFormat(42.0 * PdfPageFormat.mm, 111.0 * PdfPageFormat.mm),
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
                      style: pw.TextStyle(
                          fontSize: 40, fontWeight: pw.FontWeight.bold),
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
                  pw.Center(
                    child: pw.BarcodeWidget(
                      barcode: pw.Barcode.qrCode(),
                      data: 'https://www.example.com/ticket/12345',
                      width: 200,
                      height: 200,
                    ),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
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

  Future<void> _printPdf() async {
    try {
      final byteData =
      await rootBundle.load('assets/KAT_03_07_2024_18-Batches.pdf');
      final tempDir = await getTemporaryDirectory();
      final tempFile = File('${tempDir.path}/temp.pdf');
      await tempFile.writeAsBytes(byteData.buffer.asUint8List());

      await _printJobManager.printPdf(tempFile.path);

      _showSnackBar('PDF printing started');
      _viewPrintStatus();
    } catch (e) {
      _showSnackBar('Failed to print PDF: $e');
    }
  }

  Widget pdfPreviewWidget(Future<Uint8List> pdfFuture) {
    return PdfPreview(
      build: (format) => pdfFuture,
      canChangeOrientation: false,
      canChangePageFormat: false,
      canDebug: false,
      actions: const <PdfPreviewAction>[],
    );
  }
}