package org.example.team_pigeon.movie_pigeon.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SHENGX on 2017/3/4.
 */

public class Cinema implements Serializable{
    @Expose
    @SerializedName("cinema_id")
    private String id;
    @Expose
    @SerializedName("cinema_name")
    private String name;
    @Expose
    private String provider;
    @Expose
    @SerializedName("location_x")
    private String latitude;
    @Expose
    @SerializedName("location_y")
    private String longitude;

    private int distance = 0;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance (int distance) {
        this.distance = distance;
    }
}
