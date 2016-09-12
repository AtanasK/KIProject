package com.feridgoranatanas.projectpinkifinki;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RunService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final int COORD_UPDATE_INTERVAL = 10000;
    private final int NOTIFICATION_ID = 1;

    private Handler notificationTimer;
    private Handler coordinatesTimer;
    private Runnable notificationRunnable;
    private Runnable coordinatesRunnable;
    private ArrayList<LatLng> coords;
    private int currentTime;
    private NotificationManager notificationManager;
    private GoogleApiClient mGoogleApiClient;
    private double totalDistance;
    private Location previousLocation;
    private Intent intent;
    private PendingIntent pendingIntent;
    private Notification.Builder builder;

    public RunService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        totalDistance = 0;
        previousLocation = null;

        notificationTimer = new Handler();
        notificationRunnable = new Runnable() {
            @Override
            public void run() {
                updateNotification();
                if (isNetworkAvailable() && isGPSEnabled()) {
                    currentTime++;
                }
                notificationTimer.postDelayed(this, 1000);
            }
        };

        coordinatesTimer = new Handler();
        coordinatesRunnable = new Runnable() {
            @Override
            public void run() {
                if (isNetworkAvailable() && isGPSEnabled()) {
                    updateCoordinatesList();
                }
                coordinatesTimer.postDelayed(this, COORD_UPDATE_INTERVAL);
            }
        };

        coords = new ArrayList<>();

        currentTime = 0;

        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        intent = new Intent(this, RunActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        startForeground(NOTIFICATION_ID, getNotification(builder));

        mGoogleApiClient.connect();
        notificationTimer.postDelayed(notificationRunnable, 0);
        coordinatesTimer.postDelayed(coordinatesRunnable, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private void updateNotification() {
        notificationManager.notify(NOTIFICATION_ID, getNotification(builder));
    }

    //Polni lista so koordinati i presmetuva rastojanie
    private void updateCoordinatesList() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            LatLng currentPoint = new LatLng(location.getLatitude(), location.getLongitude());
            coords.add(currentPoint);
        }
        if (previousLocation == null) {
            previousLocation = location;
        }
        else {
            totalDistance += previousLocation.distanceTo(location);
            previousLocation = location;
        }
    }

    //Pretvara sekundi vo chitlivo vreme
    private String getCurrentTimeString() {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationTimer.removeCallbacks(notificationRunnable);
        coordinatesTimer.removeCallbacks(coordinatesRunnable);
        if (currentTime % COORD_UPDATE_INTERVAL != 0)
            updateCoordinatesList();
        notificationManager.cancel(NOTIFICATION_ID);
        mGoogleApiClient.disconnect();
        Intent intent = new Intent(this, RunStatsActivity.class);
        intent.putExtra("coords", coords);
        intent.putExtra("time", currentTime);
        intent.putExtra("distance", totalDistance);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //Ovde kje se menuva ikona za notifikacijata
    private Notification getNotification(Notification.Builder builder) {
        builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.not_bar_logo)
                .setContentTitle((isNetworkAvailable() && isGPSEnabled())?"Running...":"Error")
                .setContentText(getCurrentTimeString())
                .setContentIntent(pendingIntent)
                .setOngoing(true);
        if (!(isNetworkAvailable() && isGPSEnabled()))
            builder.setStyle(new Notification.BigTextStyle().bigText("Check internet connection and gps availability"));
        return builder.build();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
