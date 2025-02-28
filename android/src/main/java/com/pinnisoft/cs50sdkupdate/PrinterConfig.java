package com.pinnisoft.cs50sdkupdate;

import android.util.Log;

import com.ctk.sdk.PosApiHelper;

/**
 * Handles printer configuration settings
 */
public class PrinterConfig {
    private static final String TAG = "PrinterConfig";

    // Gray level (darkness) for normal text
    private int normalGray = 5;

    // Gray level for images and bitmaps
    private int imageGray = 7;

    // Print speed (0-1)
    private int printSpeed = 1;

    // Printer mode
    private int printerMode = 0;

    // Text alignment (0=left, 1=center, 2=right)
    private int alignment = 0;

    // Font settings
    private byte asciiFontHeight = 24;
    private byte extendFontHeight = 24;
    private byte fontZoom = 0x33;  // Default zoom factor

    // Line spacing
    private int lineSpace = 8;

    // Character spacing
    private int charSpace = 0;

    // Left margin/indent
    private int leftIndent = 0;

    // PDF print settings
    private int pdfDpi = 203;
    private boolean enhanceImages = true;
    private int bitmapTileHeight = 984;

    // Print voltage level
    private int voltageLevel = 0;

    /**
     * Apply default configuration to printer
     */
    public void applyDefaults(PosApiHelper posApiHelper) {
        Log.d(TAG, "Applying default printer configuration");

        posApiHelper.PrintSetGray(normalGray);
        posApiHelper.PrintSetSpeed(printSpeed);
        posApiHelper.PrintSetMode(printerMode);
        posApiHelper.PrintSetFont(asciiFontHeight, extendFontHeight, fontZoom);
        posApiHelper.PrintSetAlign(alignment);
        posApiHelper.PrintSetLineSpace(lineSpace);
        posApiHelper.PrintCharSpace(charSpace);
        posApiHelper.PrintSetLeftIndent(leftIndent);
    }

    /**
     * Apply configuration optimized for printing text
     */
    public void applyTextConfig(PosApiHelper posApiHelper) {
        Log.d(TAG, "Applying text printer configuration");

        posApiHelper.PrintSetGray(normalGray);
        posApiHelper.PrintSetSpeed(printSpeed);
        posApiHelper.PrintSetAlign(alignment);
        posApiHelper.PrintSetFont(asciiFontHeight, extendFontHeight, fontZoom);
        posApiHelper.PrintSetLineSpace(lineSpace);
    }

    /**
     * Apply configuration optimized for printing images
     */
    public void applyImageConfig(PosApiHelper posApiHelper) {
        Log.d(TAG, "Applying image printer configuration");

        posApiHelper.PrintSetGray(imageGray);
        posApiHelper.PrintSetSpeed(0);  // Slower speed for better image quality
        posApiHelper.PrintSetAlign(1);  // Center alignment for images
    }

    /**
     * Apply configuration optimized for PDF printing
     */
    public void applyPdfConfig(PosApiHelper posApiHelper) {
        Log.d(TAG, "Applying PDF printer configuration");

        posApiHelper.PrintSetGray(imageGray);
        posApiHelper.PrintSetMode(0);
        posApiHelper.PrintSetSpeed(0);  // Slower speed for better PDF quality
        posApiHelper.PrintSetAlign(0);  // Left alignment
    }

    /**
     * Apply configuration optimized for barcode printing
     */
    public void applyBarcodeConfig(PosApiHelper posApiHelper) {
        Log.d(TAG, "Applying barcode printer configuration");

        posApiHelper.PrintSetGray(imageGray - 1);  // Slightly lighter for barcodes
        posApiHelper.PrintSetAlign(1);  // Center alignment for barcodes
    }

    // Getters and setters

    public int getNormalGray() {
        return normalGray;
    }

    public void setNormalGray(int normalGray) {
        this.normalGray = normalGray;
    }

    public int getImageGray() {
        return imageGray;
    }

    public void setImageGray(int imageGray) {
        this.imageGray = imageGray;
    }

    public int getPrintSpeed() {
        return printSpeed;
    }

    public void setPrintSpeed(int printSpeed) {
        this.printSpeed = printSpeed;
    }

    public int getPrinterMode() {
        return printerMode;
    }

    public void setPrinterMode(int printerMode) {
        this.printerMode = printerMode;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public byte getAsciiFontHeight() {
        return asciiFontHeight;
    }

    public void setAsciiFontHeight(byte asciiFontHeight) {
        this.asciiFontHeight = asciiFontHeight;
    }

    public byte getExtendFontHeight() {
        return extendFontHeight;
    }

    public void setExtendFontHeight(byte extendFontHeight) {
        this.extendFontHeight = extendFontHeight;
    }

    public byte getFontZoom() {
        return fontZoom;
    }

    public void setFontZoom(byte fontZoom) {
        this.fontZoom = fontZoom;
    }

    public int getLineSpace() {
        return lineSpace;
    }

    public void setLineSpace(int lineSpace) {
        this.lineSpace = lineSpace;
    }

    public int getCharSpace() {
        return charSpace;
    }

    public void setCharSpace(int charSpace) {
        this.charSpace = charSpace;
    }

    public int getLeftIndent() {
        return leftIndent;
    }

    public void setLeftIndent(int leftIndent) {
        this.leftIndent = leftIndent;
    }

    public int getPdfDpi() {
        return pdfDpi;
    }

    public void setPdfDpi(int pdfDpi) {
        this.pdfDpi = pdfDpi;
    }

    public boolean isEnhanceImages() {
        return enhanceImages;
    }

    public void setEnhanceImages(boolean enhanceImages) {
        this.enhanceImages = enhanceImages;
    }

    public int getBitmapTileHeight() {
        return bitmapTileHeight;
    }

    public void setBitmapTileHeight(int bitmapTileHeight) {
        this.bitmapTileHeight = bitmapTileHeight;
    }

    public int getVoltageLevel() {
        return voltageLevel;
    }

    public void setVoltageLevel(int voltageLevel) {
        this.voltageLevel = voltageLevel;
    }
}