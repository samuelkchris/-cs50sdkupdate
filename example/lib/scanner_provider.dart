import 'dart:async';
import 'package:cs50sdkupdate/cs50sdkupdate_method_channel.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:cs50sdkupdate/cs50sdkupdate.dart';

/// A provider class for managing scanner state and results
class ScannerDataProvider extends ChangeNotifier {
  /// Instance of the CS50 SDK update
  final Cs50sdkupdate _cs50sdkupdate = Cs50sdkupdate();

  /// Method channel for communicating with native code
  static const platform = MethodChannel('cs50sdkupdate');

  /// Stream subscription for scan results
  StreamSubscription<ScanResult>? _scanSubscription;

  /// Current scanner power state
  bool _isPowered = false;

  /// Current scanning state
  bool _isScanning = false;

  /// Current scanning mode (normal or continuous)
  bool _isContinuousMode = false;

  /// Last scan result
  String _lastScanResult = '';

  /// List of scan history
  List<ScanResult> _scanHistory = [];

  /// Maximum history items to keep
  static const int _maxHistoryItems = 50;

  /// Error message if any
  String? _errorMessage;

  /// Getters for the state variables
  bool get isPowered => _isPowered;

  bool get isScanning => _isScanning;

  bool get isContinuousMode => _isContinuousMode;

  String get lastScanResult => _lastScanResult;

  List<ScanResult> get scanHistory => _scanHistory;

  String? get errorMessage => _errorMessage;

  /// Constructor that initializes the scanner
  ScannerDataProvider() {
    _initializeScanner();
  }

  /// Initializes the scanner and sets up listeners
  Future<void> _initializeScanner() async {
    try {
      await _cs50sdkupdate.initialize();
      platform.setMethodCallHandler(_handleMethod);

      _scanSubscription = _cs50sdkupdate.scanResults.listen(
        _handleScanResult,
        onError: _handleError,
      );

// Initialize scanner in powered off state
      await Cs50sdkupdate.closeScanner();
      _isPowered = false;
      notifyListeners();
    } catch (e) {
      _handleError(e);
    }
  }

  /// Handles incoming scan results
  void _handleScanResult(ScanResult result) {
    _lastScanResult = result.result;
    _addToHistory(result);

    if (!_isContinuousMode) {
      _isScanning = false;
    }

    _errorMessage = null;
    notifyListeners();
  }

  /// Adds a scan result to history
  void _addToHistory(ScanResult result) {
    _scanHistory = [result, ..._scanHistory];
    if (_scanHistory.length > _maxHistoryItems) {
      _scanHistory = _scanHistory.sublist(0, _maxHistoryItems);
    }
  }

  /// Handles errors
  void _handleError(dynamic error) {
    _errorMessage = error.toString();
    _isScanning = false;
    notifyListeners();
  }

  /// Handles method calls from the native side
  Future<dynamic> _handleMethod(MethodCall call) async {
    switch (call.method) {
      case 'onScanResult':
        final result = ScanResult(
          result: call.arguments['result'] as String,
          length: call.arguments['length'] as int,
          encodeType: call.arguments['encodeType'] as int,
        );
        _handleScanResult(result);
        break;
      default:
        print('Unhandled method ${call.method}');
    }
  }

  /// Powers on the scanner
  Future<void> powerOn() async {
    try {
      await Cs50sdkupdate.openScanner();
      _isPowered = true;
      _errorMessage = null;
      notifyListeners();
    } catch (e) {
      _handleError(e);
    }
  }

  /// Powers off the scanner
  Future<void> powerOff() async {
    try {
      await Cs50sdkupdate.closeScanner();
      _isPowered = false;
      _isScanning = false;
      _errorMessage = null;
      notifyListeners();
    } catch (e) {
      _handleError(e);
    }
  }

  /// Starts scanning
  Future<void> startScanning() async {
    if (!_isPowered) {
      _handleError('Scanner is not powered on');
      return;
    }

    try {
      await Cs50sdkupdate.startScanner();
      _isScanning = true;
      _errorMessage = null;
      notifyListeners();
    } catch (e) {
      _handleError(e);
    }
  }

  /// Stops scanning
  Future<void> stopScanning() async {
    try {
      await Cs50sdkupdate.stopScanner();
      _isScanning = false;
      _errorMessage = null;
      notifyListeners();
    } catch (e) {
      _handleError(e);
    }
  }

  /// Sets the scanning mode
  Future<void> setScanMode(bool continuous) async {
    try {
      await Cs50sdkupdate.setScannerMode(continuous ? 1 : 0);
      _isContinuousMode = continuous;
      _errorMessage = null;
      notifyListeners();
    } catch (e) {
      _handleError(e);
    }
  }

  /// Clears the scan history
  void clearHistory() {
    _scanHistory = [];
    notifyListeners();
  }

  /// Disposes of the provider resources
  @override
  void dispose() {
    _scanSubscription?.cancel();
    stopScanning();
    powerOff();
    super.dispose();
  }
}
