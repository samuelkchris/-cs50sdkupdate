import 'dart:typed_data';

class NfcUidConverter {
  // Convert decimal UID to hexadecimal string
  static String decimalToHex(int decimalUid) {
    // Convert to bytes (4 bytes for a 32-bit int)
    ByteData byteData = ByteData(4)..setUint32(0, decimalUid, Endian.little);

    // Convert bytes to hex string
    String hexString = byteData.buffer.asUint8List().map((byte) {
      return byte.toRadixString(16).padLeft(2, '0');
    }).join('').toUpperCase();

    return hexString;
  }

  // Convert hexadecimal string to decimal UID
  static int hexToDecimal(String hexUid) {
    // Ensure the hex string is 8 characters long
    hexUid = hexUid.padLeft(8, '0');

    // Convert hex string to bytes
    List<int> bytes = List<int>.generate(4, (i) {
      return int.parse(hexUid.substring(i * 2, i * 2 + 2), radix: 16);
    });

    // Create ByteData and read as little-endian Uint32
    ByteData byteData = ByteData.sublistView(Uint8List.fromList(bytes));
    return byteData.getUint32(0, Endian.little);
  }
}
