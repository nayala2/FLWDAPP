package valenciaprogrammers.com.waterapp;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
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

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * In the ChooseTypeActivity the user selects the type of data they wish to search
 * for: stream flow, water level, or tidal predication data. If either of
 * the freshwater data types are selected, then this activity generates a map
 * with station markers derived from parsing a USGS XML file for all freshwater continuous
 * monitoring stations.
 *
 * @author Natalie Ayala
 * @version 1.0
 * @since 2017-07-20
 */
public class FreshwaterMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private static final String TAG = "FreshwaterMapsActivity";

    GoogleMap googleMap;
    SupportMapFragment mapFragment;
    String URL = "https://waterservices.usgs.gov/nwis/iv/?format=waterml,2.0&stateCd=fl&parameterCd=00060,00065&siteType=LK,ST,ST-CA,ST-DCH,ST-TS,SP&siteStatus=active";

    String URLGraph;
    ArrayList<NameCoordsEntry> nameCoordinatesEntries;
    ArrayList<DateEntry> dateOnlyEntries;
    ArrayList<SiteEntry> siteOnlyEntries;

    Bundle bundle;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_maps);

        //Retrieves the variables passed from previous activity
        bundle = getIntent().getExtras();
        type = bundle.getString("type");
        Log.d("onCreate: ", type);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        /* This customises the styling of the base map using a JSON object defined
         *  in a raw resource file.
         */
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        this.googleMap = googleMap;

        /* This adds a focal point to the map by using coordinates of county selected in
         * StartActivity.
         */
        zoomInOnCoord(StartActivity.lat, StartActivity.lon);

        new DownloadXML().execute(URL);
    }

    private class DownloadXML extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        /* This method downloads the XML file from the URL,
         * parses through the file for each data type (water
         * level, flow, date, site ID).
         *
         * @catches e exception
         * @return null No returns, this method only parses through the XML file
         */
        @Override
        protected Void doInBackground(String... Url) {


            try {
                URL url = new URL(Url[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                // Download the XML file
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Source xmlSource = new DOMSource(doc);
                javax.xml.transform.Result outputTarget = new StreamResult(outputStream);
                TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);

                // Converts inputstream to byte array for each arraylist, than parses that
                // inputstream
                InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
                nameCoordinatesEntries = new NameCoordsParsing().parse(is);

                InputStream is3 = new ByteArrayInputStream(outputStream.toByteArray());
                dateOnlyEntries = new DateOnlyParsing().parse(is3);

                InputStream is4 = new ByteArrayInputStream(outputStream.toByteArray());
                siteOnlyEntries = new SiteOnlyParsing().parse(is4);


            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            /* This for loop pulls station coordinates from Arraylist generated from
             * @namesCoordsEntries, splits them into two doubles, then uses those double
             * to generate cooordinates. These coordinates are used as marker destinations
             * for the GoogleMap.
             * The marker snippets include data pulled from the siteOnlyEntries and
             * dateOnlyEntries arraylists.
             */
            for (int i = 0; i < nameCoordinatesEntries.size() && i < dateOnlyEntries.size(); i++) {

                String[] latlong = nameCoordinatesEntries.get(i).coordinates.split(" ");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                LatLng lng = new LatLng(latitude, longitude);

                String stationName = nameCoordinatesEntries.get(i).siteName;
                String date = dateOnlyEntries.get(i).date;
                String siteID = siteOnlyEntries.get(i).site;

                /* Markers are generated based on data pulled from
                 * arraylists.
                 */
                Marker destination = googleMap.addMarker(new MarkerOptions()
                        .position(lng)
                        .title(stationName)
                        .snippet("Click here for Water Level and Flow Data" + "\nSite ID: " + siteID + "Data current as of " + date)

                );

                /* This customises the styling of the station markers using an image resource
                 * file.
                 */
                destination.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.liquid));

            }

            /* When the marker info window is clicked, data is pulled from the snippet
             * to generate siteID, year, day, and month. These will be entered in URL
             * generate an XML file for the selected station. The date range will be
             * the date of most current data available and the 30 previous days.
             */

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    String fullSnippet = marker.getSnippet();
                    Log.d("onInfoWindowClick: ", fullSnippet);
                    String[] splitSnippet = fullSnippet.split("as of ");
                    String fullMarkerDate = splitSnippet[1];
                    Log.d("onInfoWindowClick: ", fullMarkerDate);

                    String[] markerDateSplit = fullMarkerDate.split(" ");
                    String markerDate = markerDateSplit[0];
                    Log.d("onInfoWindowClick: ", markerDate);


                    String remainingSnippet = splitSnippet[0];
                    Log.d("onInfoWindowClick: ", remainingSnippet);

                    String[] snippetSplit = remainingSnippet.split("ID: ");
                    String splitID = snippetSplit[1];
                    Log.d("onInfoWindowClick: ", splitID);

                    String[] siteIDSplit = splitID.split("D");
                    String siteID = siteIDSplit[0];
                    Log.d("onInfoWindowClick: ", siteID);


                    String[] dateSplit = markerDate.split("-");
                    int month = Integer.parseInt(dateSplit[1]);
                    Log.d("onInfoWindowClick: ", Integer.toString(month));

                    int year = Integer.parseInt(dateSplit[0]);
                    Log.d("onInfoWindowClick: ", Integer.toString(year));

                    int day = Integer.parseInt(dateSplit[2]);
                    Log.d("onInfoWindowClick: ", Integer.toString(day));


                    int previousMonth;
                    int previousYear;
                    int previousDay;

                    /*
                     * Potential date errors are avoided during calculation of 30
                     * day date range. If current month is January (1) then the previous
                     * month will default to December (12). All other months are determined
                     * by simple subtraction. If the day of the month is over 28, the default
                     * day of the month for the previous 30 day time frame defaults to 28 to avoid
                     * potential errors on shorter months. (This could be improved for better accuracy).
                     */

                    if (month != 1) {
                        previousMonth = month - 1;
                        if (32 > day && day > 28) {
                            previousDay = 28;
                        } else {
                            previousDay = day;
                        }
                        Log.d("onInfoWindowClick: ", type);
                        if (type.equals("flow")) {
                            URLGraph = "https://waterservices.usgs.gov/nwis/iv/?format=waterml,2.0&sites=" + siteID + "&startDT=" + year + "-" + previousMonth + "-" + previousDay + "&endDT=" + markerDate + "&parameterCd=00060&siteType=ES,LK,ST,ST-CA,ST-DCH,ST-TS,SP&siteStatus=active";
                        } else if (type.equals("level")) {
                            URLGraph = "https://waterservices.usgs.gov/nwis/iv/?format=waterml,2.0&sites=" + siteID + "&startDT=" + year + "-" + previousMonth + "-" + previousDay + "&endDT=" + markerDate + "&parameterCd=00065&siteType=ES,LK,ST,ST-CA,ST-DCH,ST-TS,SP&siteStatus=active";
                        }
                        Log.d("url", URLGraph);
                        Intent intent = new Intent(FreshwaterMapsActivity.this, GraphActivity.class);

                        intent.putExtra("key", URLGraph);
                        intent.putExtra("type", type);
                        startActivity(intent);

                    } else {
                        previousMonth = 12;
                        previousYear = year - 1;
                        if (32 > day && day > 28) {
                            previousDay = 28;
                        } else {
                            previousDay = day;
                        }
                        URLGraph = "https://waterservices.usgs.gov/nwis/iv/?format=waterml,2.0&sites=" + siteID + "&startDT=" + previousYear + "-" + previousMonth + "-" + previousDay + "&endDT=" + markerDate + "&parameterCd=00065&siteType=ES,LK,ST,ST-CA,ST-DCH,ST-TS,SP&siteStatus=active";
                        String URLGraph2 = URLGraph.replace("\n", "").replace("\r", "");
                        Log.d("url", URLGraph2);
                        Intent intent = new Intent(FreshwaterMapsActivity.this, GraphActivity.class);

                        intent.putExtra("key", URLGraph2);
                        startActivity(intent);
                    }


                }
            });

        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    /*This method zooms focus of map to coordinates.
     */
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
