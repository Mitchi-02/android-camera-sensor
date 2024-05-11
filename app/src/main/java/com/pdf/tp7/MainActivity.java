package com.pdf.tp7;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pdf.tp7.services.LightService;

public class MainActivity extends AppCompatActivity {

    protected void start() {
        Intent intent = new Intent(this, LightService.class);
        intent.setAction("startService");
        startForegroundService(intent);
    }

    protected void stop() {
        Intent intent = new Intent(this, LightService.class);
        intent.setAction("stopService");
        startForegroundService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.startBtn).setOnClickListener(v -> start());
        findViewById(R.id.stopBtn).setOnClickListener(v -> stop());
    }
}
