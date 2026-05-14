package com.example.moviesapp_nabih.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.example.moviesapp_nabih.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        mMap.setMyLocationEnabled(true);
        getLocationAndShowCinemas();
    }

    private void getLocationAndShowCinemas() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                showCinemasNearby(location);
            } else {
                // Position par défaut — Casablanca
                LatLng casablanca = new LatLng(33.5731, -7.5898);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(casablanca, 13));
                addCinemaMarkers(casablanca);
            }
        });
    }

    private void showCinemasNearby(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        // Centrer la carte sur l'utilisateur
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));

        // Marqueur utilisateur
        mMap.addMarker(new MarkerOptions()
                .position(userLocation)
                .title("Vous êtes ici 📍")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        addCinemaMarkers(userLocation);
    }

    private void addCinemaMarkers(LatLng center) {
        // Cinémas fictifs autour de la position
        double lat = center.latitude;
        double lng = center.longitude;

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat + 0.01, lng + 0.01))
                .title("🎬 Cinéma Megarama")
                .snippet("Ouvert 10h-23h"));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat - 0.01, lng + 0.02))
                .title("🎬 Cinéma Pathé")
                .snippet("Ouvert 11h-00h"));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat + 0.02, lng - 0.01))
                .title("🎬 Cinéma ABC")
                .snippet("Ouvert 10h-22h"));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat - 0.02, lng - 0.02))
                .title("🎬 Cinéma Colisée")
                .snippet("Ouvert 12h-23h"));

        Toast.makeText(this, "🎬 4 cinémas trouvés près de vous !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
            }
        }
    }
}