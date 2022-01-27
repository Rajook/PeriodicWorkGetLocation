package com.krunal.locationexample.Utility;

import java.io.IOException;

//import okhttp3.MediaType;
//import okhttp3.ResponseBody;
//import okio.Buffer;
//import okio.BufferedSource;
//import okio.ForwardingSource;
//import okio.Okio;
//import okio.Source;

public class ProgressResponseBody implements Runnable{   //extends ResponseBody {
    private final OnAttachmentDownloadListener progressListener;
    private boolean isDownloading;

    int progress = 0;
    public ProgressResponseBody(Boolean isDownloading, OnAttachmentDownloadListener progressListener) {
        this.isDownloading = isDownloading;
        this.progressListener = progressListener;
    }

    public void changeState(Boolean isDownloading){
        this.isDownloading = isDownloading;
    }

    @Override
    public void run() {
        while(isDownloading){
            progress++;
            progressListener.onAttachmentDownloadUpdate(progress);
            continue;
        }
    }

//    private final ResponseBody responseBody;
//    private final OnAttachmentDownloadListener progressListener;
//    private BufferedSource bufferedSource;
//
//    public ProgressResponseBody(ResponseBody responseBody, OnAttachmentDownloadListener progressListener) {
//        this.responseBody = responseBody;
//        this.progressListener = progressListener;
//    }
//
//    @Override
//    public MediaType contentType() {
//        return responseBody.contentType();
//    }
//
//    @Override
//    public long contentLength() {
//        return responseBody.contentLength();
//    }
//
//    @Override
//    public BufferedSource source() {
//        if (bufferedSource == null) {
//            bufferedSource = Okio.buffer(source(responseBody.source()));
//        }
//        return bufferedSource;
//    }
//
//    private Source source(Source source) {
//        return new ForwardingSource(source) {
//            long totalBytesRead = 0L;
//
//            @Override
//            public long read(Buffer sink, long byteCount) throws IOException {
//                long bytesRead = super.read(sink, byteCount);
//
//                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
//
//                float percent = bytesRead == -1 ? 100f : (((float) totalBytesRead / (float) responseBody.contentLength()) * 100);
//
//                if (progressListener != null)
//                    progressListener.onAttachmentDownloadUpdate((int) percent);
//
//                return bytesRead;
//            }
//        };
//    }



    public interface OnAttachmentDownloadListener {
        void onAttachmentDownloadedSuccess();

        void onAttachmentDownloadedError();

        void onAttachmentDownloadedFinished();

        void onAttachmentDownloadUpdate(int percent);
    }



}
