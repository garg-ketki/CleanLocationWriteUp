package com.example.cleanlocationwriteup;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by ketkigarg on 20/01/18.
 */

public class LocationDataManager {
  private static final String TAG = LocationDataManager.class.getSimpleName();

  public static void bindLocationListenerIn(LocationViewModel model, LifecycleOwner lifecycleOwner,
                                            Context context) {
    new BoundedLocationListener(model, lifecycleOwner, context);
  }

  @SuppressLint("MissingPermission")
  static class BoundedLocationListener
      implements LifecycleObserver, ConnectionCallbacks, OnConnectionFailedListener,
      LocationListener {

    private LocationViewModel model;
    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public BoundedLocationListener(LocationViewModel model, LifecycleOwner lifecycleOwner,
                                   Context context) {
      this.model = model;
      this.context = context;
      lifecycleOwner.getLifecycle().addObserver(this);
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void registerLocationService() {
      mGoogleApiClient = new GoogleApiClient.Builder(context)
          .addConnectionCallbacks(this)
          .addOnConnectionFailedListener(this)
          .addApi(LocationServices.API).build();

      mLocationRequest = new LocationRequest();
      mLocationRequest.setInterval(1000);
      mLocationRequest.setFastestInterval(500);
      mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void connectLocationService() {
      if (mGoogleApiClient != null) {
        mGoogleApiClient.connect();
      }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void disconnectLocationService() {
      if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
        LocationServices.FusedLocationApi
            .removeLocationUpdates(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
      }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroyServiceObject() {
      mGoogleApiClient = null;
    }

    private void postLocationData(Location location) {
      model.getLocation().postValue(location);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
      Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
      if (mLastLocation != null) {
        Log.v(TAG, "mLastLocation: " + mLastLocation.getLatitude() + " " +
            mLastLocation.getLongitude());
        onLocationChanged(mLastLocation);
      }
      registerForLocationUpdate();
    }


    private void registerForLocationUpdate() {
      LocationServices.FusedLocationApi.requestLocationUpdates(
          mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
      connectLocationService();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
      Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " +
          connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
      Log.v(TAG,
          "onLocationChanged: " + location.getLatitude() + " " + location.getLongitude());
      postLocationData(location);
    }
  }
}
