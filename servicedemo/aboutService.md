# Service

## 特点
1. 在后台运行，不依赖界面，是为了完成某个任务
2. 运行在当前进程
3. 不会自动开启线程
4. 完成任务后不会自己关闭

## 基本使用
### 直接启动

```java
mIntent = new Intent(MainActivity.this, SimpleService.class);
MainActivity.this.startService(mIntent);
MainActivity.this.stopService(mIntent);
```

1. 第一次 startService 会调用 onCreate() -> onStartCommand(),
多次调用 startService 只会走 onStartCommand()
2. 多次调用 stopService() 只会调用一次
3. 停止服务 外部调用 stopService(), 内部调用 stopSelf()

### Bind方式

```java
MainActivity.this.bindService(mBindIntent, mServiceConnection, BIND_AUTO_CREATE);
MainActivity.this.unbindService(mServiceConnection);

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

```

> 其实就是在外部调用 binder 里面的方法
1. bind 多次绑定，没用
2. 多次解绑会抛异常

## 生命周期
![](https://developer.android.com/images/service_lifecycle.png)

1. 经常会两种方式混合启动服务，必须当两个停止条件同时满足时，服务才会停止



## 服务高级

### 前台服务
```java
// 其实就是在 service 里面，开启一个 Notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("后台服务标题")
                .setContentText("后台服务内容")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .build();

        // 开启后台服务
        startForeground(1, notification);
```

> 注意：在程序退出后，后台服务还是在运行，所以那个通知还是会一直存在。所以要手动调用 stop 方法

### IntentService
- 现在的服务在使用中存在两种问题
1. 服务默认不会开启线程，coder容易忘记，造成 ANR
2. 服务在完成任务后，不会自动关闭，浪费资源

### 断点下载文件
1. 异步任务
2. 断点下载
3. 读写文件
4.
