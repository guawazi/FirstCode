package com.github.guawazi.servicedemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class BindService extends Service {
    public BindService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new DownLoadBinder();
    }


    public static class DownLoadBinder extends Binder{
        public void startDownLoad(Context context){
            Toast.makeText(context, DownLoadBinder.class.getSimpleName()+"startDownLoad", Toast.LENGTH_SHORT).show();
        }

        public int getProgress(Context context){
            Toast.makeText(context, DownLoadBinder.class.getSimpleName()+"getProgress", Toast.LENGTH_SHORT).show();
            return 50;
        }

        public void endDownLoad(Context context){
            Toast.makeText(context, DownLoadBinder.class.getSimpleName()+"endDownLoad", Toast.LENGTH_SHORT).show();
        }
    }
}
