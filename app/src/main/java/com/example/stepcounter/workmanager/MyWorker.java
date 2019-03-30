package com.example.stepcounter.workmanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.example.stepcounter.R;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {


    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        showNotification();
        return null;
    }

    private void showNotification() {

        String title;
        String subTitle;

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_1", "channel_name", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "channel_1")
                .setContentTitle("Notification Title")
                .setContentText("Notification Description")
                .setSmallIcon(R.mipmap.ic_launcher_round);

        manager.notify(1, builder.build());

    }

}
