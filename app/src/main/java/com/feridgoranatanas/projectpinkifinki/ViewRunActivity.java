package com.feridgoranatanas.projectpinkifinki;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewRunActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_run);

        Bundle bundle = getIntent().getExtras();
        Run run = (Run)bundle.getParcelable("run");

        TextView tvTest = (TextView)findViewById(R.id.tvTest);
        tvTest.setText(String.format("Date: %s, Distance: %.2f, Time: %s, CoordLat: %.2f, CoordLong: %.2f",
                run.getDate(), run.getDistance(), run.getSeconds(), run.getCoords().get(0).latitude, run.getCoords().get(0).longitude));
    }
}
