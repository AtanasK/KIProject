package com.feridgoranatanas.projectpinkifinki;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity  extends FragmentActivity {

    private SupportMapFragment mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_maps_api);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if(mMap == null) {
            mMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            if(mMap != null) {
                setUpMap();
            }
        }
    }

//    Ovde kje se setiraat markerite na mapata treba samo brojkite kaj LatLng shto se da bidat nekoi promenlivi sho kje se chitaat i kje se vnesuvaat, ja neam blage veze kako bi go napraile toa
//            bash na ovoj chekor zaglavivme na nasa :/
    private void setUpMap() {
        mMap.getMap().addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Start"));
    }

}
