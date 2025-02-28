package com.pinnisoft.cs50sdkupdate;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for print job data and history
 */
public class PrintJob implements Parcelable {
    private String id;
    private String originalPath;
    private String savedPath;
    private String timestamp;
    private int totalPages;
    private int printedPages;
    private List<Integer> failedPages;
    private String status;

    public PrintJob() {
        failedPages = new ArrayList<>();
    }

    public PrintJob(String id, String originalPath, String savedPath, String timestamp) {
        this.id = id;
        this.originalPath = originalPath;
        this.savedPath = savedPath;
        this.timestamp = timestamp;
        this.failedPages = new ArrayList<>();
        this.status = "CREATED";
    }

    /**
     * Create a PrintJob from JSON data
     */
    public static PrintJob fromJson(JSONObject json) throws JSONException {
        PrintJob job = new PrintJob();
        job.id = json.getString("id");
        job.originalPath = json.optString("originalPath", "");
        job.savedPath = json.getString("savedPath");
        job.timestamp = json.getString("timestamp");
        job.totalPages = json.optInt("totalPages", 0);
        job.printedPages = json.optInt("printedPages", 0);
        job.status = json.optString("status", "UNKNOWN");

        // Add failed pages if available
        if (json.has("failedPages")) {
            String failedPagesStr = json.getString("failedPages");
            if (failedPagesStr != null && !failedPagesStr.isEmpty()) {
                String[] pageNumbers = failedPagesStr.split(",");
                for (String page : pageNumbers) {
                    try {
                        job.failedPages.add(Integer.parseInt(page.trim()));
                    } catch (NumberFormatException e) {
                        // Skip invalid numbers
                    }
                }
            }
        }
        return job;
    }

    /**
     * Convert this PrintJob to a JSON object
     */
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("originalPath", originalPath);
        json.put("savedPath", savedPath);
        json.put("timestamp", timestamp);
        json.put("totalPages", totalPages);
        json.put("printedPages", printedPages);
        json.put("status", status);

        // Convert failed pages to comma-separated string
        if (failedPages != null && !failedPages.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < failedPages.size(); i++) {
                sb.append(failedPages.get(i));
                if (i < failedPages.size() - 1) {
                    sb.append(",");
                }
            }
            json.put("failedPages", sb.toString());
        }

        return json;
    }

    // Parcelable implementation

    protected PrintJob(Parcel in) {
        id = in.readString();
        originalPath = in.readString();
        savedPath = in.readString();
        timestamp = in.readString();
        totalPages = in.readInt();
        printedPages = in.readInt();
        status = in.readString();
        failedPages = new ArrayList<>();
        in.readList(failedPages, Integer.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(originalPath);
        dest.writeString(savedPath);
        dest.writeString(timestamp);
        dest.writeInt(totalPages);
        dest.writeInt(printedPages);
        dest.writeString(status);
        dest.writeList(failedPages);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PrintJob> CREATOR = new Creator<PrintJob>() {
        @Override
        public PrintJob createFromParcel(Parcel in) {
            return new PrintJob(in);
        }

        @Override
        public PrintJob[] newArray(int size) {
            return new PrintJob[size];
        }
    };

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getSavedPath() {
        return savedPath;
    }

    public void setSavedPath(String savedPath) {
        this.savedPath = savedPath;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPrintedPages() {
        return printedPages;
    }

    public void setPrintedPages(int printedPages) {
        this.printedPages = printedPages;
    }

    public List<Integer> getFailedPages() {
        return failedPages;
    }

    public void setFailedPages(List<Integer> failedPages) {
        this.failedPages = failedPages;
    }

    public void addFailedPage(int pageNumber) {
        if (failedPages == null) {
            failedPages = new ArrayList<>();
        }
        failedPages.add(pageNumber);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status) || "PARTIAL_SUCCESS".equals(status);
    }

    public boolean hasFailedPages() {
        return failedPages != null && !failedPages.isEmpty();
    }
}