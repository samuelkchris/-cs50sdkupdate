import 'package:cs50sdkupdate_example/pdf_printer.dart';
import 'package:flutter/material.dart';
import 'package:cs50sdkupdate/cs50sdkupdate.dart';

class CustomDocumentEditor extends StatefulWidget {
  final Cs50sdkupdate printPlugin;
  final PrintJobManager jobManager;

  const CustomDocumentEditor({Key? key, required this.printPlugin,required this.jobManager,}) : super(key: key);

  @override
  _CustomDocumentEditorState createState() => _CustomDocumentEditorState();
}

class _CustomDocumentEditorState extends State<CustomDocumentEditor> {
  final TextEditingController _textController = TextEditingController();
  bool _isBold = false;
  bool _isUnderline = false;
  bool _isReverse = false;
  TextAlign _textAlign = TextAlign.left;
  double _fontSize = 24;
  int _grayLevel = 50;
  int _lineSpacing = 0;
  int _charSpacing = 0;
  int _leftMargin = 0;
  int _printSpeed = 2;
  int _printMode = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Document Editor'),
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.print),
            onPressed: _printDocument,
          ),
        ],
      ),
      body: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [Colors.blue.shade50, Colors.blue.shade100],
          ),
        ),
        child: Column(
          children: [
            _buildToolbar(),
            Expanded(
              child: Card(
                margin: const EdgeInsets.all(16),
                elevation: 4,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: TextField(
                    controller: _textController,
                    maxLines: null,
                    expands: true,
                    style: TextStyle(
                      fontWeight: _isBold ? FontWeight.bold : FontWeight.normal,
                      decoration: _isUnderline ? TextDecoration.underline : TextDecoration.none,
                      fontSize: _fontSize,
                      color: _isReverse ? Colors.white : Colors.black,
                    ),
                    textAlign: _textAlign,
                    decoration: InputDecoration(
                      border: InputBorder.none,
                      hintText: 'Enter your text here',
                      fillColor: _isReverse ? Colors.black : Colors.white,
                      filled: true,
                    ),
                  ),
                ),
              ),
            ),
            _buildAdvancedSettings(),
          ],
        ),
      ),
    );
  }

  Widget _buildToolbar() {
    return Card(
      margin: const EdgeInsets.all(16),
      elevation: 4,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(8),
        child: Wrap(
          spacing: 8,
          children: [
            _buildToolbarButton(Icons.format_bold, _isBold, () => setState(() => _isBold = !_isBold)),
            _buildToolbarButton(Icons.format_underline, _isUnderline, () => setState(() => _isUnderline = !_isUnderline)),
            _buildToolbarButton(Icons.invert_colors, _isReverse, () => setState(() => _isReverse = !_isReverse)),
            _buildToolbarButton(Icons.format_align_left, _textAlign == TextAlign.left, () => setState(() => _textAlign = TextAlign.left)),
            _buildToolbarButton(Icons.format_align_center, _textAlign == TextAlign.center, () => setState(() => _textAlign = TextAlign.center)),
            _buildToolbarButton(Icons.format_align_right, _textAlign == TextAlign.right, () => setState(() => _textAlign = TextAlign.right)),
            _buildToolbarButton(Icons.text_increase, false, () => setState(() => _fontSize += 2)),
            _buildToolbarButton(Icons.text_decrease, false, () => setState(() => _fontSize -= 2)),
          ],
        ),
      ),
    );
  }

  Widget _buildToolbarButton(IconData icon, bool isSelected, VoidCallback onPressed) {
    return IconButton(
      icon: Icon(icon, color: isSelected ? Colors.blue : Colors.black54),
      onPressed: onPressed,
    );
  }

  Widget _buildAdvancedSettings() {
    return ExpansionTile(
      title: const Text('Advanced Printer Settings'),
      children: [
        _buildSliderSetting('Gray Level', _grayLevel, 0, 100, (value) => setState(() => _grayLevel = value.round())),
        _buildSliderSetting('Line Spacing', _lineSpacing, 0, 100, (value) => setState(() => _lineSpacing = value.round())),
        _buildSliderSetting('Char Spacing', _charSpacing, 0, 100, (value) => setState(() => _charSpacing = value.round())),
        _buildSliderSetting('Left Margin', _leftMargin, 0, 100, (value) => setState(() => _leftMargin = value.round())),
        _buildSliderSetting('Print Speed', _printSpeed, 1, 5, (value) => setState(() => _printSpeed = value.round())),
        _buildDropdownSetting('Print Mode', _printMode, {'Normal': 5, 'White on Black': 1}, (value) => setState(() => _printMode = value)),
      ],
    );
  }

  Widget _buildSliderSetting(String label, int value, double min, double max, Function(double) onChanged) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Row(
        children: [
          Expanded(child: Text(label)),
          Expanded(
            child: Slider(
              value: value.toDouble(),
              min: min,
              max: max,
              divisions: (max - min).round(),
              label: value.toString(),
              onChanged: onChanged,
            ),
          ),
          SizedBox(width: 40, child: Text(value.toString())),
        ],
      ),
    );
  }

  Widget _buildDropdownSetting(String label, int value, Map<String, int> options, Function(int) onChanged) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Row(
        children: [
          Expanded(child: Text(label)),
          DropdownButton<int>(
            value: value,
            items: options.entries.map((entry) => DropdownMenuItem<int>(
              value: entry.value,
              child: Text(entry.key),
            )).toList(),
            onChanged: (newValue) => onChanged(newValue!),
          ),
        ],
      ),
    );
  }

  void _printDocument() async {
    try {
      await widget.printPlugin.printInit();
      await widget.printPlugin.printSetAlign(_textAlign == TextAlign.left ? 0 : _textAlign == TextAlign.center ? 1 : 2);
      await widget.printPlugin.printSetBold(_isBold ? 1 : 0);
      await widget.printPlugin.printSetUnderline(_isUnderline ? 1 : 0);
      await widget.printPlugin.printSetReverse(_isReverse ? 1 : 0);
      await widget.printPlugin.printSetFont(_fontSize.round(), _fontSize.round(), 0);
      await widget.printPlugin.printSetGray(_grayLevel);
      await widget.printPlugin.printSetLineSpace(_lineSpacing);
      await widget.printPlugin.printCharSpace(_charSpacing);
      await widget.printPlugin.printSetLeftSpace(_leftMargin);
      await widget.printPlugin.printSetSpeed(_printSpeed);
      await widget.printPlugin.printSetMode(_printMode);
      await widget.printPlugin.printStr(_textController.text);
      widget.jobManager.addPage(_textController.text);
      widget.jobManager.printAllPages();
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Document sent to printer')),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Failed to print: $e')),
      );
    }
  }
}
