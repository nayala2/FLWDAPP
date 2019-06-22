package valenciaprogrammers.com.waterapp;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by nayala2 on 11/5/17.
 */

public class GraphDateParsing2 {
    private static final String ns = null;

    public ArrayList<GraphDateEntry> parse(InputStream in) {
        ArrayList<GraphDateEntry> list = null;

        try {
            XmlPullParser valueParser = Xml.newPullParser();
            valueParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            valueParser.setInput(in, null);
            valueParser.nextTag();
            list = readFeed(valueParser);
            for (int j = 0; j < list.size(); j++) {
                Log.i(j + ".......", list.get(j).date);
            }
        } catch (Exception e) {

        }
        return list;
    }

    private ArrayList<GraphDateEntry> readFeed(XmlPullParser valueParser) throws XmlPullParserException, IOException {

        ArrayList<GraphDateEntry> graphDateEntries = new ArrayList<GraphDateEntry>();
        valueParser.require(XmlPullParser.START_TAG, ns, "wml2:Collection");
        while (valueParser.next() != XmlPullParser.END_DOCUMENT) {
            if (valueParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            GraphDateEntry date = null;
            String name = valueParser.getName();

            // Starts by looking for the entry tag
            if (name.equals("wml2:observationMember")) {
                graphDateEntries = readFeed1(valueParser);
                return graphDateEntries;
            } else {
                skip(valueParser);
            }
        }
        return graphDateEntries;
    }

    private ArrayList<GraphDateEntry> readFeed1(XmlPullParser valueParser) throws XmlPullParserException, IOException {

        ArrayList<GraphDateEntry> graphDateEntries = new ArrayList<GraphDateEntry>();
        valueParser.require(XmlPullParser.START_TAG, ns, "wml2:observationMember");
        while (valueParser.next() != XmlPullParser.END_DOCUMENT) {
            if (valueParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            GraphDateEntry date = null;

            String name = valueParser.getName();
            // Starts by looking for the entry tag
            if (name.equals("om:OM_Observation")) {
                graphDateEntries = readFeed2(valueParser);
                return graphDateEntries;
            } else {
                skip(valueParser);
            }
        }
        return graphDateEntries;
    }

    private ArrayList<GraphDateEntry> readFeed2(XmlPullParser valueParser) throws XmlPullParserException, IOException {

        ArrayList<GraphDateEntry> graphDateEntries = new ArrayList<GraphDateEntry>();
        valueParser.require(XmlPullParser.START_TAG, ns, "om:OM_Observation");
        while (valueParser.next() != XmlPullParser.END_DOCUMENT) {
            if (valueParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            GraphDateEntry date = null;
            String name = valueParser.getName();

            // Starts by looking for the entry tag
            if (name.equals("om:result")) {
                graphDateEntries = readFeed3(valueParser);
                return graphDateEntries;
            } else {
                skip(valueParser);
            }
        }
        return graphDateEntries;
    }

    private ArrayList<GraphDateEntry> readFeed3(XmlPullParser valueParser) throws XmlPullParserException, IOException {

        ArrayList<GraphDateEntry> graphDateEntries = new ArrayList<GraphDateEntry>();
        valueParser.require(XmlPullParser.START_TAG, ns, "om:result");
        while (valueParser.next() != XmlPullParser.END_DOCUMENT) {
            if (valueParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            GraphDateEntry date = null;

            String name = valueParser.getName();
            // Starts by looking for the entry tag
            if (name.equals("wml2:MeasurementTimeseries")) {
                graphDateEntries = readFeed4(valueParser);
                return graphDateEntries;
            } else {
                skip(valueParser);
            }
        }
        return graphDateEntries;
    }

    private ArrayList<GraphDateEntry> readFeed4(XmlPullParser valueParser) throws XmlPullParserException, IOException {


        ArrayList<GraphDateEntry> graphDateEntries = new ArrayList<GraphDateEntry>();
        valueParser.require(XmlPullParser.START_TAG, ns, "wml2:MeasurementTimeseries");
        while (valueParser.next() != XmlPullParser.END_DOCUMENT) {
            if (valueParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = valueParser.getName();
            // Starts by looking for the entry tag
            if (name.equals("wml2:point")) {
                graphDateEntries.add(readMarker(valueParser));
//                return graphDateEntries;
            } else {
                skip(valueParser);
            }
        }
        return graphDateEntries;
    }

    private GraphDateEntry readMarker(XmlPullParser valueParser) throws XmlPullParserException, IOException {
        valueParser.require(XmlPullParser.START_TAG, ns, "wml2:point");

        String date = null;

        while (valueParser.next() != XmlPullParser.END_TAG) {
            if (valueParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = valueParser.getName();

            if (!(name.equals("wml2:MeasurementTVP"))) {
                skip(valueParser);
            } else {
                date = readValue(valueParser);
//                Log.d("date.........", date);
            }
        }
        return new GraphDateEntry(date);

    }

    private String readValue(XmlPullParser valueParser) throws IOException, XmlPullParserException {

        String date = null;

        while (valueParser.next() != XmlPullParser.END_TAG) {

            if (valueParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = valueParser.getName();

            if (name.equals("wml2:time")) {
                String fullDate = finalValue(valueParser);
                String[] splitDate = fullDate.split("T");
                date = splitDate[0];
                //T05:00:00-05:00
                String[] splitTime = splitDate[1].split(":");
                date = splitDate[0] + " " + splitTime[0] + ":" + splitTime[1];
//                Log.d("date.........", date);

                return date;
            } else {
                skip(valueParser);
            }
        }
        return date;
    }

    private String finalValue(XmlPullParser valueParser) throws IOException, XmlPullParserException {
        valueParser.require(XmlPullParser.START_TAG, ns, "wml2:time");
        String date = readText(valueParser);
        valueParser.require(XmlPullParser.END_TAG, ns, "wml2:time");
        return date;
    }

    private String readText(XmlPullParser valueParser) throws IOException, XmlPullParserException {
        String result = "";
        if (valueParser.next() == XmlPullParser.TEXT) {
            result = valueParser.getText();
            valueParser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser valueParser) throws XmlPullParserException, IOException {
        if (valueParser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (valueParser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
