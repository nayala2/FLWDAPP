package valenciaprogrammers.com.waterapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/*
 * To do list: Add spinners for date selections
 */

public class DateRangeActivity extends AppCompatActivity {

    private TextView getTidalData;
    String noaaURL;
    String siteID;

    EditText startDay;
    EditText startMonth;
    EditText startYear;
    EditText endDay;
    EditText endMonth;
    EditText endYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_range);

        Bundle bundle = getIntent().getExtras();
        siteID = bundle.getString("key");
        Log.d("onCreate: ", siteID);

        getTidalData = findViewById(R.id.button4);

        getTidalData.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startDay = findViewById(R.id.startDay);
                startMonth = findViewById(R.id.startMonth);
                startYear = findViewById(R.id.startYear);
                endDay = findViewById(R.id.endDay);
                endMonth = findViewById(R.id.endMonth);
                endYear = findViewById(R.id.endYear);

                String stringStartDay = startDay.getText().toString();
                String stringStartMonth = startMonth.getText().toString();
                String stringStartYear = startYear.getText().toString();
                String stringEndDay = endDay.getText().toString();
                String stringEndMonth = endMonth.getText().toString();
                String stringEndYear = endYear.getText().toString();

                if ((stringStartDay.length() < 2)) {
                    Intent dateErrorIntent = new Intent(DateRangeActivity.this, DateErrorActivity.class);
                    startActivity(dateErrorIntent);
                } else if ((stringStartMonth.length() < 2)) {
                    Intent dateErrorIntent = new Intent(DateRangeActivity.this, DateErrorActivity.class);
                    startActivity(dateErrorIntent);
                } else if (stringStartYear.length() < 4) {
                    Intent dateErrorIntent = new Intent(DateRangeActivity.this, DateErrorActivity.class);
                    startActivity(dateErrorIntent);
                } else if (stringEndDay.length() < 2) {
                    Intent dateErrorIntent = new Intent(DateRangeActivity.this, DateErrorActivity.class);
                    startActivity(dateErrorIntent);
                } else if (stringEndMonth.length() < 2) {
                    Intent dateErrorIntent = new Intent(DateRangeActivity.this, DateErrorActivity.class);
                    startActivity(dateErrorIntent);
                } else if (stringEndYear.length() < 4) {
                    Intent dateErrorIntent = new Intent(DateRangeActivity.this, DateErrorActivity.class);
                    startActivity(dateErrorIntent);
                } else {

                    String startDate = startYear.getText().toString() + startMonth.getText().toString() + startDay.getText().toString();
                    Log.d("onClick: ", Integer.toString(stringStartDay.length()));
                    Log.d("onClick: ", startDay.getText().toString());
                    Log.d("onClick: ", startMonth.getText().toString());

                    String endDate = endYear.getText().toString() + endMonth.getText().toString() + endDay.getText().toString();

                    noaaURL = "https://tidesandcurrents.noaa.gov/api/datagetter?begin_date=" + startDate + "&end_date=" + endDate + "&station=" + siteID + "&product=PREDICTIONS&datum=MLLW&units=english&time_zone=lst_ldt&application=web_services&format=xml";


                    Intent intent = new Intent(DateRangeActivity.this, MarineGraphActivity.class);
                    intent.putExtra("URL", noaaURL);

                    startActivity(intent);

                }
            }
        }));
    }
}