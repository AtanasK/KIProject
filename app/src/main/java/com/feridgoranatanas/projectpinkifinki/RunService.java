package com.feridgoranatanas.projectpinkifinki;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RunService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final int COORD_UPDATE_INTERVAL = 10;
    private final int NOTIFICATION_ID = 1;

    private boolean shouldFinish;
    private Handler notificationTimer;
    private Handler coordinatesTimer;
    private Runnable notificationRunnable;
    private Runnable coordinatesRunnable;
    private ArrayList<LatLng> coords;
    private int currentTime;
    private NotificationManager notificationManager;
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;

    public RunService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        shouldFinish = false;

        notificationTimer = new Handler();
        notificationRunnable = new Runnable() {
            @Override
            public void run() {
                updateNotification();
                currentTime++;
                notificationTimer.postDelayed(this, 1000);
            }
        };

        coordinatesTimer = new Handler();
        coordinatesRunnable = new Runnable() {
            @Override
            public void run() {
                updateCoordinatesList();
                coordinatesTimer.postDelayed(this, COORD_UPDATE_INTERVAL);
            }
        };

        coords = new ArrayList<>();

        currentTime = 0;

        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(new Criteria(), true);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        notificationTimer.postDelayed(notificationRunnable, 0);
        coordinatesTimer.postDelayed(coordinatesRunnable, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private void updateNotification() {

        Intent intent = new Intent(this, RunActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Ovde kje se menuva ikona za notifikacijata
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.logo_image)
                .setContentTitle("Running...")
                .setContentText(getCurrentTimeString())
                .setContentIntent(pendingIntent)
                .setOngoing(true);
        mGoogleApiClient.connect();

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    //Polni lista so koordinati
    private void updateCoordinatesList() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null)
            coords.add(new LatLng(location.getLatitude(), location.getLongitude()));
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
}
