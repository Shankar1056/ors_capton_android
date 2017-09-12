package com.bigappcompany.ors_captain.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.bigappcompany.ors_captain.R;
import com.bigappcompany.ors_captain.model.ScheduleModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.bigappcompany.ors_captain.R.id.map;


@SuppressWarnings("ConstantConditions")
public class MapsActivity extends BaseActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {
	private static final int RC_PERM = 0;
	private GoogleMap mMap;
	private LatLng mSource, mDestination;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		
		// toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_24dp);
		
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
		    .findFragmentById(map);
		mapFragment.getMapAsync(this);
		
		ScheduleModel schedule = (ScheduleModel) getIntent().getSerializableExtra(EXTRA_DATA);
		mDestination = new LatLng(schedule.getLatitude(), schedule.getLongitude());
		findViewById(R.id.cv_google_map).setOnClickListener(this);
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		
		if (grantResults.length > 0 && (grantResults[0] == PERMISSION_GRANTED
		    || grantResults[1] == PERMISSION_GRANTED)) {
			locationUpdate();
		} else {
			Toast.makeText(this, R.string.grant_permission, Toast.LENGTH_SHORT).show();
		}
	}
	
	public void locationUpdate() {
		// check permission and request if not granted
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
		    != PERMISSION_GRANTED &&
		    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
			!= PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
			    new String[]{
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION
			    },
			    RC_PERM
			);
			return;
		}
		
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			// notify user
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage(getString(R.string.gps_network_not_enabled));
			dialog.setPositiveButton(getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(myIntent);
				}
			});
			dialog.setNegativeButton(getString(R.string.cancel), null);
			dialog.show();
		}
		
		// GPS location request
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, this);
		
		// Can we at least get network location?
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			// If cannot get GPS go for Network Provider's Location service
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 100, this);
		}
	}
	
	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		
		BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green_66dp);
		
		MarkerOptions markerOptions = new MarkerOptions()
		    .position(mDestination)
		    .title("Pickup location")
		    .icon(icon);
		
		mMap.addMarker(markerOptions);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDestination, 15.0f));
		locationUpdate();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		mSource = new LatLng(location.getLatitude(), location.getLongitude());
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
	
	@Override
	public void onClick(View v) {
		if (mSource != null) {
			String url = "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f";
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
			    Uri.parse(
				String.format(
				    Locale.UK, url,
				    mSource.latitude, mSource.longitude,
				    mDestination.latitude, mDestination.longitude
				)
			    )
			);
			startActivity(intent);
		} else {
			Toast.makeText(this, R.string.no_current_location, Toast.LENGTH_LONG).show();
			locationUpdate();
		}
	}
}
