package github.guawazi.notificationdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mBtnNotification;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNotificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        mBtnNotification = (Button) findViewById(R.id.btn_notification);
        mBtnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification notification = new NotificationCompat.Builder(MainActivity.this)
                        .setContentTitle("我是标题")
                        .setContentText("我是内容")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                        .setContentIntent(PendingIntent.getActivity(MainActivity.this,
                                0,
                                new Intent(MainActivity.this, SecondActivity.class),
                                0))
                        .setPriority(Notification.PRIORITY_MAX)
                        .setAutoCancel(true)
                        .build();
                mNotificationManager.notify(10, notification);
            }
        });
    }
}
