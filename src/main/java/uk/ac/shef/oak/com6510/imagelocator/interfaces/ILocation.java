package uk.ac.shef.oak.com6510.imagelocator.interfaces;

import android.location.Location;

/*
Location Interface to be implemented on Location update
 */
public interface ILocation {
    void onLocationUpdate(Location location);
}
