package valenciaprogrammers.com.waterapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {

    InputStream inputStream;
    String[] data;

    ArrayList<String> latitude = new ArrayList<String>();
    ArrayList<String> longitude = new ArrayList<String>();
    ArrayList<String> cities = new ArrayList<String>();

    public static int ready = 0;

    public static double lat;
    public static double lon;


    AutoCompleteTextView searchBar;

    int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, cities);

        inputStream = getResources().openRawResource(R.raw.counties);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                data = csvLine.split(",");
                try {
                    //Log.e("DataCities", "" + data[0]);
                    cities.add("" + data[0]);
                    latitude.add("" + data[1]);
                    longitude.add("" + data[2]);

                } catch (Exception e) {
                    Log.e("Problem", e.toString());
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }

        final Spinner spinner = findViewById(R.id.planets_spinner);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setImageResource(R.drawable.magnifier);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter += 1;
                Log.d("imagine", "imagine");
                if (counter % 2 == 0) {
                    searchBar.setVisibility(View.VISIBLE);
                    searchBar.requestFocus();
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
                } else {
                    searchBar.setVisibility(View.INVISIBLE);
                    searchBar.setText("");
                    //searchBar.setFocusable(false);
                }

            }
        });

        spinner.setSelection(0, false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner.setVisibility(View.VISIBLE);
                counter = 1;

                String search = spinner.getSelectedItem().toString();

                for (int j = 0; j < cities.size(); j++) {
                    if (search.contains(cities.get(j))) {
                        lat = Double.parseDouble(latitude.get(j));
                        lon = Double.parseDouble(longitude.get(j));
                    }

                }
                Intent i = new Intent(StartActivity.this, ChooseTypeActivity.class);

                startActivity(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
