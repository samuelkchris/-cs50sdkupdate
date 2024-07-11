package com.pinnisoft.cs50sdkupdate;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintJob;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.print.PrintManager;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrintJobManager {
    private final Context context;
    private final PrintManager printManager;
    private final Map<PrintJobId, PrintJobStatus> printJobs;
    private final List<PrintJobListener> listeners;
    private final Handler mainHandler;

    public PrintJobManager(Context context) {
        this.context = context;
        this.printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        this.printJobs = new HashMap<>();
        this.listeners = new ArrayList<>();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void startMonitoring() {
        new Thread(this::monitorPrintJobs).start();
    }

    private void monitorPrintJobs() {
        while (true) {
            List<PrintJob> jobs = printManager.getPrintJobs();
            for (PrintJob job : jobs) {
                updatePrintJobStatus(job);
            }
            try {
                Thread.sleep(1000); // Check every second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updatePrintJobStatus(PrintJob printJob) {
        PrintJobStatus status = printJobs.get(printJob.getId());
        if (status == null) {
            status = new PrintJobStatus(printJob);
            printJobs.put(printJob.getId(), status);
        } else {
            status.update(printJob);
        }
        notifyListeners(status);
    }

    public void cancelPrintJob(String jobId) {
        PrintJobStatus status = printJobs.get(jobId);
        if (status != null && status.getPrintJob() != null) {
            status.getPrintJob().cancel();
        }
    }

    public void restartPrintJob(String jobId) {
        PrintJobStatus status = printJobs.get(jobId);
        if (status != null && status.getPrintJob() != null) {
            status.getPrintJob().restart();
        }
    }

    public List<PrintJobStatus> getAllPrintJobs() {
        return new ArrayList<>(printJobs.values());
    }

    public void addPrintJobListener(PrintJobListener listener) {
        listeners.add(listener);
    }

    public void removePrintJobListener(PrintJobListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(final PrintJobStatus status) {
        mainHandler.post(() -> {
            for (PrintJobListener listener : listeners) {
                listener.onPrintJobStatusChanged(status);
            }
        });
    }

    public static class PrintJobStatus {
        private final PrintJob printJob;
        private PrintJobInfo jobInfo;
        private int progress;

        PrintJobStatus(PrintJob printJob) {
            this.printJob = printJob;
            update(printJob);
        }

        void update(PrintJob job) {
            this.jobInfo = job.getInfo();
            this.progress = calculateProgress(job);
        }

        private int calculateProgress(PrintJob job) {
            if (job.isCompleted()) return 100;
            if (job.isStarted()) return 50;
            if (job.isQueued()) return 25;
            return 0;
        }

        public PrintJob getPrintJob() {
            return printJob;
        }

        public PrintJobInfo getJobInfo() {
            return jobInfo;
        }

        public int getProgress() {
            return progress;
        }

        @NonNull
        @Override
        public String toString() {
            return "PrintJobStatus{" +
                    "jobId=" + printJob.getId() +
                    ", state=" + jobInfo.getState() +
                    ", progress=" + progress +
                    '}';
        }
    }

    public interface PrintJobListener {
        void onPrintJobStatusChanged(PrintJobStatus status);
    }
}
