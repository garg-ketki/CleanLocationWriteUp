package com.example.cleanlocationwriteup;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class LocationActivity extends AppCompatActivity {
  private LocationViewModel viewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location);

    viewModel = ViewModelProviders.of(this).get(LocationViewModel.class);

    LocationDataManager.bindLocationListenerIn(viewModel, this, getApplicationContext());

    viewModel.getLocation().observe(this, location -> {
      ((TextView) findViewById(R.id.tv_lat_text)).setText(location.getLatitude() + "");
      ((TextView) findViewById(R.id.tv_lng_text)).setText(location.getLongitude() + "");
    });
  }
}
