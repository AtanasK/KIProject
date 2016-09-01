package com.feridgoranatanas.projectpinkifinki;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class RunStatsActivity extends FragmentActivity implements OnMapReadyCallback {
    private ArrayList<LatLng> coordsList;
    private int secondsRan;
    private SupportMapFragment supportMapFragment;
    private double totalDistance;
    private Date date;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_stats);

        Bundle bundle = getIntent().getExtras();
        coordsList = (ArrayList<LatLng>)bundle.get("coords");
        secondsRan = bundle.getInt("time");
        totalDistance = bundle.getDouble("distance");
        Calendar c = Calendar.getInstance();
        date = c.getTime();
        username = getSharedPreferences("username", Context.MODE_PRIVATE).getString("username", "");

        TextView testView = (TextView)findViewById(R.id.textStats);
        testView.setText(String.format("Time: %d, Distance: %.2f \n Last Coord: %.2f, %.2f \n Date: %s"
                ,secondsRan, totalDistance, coordsList.get(0).latitude, coordsList.get(0).longitude, date.toString()));

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

        supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        saveRun();
    }

    public void drawLine(ArrayList<LatLng> coords, GoogleMap map) {
        map.addMarker(new MarkerOptions().position(coords.get(0)).title("Start"));
        Polyline line = map.addPolyline(new PolylineOptions()
                .addAll(coords)
                .width(5)
                .color(Color.parseColor("#262626")));
        map.addMarker(new MarkerOptions().position(coords.get(coords.size() - 1)).title("End"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        drawLine(coordsList, googleMap);
        LatLngBounds latLngBounds = new LatLngBounds(coordsList.get(0), coordsList.get(coordsList.size() - 1));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter(), 19));
    }

    private void saveRun() {
        RequestParams params = new RequestParams();
        params.put("coords", coordsList);
        params.put("date", date);
        params.put("distance", totalDistance);
        params.put("time", secondsRan);
        params.put("username", username);
        ServiceClient.post("add.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast toast = Toast.makeText(RunStatsActivity.this, R.string.update_failure, Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast toast = Toast.makeText(RunStatsActivity.this, R.string.update_failure, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}
