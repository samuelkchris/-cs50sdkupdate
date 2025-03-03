/// Result from a magnetic card read operation
class MagneticCardResult {
  final bool success;
  final String? error;
  final List<int>? track1Data;
  final List<int>? track2Data;
  final List<int>? track3Data;
  final bool track1Valid;
  final bool track2Valid;
  final bool track3Valid;
  final bool track1Error;
  final bool track2Error;
  final bool track3Error;

  MagneticCardResult({
    required this.success,
    this.error,
    this.track1Data,
    this.track2Data,
    this.track3Data,
    this.track1Valid = false,
    this.track2Valid = false,
    this.track3Valid = false,
    this.track1Error = false,
    this.track2Error = false,
    this.track3Error = false,
  });

  factory MagneticCardResult.fromMap(Map<String, dynamic> map) {
    if (map.containsKey('error')) {
      return MagneticCardResult(
        success: false,
        error: map['error'] as String,
      );
    }

    return MagneticCardResult(
      success: true,
      track1Data: map['track1'] as List<int>?,
      track2Data: map['track2'] as List<int>?,
      track3Data: map['track3'] as List<int>?,
      track1Valid: map['track1Valid'] as bool? ?? false,
      track2Valid: map['track2Valid'] as bool? ?? false,
      track3Valid: map['track3Valid'] as bool? ?? false,
      track1Error: map['track1Error'] as bool? ?? false,
      track2Error: map['track2Error'] as bool? ?? false,
      track3Error: map['track3Error'] as bool? ?? false,
    );
  }

  @override
  String toString() =>
      'MagneticCardResult(success: $success, error: $error, track1Valid: $track1Valid, track2Valid: $track2Valid, track3Valid: $track3Valid)';
}

/// Callback events for PIN input operations
class PinInputEvent {
  final String type;
  final String? pin;
  final bool? success;

  PinInputEvent({
    required this.type,
    this.pin,
    this.success,
  });

  factory PinInputEvent.fromMap(Map<String, dynamic> map) {
    return PinInputEvent(
      type: map['type'] as String,
      pin: map['pin'] as String?,
      success: map['success'] as bool?,
    );
  }

  bool get isPinRequest => type == 'pinRequest';
  bool get isPinInput => type == 'pinInput';
  bool get isPinFinished => type == 'pinFinished';
  bool get isPinCancelled => type == 'pinCancelled';

  @override
  String toString() =>
      'PinInputEvent(type: $type, pin: $pin, success: $success)';
}