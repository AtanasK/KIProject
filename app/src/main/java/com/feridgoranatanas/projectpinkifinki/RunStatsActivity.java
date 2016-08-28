package com.feridgoranatanas.projectpinkifinki;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RunStatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_stats);

        Bundle bundle = getIntent().getExtras();
        TextView testView = (TextView)findViewById(R.id.textStats);
        testView.setText(String.format("%d", bundle.getInt("time")));

        Button button = (Button)findViewById(R.id.btnAllRuns);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent allRuns = new Intent(RunStatsActivity.this, AllRunsActivity.class);
                startActivity(allRuns);
            }
        });

        button = (Button)findViewById(R.id.btnGetB);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(RunStatsActivity.this, MainActivity.class);
                startActivity(main);
            }
        });

        button = (Button)findViewById(R.id.btnShare);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tuka kje bide sharingot
            }
        });
    }
}
