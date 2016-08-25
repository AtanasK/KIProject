package com.feridgoranatanas.projectpinkifinki;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class RunStatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_stats);

        Bundle bundle = getIntent().getExtras();
        TextView testView = (TextView)findViewById(R.id.textTime);
        testView.setText(String.format("%d", bundle.getInt("time")));
    }
}
