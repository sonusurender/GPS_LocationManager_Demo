package com.locationgps_demo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {

	private Context _context;

	public ConnectionDetector(Context context) {
		this._context = context;
	}

	// This method will return boolean value if internet is there or not
	public boolean isConnectingToInternet() {
		ConnectivityManager connectivityManager = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);// Connectivity
																// manager to
																// work on
																// connectivtiy
																// service

		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();// Get
																				// the
																				// network
																				// info

		if (networkInfo != null && networkInfo.isConnected()) {// Check if
																// network info
																// is not null
																// and it is
																// connected
			return true;// Internet is there
		} else {
			return false;// No internet
		}

	}
}
