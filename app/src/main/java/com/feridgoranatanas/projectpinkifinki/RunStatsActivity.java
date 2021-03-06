package com.feridgoranatanas.projectpinkifinki;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class RunStatsActivity extends FragmentActivity implements OnMapReadyCallback {
    private ArrayList<LatLng> coordsList;
    private int secondsRan;
    private SupportMapFragment supportMapFragment;
    private double totalDistance;
    private Date date;
    private String username;
    private DateFormat dateFormat;
    private Handler handler;
    private boolean first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_stats);

        Bundle bundle = getIntent().getExtras();
        coordsList = (ArrayList<LatLng>)bundle.get("coords");
        secondsRan = bundle.getInt("time");
        totalDistance = bundle.getDouble("distance");
        Calendar c = Calendar.getInstance();
        dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.ENGLISH);
        date = c.getTime();
        username = getSharedPreferences("username", Context.MODE_PRIVATE).getString("username", "");

        TextView testView = (TextView)findViewById(R.id.textStats);
        testView.setText(String.format("Time: %s, Distance: %.2f \n Last Coord: %.2f, %.2f \n Date: %s"
                ,getCurrentTimeString(secondsRan), totalDistance, (coordsList.size() != 0)?coordsList.get(0).latitude:0.0, (coordsList.size() != 0)?coordsList.get(0).longitude:0.0, dateFormat.format(date)));

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
                String text = String.format("I just ran %.2f km in %s! Beat me if you can!", totalDistance, getCurrentTimeString(secondsRan));
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(share, "Where do  you want to share your acomplishment?"));
            }
        });

        if (savedInstanceState == null) {
            first = true;
            handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!isNetworkAvailable()) {
                        if (first) {
                            Toast.makeText(RunStatsActivity.this, "Run will be archived as soon as Internet connection is available!", Toast.LENGTH_LONG).show();
                            first = false;
                        }
                        handler.postDelayed(this, 1000);
                    }
                    else {
                        supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
                        supportMapFragment.getMapAsync(RunStatsActivity.this);
                        saveRun();
                    }
                }
            });
        }
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
        if (coordsList.isEmpty())
            return;
        drawLine(coordsList, googleMap);
        LatLngBounds latLngBounds = new LatLngBounds(coordsList.get(0), coordsList.get(coordsList.size() - 1));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter(), 19));
    }

    private void saveRun() {
        RequestParams params = new RequestParams();
        JSONArray j = new JSONArray();
        try {
        for(LatLng coord: coordsList) {
                JSONObject coordJSON = new JSONObject();
                coordJSON.put("lat", coord.latitude);
                coordJSON.put("lng", coord.longitude);
                j.put(coordJSON);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("coords", j.toString());
        params.put("date", dateFormat.format(date));
        params.put("distance", totalDistance);
        params.put("time", secondsRan);
        params.put("username", username);
        ServiceClient.post("add.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast toast = Toast.makeText(RunStatsActivity.this, R.string.update_success, Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast toast = Toast.makeText(RunStatsActivity.this, R.string.update_failure, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
