package valenciaprogrammers.com.waterapp;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MarineMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private static final String TAG = "MarineMapsActivity";

    String URLGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Adds focal point to map so that Florida is centered on the screen
//        LatLng focalPoint = new LatLng(28.468357, -83.331241);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((focalPoint), 5.8f));

        zoomInOnCoord(StartActivity.lat, StartActivity.lon);

        //this code opens the xml file for parsing by the appropriate methods
        //then saves the output into an array. Then the coordinates are converted
        //to doubles, then LatLng. The station name is edited to remove extra text,
        //and the flow is presented with its unit of measurement.
        //These are used to generate markers on the map as the method loops through
        //the arrays.
        //Note: I need to add the date to the markers.

        try {
            InputStream inputStream = getResources().getAssets().open("tidalstations.csv");

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String csvLine;

            while ((csvLine = reader.readLine()) != null) {
                String[] data = csvLine.split(",");
                String stationName = data[0];
                String id = data[1];
                Double lat = Double.parseDouble(data[2]);
                Double longi = Double.parseDouble(data[3]);

                LatLng lng = new LatLng(lat, longi);

                Marker destination = googleMap.addMarker(new MarkerOptions()
                        .position(lng)
                        .title(stationName)
                        .snippet("Site = " + id + " (Click for tidal data)")
                );

                destination.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.liquid));

            }
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    String fullSnippet = marker.getSnippet();
                    String[] splitSnippet = fullSnippet.split(" ");
                    String snippet = splitSnippet[2];

                    Log.d("onInfoWindowClick: ", snippet);
                    String coordinates = marker.getPosition().toString();

                    Intent intent = new Intent(MarineMapsActivity.this, DateRangeActivity.class);
                    intent.putExtra("key", snippet);
                    startActivity(intent);
                }
            });

        } catch (Exception e) {

        }

        try {
            InputStream inputStream = getResources().getAssets().open("wavestations.csv");

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String csvLine;

            while ((csvLine = reader.readLine()) != null) {
                String[] data = csvLine.split(",");
                String stationName = data[1];
                String id = data[0];
                Double lat = Double.parseDouble(data[2]);
                Double longi = Double.parseDouble(data[3]);

                LatLng lng = new LatLng(lat, longi);

                Marker destination = googleMap.addMarker(new MarkerOptions()
                        .position(lng)
                        .title(stationName)
                        .snippet("Click this window for wave data")
                );

            }

        } catch (Exception e) {

        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    public void zoomInOnCoord(double lat, double lon) {
        LatLng city = new LatLng(lat, lon);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(city)
                .zoom(10)
                .bearing(0)
                .tilt(90)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 3500, null);
    }
}