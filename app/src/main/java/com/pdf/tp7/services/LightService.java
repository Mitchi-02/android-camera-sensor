package com.pdf.tp7.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.pdf.tp7.R;

public class LightService extends Service implements SensorEventListener {
    private boolean on = false;
    private CameraManager camManager;
    private SensorManager sensorManager;
    private Sensor sensor;
    private String camId;

    private static final int NOTIFICATION = 999999;

    protected void setLight(boolean value) {
        try {
            camManager.setTorchMode(camId, value);
            on = value;
        } catch (CameraAccessException ignored) {}
    }

    private Notification createNotification() {
        Intent stopIntent = new Intent(this, LightService.class);
        stopIntent.setAction("stopService");

        // Create a PendingIntent for the action button
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create the action button for stopping the service
        NotificationCompat.Action stopAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Stop Service",
                stopPendingIntent
        ).build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "light_channel")
                .setContentTitle("Light Service")
                .setContentText("Couvrer pour lancer / arreter la torche.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .addAction(stopAction);

        return builder.build();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel("light_channel", "light Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        try {
            camId = camManager.getCameraIdList()[0];
        } catch (CameraAccessException ignored) {}
        setLight(false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("startService")) {
                startForeground(NOTIFICATION, createNotification());
                setLight(true);
            } else if (intent.getAction().equals("stopService")) {
                stopForeground(true);
                setLight(false);
                stopSelf();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensor != null) {
            sensorManager.unregisterListener(this);
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() != Sensor.TYPE_PROXIMITY) return;
        if (event.values[0] < sensor.getMaximumRange()) {
            setLight(!on);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
