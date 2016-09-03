package com.feridgoranatanas.projectpinkifinki;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class ViewRunActivity extends FragmentActivity implements OnMapReadyCallback {

    Run run;
    SupportMapFragment supportMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_run);

        Bundle bundle = getIntent().getExtras();
        run = (Run)bundle.getParcelable("run");

        TextView tvStats = (TextView)findViewById(R.id.tvStats);
        tvStats.setText(String.format("On %s you ran %.2fkm in %s!",
                run.getDate(), run.getDistance(), getCurrentTimeString(run.getSeconds())));

        supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        Button button = (Button)findViewById(R.id.bBackToAllRuns);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent allRuns = new Intent(ViewRunActivity.this, AllRunsActivity.class);
                startActivity(allRuns);
            }
        });

        button = (Button)findViewById(R.id.bHome);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(ViewRunActivity.this, MainActivity.class);
                startActivity(home);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        List<LatLng> coordsList = run.getCoords();
        drawLine(coordsList, googleMap);
        LatLngBounds latLngBounds = new LatLngBounds(coordsList.get(0), coordsList.get(coordsList.size() - 1));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter(), 19));
    }

    public void drawLine(List<LatLng> coords, GoogleMap map) {
        map.addMarker(new MarkerOptions().position(coords.get(0)).title("Start"));
        Polyline line = map.addPolyline(new PolylineOptions()
                .addAll(coords)
                .width(5)
                .color(Color.parseColor("#262626")));
        map.addMarker(new MarkerOptions().position(coords.get(coords.size() - 1)).title("End"));
    }

    private String getCurrentTimeString(int currentTime) {
        int hours = currentTime / 3600;
        int minutes = (currentTime % 3600) / 60;
        int seconds = currentTime % 60;

        StringBuilder stringBuilder = new StringBuilder()
                .append((hours != 0) ? hours : "")
                .append((hours != 0) ? "h:" : "")
                .append((minutes != 0) ? minutes : "")
                .append((minutes != 0) ? "m: " : "")
                .append(seconds)
                .append("s");

        return stringBuilder.toString();
    }
}
