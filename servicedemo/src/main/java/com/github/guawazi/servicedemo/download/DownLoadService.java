package com.github.guawazi.servicedemo.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.github.guawazi.servicedemo.MainActivity;
import com.github.guawazi.servicedemo.R;

import java.io.File;

/**
 * 下载服务
 * 1. 监听下载过程中的状态，并做相应的处理
 * 2. 把 binder 提供出去，并且这个 binder 给外层提供了 开始，暂停，取消的操作,供外部使用
 */
public class DownLoadService extends Service {


    private DownLoadListener mDownLoadListener;
    private DownLoadTask mDownLoadTask;
    private String mUrl;

    @Override
    public void onCreate() {
        super.onCreate();

        mDownLoadListener = new DownLoadListener() {
            @Override
            public void onProgress(int progress) {
                getNotificationManager().notify(1, getNotification("downloading....", progress));
            }

            @Override
            public void onSuccess() {
                mDownLoadTask = null;
                // 下载成功时将后台服务通知关闭，并创建一个下载成功的通知
                stopForeground(true);
                getNotificationManager().notify(1, getNotification("download success", -1));
                Toast.makeText(DownLoadService.this, "download success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed() {
                mDownLoadTask = null;
                // 下载失败关闭服务，并创建一个下载失败的通知
                stopForeground(true);
                getNotificationManager().notify(1, getNotification("download failed", -1));
                Toast.makeText(DownLoadService.this, "download failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPaused() {
                mDownLoadTask = null;
                Toast.makeText(DownLoadService.this, "download pause", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCanceled() {
                mDownLoadTask = null;
                stopForeground(true);
                Toast.makeText(DownLoadService.this, "download cancel", Toast.LENGTH_SHORT).show();
            }
        };

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new DownloadBinder();
    }

    public class DownloadBinder extends Binder {
        public void startDownLoad(String url) {
            if (mDownLoadTask == null) {
                mUrl = url;
                mDownLoadTask = new DownLoadTask(mDownLoadListener);
                mDownLoadTask.execute(url);
                startForeground(1, getNotification("Downloading", 0));
                Toast.makeText(DownLoadService.this, "download start", Toast.LENGTH_SHORT).show();
            }
        }

        public void pauseDownload() {
            if (mDownLoadTask != null) {
                mDownLoadTask.pauseDownload();
            }
        }

        public void cancelDownload() {
            if (mDownLoadTask != null) {
                mDownLoadTask.cancelDownLoad();
            } else { // 删除文件，并且关闭通知
                if (mUrl != null) {
                    String fileName = mUrl.substring(mUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory, fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownLoadService.this, "download cancel", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }


    Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);
        if (progress > 0) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }
}
