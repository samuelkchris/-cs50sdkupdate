package com.pinnisoft.cs50sdkupdate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONException;

import com.ctk.sdk.PosApiHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * Handles printer operations
 */
public class PrinterHandler {
    private static final String TAG = "PrinterHandler";

    private final PosApiHelper posApiHelper;
    private final Context context;
    private Activity activity;
    private final MethodChannel channel;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final PrinterConfig printerConfig;
    private final PrintHistoryManager historyManager;

    // PDF printing state
    private String currentPdfPath;
    private int currentPage = 0;
    private int totalPages = 0;
    private List<Integer> failedPages = new ArrayList<>();
    private String lastPrintedPdfPath;
    private int lastPrintedPageIndex = -1;
    private boolean cancelRequested = false;

    public PrinterHandler(PosApiHelper posApiHelper, Context context, Activity activity,
                          MethodChannel channel, ExecutorService executorService) {
        this.posApiHelper = posApiHelper;
        this.context = context;
        this.activity = activity;
        this.channel = channel;
        this.executorService = executorService;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.printerConfig = new PrinterConfig();
        this.historyManager = new PrintHistoryManager(context);
    }

    /**
     * Update the activity reference
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    // Basic printer operations

    /**
     * Initialize the printer with default settings
     */
    public void initializePrinter(MethodCall call, Result result) {
        Log.d(TAG, "Initializing printer with default settings");
        int ret = posApiHelper.PrintInit();
        if (ret == 0) {
            printerConfig.applyDefaults(posApiHelper);
            result.success("Printer initialized successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to initialize printer", ret);
        }
    }

    /**
     * Initialize printer with custom parameters
     */
    public void initializeWithParams(MethodCall call, Result result) {
        Integer gray = call.argument("gray");
        Integer fontHeight = call.argument("fontHeight");
        Integer fontWidth = call.argument("fontWidth");
        Integer fontZoom = call.argument("fontZoom");

        if (gray == null || fontHeight == null || fontWidth == null || fontZoom == null) {
            ErrorUtils.invalidArgument(result, "All parameters must be provided");
            return;
        }

        Log.d(TAG, String.format("Initializing printer with custom settings: gray=%d, fontHeight=%d, fontWidth=%d, fontZoom=%d",
                gray, fontHeight, fontWidth, fontZoom));

        int ret = posApiHelper.PrintInit(gray, fontHeight, fontWidth, fontZoom);
        if (ret == 0) {
            // Update config with custom values
            printerConfig.setNormalGray(gray);
            printerConfig.setAsciiFontHeight((byte) fontHeight.intValue());
            printerConfig.setExtendFontHeight((byte) fontWidth.intValue());
            printerConfig.setFontZoom((byte) fontZoom.intValue());

            result.success("Printer initialized with parameters successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to initialize printer with parameters", ret);
        }
    }

    /**
     * Set printer font
     */
    public void setFont(MethodCall call, Result result) {
        Number asciiFontHeightNumber = call.argument("asciiFontHeight");
        Number extendFontHeightNumber = call.argument("extendFontHeight");
        Number zoomNumber = call.argument("zoom");

        if (asciiFontHeightNumber == null || extendFontHeightNumber == null || zoomNumber == null) {
            ErrorUtils.invalidArgument(result, "All font parameters must be provided");
            return;
        }

        byte asciiFontHeight = asciiFontHeightNumber.byteValue();
        byte extendFontHeight = extendFontHeightNumber.byteValue();
        byte zoom = zoomNumber.byteValue();

        Log.d(TAG, String.format("Setting printer font: ASCII=%d, Extend=%d, Zoom=%d",
                asciiFontHeight, extendFontHeight, zoom));

        int ret = posApiHelper.PrintSetFont(asciiFontHeight, extendFontHeight, zoom);
        if (ret == 0) {
            // Update config
            printerConfig.setAsciiFontHeight(asciiFontHeight);
            printerConfig.setExtendFontHeight(extendFontHeight);
            printerConfig.setFontZoom(zoom);

            result.success("Printer font set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set printer font", ret);
        }
    }

    /**
     * Set printer gray level (darkness)
     */
    public void setGray(MethodCall call, Result result) {
        Integer nLevel = call.argument("nLevel");
        if (nLevel == null) {
            ErrorUtils.invalidArgument(result, "Gray level must be provided");
            return;
        }

        Log.d(TAG, "Setting printer gray level: " + nLevel);
        int ret = posApiHelper.PrintSetGray(nLevel);
        if (ret == 0) {
            printerConfig.setNormalGray(nLevel);
            result.success("Printer gray level set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set printer gray level", ret);
        }
    }

