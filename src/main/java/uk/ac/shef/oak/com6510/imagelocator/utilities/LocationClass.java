package uk.ac.shef.oak.com6510.imagelocator.utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;

import uk.ac.shef.oak.com6510.imagelocator.interfaces.ILocation;

/*
It implements LocationListener
Checks for the permission to access the location using ActivityCompat
checks if the gps is enabled through locationManager
checks if the network is available through locationManager
then requests for the single update
 */
public class LocationClass  implements LocationListener{
    private ILocation iLocation;
    private  LocationManager locationManager;
    public void getLocation(Context context,ILocation iLocation) {
        this.iLocation=iLocation;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnble=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnable=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (isGpsEnble)
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.myLooper());
        if (isNetworkEnable)
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.myLooper());
    }

    @Override
    public void onLocationChanged(Location location) {
        iLocation.onLocationUpdate(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    public void stopLocation() {
        locationManager.removeUpdates(this);
    }
}
