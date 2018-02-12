package com.example.cleanlocationwriteup.location;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.location.Location;

/**
 * Created by ketkigarg on 20/01/18.
 */

public class LocationViewModel extends ViewModel {
  private MutableLiveData<Location> locationLiveData;

  public LocationViewModel() {
    locationLiveData = new MutableLiveData<>();
  }

  public MutableLiveData<Location> getLocation() {
    return locationLiveData;
  }
}