    /**
     * Set printer spacing
     */
    public void setSpace(MethodCall call, Result result) {
        Number xNumber = call.argument("x");
        Number yNumber = call.argument("y");

        if (xNumber == null || yNumber == null) {
            ErrorUtils.invalidArgument(result, "Both x and y spacing must be provided");
            return;
        }

        byte x = xNumber.byteValue();
        byte y = yNumber.byteValue();

        Log.d(TAG, String.format("Setting printer space: x=%d, y=%d", x, y));
        int ret = posApiHelper.PrintSetSpace(x, y);
        if (ret == 0) {
            result.success("Printer space set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set printer space", ret);
        }
    }

    /**
     * Get current printer font settings
     */
    public void getFont(MethodCall call, Result result) {
        Log.d(TAG, "Getting printer font settings");
        byte[] asciiFontHeight = new byte[1];
        byte[] extendFontHeight = new byte[1];
        byte[] zoom = new byte[1];

        int ret = posApiHelper.PrintGetFont(asciiFontHeight, extendFontHeight, zoom);
        if (ret == 0) {
            String fontInfo = String.format("ASCII: %d, Extend: %d, Zoom: %d",
                    asciiFontHeight[0], extendFontHeight[0], zoom[0]);
            Log.d(TAG, "Current font settings: " + fontInfo);
            result.success(fontInfo);
        } else {
            ErrorUtils.hardwareError(result, "Failed to get printer font", ret);
        }
    }

    /**
     * Set printer step (line feed)
     */
    public void setStep(MethodCall call, Result result) {
        Integer pixel = call.argument("pixel");
        if (pixel == null) {
            ErrorUtils.invalidArgument(result, "Pixel value must be provided");
            return;
        }

        Log.d(TAG, "Setting print step to " + pixel + " pixels");
        int ret = posApiHelper.PrintStep(pixel);
        if (ret == 0) {
            result.success("Print step set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set print step", ret);
        }
    }

    /**
     * Set printer voltage
     */
    public void setVoltage(MethodCall call, Result result) {
        Integer voltage = call.argument("voltage");
        if (voltage == null) {
            ErrorUtils.invalidArgument(result, "Voltage value must be provided");
            return;
        }

        Log.d(TAG, "Setting printer voltage to " + voltage);
        int ret = posApiHelper.PrintSetVoltage(voltage);
        if (ret == 0) {
            printerConfig.setVoltageLevel(voltage);
            result.success("Printer voltage set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set printer voltage", ret);
        }
    }

    /**
     * Set printer charge status
     */
    public void setChargeStatus(MethodCall call, Result result) {
        Integer ischarge = call.argument("ischarge");
        if (ischarge == null) {
            ErrorUtils.invalidArgument(result, "Charge status must be provided");
            return;
        }

        Log.d(TAG, "Setting printer charge status to " + ischarge);
        int ret = posApiHelper.PrintIsCharge(ischarge);
        if (ret == 0) {
            result.success("Printer charge status set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set printer charge status", ret);
        }
    }

    /**
     * Set line pixel distance
     */
    public void setLinePixelDistance(MethodCall call, Result result) {
        Integer linDistanceInt = call.argument("linDistance");
        if (linDistanceInt == null) {
            ErrorUtils.invalidArgument(result, "Line distance must be provided");
            return;
        }

        char linDistance = (char) linDistanceInt.intValue();
        Log.d(TAG, "Setting line pixel distance to " + linDistance);
        int ret = posApiHelper.PrintSetLinPixelDis(linDistance);
        if (ret == 0) {
            result.success("Print line pixel distance set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set print line pixel distance", ret);
        }
    }

    /**
     * Print text string
     */
    public void printString(MethodCall call, Result result) {
        String str = call.argument("str");
        if (str == null) {
            ErrorUtils.invalidArgument(result, "String to print must be provided");
            return;
        }

        Log.d(TAG, "Printing string: " + str);
        printerConfig.applyTextConfig(posApiHelper);

        int ret = posApiHelper.PrintStr(str);
        if (ret == 0) {
            result.success("String printed successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to print string", ret);
        }
    }

