// final logo = pw.FlutterLogo();
// for (int i = 0; i < 5; i++) {
// pdf.addPage(
// index: 0,
// pw.Page(
// pageFormat: const PdfPageFormat(42.0, 111),
// margin: const pw.EdgeInsets.only(top: 10),
// build: (context) {
// return pw.Container(
// width: 58.0,
// height: 210.0,
// decoration: pw.BoxDecoration(
// border: pw.Border.all(
// color: PdfColors.black,
// width: 1,
// style: pw.BorderStyle.dotted,
// ),
// ),
// padding: const pw.EdgeInsets.only(top: 5),
// // Adjust padding
// child: pw.Column(
// mainAxisAlignment: pw.MainAxisAlignment.start,
// crossAxisAlignment: pw.CrossAxisAlignment.center,
// children: [
// pw.Text(
// 'TICKET NUMBER: 44333344',
// textAlign: pw.TextAlign.center,
// style: pw.TextStyle(
//
// fontSize: 1.5, // Reduce font size
// fontWeight: pw.FontWeight.bold,
// ),
// ),
// pw.SizedBox(height: 1),
// pw.Container(
// width: 50,
// child: pw.Divider(
// thickness: 0.2, // Reduce thickness
// height: 1, // Reduce height
// color: PdfColors.black,
// borderStyle: pw.BorderStyle.dotted,
// ),
// ),
// pw.SizedBox(height: 3),
// pw.Text(
// 'Printed by: ${'John Doe'}',
// textAlign: pw.TextAlign.center,
// style: pw.TextStyle(
// font: font,
// fontSize: 1.5,
// fontWeight: pw.FontWeight.bold,
// ),
// ),
// pw.Text(
// 'Printed Date: ${DateTime.now()}',
// textAlign: pw.TextAlign.center,
// style: pw.TextStyle(
// font: font,
// fontSize: 1.5,
// fontWeight: pw.FontWeight.bold,
// ),
// ),
// pw.Row(
// mainAxisAlignment: pw.MainAxisAlignment.center,
// children: [
// pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: pw.Text(
// 'Confirm the validity of your ticket on pinnket.com\n'
// 'Terms And Conditions Apply.',
// textAlign: pw.TextAlign.center,
// style: pw.TextStyle(
// font: font,
// fontSize: 1.7,
// fontWeight: pw.FontWeight.bold,
// ),
// ),
// ),
// pw.SizedBox(width: 1),
// pw.Column(
// mainAxisAlignment: pw.MainAxisAlignment.center,
// children: [
// pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: pw.Text(
// 'ADMITS',
// style: pw.TextStyle(
// font: font,
// fontSize: 3,
// fontWeight: pw.FontWeight.bold,
// ),
// ),
// ),
// pw.SizedBox(height: 1),
// pw.ClipRRect(
// horizontalRadius: 1,
// child: pw.Container(
// width: 7,
// height: 7,
// alignment: pw.Alignment.center,
// decoration: pw.BoxDecoration(
// border:
// pw.Border.all(color: PdfColors.black),
// borderRadius: pw.BorderRadius.circular(3)),
// child: pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: pw.Text(
// "2",
// style: pw.TextStyle(
// font: font,
// fontSize: 3,
// fontWeight: pw.FontWeight.bold,
// ),
// )),
// )),
// ],
// ),
// pw.SizedBox(height: 1),
// pw.Column(
// mainAxisAlignment: pw.MainAxisAlignment.center,
// children: [
// pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: pw.RichText(
// textAlign: pw.TextAlign.center,
// text: pw.TextSpan(
// text:
// '12',
// style: pw.TextStyle(
// font: font,
// fontSize: 2,
// fontWeight: pw.FontWeight.bold,
// ),
// children: [
// pw.TextSpan(
// text:
// '2024',
// style: pw.TextStyle(
// font: font,
// fontSize: 4,
// fontWeight: pw.FontWeight.bold,
// ),
// ),
// pw.TextSpan(
// text:
// 'DEC',
// style: pw.TextStyle(
// font: font,
// fontSize: 2,
// fontWeight: pw.FontWeight.bold,
// ),
// ),
// ],
// ),
// ),
// ),
// pw.SizedBox(height: 0.5),
// //dot line
// pw.Container(
// width: 10,
// child: pw.Divider(
// thickness: 0.2,
// height: 1,
// color: PdfColors.black,
// ),
// ),
// pw.SizedBox(height: 0.5),
//
// pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: pw.RichText(
// textAlign: pw.TextAlign.center,
// text: pw.TextSpan(
// text: 'Time\n',
// style: pw.TextStyle(
// font: font,
// fontSize: 1.8,
// fontWeight: pw.FontWeight.bold,
// ),
// children: [
// pw.TextSpan(
// text: "12:00",
// style: pw.TextStyle(
// font: font,
// fontSize: 3,
// fontWeight: pw.FontWeight.bold,
// ),
// ),
// ],
// ),
// ),
// ),
// pw.SizedBox(height: 0.5),
//
// pw.Container(
// width: 5,
// child: pw.Divider(
// thickness: 0.2,
// height: 1,
// color: PdfColors.black,
// ),
// ),
// pw.SizedBox(height: 0.5),
// pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: pw.RichText(
// textAlign: pw.TextAlign.center,
// text: pw.TextSpan(
// text: 'Seat\n',
// style: pw.TextStyle(
// font: font,
// fontSize: 1.8,
// fontWeight: pw.FontWeight.bold,
// ),
// children: [
// pw.TextSpan(
// text: "eventzone",
// style: pw.TextStyle(
// font: font,
// fontSize: 3,
// fontWeight: pw.FontWeight.bold,
// ),
// ),
// ],
// ),
// ),
// ),
// pw.SizedBox(height: 0.5),
//
// pw.Container(
// width: 5,
// child: pw.Divider(
// thickness: 0.2,
// height: 1,
// color: PdfColors.black,
// ),
// ),
// pw.SizedBox(height: 0.5),
// pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: pw.RichText(
// textAlign: pw.TextAlign.center,
// text: pw.TextSpan(
// text: 'Amount\n',
// style: pw.TextStyle(
// font: font,
// fontSize: 1.8,
// fontWeight: pw.FontWeight.bold,
// ),
// children: [
// pw.TextSpan(
// text:
// "20,000 UGX",
// style: pw.TextStyle(
// font: font,
// fontSize: 3,
// fontWeight: pw.FontWeight.bold,
// ),
// ),
// ],
// ),
// ),
// ),
// ]),
// pw.SizedBox(width: 2),
// pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: pw.Text(
// "Kampala - Mbuya",
// textAlign: pw.TextAlign.center,
// style: pw.TextStyle(
// font: font,
// fontSize: 2.3,
// fontWeight: pw.FontWeight.bold,
// ),
// ),
// ),
// pw.SizedBox(width: 0.2),
// pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: pw.Container(
// width: 30,
// child: pw.Divider(
// thickness: 0.2,
// height: 2,
// color: PdfColors.black,
// ),
// ),
// ),
// pw.SizedBox(width: 0.2),
// pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: pw.Text("The event name",
// style: pw.TextStyle(
// font: font,
// fontSize: 2.3,
// fontWeight: pw.FontWeight.bold,
// ),
// ),
// ),
// pw.SizedBox(width: 0.2),
// pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: pw.Container(
// width: 30,
// child: pw.Divider(
// thickness: 0.2,
// height: 2,
// color: PdfColors.black,
// ),
// ),
// ),
// pw.SizedBox(width: 0.2),
// pw.Container(
// width: 50,
// child: pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: pw.Text("The event date",
// textAlign: pw.TextAlign.center,
// style: pw.TextStyle(
// font: font,
// fontSize: 3,
// fontWeight: pw.FontWeight.bold,
// ),
// ),
// ),
// ),
// ],
// ),
// // pw.SizedBox(height: 5),
//
// pw.SizedBox(width: 1),
// //doted line
// pw.Container(
// width: 40,
// child: pw.Divider(
// thickness: 0.2,
// height: 1,
// color: PdfColors.black,
// borderStyle: pw.BorderStyle.dotted,
// ),
// ),
// pw.SizedBox(width: 3),
// pw.Row(
// mainAxisAlignment: pw.MainAxisAlignment.center,
// children: [
// pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: logo,
// ),
// pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: pw.Text(
// 'Powered by',
// style: pw.TextStyle(
// font: font,
// fontSize: 2,
// fontWeight: pw.FontWeight.bold,
// ),
// ),
// ),
// pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: pw.BarcodeWidget(
// padding: const pw.EdgeInsets.only(
// left: 3,
// ),
// barcode: pw.Barcode.qrCode(
// errorCorrectLevel: pw.BarcodeQRCorrectionLevel.high,
// ),
// data: "https://c.pinnitags.com/passticket/registration/1/",
// width: 20,
// height: 25,
// ),
// ),
// pw.Transform.rotateBox(
// angle: -3.14 / 2,
// child: pw.Text(
// "44223",
// style: pw.TextStyle(
// font: font,
// fontSize: 3,
// fontWeight: pw.FontWeight.bold,
// ),
// ),
// ),
// ],
// ),
// ],
// ),
// );
// },
// ),
// );
// }
