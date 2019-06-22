package valenciaprogrammers.com.waterapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class GraphActivity extends AppCompatActivity {

    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    ArrayList<GraphDateEntry> dateOnlyEntrySeries;
    ArrayList<GraphValueEntry> valueOnlySeries;
    ArrayList<GraphNameEntry> nameOnlySeries;


    DataPoint[] dp;
    String currentValue;
    String currentDate;
    String currentName;
    String type;
    Double finalDoubleValue;

    String URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        graphView = findViewById(R.id.graph);
        Bundle bundle = getIntent().getExtras();
        URL = bundle.getString("key");
        type = bundle.getString("type");
        Log.d("onCreate: ", URL);

        new DownloadXMLForGraph().execute(URL);
    }

    // DownloadXML AsyncTask
    private class DownloadXMLForGraph extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


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

                InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
                dateOnlyEntrySeries = new GraphDateParsing2().parse(is);

                InputStream is1 = new ByteArrayInputStream(outputStream.toByteArray());
                valueOnlySeries = new GraphValueParsing().parse(is1);

                InputStream is2 = new ByteArrayInputStream(outputStream.toByteArray());
                nameOnlySeries = new GraphNameParsing().parse(is2);

            } catch (ParserConfigurationException pce) {
                pce.printStackTrace();
            } catch (IOException io) {
                io.printStackTrace();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            try {
                if (valueOnlySeries != null) {

                    currentValue = valueOnlySeries.get(valueOnlySeries.size() - 2).value;
                    currentDate = dateOnlyEntrySeries.get(dateOnlyEntrySeries.size() - 2).date;
                    currentName = nameOnlySeries.get(0).siteName;

                    int n = 0;

                    dp = new DataPoint[dateOnlyEntrySeries.size() - 1];
                    int arraySize = dateOnlyEntrySeries.size();

                    for (int i = 0; i < dateOnlyEntrySeries.size() - 1; i++) {

                        if (valueOnlySeries.get(i).value.length() > 1) {
                            Double newDoubleValue = Double.parseDouble(valueOnlySeries.get(i).value);
                            String newStringDate = dateOnlyEntrySeries.get(i).date;

                            dp[i] = new DataPoint(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(newStringDate), newDoubleValue);

                            Log.d("did i make it here: ", newStringDate + ", " + newDoubleValue.toString());
                        }
                    }


                    series = new LineGraphSeries<>(dp);

                    graphView.addSeries(series);

                    graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                        @Override
                        public String formatLabel(double value, boolean isValueX) {
                            Log.d("onPostExecute: ", "did i make it to labelrender");
                            if (isValueX) {
                                Log.d("formatLabel: ", sdf.format(new Date((long) value)));
                                return sdf.format(new Date((long) value));
                            } else {
                                return super.formatLabel(value, isValueX);
                            }
                        }
                    });

                    graphView.setTitle(nameOnlySeries.get(0).siteName);

                    if (nameOnlySeries.get(0).siteName.length() > 35) {
                        graphView.setTitleTextSize(35);
                    } else {
                        graphView.setTitleTextSize(50);
                    }

                    series.setColor(Color.BLACK);
                    series.setThickness(4);

                    graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);

                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMinimumFractionDigits(2);
                    nf.setMinimumIntegerDigits(1);

                    graphView.getGridLabelRenderer().setTextSize(35f);
                    graphView.getGridLabelRenderer().setNumHorizontalLabels(5);
                    graphView.getGridLabelRenderer().setHorizontalLabelsAngle(90);

                    Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateOnlyEntrySeries.get(0).date);
                    Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateOnlyEntrySeries.get(dateOnlyEntrySeries.size() - 1).date);

                    graphView.getViewport().setMinX(date1.getTime());
                    graphView.getViewport().setMaxX(date2.getTime());
                    graphView.getViewport().setXAxisBoundsManual(true);

                    graphView.getGridLabelRenderer().setGridColor(Color.BLACK);
                    graphView.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLACK);
                    graphView.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLACK);
                    graphView.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
                    graphView.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);

                    TextView chartTitle = findViewById(R.id.textView);
                    if (type.equals("flow")) {
                        chartTitle.setText("Monthly Flow Chart");
                    } else if (type.equals("level")) {
                        chartTitle.setText("Monthly Water Level Chart");
                    }

                    graphView.setTitleColor(Color.BLACK);


                    TextView currentDataTextView = findViewById(R.id.textView4);
                    if (type.equals("flow")) {
                        currentDataTextView.setText(currentValue + " ft3/s on " + currentDate);
                    } else if (type.equals("level")) {
                        currentDataTextView.setText(currentValue + " ft on " + currentDate);
                    }

                } else {
                    Intent errorIntent = new Intent(GraphActivity.this, ErrorActivity.class);
                    startActivity(errorIntent);
                }
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
        }
    }


    /* if the back button is pressing, the the necessary information may not be carried over
     * between activities correctly. As a temporary fix, hitting the back button returns you two
     * the home screen to restart your search so that all data are transferred across activities
     * correctly.
     */
    public void onBackPressed() {
        Intent startMain = new Intent(GraphActivity.this, StartActivity.class);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}



