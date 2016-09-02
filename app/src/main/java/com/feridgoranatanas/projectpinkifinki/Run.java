package com.feridgoranatanas.projectpinkifinki;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ferid on 02-Sep-16.
 */
public class Run implements Parcelable {

    private List<LatLng> coords;
    private int seconds;
    private double distance;
    private String date;

    public Run() {
        coords = new ArrayList<>();
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }

    public void addCoord(LatLng coord) {
        coords.add(coord);
    }

    public List<LatLng> getCoords() {
        return coords;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(seconds);
        dest.writeDouble(distance);
        dest.writeString(date);
        dest.writeInt(coords.size());
        for(LatLng coord: coords) {
            dest.writeDouble(coord.latitude);
            dest.writeDouble(coord.longitude);
        }
    }

    public static final Parcelable.Creator<Run> CREATOR = new Parcelable.Creator<Run>() {
        public Run createFromParcel(Parcel in) {
            return new Run(in);
        }

        public Run[] newArray(int size) {
            return new Run[size];
        }
    };

    private Run(Parcel in) {
        seconds = in.readInt();
        distance = in.readDouble();
        date = in.readString();
        coords = new ArrayList<>();
        int elems = in.readInt();
        for(int i = 0; i < elems; ++i) {
            double lat = in.readDouble();
            double lng = in.readDouble();
            coords.add(new LatLng(lat, lng));
        }
    }
}
