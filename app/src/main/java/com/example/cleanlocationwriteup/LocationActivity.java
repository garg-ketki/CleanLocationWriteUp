package com.example.cleanlocationwriteup;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.cleanlocationwriteup.location.LocationDataManager;
import com.example.cleanlocationwriteup.location.LocationViewModel;
import com.tbruyelle.rxpermissions.RxPermissions;

public class LocationActivity extends AppCompatActivity {
  private static final String TAG = LocationActivity.class.getSimpleName();
  private LocationViewModel viewModel;
  private RxPermissions rxPermissions;
  private Snackbar snackbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location);

    rxPermissions = new RxPermissions(this);
    viewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
    viewModel.getLocation().observe(this, location -> {
      ((TextView) findViewById(R.id.tv_lat_text)).setText(location.getLatitude() + "");
      ((TextView) findViewById(R.id.tv_lng_text)).setText(location.getLongitude() + "");
    });
  }

  @Override
  protected void onStart() {
    super.onStart();
    requestLocationPermission();
  }

  private void requestLocationPermission() {
    rxPermissions.requestEach(Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)
        .subscribe(permission -> { // will emit 2 Permission objects`permission.name` is granted !
          if (permission.granted) {
            dismissSnackBar();
            LocationDataManager.bindLocationListenerIn(viewModel, this, this);
          } else if (permission.shouldShowRequestPermissionRationale) {// Denied permission without ask never again
            showSnackBar("Please Grant Permissions", "ENABLE", v -> requestLocationPermission());
          } else { // Denied permission with ask never again
            showSnackBar("Enable Permissions from settings", "ENABLE", v -> {
              Intent intent = new Intent();
              intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
              intent.addCategory(Intent.CATEGORY_DEFAULT);
              intent.setData(Uri.parse("package:" + getPackageName()));
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
              intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
              startActivity(intent);
            });
          }
        }, throwable -> Log.e(TAG, "onError," + throwable.getMessage()));
  }

  private void showSnackBar(String text, String actionText, View.OnClickListener clickListener) {
    if (snackbar == null || !snackbar.isShown()) {
      snackbar = Snackbar.make(findViewById(android.R.id.content),
          text,
          Snackbar.LENGTH_INDEFINITE).setAction(actionText, clickListener);
      snackbar.show();
    }
  }

  private void dismissSnackBar() {
    if (snackbar != null && snackbar.isShown()) {
      snackbar.dismiss();
    }
  }
}
