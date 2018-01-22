package com.github.guawazi.servicedemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.guawazi.servicedemo.download.DownLoadService;

public class MainActivity extends AppCompatActivity {

    private Button mBtnStartSimple;
    private Button mBtnStopSimple;
    private Button mBtnBindService;
    private Button mBtnUnbindService;
    private Button mBtnStartDownload;
    private Button mBtnPauseDownload;
    private Button mBtnCancelDownload;
    private Intent mIntent;
    private ServiceConnection mServiceConnection;
    private BindService.DownLoadBinder mBinder;
    private Intent mBindIntent;
    private Intent mDownloadIntent;
    private ServiceConnection mDownloadServiceConnection;
    private DownLoadService.DownloadBinder mDownloadBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnStartSimple = (Button) findViewById(R.id.btn_start_simple);
        mBtnStopSimple = (Button) findViewById(R.id.btn_stop_simple);
        mBtnBindService = (Button) findViewById(R.id.btn_bind_service);
        mBtnUnbindService = (Button) findViewById(R.id.btn_unbind_service);
        mBtnStartDownload = (Button) findViewById(R.id.btn_start_download);
        mBtnPauseDownload = (Button) findViewById(R.id.btn_pause_download);
        mBtnCancelDownload = (Button) findViewById(R.id.btn_cancel_download);

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBinder = ((BindService.DownLoadBinder) service);
                mBinder.startDownLoad(MainActivity.this);
                mBinder.getProgress(MainActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mBinder.endDownLoad(MainActivity.this);
            }
        };


        mDownloadServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mDownloadBinder = ((DownLoadService.DownloadBinder) service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        mIntent = new Intent(MainActivity.this, SimpleService.class);
        mBindIntent = new Intent(MainActivity.this, BindService.class);
        mDownloadIntent = new Intent(MainActivity.this, DownLoadService.class);

        MainActivity.this.startService(mDownloadIntent);
        MainActivity.this.bindService(mDownloadIntent, mDownloadServiceConnection, 0);


        mBtnStartSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startService(mIntent);
            }
        });
        mBtnStopSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.stopService(mIntent);
            }
        });

        mBtnBindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.bindService(mBindIntent, mServiceConnection, BIND_AUTO_CREATE);
            }
        });

        mBtnUnbindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 不可重复解绑服务
                MainActivity.this.unbindService(mServiceConnection);
            }
        });


        // 开始下载
        mBtnStartDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDownloadBinder != null) {
                    mDownloadBinder.startDownLoad("http://trinea.cn/app/dev-tools.apk");
                }
            }
        });

        // 暂停下载
        mBtnPauseDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDownloadBinder != null) {
                    mDownloadBinder.pauseDownload();
                }
            }
        });
        // 取消下载
        mBtnCancelDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDownloadBinder != null) {
                    mDownloadBinder.cancelDownload();
                }
            }
        });
    }
}
