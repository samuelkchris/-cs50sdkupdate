package com.pinnisoft.cs50sdkupdate;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages print job history storage and retrieval
 */
public class PrintHistoryManager {
    private static final String TAG = "PrintHistoryManager";
    private static final String HISTORY_FILE_NAME = "print_history.json";
    private static final int MAX_HISTORY_ENTRIES = 50;

    private File historyDir;
    private final File historyFile;
    private final Context context;

    public PrintHistoryManager(Context context) {
        this.context = context;

        // Initialize history directory
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            historyDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        } else {
            historyDir = new File(context.getFilesDir(), "print_history");
        }

        if (historyDir == null) {
            // Fallback to internal storage if external is not available
            historyDir = new File(context.getFilesDir(), "print_history");
        }

        if (!historyDir.exists()) {
            historyDir.mkdirs();
        }

        historyFile = new File(historyDir, HISTORY_FILE_NAME);
        initializeHistoryFile();
    }

    /**
     * Initialize the history file if it doesn't exist
     */
    private void initializeHistoryFile() {
        if (!historyFile.exists()) {
            try {
                historyFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(historyFile);
                fos.write("[]".getBytes());
                fos.close();
                Log.d(TAG, "Created new print history file");
            } catch (IOException e) {
                Log.e(TAG, "Failed to create print history file", e);
            }
        }
    }

    /**
     * Save a new print job to history
     */
    public String savePrintedDocument(String originalPdfPath, int totalPages, List<Integer> failedPages) throws IOException, JSONException {
        // Read current history
        JSONArray historyArray = loadHistoryArray();

        // Generate new document ID
        int documentCount = historyArray.length();
        String documentId = String.valueOf(documentCount + 1);

        // Create destination file
        File destFile = new File(historyDir, documentId + ".pdf");

        // Copy the PDF file
        copyFile(new File(originalPdfPath), destFile);

        // Create and add entry to history
        PrintJob job = new PrintJob(
                documentId,
                originalPdfPath,
                destFile.getAbsolutePath(),
                getCurrentTimestamp()
        );
        job.setTotalPages(totalPages);
        job.setPrintedPages(totalPages - failedPages.size());
        job.setFailedPages(failedPages);

        if (failedPages.isEmpty()) {
            job.setStatus("COMPLETED");
        } else {
            job.setStatus("PARTIAL_SUCCESS");
        }

        historyArray.put(job.toJson());

        // Trim history if necessary
        if (historyArray.length() > MAX_HISTORY_ENTRIES) {
            JSONArray trimmedArray = new JSONArray();
            for (int i = historyArray.length() - MAX_HISTORY_ENTRIES; i < historyArray.length(); i++) {
                trimmedArray.put(historyArray.get(i));
            }
            historyArray = trimmedArray;
        }

        // Save updated history
        saveHistoryArray(historyArray);

        return documentId;
    }

    /**
     * Update an existing print job history
     */
    public void updatePrintJob(PrintJob job) throws IOException, JSONException {
        JSONArray historyArray = loadHistoryArray();
        boolean found = false;

        for (int i = 0; i < historyArray.length(); i++) {
            JSONObject entry = historyArray.getJSONObject(i);
            if (entry.getString("id").equals(job.getId())) {
                historyArray.put(i, job.toJson());
                found = true;
                break;
            }
        }

        if (found) {
            saveHistoryArray(historyArray);
        } else {
            Log.w(TAG, "Print job not found for updating: " + job.getId());
        }
    }

    /**
     * Get a list of all print jobs
     */
    public List<PrintJob> getAllPrintJobs() throws IOException, JSONException {
        JSONArray historyArray = loadHistoryArray();
        List<PrintJob> jobs = new ArrayList<>();

        for (int i = 0; i < historyArray.length(); i++) {
            jobs.add(PrintJob.fromJson(historyArray.getJSONObject(i)));
        }

        return jobs;
    }

    /**
     * Find a print job by ID
     */
    public PrintJob findPrintJobById(String jobId) throws IOException, JSONException {
        JSONArray historyArray = loadHistoryArray();

        for (int i = 0; i < historyArray.length(); i++) {
            JSONObject entry = historyArray.getJSONObject(i);
            if (entry.getString("id").equals(jobId)) {
                return PrintJob.fromJson(entry);
            }
        }

        return null;
    }

    /**
     * Get history content as a JSON string
     */
    public String getHistoryJson() throws IOException {
        return readFileContent(historyFile);
    }

    /**
     * Load the history array from file
     */
    private JSONArray loadHistoryArray() throws IOException, JSONException {
        String historyContent = readFileContent(historyFile);
        if (historyContent.isEmpty()) {
            return new JSONArray();
        }
        return new JSONArray(historyContent);
    }

    /**
     * Save the history array to file
     */
    private void saveHistoryArray(JSONArray historyArray) throws IOException {
        FileOutputStream fos = new FileOutputStream(historyFile);
        try {
            fos.write(historyArray.toString(2).getBytes());
        } catch (JSONException e) {
            Log.e(TAG, "Error converting JSON to string", e);
            fos.write("[]".getBytes()); // Write empty array as fallback
        }
        fos.close();
    }

    /**
     * Read content from a file
     */
    private String readFileContent(File file) throws IOException {
        if (!file.exists() || file.length() == 0) {
            return "";
        }

        byte[] content = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(content);
        fis.close();
        return new String(content);
    }

    /**
     * Copy a file from source to destination
     */
    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            throw new IOException("Source file does not exist: " + sourceFile.getAbsolutePath());
        }

        try (FileChannel source = new FileInputStream(sourceFile).getChannel();
             FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        }
    }

    /**
     * Get current timestamp in format yyyyMMdd_HHmmss
     */
    private String getCurrentTimestamp() {
        return new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }
}