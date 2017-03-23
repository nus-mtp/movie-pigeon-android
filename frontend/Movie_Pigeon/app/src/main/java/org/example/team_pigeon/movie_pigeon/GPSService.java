package org.example.team_pigeon.movie_pigeon;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Guo Mingxuan on 23/3/2017.
 */

public class GPSService extends Service {
    // location update intervals are 10s and 100m
    private static final int LOCATION_INTERVAL = 10000;
    private static final float LOCATION_DISTANCE = 100;
    private LocationManager mLocationManager;
    private String TAG = "GPSService";

    private class GPSListener implements LocationListener {
        Location lastLocation;

        public GPSListener(String provider) {
            Log.i(TAG, "Provider is " + provider);
            lastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged: " + location);
            lastLocation.set(location);
//            userInfoBulk.setUserLocation(lastLocation);
            Intent intent = new Intent("locationLoaded");
            Bundle bundle = new Bundle();
            bundle.putDouble("lat", lastLocation.getLatitude());
            bundle.putDouble("lon", lastLocation.getLongitude());
            intent.putExtras(bundle);
            sendBroadcast(intent);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }
    }

    GPSListener[] listeners = new GPSListener[]{
            new GPSListener(LocationManager.NETWORK_PROVIDER), new GPSListener(LocationManager.GPS_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        // init location manager
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        boolean isNetworkProviderWorking = true;
        // network provider
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, listeners[0]);
        } catch (java.lang.SecurityException e) {
            Log.e(TAG, "failed to request location update from network provider");
            isNetworkProviderWorking = false;
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "network provider does not exist, " + e.getMessage());
            isNetworkProviderWorking = false;
        }
        // gps provider only after trying for network provider
        // as gps provider is more power consuming
        if (!isNetworkProviderWorking) {
            try {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, listeners[1]);
            } catch (java.lang.SecurityException e) {
                Log.i(TAG, "failed to request location update from gps provider");
            } catch (IllegalArgumentException e) {
                Log.d(TAG, "gps provider does not exist " + e.getMessage());
            }
        }
    }
}
