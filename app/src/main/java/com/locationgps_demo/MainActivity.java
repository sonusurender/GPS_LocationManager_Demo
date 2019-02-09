package com.locationgps_demo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnClickListener {
	private static Button getLocation, getLocation_usingInternet;
	private static TextView displayLocation, displayInternetLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		setListeners();

	}

	// Initialize the views
	private void init() {
		getLocation = (Button) findViewById(R.id.get_location_without_internet);
		getLocation_usingInternet = (Button) findViewById(R.id.get_location_using_internet);
		displayLocation = (TextView) findViewById(R.id.show_location_without_internet);
		displayInternetLocation = (TextView) findViewById(R.id.show_location_using_internet);
	}

	// Set listeners over both button
	private void setListeners() {
		getLocation.setOnClickListener(this);
		getLocation_usingInternet.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.get_location_using_internet:
			getLocationUsingInternet();
			break;

		case R.id.get_location_without_internet:
			getLocationWithoutInternet();
			break;
		}

	}

	// Method that will return location in longitude, latitude, city, state, and
	// country
	private void getLocationUsingInternet() {

		boolean isInternetConnected = new ConnectionDetector(MainActivity.this)
				.isConnectingToInternet();

		// Before proceding we have to check if therr is internet connection or
		// not
		if (isInternetConnected) {
			getLocation_usingInternet.setText("Please wait...");// while getting
																// location
																// please wait
																// and disable
																// the button
			getLocation_usingInternet.setEnabled(false);
			new GPSLocation(MainActivity.this).turnGPSOn();// First turn on GPS
			String getLocation = new GPSLocation(MainActivity.this)
					.getMyCurrentLocation();// Get current location from
											// Location class
			displayInternetLocation.setText(getLocation);// Set location over
															// textview

			// Now again change the state of button
			getLocation_usingInternet.setText(R.string.location_internet);
			getLocation_usingInternet.setEnabled(true);
		} else {

			// If there is no internet connection toast will be displayed
			Toast.makeText(MainActivity.this,
					"There is no internet connection.", Toast.LENGTH_SHORT)
					.show();
		}

	}

	// Method that will fetch the location in longitude and latitude in absence
	// of internet
	private void getLocationWithoutInternet() {

		// Change the state of button
		getLocation.setText("Please wait..");
		getLocation.setEnabled(false);

		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onLocationChanged(Location location) {

				// Display currnet longitude and latitude over textview
				displayLocation.setText("Latitude : " + location.getLatitude()
						+ "\nLongitude : " + location.getLongitude());

				// The toast will show loaction continuosly as we are requesting
				// local updates
				Toast.makeText(
						getApplicationContext(),
						location.getLatitude() + "     "
								+ location.getLongitude(), Toast.LENGTH_SHORT)
						.show();

				// Now, again change the state of button
				getLocation.setText(R.string.location_without_internet);
				getLocation.setEnabled(true);

			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
		    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

		if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
		    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		// When activity destroyed we have to turn off GPS
		new GPSLocation(MainActivity.this).turnGPSOff();
	}

}
