import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:cs50sdkupdate/cs50sdkupdate.dart';



class CardPaymentTestApp extends StatelessWidget {
  const CardPaymentTestApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Card & Payment Test',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        brightness: Brightness.light,
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.blue,
          brightness: Brightness.light,
        ),
      ),
      darkTheme: ThemeData(
        primarySwatch: Colors.blue,
        brightness: Brightness.dark,
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.blue,
          brightness: Brightness.dark,
        ),
      ),
      themeMode: ThemeMode.system,
      home: HomeScreen(),
    );
  }
}

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final Cs50sdkupdate _cs50sdkupdate = Cs50sdkupdate();
  bool _isInitialized = false;
  String _statusMessage = 'Initializing...';

  @override
  void initState() {
    super.initState();
    _initializeSDK();
  }

  Future<void> _initializeSDK() async {
    try {
      await _cs50sdkupdate.initialize();
      setState(() {
        _isInitialized = true;
        _statusMessage = 'SDK initialized successfully';
      });
    } catch (e) {
      setState(() {
        _statusMessage = 'Failed to initialize SDK: $e';
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Card & Payment Testing'),
        elevation: 0,
      ),
      body: _isInitialized
          ? _buildMainContent()
          : Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  CircularProgressIndicator(),
                  SizedBox(height: 16),
                  Text(_statusMessage),
                ],
              ),
            ),
    );
  }

  Widget _buildMainContent() {
    return Container(
      decoration: BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topCenter,
          end: Alignment.bottomCenter,
          colors: [
            Theme.of(context).colorScheme.surface,
            Theme.of(context).colorScheme.surfaceVariant,
          ],
        ),
      ),
      child: Center(
        child: SingleChildScrollView(
          padding: EdgeInsets.all(16),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: <Widget>[
              _buildFeatureCard(
                title: 'IC Card Operations',
                description: 'Test IC card reading/writing operations',
                icon: Icons.credit_card,
                color: Colors.blue,
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) => IcCardScreen(_cs50sdkupdate)),
                  );
                },
              ),
              SizedBox(height: 16),
              _buildFeatureCard(
                title: 'Magnetic Card',
                description: 'Test magnetic card reading operations',
                icon: Icons.credit_score,
                color: Colors.green,
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) =>
                            MagneticCardScreen(_cs50sdkupdate)),
                  );
                },
              ),
              SizedBox(height: 16),
              _buildFeatureCard(
                title: 'PIN & Payment',
                description: 'Test PIN entry and payment operations',
                icon: Icons.payment,
                color: Colors.orange,
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) => PinPaymentScreen(_cs50sdkupdate)),
                  );
                },
              ),
              SizedBox(height: 16),
              _buildFeatureCard(
                title: 'Security Operations',
                description: 'Test PCI security operations',
                icon: Icons.shield,
                color: Colors.purple,
                onTap: () {
                  // Navigator.push(
                  //   context,
                  //   MaterialPageRoute(builder: (context) => SecurityScreen(_cs50sdkupdate)),
                  // );
                },
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildFeatureCard({
    required String title,
    required String description,
    required IconData icon,
    required Color color,
    required VoidCallback onTap,
  }) {
    return Container(

      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(16),
        child: Padding(
          padding: EdgeInsets.all(6),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Container(
                    padding: EdgeInsets.all(8),
                    decoration: BoxDecoration(
                      color: color.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Icon(
                      icon,
                      color: color,
                      size: 28,
                    ),
                  ),
                  SizedBox(width: 16),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        title,
                        style: Theme.of(context).textTheme.titleLarge?.copyWith(
                              fontWeight: FontWeight.bold,
                          overflow: TextOverflow.ellipsis,

                            ),
                        maxLines: 2,
                      ),
                      SizedBox(height: 4),
                      Text(
                        description,
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                              color: Theme.of(context)
                                  .colorScheme
                                  .onSurfaceVariant,
                          overflow: TextOverflow.ellipsis,
                            ),
                        maxLines: 2,
                      ),
                    ],
                  ),
                ],
              ),
              SizedBox(height: 12),
              Align(
                alignment: Alignment.centerRight,
                child: Icon(
                  Icons.arrow_forward_rounded,
                  color: color,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

// IC Card Screen
class IcCardScreen extends StatefulWidget {
  final Cs50sdkupdate cs50sdkupdate;

  IcCardScreen(this.cs50sdkupdate);

  @override
  _IcCardScreenState createState() => _IcCardScreenState();
}

class _IcCardScreenState extends State<IcCardScreen> {
  final TextEditingController _slotController =
  TextEditingController(text: '0');
  final TextEditingController _vccModeController =
  TextEditingController(text: '2'); // Changed from '1' to '2' (3V instead of 5V)
  final TextEditingController _apduController =
  TextEditingController(text: '00A4040000');

  List<String> _logs = [];
  bool _isBusy = false;

  void _log(String message) {
    setState(() {
      _logs.add("[${DateTime.now().toString().split('.').first}] $message");
      // Limit logs to 100 entries
      if (_logs.length > 100) _logs.removeAt(0);
    });
  }

  Future<void> _openIcCard() async {
    if (_isBusy) return;

    setState(() {
      _isBusy = true;
    });

    try {
      final slot = int.parse(_slotController.text);
      final vccMode = int.parse(_vccModeController.text);
      final atr = List<int>.filled(32, 0);

      _log('Opening IC card on slot $slot with VCC mode $vccMode...');

      final result = await widget.cs50sdkupdate.iccOpen(slot, vccMode, atr);

      if (result != null) {
        _log('IC card opened successfully: $result');

        // Convert ATR bytes to hex string for display
        String atrHex = '';
        for (int i = 0; i < atr.length; i++) {
          if (atr[i] != 0) {
            atrHex += atr[i].toRadixString(16).padLeft(2, '0');
          }
        }

        if (atrHex.isNotEmpty) {
          _log('ATR: $atrHex');
        }
      } else {
        _log('Failed to open IC card');
      }
    } catch (e) {
      _log('Error: $e');
    } finally {
      setState(() {
        _isBusy = false;
      });
    }
  }

  Future<void> _checkIcCard() async {
    if (_isBusy) return;

    setState(() {
      _isBusy = true;
    });

    try {
      final slot = int.parse(_slotController.text);

      _log('Checking IC card on slot $slot...');

      final result = await widget.cs50sdkupdate.iccCheck(slot);

      if (result != null) {
        _log('IC card check result: $result');
      } else {
        _log('Failed to check IC card');
      }
    } catch (e) {
      _log('Error: $e');
    } finally {
      setState(() {
        _isBusy = false;
      });
    }
  }

  Future<void> _sendCommand() async {
    if (_isBusy) return;

    setState(() {
      _isBusy = true;
    });

    try {
      final slot = int.parse(_slotController.text);
      final apduStr = _apduController.text.replaceAll(' ', '');

      if (apduStr.isEmpty || apduStr.length % 2 != 0) {
        _log('Invalid APDU: Must be even length hex string');
        setState(() {
          _isBusy = false;
        });
        return;
      }

      // Convert hex string to byte array
      List<int> apduSend = [];
      for (int i = 0; i < apduStr.length; i += 2) {
        apduSend.add(int.parse(apduStr.substring(i, i + 2), radix: 16));
      }

      List<int> apduResp = List<int>.filled(256, 0);

      _log('Sending APDU command to IC card...');

      final result =
          await widget.cs50sdkupdate.iccCommand(slot, apduSend, apduResp);

      if (result != null) {
        _log('Command result: $result');

        // Convert response bytes to hex string for display
        String respHex = '';
        for (int i = 0; i < apduResp.length; i++) {
          if (apduResp[i] != 0) {
            respHex += apduResp[i].toRadixString(16).padLeft(2, '0');
          }
        }

        if (respHex.isNotEmpty) {
          _log('Response: $respHex');
        }
      } else {
        _log('Failed to send command');
      }
    } catch (e) {
      _log('Error: $e');
    } finally {
      setState(() {
        _isBusy = false;
      });
    }
  }

  Future<void> _closeIcCard() async {
    if (_isBusy) return;

    setState(() {
      _isBusy = true;
    });

    try {
      final slot = int.parse(_slotController.text);

      _log('Closing IC card on slot $slot...');

      final result = await widget.cs50sdkupdate.iccClose(slot);

      if (result != null) {
        _log('IC card closed successfully: $result');
      } else {
        _log('Failed to close IC card');
      }
    } catch (e) {
      _log('Error: $e');
    } finally {
      setState(() {
        _isBusy = false;
      });
    }
  }

  void _clearLogs() {
    setState(() {
      _logs.clear();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('IC Card Operations'),
        actions: [
          IconButton(
            icon: Icon(Icons.delete_outline),
            onPressed: _clearLogs,
            tooltip: 'Clear logs',
          ),
        ],
      ),
      body: Column(
        children: [
          Expanded(
            flex: 2,
            child: SingleChildScrollView(
              padding: EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  Card(
                    elevation: 2,
                    child: Padding(
                      padding: EdgeInsets.all(16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'IC Card Parameters',
                            style: Theme.of(context).textTheme.titleMedium,
                          ),
                          SizedBox(height: 16),
                          Row(
                            children: [
                              Expanded(
                                child: TextField(
                                  controller: _slotController,
                                  keyboardType: TextInputType.number,
                                  decoration: InputDecoration(
                                    labelText: 'Slot',
                                    border: OutlineInputBorder(),
                                    helperText: '0=IC, 1=SAM1, 2=SAM2',
                                  ),
                                ),
                              ),
                              SizedBox(width: 16),
                              Expanded(
                                child: TextField(
                                  controller: _vccModeController,
                                  keyboardType: TextInputType.number,
                                  decoration: InputDecoration(
                                    labelText: 'VCC Mode',
                                    border: OutlineInputBorder(),
                                    helperText: '1=5V, 2=3V, 3=1.8V',
                                  ),
                                ),
                              ),
                            ],
                          ),
                          SizedBox(height: 16),
                          TextField(
                            controller: _apduController,
                            decoration: InputDecoration(
                              labelText: 'APDU Command (Hex)',
                              border: OutlineInputBorder(),
                              helperText: 'Example: 00A4040000',
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                  SizedBox(height: 16),
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton.icon(
                          icon: Icon(Icons.play_arrow),
                          label: Text('Open Card'),
                          onPressed: _isBusy ? null : _openIcCard,
                          style: ElevatedButton.styleFrom(
                            padding: EdgeInsets.symmetric(vertical: 12),
                          ),
                        ),
                      ),
                      SizedBox(width: 8),
                      Expanded(
                        child: ElevatedButton.icon(
                          icon: Icon(Icons.check_circle_outline),
                          label: Text('Check Card'),
                          onPressed: _isBusy ? null : _checkIcCard,
                          style: ElevatedButton.styleFrom(
                            padding: EdgeInsets.symmetric(vertical: 12),
                          ),
                        ),
                      ),
                    ],
                  ),
                  SizedBox(height: 8),
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton.icon(
                          icon: Icon(Icons.send),
                          label: Text('Send Command'),
                          onPressed: _isBusy ? null : _sendCommand,
                          style: ElevatedButton.styleFrom(
                            padding: EdgeInsets.symmetric(vertical: 12),
                          ),
                        ),
                      ),
                      SizedBox(width: 8),
                      Expanded(
                        child: ElevatedButton.icon(
                          icon: Icon(Icons.close),
                          label: Text('Close Card'),
                          onPressed: _isBusy ? null : _closeIcCard,
                          style: ElevatedButton.styleFrom(
                            padding: EdgeInsets.symmetric(vertical: 12),
                          ),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
          Divider(height: 1),
          Expanded(
            flex: 3,
            child: Container(
              color:
                  Theme.of(context).colorScheme.surfaceVariant.withOpacity(0.3),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  Padding(
                    padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                    child: Text(
                      'Logs',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                  ),
                  Expanded(
                    child: _isBusy
                        ? Center(
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                CircularProgressIndicator(),
                                SizedBox(height: 16),
                                Text('Processing...'),
                              ],
                            ),
                          )
                        : ListView.builder(
                            padding: EdgeInsets.symmetric(horizontal: 16),
                            itemCount: _logs.length,
                            itemBuilder: (context, index) {
                              final log = _logs[index];
                              return Padding(
                                padding: EdgeInsets.symmetric(vertical: 4),
                                child: Text(
                                  log,
                                  style: TextStyle(
                                    fontFamily: 'monospace',
                                    fontSize: 12,
                                  ),
                                ),
                              );
                            },
                          ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

// Magnetic Card Screen
class MagneticCardScreen extends StatefulWidget {
  final Cs50sdkupdate cs50sdkupdate;

  MagneticCardScreen(this.cs50sdkupdate);

  @override
  _MagneticCardScreenState createState() => _MagneticCardScreenState();
}

class _MagneticCardScreenState extends State<MagneticCardScreen> {
  final TextEditingController _keyNoController =
      TextEditingController(text: '0');
  final TextEditingController _modeController =
      TextEditingController(text: '0');

  List<String> _logs = [];
  bool _isBusy = false;
  bool _isReaderOpen = false;

  void _log(String message) {
    setState(() {
      _logs.add("[${DateTime.now().toString().split('.').first}] $message");
      // Limit logs to 100 entries
      if (_logs.length > 100) _logs.removeAt(0);
    });
  }

  Future<void> _openReader() async {
    if (_isBusy) return;

    setState(() {
      _isBusy = true;
    });

    try {
      _log('Opening magnetic card reader...');

      final result = await widget.cs50sdkupdate.mcrOpen();

      if (result != null) {
        _log('Reader opened successfully: $result');
        setState(() {
          _isReaderOpen = true;
        });
      } else {
        _log('Failed to open reader');
      }
    } catch (e) {
      _log('Error: $e');
    } finally {
      setState(() {
        _isBusy = false;
      });
    }
  }

  Future<void> _checkCard() async {
    if (_isBusy || !_isReaderOpen) return;

    setState(() {
      _isBusy = true;
    });

    try {
      _log('Checking for magnetic card...');

      final result = await widget.cs50sdkupdate.mcrCheck();

      if (result != null) {
        _log('Card check result: $result');
      } else {
        _log('Failed to check for card');
      }
    } catch (e) {
      _log('Error: $e');
    } finally {
      setState(() {
        _isBusy = false;
      });
    }
  }

  Future<void> _readCard() async {
    if (_isBusy || !_isReaderOpen) return;

    setState(() {
      _isBusy = true;
    });

    try {
      final keyNo = int.parse(_keyNoController.text);
      final mode = int.parse(_modeController.text);

      // Create buffer for 3 tracks (256 bytes each)
      List<int> trackBuffers = List<int>.filled(256 * 3, 0);

      _log('Reading magnetic card...');

      final result =
          await widget.cs50sdkupdate.mcrRead(keyNo, mode, trackBuffers);

      if (result.isNotEmpty) {
        _log('Card read result: $result');

        if (result.containsKey('error')) {
          _log('Error: ${result['error']}');
        } else {
          if (result['track1Valid'] == true) {
            final track1Data = result['track1'] as List<int>?;
            if (track1Data != null && track1Data.isNotEmpty) {
              String track1Str = '';
              for (int i = 0;
                  i < track1Data.length && track1Data[i] != 0;
                  i++) {
                track1Str += String.fromCharCode(track1Data[i]);
              }
              _log('Track 1: $track1Str');
            }
          }

          if (result['track2Valid'] == true) {
            final track2Data = result['track2'] as List<int>?;
            if (track2Data != null && track2Data.isNotEmpty) {
              String track2Str = '';
              for (int i = 0;
                  i < track2Data.length && track2Data[i] != 0;
                  i++) {
                track2Str += String.fromCharCode(track2Data[i]);
              }
              _log('Track 2: $track2Str');
            }
          }

          if (result['track3Valid'] == true) {
            final track3Data = result['track3'] as List<int>?;
            if (track3Data != null && track3Data.isNotEmpty) {
              String track3Str = '';
              for (int i = 0;
                  i < track3Data.length && track3Data[i] != 0;
                  i++) {
                track3Str += String.fromCharCode(track3Data[i]);
              }
              _log('Track 3: $track3Str');
            }
          }
        }
      } else {
        _log('Failed to read card');
      }
    } catch (e) {
      _log('Error: $e');
    } finally {
      setState(() {
        _isBusy = false;
      });
    }
  }

  Future<void> _resetReader() async {
    if (_isBusy || !_isReaderOpen) return;

    setState(() {
      _isBusy = true;
    });

    try {
      _log('Resetting magnetic card reader...');

      final result = await widget.cs50sdkupdate.mcrReset();

      if (result != null) {
        _log('Reader reset successfully: $result');
      } else {
        _log('Failed to reset reader');
      }
    } catch (e) {
      _log('Error: $e');
    } finally {
      setState(() {
        _isBusy = false;
      });
    }
  }

  Future<void> _closeReader() async {
    if (_isBusy || !_isReaderOpen) return;

    setState(() {
      _isBusy = true;
    });

    try {
      _log('Closing magnetic card reader...');

      final result = await widget.cs50sdkupdate.mcrClose();

      if (result != null) {
        _log('Reader closed successfully: $result');
        setState(() {
          _isReaderOpen = false;
        });
      } else {
        _log('Failed to close reader');
      }
    } catch (e) {
      _log('Error: $e');
    } finally {
      setState(() {
        _isBusy = false;
      });
    }
  }

  void _clearLogs() {
    setState(() {
      _logs.clear();
    });
  }

  @override
  void dispose() {
    if (_isReaderOpen) {
      widget.cs50sdkupdate.mcrClose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Magnetic Card Operations'),
        actions: [
          IconButton(
            icon: Icon(Icons.delete_outline),
            onPressed: _clearLogs,
            tooltip: 'Clear logs',
          ),
        ],
      ),
      body: Column(
        children: [
          Expanded(
            flex: 2,
            child: SingleChildScrollView(
              padding: EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  Card(
                    elevation: 2,
                    child: Padding(
                      padding: EdgeInsets.all(16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Magnetic Card Parameters',
                            style: Theme.of(context).textTheme.titleMedium,
                          ),
                          SizedBox(height: 16),
                          Row(
                            children: [
                              Expanded(
                                child: TextField(
                                  controller: _keyNoController,
                                  keyboardType: TextInputType.number,
                                  decoration: InputDecoration(
                                    labelText: 'Key Number',
                                    border: OutlineInputBorder(),
                                    helperText: '0-9',
                                  ),
                                ),
                              ),
                              SizedBox(width: 16),
                              Expanded(
                                child: TextField(
                                  controller: _modeController,
                                  keyboardType: TextInputType.number,
                                  decoration: InputDecoration(
                                    labelText: 'Mode',
                                    border: OutlineInputBorder(),
                                    helperText: '0=Unencrypted, 1=Encrypted',
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ),
                  SizedBox(height: 16),
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton.icon(
                          icon: Icon(
                              _isReaderOpen ? Icons.close : Icons.play_arrow),
                          label: Text(
                              _isReaderOpen ? 'Close Reader' : 'Open Reader'),
                          onPressed: _isBusy
                              ? null
                              : (_isReaderOpen ? _closeReader : _openReader),
                          style: ElevatedButton.styleFrom(
                            padding: EdgeInsets.symmetric(vertical: 12),
                            backgroundColor: _isReaderOpen
                                ? Colors.red.withOpacity(0.8)
                                : Theme.of(context).colorScheme.primary,
                          ),
                        ),
                      ),
                      SizedBox(width: 8),
                      Expanded(
                        child: ElevatedButton.icon(
                          icon: Icon(Icons.refresh),
                          label: Text('Reset Reader'),
                          onPressed:
                              (_isBusy || !_isReaderOpen) ? null : _resetReader,
                          style: ElevatedButton.styleFrom(
                            padding: EdgeInsets.symmetric(vertical: 12),
                          ),
                        ),
                      ),
                    ],
                  ),
                  SizedBox(height: 8),
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton.icon(
                          icon: Icon(Icons.check_circle_outline),
                          label: Text('Check Card'),
                          onPressed:
                              (_isBusy || !_isReaderOpen) ? null : _checkCard,
                          style: ElevatedButton.styleFrom(
                            padding: EdgeInsets.symmetric(vertical: 12),
                          ),
                        ),
                      ),
                      SizedBox(width: 8),
                      Expanded(
                        child: ElevatedButton.icon(
                          icon: Icon(Icons.credit_card),
                          label: Text('Read Card'),
                          onPressed:
                              (_isBusy || !_isReaderOpen) ? null : _readCard,
                          style: ElevatedButton.styleFrom(
                            padding: EdgeInsets.symmetric(vertical: 12),
                          ),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
          Divider(height: 1),
          Expanded(
            flex: 3,
            child: Container(
              color:
                  Theme.of(context).colorScheme.surfaceVariant.withOpacity(0.3),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  Padding(
                    padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          'Logs',
                          style: Theme.of(context).textTheme.titleMedium,
                        ),
                        Container(
                          padding:
                              EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                          decoration: BoxDecoration(
                            color: _isReaderOpen
                                ? Colors.green.withOpacity(0.2)
                                : Colors.red.withOpacity(0.2),
                            borderRadius: BorderRadius.circular(16),
                          ),
                          child: Text(
                            _isReaderOpen ? 'Reader Open' : 'Reader Closed',
                            style: TextStyle(
                              color: _isReaderOpen ? Colors.green : Colors.red,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        )
                      ],
                    ),
                  ),
                  Expanded(
                    child: _isBusy
                        ? Center(
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                CircularProgressIndicator(),
                                SizedBox(height: 16),
                                Text('Processing...'),
                              ],
                            ),
                          )
                        : ListView.builder(
                            padding: EdgeInsets.symmetric(horizontal: 16),
                            itemCount: _logs.length,
                            itemBuilder: (context, index) {
                              final log = _logs[index];
                              return Padding(
                                padding: EdgeInsets.symmetric(vertical: 4),
                                child: Text(
                                  log,
                                  style: TextStyle(
                                    fontFamily: 'monospace',
                                    fontSize: 12,
                                  ),
                                ),
                              );
                            },
                          ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

// PIN Payment Screen
class PinPaymentScreen extends StatefulWidget {
  final Cs50sdkupdate cs50sdkupdate;

  PinPaymentScreen(this.cs50sdkupdate);

  @override
  _PinPaymentScreenState createState() => _PinPaymentScreenState();
}

class _PinPaymentScreenState extends State<PinPaymentScreen> {
  final TextEditingController _timeoutController =
      TextEditingController(text: '30');
  final TextEditingController _keyIdController =
      TextEditingController(text: '0');
  final TextEditingController _promptController =
      TextEditingController(text: 'Please Enter PIN:');
  final TextEditingController _currencyController =
      TextEditingController(text: '840');

  List<String> _logs = [];
  bool _isBusy = false;
  bool _isKernelInitialized = false;
  String _pinEntryStatus = 'Not started';

  void _log(String message) {
    setState(() {
      _logs.add("[${DateTime.now().toString().split('.').first}] $message");
      // Limit logs to 100 entries
      if (_logs.length > 100) _logs.removeAt(0);
    });
  }

  Future<void> _initializeKernel() async {
    if (_isBusy) return;

    setState(() {
      _isBusy = true;
    });

    try {
      _log('Initializing payment system kernel...');

      final result = await widget.cs50sdkupdate.initPaySysKernel();

      if (result != null) {
        _log('Kernel initialized successfully: $result');
        setState(() {
          _isKernelInitialized = true;
        });
      } else {
        _log('Failed to initialize kernel');
      }
    } catch (e) {
      _log('Error: $e');
    } finally {
      setState(() {
        _isBusy = false;
      });
    }
  }

  Future<void> _setCurrencyCode() async {
    if (_isBusy || !_isKernelInitialized) return;

    setState(() {
      _isBusy = true;
    });

    try {
      final currencyCode = _currencyController.text;

      _log('Setting currency code to $currencyCode...');

      final result =
          await widget.cs50sdkupdate.emvSetCurrencyCode(currencyCode);

      if (result != null) {
        _log('Currency code set successfully: $result');
      } else {
        _log('Failed to set currency code');
      }
    } catch (e) {
      _log('Error: $e');
    } finally {
      setState(() {
        _isBusy = false;
      });
    }
  }

  Future<void> _setPrompt() async {
    if (_isBusy || !_isKernelInitialized) return;

    setState(() {
      _isBusy = true;
    });

    try {
      final prompt = _promptController.text;

      _log('Setting keypad prompt to "$prompt"...');

      final result = await widget.cs50sdkupdate.emvSetKeyPadPrompt(prompt);

      if (result != null) {
        _log('Prompt set successfully: $result');
      } else {
        _log('Failed to set prompt');
      }
    } catch (e) {
      _log('Error: $e');
    } finally {
      setState(() {
        _isBusy = false;
      });
    }
  }

  Future<void> _startPinEntry() async {
    if (_isBusy || !_isKernelInitialized) return;

    setState(() {
      _isBusy = true;
      _pinEntryStatus = 'Initializing PIN entry...';
    });

    try {
      final timeout = int.parse(_timeoutController.text);
      final keyId = int.parse(_keyIdController.text);

      _log('Setting up PIN input callback with timeout $timeout seconds...');

      // First set the callback
      final callbackResult =
          await widget.cs50sdkupdate.emvSetInputPinCallback(timeout);

      if (callbackResult != null) {
        _log('PIN input callback set successfully: $callbackResult');

        _log(
            'Starting PIN input with timeout $timeout seconds and key ID $keyId...');
        setState(() {
          _pinEntryStatus = 'PIN entry started. Please enter PIN on device...';
        });

        // Then start the PIN input
        final result =
            await widget.cs50sdkupdate.emvKernelPinInput(timeout, keyId);

        if (result != null) {
          _log('PIN input completed: $result');
          setState(() {
            _pinEntryStatus = 'PIN entry completed';
          });
        } else {
          _log('Failed to complete PIN input');
          setState(() {
            _pinEntryStatus = 'PIN entry failed';
          });
        }
      } else {
        _log('Failed to set PIN input callback');
        setState(() {
          _pinEntryStatus = 'Failed to start PIN entry';
        });
      }
    } catch (e) {
      _log('Error: $e');
      setState(() {
        _pinEntryStatus = 'Error during PIN entry: $e';
      });
    } finally {
      setState(() {
        _isBusy = false;
      });
    }
  }

  Future<void> _runContactEmvPinblock() async {
    if (_isBusy || !_isKernelInitialized) return;

    setState(() {
      _isBusy = true;
    });

    try {
      _log('Running Contact EMV PINBLOCK...');

      final result = await widget.cs50sdkupdate
          .callContactEmvPinblock(0); // pinType=0 for default

      if (result != null) {
        _log('EMV PINBLOCK completed: $result');
      } else {
        _log('Failed to complete EMV PINBLOCK');
      }
    } catch (e) {
      _log('Error: $e');
    } finally {
      setState(() {
        _isBusy = false;
      });
    }
  }

  void _clearLogs() {
    setState(() {
      _logs.clear();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('PIN & Payment Operations'),
        actions: [
          IconButton(
            icon: Icon(Icons.delete_outline),
            onPressed: _clearLogs,
            tooltip: 'Clear logs',
          ),
        ],
      ),
      body: Column(
        children: [
          Expanded(
            flex: 2,
            child: SingleChildScrollView(
              padding: EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  Card(
                    elevation: 2,
                    child: Padding(
                      padding: EdgeInsets.all(16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'PIN Entry Parameters',
                            style: Theme.of(context).textTheme.titleMedium,
                          ),
                          SizedBox(height: 16),
                          Row(
                            children: [
                              Expanded(
                                child: TextField(
                                  controller: _timeoutController,
                                  keyboardType: TextInputType.number,
                                  decoration: InputDecoration(
                                    labelText: 'Timeout (seconds)',
                                    border: OutlineInputBorder(),
                                    helperText: 'Default: 30',
                                  ),
                                ),
                              ),
                              SizedBox(width: 16),
                              Expanded(
                                child: TextField(
                                  controller: _keyIdController,
                                  keyboardType: TextInputType.number,
                                  decoration: InputDecoration(
                                    labelText: 'Key ID',
                                    border: OutlineInputBorder(),
                                    helperText: '0-9',
                                  ),
                                ),
                              ),
                            ],
                          ),
                          SizedBox(height: 16),
                          TextField(
                            controller: _promptController,
                            decoration: InputDecoration(
                              labelText: 'PIN Prompt',
                              border: OutlineInputBorder(),
                              helperText: 'Text to display during PIN entry',
                            ),
                          ),
                          SizedBox(height: 16),
                          TextField(
                            controller: _currencyController,
                            decoration: InputDecoration(
                              labelText: 'Currency Code',
                              border: OutlineInputBorder(),
                              helperText: 'ISO 4217 code (e.g., 840 for USD)',
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                  SizedBox(height: 16),
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton.icon(
                          icon: Icon(_isKernelInitialized
                              ? Icons.check_circle
                              : Icons.play_arrow),
                          label: Text(_isKernelInitialized
                              ? 'Kernel Initialized'
                              : 'Initialize Kernel'),
                          onPressed: _isBusy
                              ? null
                              : (_isKernelInitialized
                                  ? null
                                  : _initializeKernel),
                          style: ElevatedButton.styleFrom(
                            padding: EdgeInsets.symmetric(vertical: 12),
                            backgroundColor: _isKernelInitialized
                                ? Colors.green
                                : Theme.of(context).colorScheme.primary,
                          ),
                        ),
                      ),
                    ],
                  ),
                  SizedBox(height: 8),
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton.icon(
                          icon: Icon(Icons.attach_money),
                          label: Text('Set Currency'),
                          onPressed: (_isBusy || !_isKernelInitialized)
                              ? null
                              : _setCurrencyCode,
                          style: ElevatedButton.styleFrom(
                            padding: EdgeInsets.symmetric(vertical: 12),
                          ),
                        ),
                      ),
                      SizedBox(width: 8),
                      Expanded(
                        child: ElevatedButton.icon(
                          icon: Icon(Icons.text_fields),
                          label: Text('Set Prompt'),
                          onPressed: (_isBusy || !_isKernelInitialized)
                              ? null
                              : _setPrompt,
                          style: ElevatedButton.styleFrom(
                            padding: EdgeInsets.symmetric(vertical: 12),
                          ),
                        ),
                      ),
                    ],
                  ),
                  SizedBox(height: 8),
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton.icon(
                          icon: Icon(Icons.pin),
                          label: Text('Start PIN Entry'),
                          onPressed: (_isBusy || !_isKernelInitialized)
                              ? null
                              : _startPinEntry,
                          style: ElevatedButton.styleFrom(
                            padding: EdgeInsets.symmetric(vertical: 12),
                          ),
                        ),
                      ),
                      SizedBox(width: 8),
                      Expanded(
                        child: ElevatedButton.icon(
                          icon: Icon(Icons.security),
                          label: Text('EMV PINBLOCK'),
                          onPressed: (_isBusy || !_isKernelInitialized)
                              ? null
                              : _runContactEmvPinblock,
                          style: ElevatedButton.styleFrom(
                            padding: EdgeInsets.symmetric(vertical: 12),
                          ),
                        ),
                      ),
                    ],
                  ),
                  SizedBox(height: 16),
                  Card(
                    elevation: 2,
                    color: _pinEntryStatus.contains('completed')
                        ? Colors.green.withOpacity(0.1)
                        : _pinEntryStatus.contains('failed') ||
                                _pinEntryStatus.contains('Error')
                            ? Colors.red.withOpacity(0.1)
                            : _pinEntryStatus.contains('started')
                                ? Colors.blue.withOpacity(0.1)
                                : Theme.of(context).colorScheme.surface,
                    child: Padding(
                      padding: EdgeInsets.all(16),
                      child: Row(
                        children: [
                          Icon(
                            _pinEntryStatus.contains('completed')
                                ? Icons.check_circle
                                : _pinEntryStatus.contains('failed') ||
                                        _pinEntryStatus.contains('Error')
                                    ? Icons.error
                                    : _pinEntryStatus.contains('started')
                                        ? Icons.hourglass_top
                                        : Icons.info,
                            color: _pinEntryStatus.contains('completed')
                                ? Colors.green
                                : _pinEntryStatus.contains('failed') ||
                                        _pinEntryStatus.contains('Error')
                                    ? Colors.red
                                    : _pinEntryStatus.contains('started')
                                        ? Colors.blue
                                        : Colors.grey,
                          ),
                          SizedBox(width: 16),
                          Expanded(
                            child: Text(
                              _pinEntryStatus,
                              style: TextStyle(
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
          Divider(height: 1),
          Expanded(
            flex: 3,
            child: Container(
              color:
                  Theme.of(context).colorScheme.surfaceVariant.withOpacity(0.3),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  Padding(
                    padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          'Logs',
                          style: Theme.of(context).textTheme.titleMedium,
                        ),
                        Container(
                          padding:
                              EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                          decoration: BoxDecoration(
                            color: _isKernelInitialized
                                ? Colors.green.withOpacity(0.2)
                                : Colors.red.withOpacity(0.2),
                            borderRadius: BorderRadius.circular(16),
                          ),
                          child: Text(
                            _isKernelInitialized
                                ? 'Kernel Initialized'
                                : 'Kernel Not Initialized',
                            style: TextStyle(
                              color: _isKernelInitialized
                                  ? Colors.green
                                  : Colors.red,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                  Expanded(
                    child: _isBusy
                        ? const Center(
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                CircularProgressIndicator(),
                                SizedBox(height: 16),
                                Text('Processing...'),
                              ],
                            ),
                          )
                        : ListView.builder(
                            padding: EdgeInsets.symmetric(horizontal: 16),
                            itemCount: _logs.length,
                            itemBuilder: (context, index) {
                              final log = _logs[index];
                              return Padding(
                                padding: EdgeInsets.symmetric(vertical: 4),
                                child: Text(
                                  log,
                                  style: TextStyle(
                                    fontFamily: 'monospace',
                                    fontSize: 12,
                                  ),
                                ),
                              );
                            },
                          ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

// // Security Operations Screen
// class SecurityScreen extends StatefulWidget {
//   final Cs50sdkupdate cs50sdkupdate;
//
//   SecurityScreen(this.cs50sdkupdate);
//
//   @override
//   _SecurityScreenState createState() => _SecurityScreenState();
// }
//
// class _SecurityScreenState extends State<SecurityScreen> {
//   final TextEditingController _keyNoController = TextEditingController(text: '0');
//   final TextEditingController _keyLenController = TextEditingController(text: '16');
//   final TextEditingController _keyDataController = TextEditingController(text: '00112233445566778899AABBCCDDEEFF');
//   final TextEditingController _modeController = TextEditingController(text: '0');
//   final TextEditingController _mainKeyNoController = TextEditingController(text: '0');
//
//   List<String> _logs = [];
//   bool _isBusy = false;
//   String _selectedTab = 'pinkey';
//
//   void _log(String message) {
//     setState(() {
//       _logs.add("[${DateTime.now().toString().split('.').first}] $message");
//       // Limit logs to 100 entries
//       if (_logs.length > 100) _logs.removeAt(0);
//     });
//   }
//
//   Future<void> _writePinMKey() async {
//     if (_isBusy) return;
//
//     setState(() {
//       _isBusy = true;
//     });
//
//     try {
//       final keyNo = int.parse(_keyNoController.text);
//       final keyLen = int.parse(_keyLenController.text);
//       final keyDataStr = _keyDataController.text.replaceAll(' ', '');
//       final mode = int.parse(_modeController.text);
//
//       if (keyDataStr.length != keyLen * 2) {
//         _log('Error: Key data length does not match key length');
//         setState(() {
//           _isBusy = false;
//         });
//         return;
//       }
//
//       // Convert hex string to byte array
//       List<int> keyData = [];
//       for (int i = 0; i < keyDataStr.length; i += 2) {
//         keyData.add(int.parse(keyDataStr.substring(i, i + 2), radix: 16));
//       }
//
//       _log('Writing PIN main key...');
//
//       final result = await widget.cs50sdkupdate.pciWritePinMKey(keyNo, keyLen, keyData, mode);
//
//       if (result != null) {
//         _log('PIN main key written successfully: $result');
//       } else {
//         _log('Failed to write PIN main key');
//       }
//     } catch (e) {
//       _log('Error: $e');
//     } finally {
//       setState(() {
//         _isBusy = false;
//       });
//     }
//   }
//
//   Future<void> _writeMacMKey() async {
//     if (_isBusy) return;
//
//     setState(() {
//       _isBusy = true;
//     });
//
//     try {
//       final keyNo = int.parse(_keyNoController.text);
//       final keyLen = int.parse(_keyLenController.text);
//       final keyDataStr = _keyDataController.text.replaceAll(' ', '');
//       final mode = int.parse(_modeController.text);
//
//       if (keyDataStr.length != keyLen * 2) {
//         _log('Error: Key data length does not match key length');
//         setState(() {
//           _isBusy = false;
//         });
//         return;
//       }
//
//       // Convert hex string to byte array
//       List<int> keyData = [];
//       for (int i = 0; i < keyDataStr.length; i += 2) {
//         keyData.add(int.parse(keyDataStr.substring(i, i + 2), radix: 16));
//       }
//
//       _log('Writing MAC main key...');
//
//       final result = await widget.cs50sdkupdate.pciWriteMacMKey(keyNo, keyLen, keyData, mode);
//
//       if (result != null) {
//         _log('MAC main key written successfully: $result');
//       } else {
//         _log('Failed to write MAC main key');
//       }
//     } catch (e) {
//       _log('Error: $e');
//     } finally {
//       setState(() {
//         _isBusy = false;
//       });
//     }
//   }
//
//   Future<void> _writeDesMKey() async {
//     if (_isBusy) return;
//
//     setState(() {
//       _isBusy = true;
//     });
//
//     try {
//       final keyNo = int.parse(_keyNoController.text);
//       final keyLen = int.parse(_keyLenController.text);
//       final keyDataStr = _keyDataController.text.replaceAll(' ', '');
//       final mode = int.parse(_modeController.text);
//
//       if (keyDataStr.length != keyLen * 2) {
//         _log('Error: Key data length does not match key length');
//         setState(() {
//           _isBusy = false;
//         });
//         return;
//       }
//
//       // Convert hex string to byte array
//       List<int> keyData = [];
//       for (int i = 0; i < keyDataStr.length; i += 2) {
//         keyData.add(int.parse(keyDataStr.substring(i, i + 2), radix: 16));
//       }
//
//       _log('Writing DES main key...');
//
//       final result = await widget.cs50sdkupdate.pciWriteDesMKey(keyNo, keyLen, keyData, mode);
//
//       if (result != null) {
//         _log('DES main key written successfully: $result');
//       } else {
//         _log('Failed to write DES main key');
//       }
//     } catch (e) {
//       _log('Error: $e');
//     } finally {
//       setState(() {
//         _isBusy = false;
//       });
//     }
//   }
//
//   Future<void> _writePinKey() async {
//     if (_isBusy) return;
//
//     setState(() {
//       _isBusy = true;
//     });
//
//     try {
//       final keyNo = int.parse(_keyNoController.text);
//       final keyLen = int.parse(_keyLenController.text);
//       final keyDataStr = _keyDataController.text.replaceAll(' ', '');
//       final mode = int.parse(_modeController.text);
//       final mKeyNo = int.parse(_mainKeyNoController.text);
//
//       if (keyDataStr.length != keyLen * 2) {
//         _log('Error: Key data length does not match key length');
//         setState(() {
//           _isBusy = false;
//         });
//         return;
//       }
//
//       // Convert hex string to byte array
//       List<int> keyData = [];
//       for (int i = 0; i < keyDataStr.length; i += 2) {
//         keyData.add(int.parse(keyDataStr.substring(i, i + 2), radix: 16));
//       }
//
//       _log('Writing PIN key...');
//
//       final result = await widget.cs50sdkupdate.pciWritePinKey(keyNo, keyLen, keyData, mode, mKeyNo);
//
//       if (result != null) {
//         _log('PIN key written successfully: $result');
//       } else {
//         _log('Failed to write PIN key');
//       }
//     } catch (e) {
//       _log('Error: $e');
//     } finally {
//       setState(() {
//         _isBusy = false;
//       });
//     }
//   }
//
//   Future<void> _writeMacKey() async {
//     if (_isBusy) return;
//
//     setState(() {
//       _isBusy = true;
//     });
//
//     try {
//       final keyNo = int.parse(_keyNoController.text);
//       final keyLen = int.parse(_keyLenController.text);
//       final keyDataStr = _keyDataController.text.replaceAll(' ', '');
//       final mode = int.parse(_modeController.text);
//       final mKeyNo = int.parse(_mainKeyNoController.text);
//
//       if (keyDataStr.length != keyLen * 2) {
//         _log('Error: Key data length does not match key length');
//         setState(() {
//           _isBusy = false;
//         });
//         return;
//       }
//
//       // Convert hex string to byte array
//       List<int> keyData = [];
//       for (int i = 0; i < keyDataStr.length; i += 2) {
//         keyData.add(int.parse(keyDataStr.substring(i, i + 2), radix: 16));
//       }
//
//       _log('Writing MAC key...');
//
//       final result = await widget.cs50sdkupdate.pciWriteMacKey(keyNo, keyLen, keyData, mode, mKeyNo);
//
//       if (result != null) {
//         _log('MAC key written successfully: $result');
//       } else {
//         _log('Failed to write MAC key');
//       }
//     } catch (e) {
//       _log('Error: $e');
//     } finally {
//       setState(() {
//         _isBusy = false;
//       });
//     }
//   }
//
//   Future<void> _writeDesKey() async {
//     if (_isBusy) return;
//
//     setState(() {
//       _isBusy = true;
//     });
//
//     try {
//       final keyNo = int.parse(_keyNoController.text);
//       final keyLen = int.parse(_keyLenController.text);
//       final keyDataStr = _keyDataController.text.replaceAll(' ', '');
//       final mode = int.parse(_modeController.text);
//       final mKeyNo = int.parse(_mainKeyNoController.text);
//
//       if (keyDataStr.length != keyLen * 2) {
//         _log('Error: Key data length does not match key length');
//         setState(() {
//           _isBusy = false;
//         });
//         return;
//       }
//
//       // Convert hex string to byte array
//       List<int> keyData = [];
//       for (int i = 0; i < keyDataStr.length; i += 2) {
//         keyData.add(int.parse(keyDataStr.substring(i, i + 2), radix: 16));
//       }
//
//       _log('Writing DES key...');
//
//       final result = await widget.cs50sdkupdate.pciWriteDesKey(keyNo, keyLen, keyData, mode, mKeyNo);
//
//       if (result != null) {
//         _log('DES key written successfully: $result');
//       } else {
//         _log('Failed to write DES key');
//       }
//     } catch (e) {
//       _log('Error: $e');
//     } finally {
//       setState(() {
//         _isBusy = false;
//       });
//     }
//   }
//
//   Future<void> _readKCV() async {
//     if (_isBusy) return;
//
//     setState(() {
//       _isBusy = true;
//     });
//
//     try {
//       final mKeyNo = int.parse(_keyNoController.text);
//       int keyType = 0x0A; // Default to MAIN_PIN_Type
//
//       switch (_selectedTab) {
//         case 'pinkey':
//           keyType = 0x0A; // MAIN_PIN_Type
//           break;
//         case 'mackey':
//           keyType = 0x0B; // MAIN_MAC_Type
//           break;
//         case 'deskey':
//           keyType = 0x0C; // MAIN_DES_Type
//           break;
//         case 'workpinkey':
//           keyType = 0x0D; // WORKKey_PIN_Type
//           break;
//         case 'workmackey':
//           keyType = 0x0E; // WORKKey_MAC_Type
//           break;
//         case 'workdeskey':
//           keyType = 0x0F; // WORKKey_DES_Type
//           break;
//       }
//
//       // Buffer for KCV
//       List<int> mKeyKcv = List<int>.filled(8, 0);
//
//       _log('Reading KCV for key type 0x${keyType.toRadixString(16)}...');
//
//       final result = await widget.cs50sdkupdate.pciReadKCV(mKeyNo, keyType, mKeyKcv);
//
//       if (result != null) {
//         _log('KCV read successfully: $result');
//
//         // Convert KCV bytes to hex string
//         String kcvHex = '';
//         for (int i = 0; i < mKeyKcv.length; i++) {
//           kcvHex += mKeyKcv[i].toRadixString(16).padLeft(2, '0');
//         }
//
//         _log('KCV: $kcvHex');
//       } else {
//         _log('Failed to read KCV');
//       }
//     } catch (e) {
//       _log('Error: $e');
//     } finally {
//       setState(() {
//         _isBusy = false;
//       });
//     }
//   }
//
//   void _clearLogs() {
//     setState(() {
//       _logs.clear();
//     });
//   }
//
//   Widget _buildTabButton(String tab, String label, IconData icon) {
//     final isSelected = _selectedTab == tab;
//
//     return Expanded(
//         child: InkWell(
//         onTap: () {
//       setState(() {
//         _selectedTab = tab;
//       });
//     },
//     child: Container(
//     padding: EdgeInsets.symmetric(vertical: 12),
//     decoration: BoxDecoration(
//     color: isSelected ? Theme.of(context).colorScheme.primary.withOpacity(0.1) : Colors.transparent,
//     border: Border(
//     bottom: BorderSide(
//     color: isSelected ? Theme.of(context).colorScheme.primary : Colors.transparent,
//     width: 2,
//     ),
//     ),
//     ),
//     child: Column(
//     children: [
//     Icon(
//     icon,
//     color: isSelected ? Theme.of(context
