package com.github.guawazi.servicedemo.download;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wangliang on 2018/1/22.
 * 下载的异步任务
 * 只提供了单纯的下载功能，并把下载的完成情况通过 DownLoadListener 回调出去，自己处理
 */

public class DownLoadTask extends AsyncTask<String, Integer, Integer> {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    private final DownLoadListener mDownLoadListener;
    private final OkHttpClient mOkHttpClient;


    private boolean mIsCanceled = false;
    private boolean mIsPaused = false;
    private int lastProgress;

    public DownLoadTask(DownLoadListener downLoadListener) {
        mDownLoadListener = downLoadListener;
        mOkHttpClient = new OkHttpClient();
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        try {
            long downloadedLength = 0; // 记录已经下载文件的长度
            String downloadUrl = params[0];
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory, fileName);
            if (file.exists()) {
                downloadedLength = file.length();
            }
            // 文件总长度
            long contentLength = getContentLength(downloadUrl);
            if (contentLength == 0) { // 下载失败
                return TYPE_FAILED;
            } else if (downloadedLength == contentLength) { //文件数和下载数相同，下载成功
                return TYPE_SUCCESS;
            }

            // 断点下载的 range 头
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(downloadUrl)
                    .build();

            Response response = mOkHttpClient.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                // 跳过已经下载的
                savedFile.seek(downloadedLength);

                // 读写文件(基本操作)
                byte[] b = new byte[1024];
                int total = 0;
                int len = 0;
                while ((len = is.read(b)) != -1) {
                    if (mIsCanceled) { // 取消了
                        return TYPE_CANCELED;
                    } else if (mIsPaused) { // 暂停了
                        return TYPE_PAUSED;
                    } else {
                        total += len;
                        savedFile.write(b, 0, len);
                        // 计算已经下载的 百分比
                        int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                // 下载完成
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
                if (mIsCanceled && file != null) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        Integer progress = values[0];
        if (progress > lastProgress) {
            mDownLoadListener.onProgress(progress);
            lastProgress = progress;
        }
    }


    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer) {
            case TYPE_SUCCESS:
                mDownLoadListener.onSuccess();
                break;
            case TYPE_FAILED:
                mDownLoadListener.onFailed();
                break;
            case TYPE_PAUSED:
                mDownLoadListener.onPaused();
                break;
            case TYPE_CANCELED:
                mDownLoadListener.onCanceled();
                break;
        }
    }

    // 获取文件总长度
    private long getContentLength(String downloadUrl) throws IOException {
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }

    public void pauseDownload() {
        mIsPaused = true;
    }

    public void cancelDownLoad() {
        mIsCanceled = true;
    }
}
