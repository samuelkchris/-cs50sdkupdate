import 'dart:async';

import 'package:flutter/material.dart';
import 'dart:math';


// Step 1: Create a StreamController
final StreamController<int> _printedJobsStreamController = StreamController<int>.broadcast();

void disposeStream() {
  _printedJobsStreamController.close();
}

class CircularPrintCounter extends StatefulWidget {
  final int totalJobs;
  // Remove the printedJobs parameter since we'll use a stream
  // final int printedJobs;

  const CircularPrintCounter({
    Key? key,
    required this.totalJobs,
    // Remove the printedJobs parameter
  }) : super(key: key);

  @override
  _CircularPrintCounterState createState() => _CircularPrintCounterState();
}

class _CircularPrintCounterState extends State<CircularPrintCounter> with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _animation;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1500),
    );
    _animation = Tween<double>(begin: 0, end: 1).animate(
      CurvedAnimation(parent: _animationController, curve: Curves.easeInOut),
    );
    _animationController.forward();
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    // Step 2: Update the Widget to Use StreamBuilder
    return StreamBuilder<int>(
      stream: _printedJobsStreamController.stream,
      builder: (context, snapshot) {
        int printedJobs = snapshot.data ?? 0;
        _animationController.reset();
        _animationController.forward();
        return AnimatedBuilder(
          animation: _animation,
          builder: (context, child) {
            return SizedBox(
              width: 200,
              height: 200,
              child: Stack(
                children: [
                  CustomPaint(
                    size: const Size(200, 200),
                    painter: CircularProgressPainter(
                      progress: _animation.value * (printedJobs / widget.totalJobs),
                      color: Colors.blue,
                      backgroundColor: Colors.grey.shade300,
                    ),
                  ),
                  Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          '${(printedJobs * _animation.value).toInt()}',
                          style: const TextStyle(
                            fontSize: 40,
                            fontWeight: FontWeight.bold,
                            color: Colors.blue,
                          ),
                        ),
                        Text(
                          'of ${widget.totalJobs}',
                          style: TextStyle(
                            fontSize: 16,
                            color: Colors.grey.shade600,
                          ),
                        ),
                        const SizedBox(height: 8),
                        Text(
                          'Printed',
                          style: TextStyle(
                            fontSize: 18,
                        fontWeight: FontWeight.w500,
                        color: Colors.grey.shade800,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        );
      },
    );
  },
);
  }
}

class CircularProgressPainter extends CustomPainter {
  final double progress;
  final Color color;
  final Color backgroundColor;

  CircularProgressPainter({
    required this.progress,
    required this.color,
    required this.backgroundColor,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = min(size.width, size.height) / 2;
    final strokeWidth = 15.0;

    // Draw background circle
    final backgroundPaint = Paint()
      ..color = backgroundColor
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth;
    canvas.drawCircle(center, radius, backgroundPaint);

    // Draw progress arc
    final progressPaint = Paint()
      ..color = color
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round;
    canvas.drawArc(
      Rect.fromCircle(center: center, radius: radius),
      -pi / 2,
      2 * pi * progress,
      false,
      progressPaint,
    );
  }

  @override
  bool shouldRepaint(CustomPainter oldDelegate) => true;
}
