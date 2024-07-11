//package com.pinnisoft.cs50sdkupdate;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.pdf.PdfRenderer;
//import android.os.ParcelFileDescriptor;
//import android.print.PrintAttributes;
//import android.print.PrintJob;
//import android.print.PrintManager;
//import com.ctk.sdk.PosApiHelper;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PrinterManager {
//    private final PosApiHelper posApiHelper;
//    private final Context context;
//    private int numCopies;
//    private PrintAttributes.MediaSize mediaSize;
//    private PrintAttributes.Orientation orientation;
//    private int currentPage;
//    private int totalPages;
//    private List<PrintJob> activeJobs;
//
//    public PrinterManager(Context context, PosApiHelper posApiHelper) {
//        this.context = context;
//        this.posApiHelper = posApiHelper;
//        this.numCopies = 1;
//        this.mediaSize = PrintAttributes.MediaSize.ISO_A4;
//        this.orientation = PrintAttributes.Orientation.PORTRAIT;
//        this.currentPage = 1;
//        this.totalPages = 1;
//        this.activeJobs = new ArrayList<>();
//    }
//
//    public void setNumCopies(int numCopies) {
//        this.numCopies = numCopies;
//    }
//
//    public void setMediaSize(PrintAttributes.MediaSize mediaSize) {
//        this.mediaSize = mediaSize;
//    }
//
//    public void setOrientation(PrintAttributes.Orientation orientation) {
//        this.orientation = orientation;
//    }
//
//    public void setTotalPages(int totalPages) {
//        this.totalPages = totalPages;
//    }
//
//    public void startPrintJob() {
//        posApiHelper.PrintInit();
//        applyPrinterSettings();
//    }
//
//    private void applyPrinterSettings() {
//        // Apply media size
//        if (mediaSize.equals(PrintAttributes.MediaSize.ISO_A4)) {
//            posApiHelper.PrintSetLinPixelDis((char) 384);
//        } else if (mediaSize.equals(PrintAttributes.MediaSize.ISO_A5)) {
//            posApiHelper.PrintSetLinPixelDis((char) 288);
//        } else if (mediaSize.equals(PrintAttributes.MediaSize.NA_LETTER)) {
//            posApiHelper.PrintSetLinPixelDis((char) 400);
//        }
//
//        // Apply orientation
//        if (orientation == PrintAttributes.Orientation.LANDSCAPE) {
//            posApiHelper.PrintSetMode(1);
//        } else {
//            posApiHelper.PrintSetMode(0);
//        }
//    }
//
//    public void printPage(Bitmap pageContent) {
//        for (int i = 0; i < numCopies; i++) {
//            posApiHelper.PrintBmp(pageContent);
//            posApiHelper.PrintStr("Page " + currentPage + " of " + totalPages);
//            posApiHelper.PrintStart();
//        }
//        currentPage++;
//    }
//
//    public void finishPrintJob() {
//        posApiHelper.PrintFeedPaper(100);
//        currentPage = 1;
//    }
//
//    public void printPDF(String pdfPath) throws IOException {
//        File file = new File(pdfPath);
//        PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
//
//        totalPages = renderer.getPageCount();
//        setTotalPages(totalPages);
//
//        for (int i = 0; i < totalPages; i++) {
//            PdfRenderer.Page page = renderer.openPage(i);
//            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
//            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);
//            printPage(bitmap);
//            page.close();
//            bitmap.recycle();
//        }
//
//        renderer.close();
//        finishPrintJob();
//    }
//
//    public void printText(String text) {
//        posApiHelper.PrintStr(text);
//        posApiHelper.PrintStart();
//    }
//
//    public void printBarcode(String contents, int width, int height, String format) {
//        posApiHelper.PrintBarcode(contents, width, height, format);
//        posApiHelper.PrintStart();
//    }
//
//    public void printQRCode(String contents, int width, int height) {
//        posApiHelper.PrintQrCode_Cut(contents, width, height, "QR_CODE");
//        posApiHelper.PrintStart();
//    }
//
//    public boolean isPrinterReady() {
//        return posApiHelper.PrintCheckStatus() == 0;
//    }
//
//    public String getPrintJobStatus() {
//        return "Printing page " + currentPage + " of " + totalPages;
//    }
//
//    public void queuePrintJob(String name, PrintAttributes attributes, String contentType, String content) {
//        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
//        PrintJob printJob = printManager.print(name, new PrintContentAdapter(context, contentType, content), attributes);
//        activeJobs.add(printJob);
//    }
//
//    public List<String> getActiveJobStatuses() {
//        List<String> statuses = new ArrayList<>();
//        for (PrintJob job : activeJobs) {
//            if (job.isCompleted() || job.isFailed() || job.isCancelled()) {
//                activeJobs.remove(job);
//            } else {
//                statuses.add(job.getInfo().getLabel() + ": " + getJobState(job));
//            }
//        }
//        return statuses;
//    }
//
//    private String getJobState(PrintJob printJob) {
//        if (printJob.isBlocked()) return "Blocked";
//        if (printJob.isCancelled()) return "Cancelled";
//        if (printJob.isCompleted()) return "Completed";
//        if (printJob.isFailed()) return "Failed";
//        if (printJob.isQueued()) return "Queued";
//        if (printJob.isStarted()) return "Started";
//        return "Unknown";
//    }
//}