    /**
     * Print bitmap image
     */
    public void printBitmap(MethodCall call, Result result) {
        byte[] bmpData = call.argument("bmpData");
        if (bmpData == null) {
            ErrorUtils.invalidArgument(result, "Bitmap data must be provided");
            return;
        }

        Log.d(TAG, "Received bitmap data of size: " + bmpData.length);

        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bmpData, 0, bmpData.length);
            if (bitmap == null) {
                result.error(ErrorUtils.ERROR_INVALID_ARGUMENT, "Invalid bitmap data", null);
                return;
            }

            Log.d(TAG, "Bitmap decoded successfully. Width: " + bitmap.getWidth() + ", Height: " + bitmap.getHeight());

            printerConfig.applyImageConfig(posApiHelper);
            int ret = posApiHelper.PrintBmp(bitmap);

            if (ret == 0) {
                result.success("Bitmap printed successfully");
            } else {
                Log.e(TAG, "Failed to print bitmap. Error code: " + ret);
                ErrorUtils.hardwareError(result, "Failed to print bitmap", ret);
            }
        } catch (Exception e) {
            ErrorUtils.handleException("printBitmap", e, result);
        }
    }

    /**
     * Print barcode
     */
    public void printBarcode(MethodCall call, Result result) {
        String contents = call.argument("contents");
        Integer desiredWidth = call.argument("desiredWidth");
        Integer desiredHeight = call.argument("desiredHeight");
        String barcodeFormat = call.argument("barcodeFormat");

        if (contents == null || desiredWidth == null || desiredHeight == null || barcodeFormat == null) {
            ErrorUtils.invalidArgument(result, "Barcode parameters must be provided");
            return;
        }

        Log.d(TAG, String.format("Printing barcode: content=%s, width=%d, height=%d, format=%s",
                contents, desiredWidth, desiredHeight, barcodeFormat));

        printerConfig.applyBarcodeConfig(posApiHelper);
        int ret = posApiHelper.PrintBarcode(contents, desiredWidth, desiredHeight, barcodeFormat);

        if (ret == 0) {
            result.success("Barcode printed successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to print barcode", ret);
        }
    }

    /**
     * Print QR code
     */
    public void printQrCode(MethodCall call, Result result) {
        String contents = call.argument("contents");
        Integer desiredWidth = call.argument("desiredWidth");
        Integer desiredHeight = call.argument("desiredHeight");
        String barcodeFormat = call.argument("barcodeFormat");

        if (contents == null || desiredWidth == null || desiredHeight == null || barcodeFormat == null) {
            ErrorUtils.invalidArgument(result, "QR code parameters must be provided");
            return;
        }

        Log.d(TAG, String.format("Printing QR code: content=%s, width=%d, height=%d, format=%s",
                contents, desiredWidth, desiredHeight, barcodeFormat));

        printerConfig.applyBarcodeConfig(posApiHelper);
        int ret = posApiHelper.PrintQrCode_Cut(contents, desiredWidth, desiredHeight, barcodeFormat);

        if (ret == 0) {
            result.success("QR code printed successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to print QR code", ret);
        }
    }
    /**
     * Print QR code with text
     */
    public void printQrCodeWithText(MethodCall call, Result result) {
        String contents = call.argument("contents");
        String printTxt = call.argument("printTxt");
        Integer distance = call.argument("distance");
        Integer desiredWidth = call.argument("desiredWidth");
        Integer desiredHeight = call.argument("desiredHeight");
        String barcodeFormat = call.argument("barcodeFormat");

        if (contents == null || printTxt == null || distance == null ||
                desiredWidth == null || desiredHeight == null || barcodeFormat == null) {
            ErrorUtils.invalidArgument(result, "All QR code parameters must be provided");
            return;
        }

        Log.d(TAG, String.format("Printing QR code with text: content=%s, text=%s, distance=%d",
                contents, printTxt, distance));

        printerConfig.applyBarcodeConfig(posApiHelper);
        int ret = posApiHelper.PrintCutQrCode_Str(contents, printTxt, distance,
                desiredWidth, desiredHeight, barcodeFormat);

        if (ret == 0) {
            result.success("QR code with text printed successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to print QR code with text", ret);
        }
    }

    /**
     * Start printing
     */
    public void startPrinting(MethodCall call, Result result) {
        Log.d(TAG, "Starting print job");
        int ret = posApiHelper.PrintStart();
        if (ret == 0) {
            result.success("Print started successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to start print", ret);
        }
    }

    /**
     * Set left indent
     */
    public void setLeftIndent(MethodCall call, Result result) {
        Integer x = call.argument("x");
        if (x == null) {
            ErrorUtils.invalidArgument(result, "Indent value must be provided");
            return;
        }

        Log.d(TAG, "Setting left indent to " + x);
        int ret = posApiHelper.PrintSetLeftIndent(x);
        if (ret == 0) {
            printerConfig.setLeftIndent(x);
            result.success("Left indent set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set left indent", ret);
        }
    }

    /**
     * Set print alignment
     */
    public void setAlignment(MethodCall call, Result result) {
        Integer x = call.argument("x");
        if (x == null) {
            ErrorUtils.invalidArgument(result, "Alignment value must be provided");
            return;
        }

        Log.d(TAG, "Setting alignment to " + x);
        int ret = posApiHelper.PrintSetAlign(x);
        if (ret == 0) {
            printerConfig.setAlignment(x);
            result.success("Alignment set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set alignment", ret);
        }
    }

    /**
     * Set character spacing
     */
    public void setCharSpace(MethodCall call, Result result) {
        Integer x = call.argument("x");
        if (x == null) {
            ErrorUtils.invalidArgument(result, "Character space value must be provided");
            return;
        }

        Log.d(TAG, "Setting character space to " + x);
        int ret = posApiHelper.PrintCharSpace(x);
        if (ret == 0) {
            printerConfig.setCharSpace(x);
            result.success("Character space set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set character space", ret);
        }
    }

    /**
     * Set line spacing
     */
    public void setLineSpace(MethodCall call, Result result) {
        Integer x = call.argument("x");
        if (x == null) {
            ErrorUtils.invalidArgument(result, "Line space value must be provided");
            return;
        }

        Log.d(TAG, "Setting line space to " + x);
        int ret = posApiHelper.PrintSetLineSpace(x);
        if (ret == 0) {
            printerConfig.setLineSpace(x);
            result.success("Line space set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set line space", ret);
        }
    }

    /**
     * Set left space
     */
    public void setLeftSpace(MethodCall call, Result result) {
        Integer x = call.argument("x");
        if (x == null) {
            ErrorUtils.invalidArgument(result, "Left space value must be provided");
            return;
        }

        Log.d(TAG, "Setting left space to " + x);
        int ret = posApiHelper.PrintSetLeftSpace(x);
        if (ret == 0) {
            result.success("Left space set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set left space", ret);
        }
    }

    /**
     * Set print speed
     */
    public void setSpeed(MethodCall call, Result result) {
        Integer iSpeed = call.argument("iSpeed");
        if (iSpeed == null) {
            ErrorUtils.invalidArgument(result, "Speed value must be provided");
            return;
        }

        Log.d(TAG, "Setting print speed to " + iSpeed);
        int ret = posApiHelper.PrintSetSpeed(iSpeed);
        if (ret == 0) {
            printerConfig.setPrintSpeed(iSpeed);
            result.success("Print speed set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set print speed", ret);
        }
    }

    /**
     * Check printer status
     */
    public void checkStatus(MethodCall call, Result result) {
        Log.d(TAG, "Checking printer status");
        int ret = posApiHelper.PrintCheckStatus();
        if (ret == 0) {
            result.success("Printer status: OK");
        } else {
            String errorMessage;
            switch (ret) {
                case 1:
                    errorMessage = "Printer is busy";
                    break;
                case 2:
                    errorMessage = "Printer is out of paper";
                    break;
                case 3:
                    errorMessage = "Printer head is overheated";
                    break;
                case 4:
                    errorMessage = "Printer battery is low";
                    break;
                default:
                    errorMessage = "Unknown printer error";
                    break;
            }
            ErrorUtils.hardwareError(result, errorMessage, ret);
        }
    }

    /**
     * Feed paper
     */
    public void feedPaper(MethodCall call, Result result) {
        Integer step = call.argument("step");
        if (step == null) {
            ErrorUtils.invalidArgument(result, "Step value must be provided");
            return;
        }

        Log.d(TAG, "Feeding paper by " + step + " steps");
        int ret = posApiHelper.PrintFeedPaper(step);
        if (ret == 0) {
            result.success("Paper fed successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to feed paper", ret);
        }
    }

    /**
     * Set printer mode
     */
    public void setMode(MethodCall call, Result result) {
        Integer mode = call.argument("mode");
        if (mode == null) {
            ErrorUtils.invalidArgument(result, "Mode value must be provided");
            return;
        }

        Log.d(TAG, "Setting printer mode to " + mode);
        int ret = posApiHelper.PrintSetMode(mode);
        if (ret == 0) {
            printerConfig.setPrinterMode(mode);
            result.success("Print mode set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set print mode", ret);
        }
    }

    /**
     * Set underline
     */
    public void setUnderline(MethodCall call, Result result) {
        Integer x = call.argument("x");
        if (x == null) {
            ErrorUtils.invalidArgument(result, "Underline value must be provided");
            return;
        }

        Log.d(TAG, "Setting underline to " + x);
        int ret = posApiHelper.PrintSetUnderline(x);
        if (ret == 0) {
            result.success("Underline set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set underline", ret);
        }
    }

    /**
     * Set reverse mode
     */
    public void setReverse(MethodCall call, Result result) {
        Integer x = call.argument("x");
        if (x == null) {
            ErrorUtils.invalidArgument(result, "Reverse mode value must be provided");
            return;
        }

        Log.d(TAG, "Setting reverse mode to " + x);
        int ret = posApiHelper.PrintSetReverse(x);
        if (ret == 0) {
            result.success("Reverse mode set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set reverse mode", ret);
        }
    }

    /**
     * Set bold mode
     */
    public void setBold(MethodCall call, Result result) {
        Integer x = call.argument("x");
        if (x == null) {
            ErrorUtils.invalidArgument(result, "Bold mode value must be provided");
            return;
        }

        Log.d(TAG, "Setting bold mode to " + x);
        int ret = posApiHelper.PrintSetBold(x);
        if (ret == 0) {
            result.success("Bold mode set successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to set bold mode", ret);
        }
    }

    /**
     * Print logo
     */
    public void printLogo(MethodCall call, Result result) {
        byte[] logo = call.argument("logo");
        if (logo == null) {
            ErrorUtils.invalidArgument(result, "Logo data must be provided");
            return;
        }

        Log.d(TAG, "Printing logo of size " + logo.length);
        int ret = posApiHelper.PrintLogo(logo);
        if (ret == 0) {
            result.success("Logo printed successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to print logo", ret);
        }
    }

    /**
     * Locate label position
     */
    public void locateLabel(MethodCall call, Result result) {
        Integer step = call.argument("step");
        if (step == null) {
            ErrorUtils.invalidArgument(result, "Step value must be provided");
            return;
        }

        Log.d(TAG, "Locating label at step " + step);
        int ret = posApiHelper.PrintLabLocate(step);
        if (ret == 0) {
            result.success("Label located successfully");
        } else {
            ErrorUtils.hardwareError(result, "Failed to locate label", ret);
        }
    }

    /**
     * Print a PDF file
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void printPdf(MethodCall call, Result result) {
        String pdfPath = call.argument("pdfPath");
        if (pdfPath == null) {
            ErrorUtils.invalidArgument(result, "PDF path must be provided");
            return;
        }

        if (activity == null) {
            result.error(ErrorUtils.ERROR_UNEXPECTED, "Cannot print without an activity context", null);
            return;
        }

        Log.d(TAG, "Starting to print PDF: " + pdfPath);
        currentPdfPath = pdfPath;
        failedPages.clear();
        cancelRequested = false;

        executorService.execute(() -> {
            try {
                File file = new File(pdfPath);
                if (!file.exists()) {
                    String errorMsg = "PDF file does not exist: " + pdfPath;
                    Log.e(TAG, errorMsg);
                    mainHandler.post(() -> ErrorUtils.fileNotFound(result, pdfPath));
                    return;
                }

                ParcelFileDescriptor fileDescriptor =
                        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                PdfRenderer renderer = new PdfRenderer(fileDescriptor);
                totalPages = renderer.getPageCount();
                Log.d(TAG, "PdfRenderer created successfully. Page count: " + totalPages);

                // Apply PDF-specific printer settings
                printerConfig.applyPdfConfig(posApiHelper);

                boolean allPagesPrinted = processAndPrintPages(renderer, 0, totalPages - 1);

                renderer.close();
                fileDescriptor.close();

                // Save the printed document to history
                String documentId = null;
                try {
                    documentId = historyManager.savePrintedDocument(pdfPath, totalPages, failedPages);
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "Failed to save print history", e);
                }

                final String finalDocumentId = documentId;

                mainHandler.post(() -> {
                    if (cancelRequested) {
                        result.success(new HashMap<String, Object>() {{
                            put("status", "CANCELLED");
                            put("message", "Print job was cancelled");
                            put("documentId", finalDocumentId);
                        }});
                    } else if (!failedPages.isEmpty()) {
                        String warningMsg = "Some pages failed to print: " + failedPages;
                        Log.w(TAG, warningMsg);
                        result.success(new HashMap<String, Object>() {{
                            put("status", "PARTIAL_SUCCESS");
                            put("failedPages", failedPages);
                            put("message", warningMsg);
                            put("documentId", finalDocumentId);
                        }});
                    } else if (allPagesPrinted) {
                        Log.d(TAG, "PDF processed and printed successfully");
                        result.success(new HashMap<String, Object>() {{
                            put("status", "SUCCESS");
                            put("message", "PDF processed and printed successfully");
                            put("documentId", finalDocumentId);
                        }});
                    } else {
                        String errorMsg = "Failed to print all pages. Failed pages: " + failedPages;
                        Log.e(TAG, errorMsg);
                        result.error(ErrorUtils.ERROR_HARDWARE, errorMsg, null);
                    }
                });
            } catch (IOException e) {
                String errorMsg = "IOException occurred: " + e.getMessage();
                Log.e(TAG, errorMsg, e);
                mainHandler.post(() -> ErrorUtils.handleException("printPdf", e, result));
            } catch (Exception e) {
                String errorMsg = "Unexpected error occurred: " + e.getMessage();
                Log.e(TAG, errorMsg, e);
                mainHandler.post(() -> ErrorUtils.handleException("printPdf", e, result));
            }
        });
    }

    /**
     * Process and print PDF pages
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean processAndPrintPages(PdfRenderer renderer, int startPage, int endPage) {
        int tileWidth = 384; // Fixed width for the thermal printer
        int tileHeight = printerConfig.getBitmapTileHeight();

        boolean allPagesPrinted = true;

        for (int i = startPage; i <= endPage; i++) {
            if (cancelRequested) {
                Log.d(TAG, "Print job cancelled during processing");
                return false;
            }

            if (!processAndPrintPage(renderer, i, tileWidth, tileHeight)) {
                failedPages.add(i);
                allPagesPrinted = false;
                Log.e(TAG, "Failed to process and print page " + (i + 1));
            } else {
                sendPrintingProgressUpdate(i + 1, totalPages);
            }
        }

        return allPagesPrinted;
    }

    /**
     * Process and print a single PDF page
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean processAndPrintPage(PdfRenderer renderer, int pageIndex, int tileWidth, int tileHeight) {
        PdfRenderer.Page page = renderer.openPage(pageIndex);
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();

        boolean pageSuccessful = true;

        // Initialize printer for this page
        int ret = posApiHelper.PrintInit();
        if (ret != 0) {
            String errorMsg = "Failed to initialize printer for page " + (pageIndex + 1) + ". Error code: " + ret;
            Log.e(TAG, errorMsg);
            page.close();
            return false;
        }

        // Apply PDF-specific settings
        printerConfig.applyPdfConfig(posApiHelper);

        try {
            // Create bitmap with proper scale to fit printer width
            float scale = (float) tileWidth / pageWidth;
            int scaledHeight = (int) (pageHeight * scale);

            Bitmap fullPageBitmap = Bitmap.createBitmap(tileWidth, scaledHeight, Bitmap.Config.ARGB_8888);

            // Set up rendering for the scaled bitmap
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            // Create a Canvas to draw the page content on the bitmap
            Canvas canvas = new Canvas(fullPageBitmap);
            canvas.drawColor(0xFFFFFFFF); // White background
            canvas.setMatrix(matrix);

            // Render PDF page to bitmap
            page.render(fullPageBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);

            // Enhance bitmap for thermal printing if enabled
            if (printerConfig.isEnhanceImages() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                fullPageBitmap = enhanceBitmapForThermalPrinting(fullPageBitmap);
            }

            // Process and print the bitmap in tiles to avoid memory issues
            for (int y = 0; y < scaledHeight; y += tileHeight) {
                if (cancelRequested) {
                    Log.d(TAG, "Print job cancelled during page processing");
                    fullPageBitmap.recycle();
                    page.close();
                    return false;
                }

                int currentTileHeight = Math.min(tileHeight, scaledHeight - y);

                // Extract tile from full page bitmap
                Bitmap tileBitmap = Bitmap.createBitmap(fullPageBitmap, 0, y, tileWidth, currentTileHeight);

                // Print the tile
                ret = posApiHelper.PrintBmp(tileBitmap);
                tileBitmap.recycle();

                if (ret != 0) {
                    String errorMsg = "Failed to queue tile at page " + (pageIndex + 1) +
                            ", y=" + y + ". Error code: " + ret;
                    Log.e(TAG, errorMsg);
                    pageSuccessful = false;
                    break;
                }

                // Add a small step between tiles
                posApiHelper.PrintStep(1);
            }

            // Clean up
            fullPageBitmap.recycle();

            // Start printing if all tiles were successfully queued
            if (pageSuccessful) {
                ret = posApiHelper.PrintStart();
                if (ret != 0) {
                    String errorMsg = "Failed to start printing for page " + (pageIndex + 1) +
                            ". Error code: " + ret;
                    Log.e(TAG, errorMsg);
                    pageSuccessful = false;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error processing PDF page " + (pageIndex + 1), e);
            pageSuccessful = false;
        } finally {
            page.close();
        }

        // Update progress
        sendProcessingProgressUpdate(pageIndex + 1, totalPages);
        lastPrintedPageIndex = pageIndex;

        return pageSuccessful;
    }

    /**
     * Enhance bitmap for better thermal printing quality
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Bitmap enhanceBitmapForThermalPrinting(Bitmap original) {
        Bitmap output = Bitmap.createBitmap(original.getWidth(), original.getHeight(), original.getConfig());

        // Apply sharpening filter
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicConvolve3x3 convolution = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        Allocation input = Allocation.createFromBitmap(rs, original);
        Allocation outputAlloc = Allocation.createFromBitmap(rs, output);

        // Sharpening kernel
        float[] sharpening = {-1, -1, -1, -1, 9, -1, -1, -1, -1};
        convolution.setCoefficients(sharpening);
        convolution.setInput(input);
        convolution.forEach(outputAlloc);
        outputAlloc.copyTo(output);

        // Apply contrast enhancement
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix(new float[]{
                1.5f, 0, 0, 0, -20,
                0, 1.5f, 0, 0, -20,
                0, 0, 1.5f, 0, -20,
                0, 0, 0, 1, 0
        });

        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(output, 0, 0, paint);

        // Clean up RenderScript resources
        input.destroy();
        outputAlloc.destroy();
        convolution.destroy();
        rs.destroy();

        return output;
    }

    /**
     * Retry printing failed pages
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void retryJob(MethodCall call, Result result) {
        Log.d(TAG, "Starting retryFailedPages");

        if (failedPages.isEmpty()) {
            Log.d(TAG, "No failed pages to retry");
            result.success(new HashMap<String, Object>() {{
                put("status", "NO_RETRY_NEEDED");
                put("message", "No failed pages to retry");
            }});
            return;
        }

        if (currentPdfPath == null) {
            result.error(ErrorUtils.ERROR_INVALID_ARGUMENT, "No PDF has been printed yet", null);
            return;
        }

        executorService.execute(() -> {
            try {
                File file = new File(currentPdfPath);
                if (!file.exists()) {
                    String errorMsg = "PDF file does not exist: " + currentPdfPath;
                    Log.e(TAG, errorMsg);
                    mainHandler.post(() -> ErrorUtils.fileNotFound(result, currentPdfPath));
                    return;
                }

                ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                PdfRenderer renderer = new PdfRenderer(fileDescriptor);

                List<Integer> stillFailedPages = new ArrayList<>();
                int totalRetryPages = failedPages.size();
                int currentRetryPage = 0;

                // Apply PDF settings
                printerConfig.applyPdfConfig(posApiHelper);

                for (int pageIndex : failedPages) {
                    currentRetryPage++;
                    if (!processAndPrintPage(renderer, pageIndex, 384, printerConfig.getBitmapTileHeight())) {
                        stillFailedPages.add(pageIndex);
                        Log.e(TAG, "Failed to reprint page " + (pageIndex + 1));
                    } else {
                        Log.d(TAG, "Successfully reprinted page " + (pageIndex + 1));
                    }
                    sendRetryProgressUpdate(currentRetryPage, totalRetryPages);
                }

                renderer.close();
                fileDescriptor.close();

                // Update print job in history if available
                try {
                    if (currentPdfPath != null) {
                        PrintJob job = historyManager.findPrintJobById(
                                new File(currentPdfPath).getName().replace(".pdf", ""));
                        if (job != null) {
                            job.setFailedPages(stillFailedPages);
                            job.setStatus(stillFailedPages.isEmpty() ? "COMPLETED" : "PARTIAL_SUCCESS");
                            historyManager.updatePrintJob(job);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to update print job history", e);
                }

                // Update the list of failed pages
                failedPages = stillFailedPages;

                mainHandler.post(() -> {
                    if (!failedPages.isEmpty()) {
                        String warningMsg = "Some pages still failed after retry: " + failedPages;
                        Log.w(TAG, warningMsg);
                        result.success(new HashMap<String, Object>() {{
                            put("status", "PARTIAL_RETRY_SUCCESS");
                            put("failedPages", failedPages);
                            put("message", warningMsg);
                        }});
                    } else {
                        Log.d(TAG, "All failed pages printed successfully on retry");
                        result.success(new HashMap<String, Object>() {{
                            put("status", "RETRY_SUCCESS");
                            put("message", "All failed pages printed successfully on retry");
                        }});
                    }
                });

            } catch (IOException e) {
                String errorMsg = "IOException occurred during retry: " + e.getMessage();
                Log.e(TAG, errorMsg, e);
                mainHandler.post(() -> ErrorUtils.handleException("retryJob", e, result));
            } catch (Exception e) {
                String errorMsg = "Unexpected error occurred during retry: " + e.getMessage();
                Log.e(TAG, errorMsg, e);
                mainHandler.post(() -> ErrorUtils.handleException("retryJob", e, result));
            }
        });
    }

    /**
     * Cancel current print job
     */
    public void cancelJob(MethodCall call, Result result) {
        Log.d(TAG, "Cancelling print job");
        cancelRequested = true;
        result.success("Print job cancellation requested");
    }

    /**
     * Get print history
     */
    public void getPrintHistory(MethodCall call, Result result) {
        Log.d(TAG, "Getting print history");
        try {
            String historyContent = historyManager.getHistoryJson();
            result.success(historyContent);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read print history", e);
            ErrorUtils.handleException("getPrintHistory", e, result);
        }
    }

    /**
     * Reprint a document from history
     */
    public void reprintDocument(MethodCall call, Result result) {
        String documentId = call.argument("documentId");
        if (documentId == null) {
            ErrorUtils.invalidArgument(result, "Document ID must be provided");
            return;
        }

        Log.d(TAG, "Reprinting document with ID: " + documentId);

        try {
            PrintJob job = historyManager.findPrintJobById(documentId);
            if (job == null) {
                result.error(ErrorUtils.ERROR_NOT_FOUND, "Document with ID " + documentId + " not found", null);
                return;
            }

            // Check if the saved PDF exists
            File savedFile = new File(job.getSavedPath());
            if (!savedFile.exists()) {
                result.error(ErrorUtils.ERROR_NOT_FOUND, "Saved PDF file does not exist: " + job.getSavedPath(), null);
                return;
            }

            // Print the saved PDF
            printPdf(new MethodCall("PrintPdf", new HashMap<String, Object>() {{
                put("pdfPath", job.getSavedPath());
            }}), result);

        } catch (IOException | JSONException e) {
            Log.e(TAG, "Failed to reprint document", e);
            ErrorUtils.handleException("reprintDocument", e, result);
        }
    }

    // Helper methods for progress updates

    private void sendProcessingProgressUpdate(int currentPage, int totalPages) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;

        if (channel != null) {
            Map<String, Object> progressMap = new HashMap<>();
            progressMap.put("currentPage", currentPage);
            progressMap.put("totalPages", totalPages);
            progressMap.put("method", "processingProgress");

            Log.d(TAG, "Sending processing progress update: " + currentPage + "/" + totalPages);

            mainHandler.post(() -> channel.invokeMethod("processingProgress", progressMap));
        }
    }

    private void sendPrintingProgressUpdate(int currentPage, int totalPages) {
        if (channel != null) {
            Map<String, Object> progressMap = new HashMap<>();
            progressMap.put("currentPage", currentPage);
            progressMap.put("totalPages", totalPages);
            progressMap.put("method", "printingProgress");

            Log.d(TAG, "Sending printing progress update: " + currentPage + "/" + totalPages);
            new Handler(Looper.getMainLooper()).post(() -> channel.invokeMethod("printingProgress", progressMap));
//            mainHandler.post(() -> channel.invokeMethod("printingProgress", progressMap));
        }
    }

    private void sendRetryProgressUpdate(int currentPage, int totalPages) {
        if (channel != null) {
            Map<String, Object> progressMap = new HashMap<>();
            progressMap.put("currentPage", currentPage);
            progressMap.put("totalPages", totalPages);
            progressMap.put("method", "retryProgress");

            Log.d(TAG, "Sending retry progress update: " + currentPage + "/" + totalPages);

            mainHandler.post(() -> channel.invokeMethod("retryProgress", progressMap));
        }
    }
}