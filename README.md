# CS50 SDK Update Package

Welcome to the **CS50 SDK Update Package** for Flutter. This package provides a comprehensive set of tools to manage various functions for the CS50 POS (Point of Sale) system. It includes capabilities for platform version retrieval, PICC operations, command execution, and advanced printer management.

## Table of Contents
- [Installation](#installation)
- [Usage](#usage)
- [Features](#features)
    - [Initialization](#initialization)
    - [PICC Operations](#picc-operations)
    - [Command Execution](#command-execution)
    - [Printer Initialization](#printer-initialization)
    - [Printer Settings](#printer-settings)
    - [Printing Operations](#printing-operations)
    - [Printer Utilities](#printer-utilities)
- [Examples](#examples)
- [Contributing](#contributing)
- [License](#license)

## Installation

Add the following dependency to your `pubspec.yaml` file:

```yaml
dependencies:
  cs50sdkupdate: ^1.0.0
```

Then, run `flutter pub get` to install the package.

## Usage

Import the package into your Dart code:

```dart
import 'package:cs50sdkupdate/cs50sdkupdate.dart';
```

Initialize the plugin and use its functions within your Flutter app. Here's a basic example:

```dart
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:cs50sdkupdate/cs50sdkupdate.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'CS50 SDK Update Demo',
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
  final _cs50sdkupdatePlugin = Cs50sdkupdate();

  @override
  void initState() {
    super.initState();
  }

  // Define your functions to use the plugin's capabilities here.

  @override
  Widget build(BuildContext context) {
    // Build your UI here.
  }
}
```

## Features

### Initialization

Initialize the platform and various components:

- **Get Platform Version**: Retrieve the platform version.
- **Initialize PICC**: Initialize the PICC.

### PICC Operations

Perform operations related to the PICC:

- **Close PICC**: Close the PICC.
- **Remove PICC**: Remove the PICC.
- **PICC Check**: Check the PICC status.
- **Start Polling**: Start polling for PICC data.

### Command Execution

Execute various system and NFC commands:

- **Execute Commands**: Run system commands.
- **SAM AV2 Init**: Initialize SAM AV2.
- **Execute NFC**: Execute NFC commands.

### Printer Initialization

Initialize and configure the printer:

- **Initialize Printer**: Initialize the printer.
- **Init with Params**: Initialize the printer with specific parameters.

### Printer Settings

Configure the printer settings:

- **Set Font**: Set the printer font.
- **Set Gray**: Set the gray level for printing.
- **Set Space**: Configure the spacing.
- **Get Font**: Retrieve the current font settings.
- **Set Step**: Set the step value for printing.
- **Set Voltage**: Configure the printer voltage.
- **Set Charge**: Set the charge status.
- **Set Line Pixel**: Configure the line pixel distance.
- **Set Left Indent**: Set the left indent for printing.
- **Set Alignment**: Configure the text alignment.
- **Set Char Space**: Set character spacing.
- **Set Line Space**: Configure line spacing.
- **Set Left Space**: Set left space for printing.
- **Set Speed**: Configure the printing speed.
- **Set Mode**: Set the print mode.
- **Set Underline**: Enable or disable underline.
- **Set Reverse**: Enable or disable reverse printing.
- **Set Bold**: Enable or disable bold printing.

### Printing Operations

Perform various printing tasks:

- **Print Text**: Print text strings.
- **Print Bitmap**: Print bitmap images.
- **Print Barcode**: Print barcodes.
- **Print QR Code**: Print QR codes.
- **Print QR with Text**: Print QR codes with accompanying text.
- **Start Print**: Start the printing process.
- **Print Logo**: Print a logo.
- **Print All Text**: Print all queued text.
- **Print PDF**: Print PDF files.
- **Print Existing PDF**: Print existing PDF files.
- **Create & Print Simple PDF**: Create and print simple PDF documents.

### Printer Utilities

Utility functions for the printer:

- **Check Status**: Check the printer status.
- **Feed Paper**: Feed paper through the printer.
- **Locate Label**: Locate the label position.
- **Print Status**: Display the print status.

## Examples

Here is a complete example demonstrating various features:

```dart
import 'dart:io';

import 'package:cs50sdkupdate_example/pdf_printer.dart';
import 'package:cs50sdkupdate_example/print_status_screen.dart';
import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:cs50sdkupdate/cs50sdkupdate.dart';
import 'package:path_provider/path_provider.dart';
import 'editor.dart';

void main() {
  runApp(const MyApp());
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

  @override
  void initState() {
    super.initState();
    _printJobManager = PrintJobManager(_cs50sdkupdatePlugin);

  }
  Future<void> _getPlatformVersion() async {
    String platformVersion;
    try {
      platformVersion = await _cs50sdkupdatePlugin.getPlatformVersion() ?? 'Unknown platform version';
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

  Future<void> startPolling() async {
    String pollingData;
    try {
      pollingData = await _cs50sdkupdatePlugin.piccPolling() ?? 'No data';
      _showSnackBar('Polling data: $pollingData');
    } on PlatformException {
      _showSnackBar('Failed to poll data.');
      pollingData = 'Failed to poll data.';
    }

    setState(() {
      _pollingData = pollingData;
    });
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
      String? samInitResponse = await _cs50sdkupdatePlugin.piccSamAv2Init(samSlotNo, samHostKey);
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
      String? nfcResponse = await _cs50sdkupdatePlugin.piccNfc(nfcDataLen, technology, nfcUid, ndefMessage);
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
      await _cs50sdkupdatePlugin.printStr('Hello, World!\n');
      await _cs50sdkupdatePlugin.printStr('This is a test print.\n');

      await _printJobManager.printAllPages();
      _showSnackBar('Text printed successfully');
    } catch (e) {
      _showSnackBar('Failed to print text: $e');
    }
  }

  Future<void> _printBarcode() async {
    try {
      await _cs50sdkupdatePlugin.printBarcode('1234567890', 300, 100, 'CODE_128');
      await _cs50sdkupdatePlugin.printStart();
      _showSnackBar('Barcode printed successfully');
    } catch (e) {
      _showSnackBar('Failed to print barcode: $e');
    }
  }

  Future<void> _printQRCode() async {
    try {
      await _cs50sdkupdatePlugin.printQrCode('https://example.com', 200, 200, 'QR_CODE');
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
      await _cs50sdkupdatePlugin.printCutQrCodeStr('https://example.com', 'Scan me!', 10, 200, 200, 'QR_CODE');
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
      await _cs50sdkupdatePlugin.printSetAlign(1); // 0: left, 1: center, 2: right
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
      await _cs50sdkupdatePlugin.printSetMode(0); // 0: normal, 1: white on black
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
      Uint8List logoData =  Uint8List.fromList([
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
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    children: [
                      Text('Platform: $_platformVersion', style: const TextStyle(fontSize: 16)),
                      const SizedBox(height: 8),
                      Text('PICC Status: $_piccStatus', style: const TextStyle(fontSize: 16)),
                      const SizedBox(height: 8),
                      Text('Polling: $_pollingData', style: const TextStyle(fontSize: 16)),
                      const SizedBox(height: 8),
                      Text('Printer: $_printStatus', style: const TextStyle(fontSize: 16)),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 16),
              _buildSection('Initialization', [
                _buildButton(Icons.power_settings_new, 'Get Platform Version', _getPlatformVersion),
                _buildButton(Icons.nfc, 'Initialize PICC', initializePicc),
              ]),
              const SizedBox(height: 16),
              _buildSection('PICC Operations', [
                _buildButton(Icons.close, 'Close PICC', closePicc),
                _buildButton(Icons.remove_circle, 'Remove PICC', removePicc),
                _buildButton(Icons.check_circle, 'PICC Check', piccCheck),
                _buildButton(Icons.loop, 'Start Polling', startPolling),
              ]),
              const SizedBox(height: 16),
              _buildSection('Other Operations', [
                _buildButton(Icons.code, 'Execute Commands', executeCommands),
                _buildButton(Icons.memory, 'SAM AV2 Init', executePiccSamAv2Init),
                _buildButton(Icons.nfc, 'Execute NFC', executeNfc),
              ]),
              const SizedBox(height: 16),
              _buildSection('Printer Initialization', [
                _buildButton(Icons.print, 'Initialize Printer', _initPrinter),
                _buildButton(Icons.settings, 'Init with Params', _initPrinterWithParams),
              ]),
              const SizedBox(height: 16),
              _buildSection('Printer Settings', [
                _buildButton(Icons.font_download, 'Set Font', _printSetFont),
                _buildButton(Icons.format_color_fill, 'Set Gray', _printSetGray),
                _buildButton(Icons.space_bar, 'Set Space', _printSetSpace),
                _buildButton(Icons.font_download_outlined, 'Get Font', _printGetFont),
                _buildButton(Icons.height, 'Set Step', _printStep),
                _buildButton(Icons.battery_charging_full, 'Set Voltage', _printSetVoltage),
                _buildButton(Icons.power, 'Set Charge', _printIsCharge),
                _buildButton(Icons.linear_scale, 'Set Line Pixel', _printSetLinPixelDis),
                _buildButton(Icons.format_align_left, 'Set Left Indent', _printSetLeftIndent),
                _buildButton(Icons.format_align_center, 'Set Alignment', _printSetAlign),
                _buildButton(Icons.space_bar_outlined, 'Set Char Space', _printCharSpace),
                _buildButton(Icons.format_line_spacing, 'Set Line Space', _printSetLineSpace),
                _buildButton(Icons.format_indent_increase, 'Set Left Space', _printSetLeftSpace),
                _buildButton(Icons.speed, 'Set Speed', _printSetSpeed),
                _buildButton(Icons.mode, 'Set Mode', _printSetMode),
                _buildButton(Icons.format_underlined, 'Set Underline', _printSetUnderline),
                _buildButton(Icons.flip, 'Set Reverse', _printSetReverse),
                _buildButton(Icons.format_bold, 'Set Bold', _printSetBold),
                _buildButton(Icons.edit_note_outlined, 'Document Editor',_openEditor)
              ]),
              const SizedBox(height: 16),
              _buildSection('Printing Operations', [
                _buildButton(Icons.text_fields, 'Print Text', _printStr),
                _buildButton(Icons.image, 'Print Bitmap', _printBmp),
                _buildButton(Icons.qr_code, 'Print Barcode', _printBarcode),
                _buildButton(Icons.qr_code_2, 'Print QR Code', _printQRCode),
                _buildButton(Icons.qr_code_scanner, 'Print QR with Text', _printCutQRCodeStr),
                _buildButton(Icons.play_arrow, 'Start Print', _printStart),
                _buildButton(Icons.logo_dev, 'Print Logo', _printLogo),
                _buildButton(Icons.text_snippet_rounded, 'Print All Text', _printText),
                _buildButton(Icons.picture_as_pdf, 'Print PDF', _printPdf),
                _buildButton(Icons.picture_as_pdf, 'Print Existing PDF', _printExistingPdf),
                _buildButton(Icons.add_to_photos, 'Create & Print Simple PDF', _createAndPrintSimplePdf),

              ]),
              const SizedBox(height: 16),
              _buildSection('Printer Utilities', [
                _buildButton(Icons.check_circle_outline, 'Check Status', _printCheckStatus),
                _buildButton(Icons.vertical_align_bottom, 'Feed Paper', _printFeedPaper),
                _buildButton(Icons.label_outline, 'Locate Label', _printLabLocate),
                _buildButton(Icons.list, 'Print Status', _openPrintStatusScreen),
              ]),
            ],
          ),
        ),
      ),
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
            Text(title, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
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

  void _openEditor() {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => CustomDocumentEditor(
          printPlugin: _cs50sdkupdatePlugin,
          jobManager: _printJobManager,
        ),
      ),
    );
  }

  void _openPrintStatusScreen() {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => ModernPrintStatusScreen(jobManager: _printJobManager),
      ),
    );
  }

  Future<void> _printExistingPdf() async {
    try {
      // For this example, we'll use a PDF file from the assets folder
      // Make sure to add a PDF file to your assets and update the pubspec.yaml accordingly
      final byteData = await rootBundle.load('assets/KAT_03_07_2024_18-Batches.pdf');
      final tempDir = await getTemporaryDirectory();
      final tempFile = File('${tempDir.path}/temp.pdf');
      await tempFile.writeAsBytes(byteData.buffer.asUint8List());

      await _printJobManager.printPdf(tempFile.path);
      _showSnackBar('PDF printing started');

      // Open the print status screen
      _openPrintStatusScreen();
    } catch (e) {
      _showSnackBar('Failed to print PDF: $e');
    }
  }

  Future<void> _createAndPrintSimplePdf() async {
    try {
      _showSnackBar('Simple PDF created and printing started');
      // _pdfPrinter.createAndPrintSimplePdf();
      _printJobManager.printImagePdf();
      // Open the print status screen
      _openPrintStatusScreen();
    } catch (e) {
      _showSnackBar('Failed to create and print simple PDF: $e');
    }
  }

  Future<void> _printPdf() async {
    try {
      // For this example, we'll use a PDF file from the assets folder
      // Make sure to add a PDF file to your assets and update the pubspec.yaml accordingly
      final byteData = await rootBundle.load('assets/KAT_03_07_2024_18-Batches.pdf');
      final tempDir = await getTemporaryDirectory();
      final tempFile = File('${tempDir.path}/temp.pdf');
      await tempFile.writeAsBytes(byteData.buffer.asUint8List());

      await _printJobManager.printPdf(tempFile.path);
      //
      // await _pdfPrinter.printPdf(tempFile.path);
      // await _pdfPrinter.printImagePdf();
      _showSnackBar('PDF printing started');

      // Open the print status screen
      _openPrintStatusScreen();
    } catch (e) {
      _showSnackBar('Failed to print PDF: $e');
    }
  }


}

```

## Contributing

Contributions are welcome! Please submit a pull request or file an issue on the [GitHub repository](https://github.com/your-repo).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
