package com.feridgoranatanas.projectpinkifinki;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class RunActivity extends AppCompatActivity {

    private Intent service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        GIFView gifView = (GIFView)findViewById(R.id.runGif);
        gifView.loadGIFResource(this, R.raw.logo_animation);

        service = new Intent(this, RunService.class);
        startService(service);

        Button button = (Button)findViewById(R.id.buttonDoneRunning);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(service);
            }
        });
    }
}
