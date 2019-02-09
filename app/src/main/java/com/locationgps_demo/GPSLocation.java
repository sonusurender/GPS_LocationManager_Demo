package com.locationgps_demo;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class GPSLocation {
	private Context context;
	private StringBuilder currentLocation = new StringBuilder();

	public GPSLocation(Context context) {
		this.context = context;
	}

	/**
	 * Check the type of GPS Provider available at that instance and collect the
	 * location informations
	 * 
	 * @Output Latitude and Longitude
	 */

	public void turnGPSOn() {
		try {

			@SuppressWarnings("deprecation")
			String provider = Settings.Secure.getString(
					context.getContentResolver(),
					Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

			if (!provider.contains("gps")) { // if gps is disabled
				final Intent poke = new Intent();
				poke.setClassName("com.android.settings",
						"com.android.settings.widget.SettingsAppWidgetProvider");
				poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
				poke.setData(Uri.parse("3"));
				context.sendBroadcast(poke);
			}
		} catch (Exception e) {

		}
	}

	// Method to turn off the GPS
	@SuppressWarnings("deprecation")
	public void turnGPSOff() {
		String provider = Settings.Secure.getString(
				context.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

		if (provider.contains("gps")) { // if gps is enabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			context.sendBroadcast(poke);
		}
	}

	public String getMyCurrentLocation() {

		LocationManager locManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		LocationListener locListener = new MyLocationListener();

		try {
			gps_enabled = locManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			network_enabled = locManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		// don't start listeners if no provider is enabled
		// if(!gps_enabled && !network_enabled)
		// return false;

		if (gps_enabled) {
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
					0, locListener);

		}

		if (gps_enabled) {
			location = locManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		}

		if (network_enabled && location == null) {
			locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					0, 0, locListener);

		}

		if (network_enabled && location == null) {
			location = locManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		}

		if (location != null) {

			MyLat = location.getLatitude();
			MyLong = location.getLongitude();
			currentLocation.append("Latitude : " + MyLat + "\nLongitude : "
					+ MyLong);

		} else {
			Location loc = getLastKnownLocation(context);
			if (loc != null) {

				MyLat = loc.getLatitude();
				MyLong = loc.getLongitude();

			}
		}
		locManager.removeUpdates(locListener); // removes the periodic updates
												// from location listener to
												// //avoid battery drainage. If
												// you want to get location at
												// the periodic intervals call
												// this method using //pending
												// intent.

		try {
			// Getting address from found locations.
			Geocoder geocoder;

			List<Address> addresses;
			geocoder = new Geocoder(context, Locale.getDefault());
			addresses = geocoder.getFromLocation(MyLat, MyLong, 1);

			StateName = addresses.get(0).getAdminArea();
			CityName = addresses.get(0).getLocality();
			CountryName = addresses.get(0).getCountryName();

			currentLocation.append("\nState Name : " + StateName
					+ "\nCity Name : " + CityName + "\nCountry Name : "
					+ CountryName);
			// you can get more details other than this . like country code,
			// state code, etc.

		} catch (Exception e) {
			e.printStackTrace();
		}

		return currentLocation.toString();
	}

	// Location listener class. to get location.
	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			if (location != null) {
			}
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	}

	private boolean gps_enabled = false;
	private boolean network_enabled = false;
	Location location;

	Double MyLat, MyLong;
	String CityName = "", StateName = "", CountryName = "";

	// below method to get the last remembered location. because we don't get
	// locations all the times .At some instances we are unable to get the
	// location from GPS. so at that moment it will show us the last stored
	// location.

	public static Location getLastKnownLocation(Context context) {
		Location location = null;
		LocationManager locationmanager = (LocationManager) context
				.getSystemService("location");
		List<String> list = locationmanager.getAllProviders();
		boolean i = false;
		Iterator<?> iterator = list.iterator();
		do {

			if (!iterator.hasNext())
				break;
			String s = (String) iterator.next();

			if (i != false && !locationmanager.isProviderEnabled(s))
				continue;

			Location location1 = locationmanager.getLastKnownLocation(s);
			if (location1 == null)
				continue;
			if (location != null) {

				float f = location.getAccuracy();
				float f1 = location1.getAccuracy();
				if (f >= f1) {
					long l = location1.getTime();
					long l1 = location.getTime();
					if (l - l1 <= 600000L)
						continue;
				}
			}
			location = location1;

			i = locationmanager.isProviderEnabled(s);

		} while (true);
		return location;
	}

}
