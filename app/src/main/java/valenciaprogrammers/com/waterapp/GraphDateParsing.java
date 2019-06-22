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

public class GraphDateParsing {
    private static final String ns = null;

    public ArrayList<GraphDateEntry> parse(InputStream in) {
        ArrayList<GraphDateEntry> list = null;

        try {
            XmlPullParser dateParser = Xml.newPullParser();
            dateParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            dateParser.setInput(in, null);
            dateParser.nextTag();
            list = readFeed(dateParser);
            for (int j = 0; j < list.size(); j++) {
                Log.i(j + ".......", list.get(j).date);
            }
        } catch (Exception e) {

        }
        return list;
    }

    private ArrayList<GraphDateEntry> readFeed(XmlPullParser dateParser) throws XmlPullParserException, IOException {

        ArrayList<GraphDateEntry> graphDateEntry = new ArrayList<GraphDateEntry>();
        dateParser.require(XmlPullParser.START_TAG, ns, "wml2:Collection");
        while (dateParser.next() != XmlPullParser.END_DOCUMENT) {
            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = dateParser.getName();

            // Starts by looking for the entry tag
            if (name.equals("wml2:observationMember")) {
                readFeed1(dateParser);
            } else {
                skip(dateParser);
            }
        }
        return graphDateEntry;
    }

    private ArrayList<GraphDateEntry> readFeed1(XmlPullParser dateParser) throws XmlPullParserException, IOException {

        ArrayList<GraphDateEntry> graphDateEntry = new ArrayList<GraphDateEntry>();
        dateParser.require(XmlPullParser.START_TAG, ns, "wml2:observationMember");
        while (dateParser.next() != XmlPullParser.END_DOCUMENT) {
            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = dateParser.getName();
            // Starts by looking for the entry tag
            if (name.equals("om:OM_Observation")) {
//                Log.d("om:OM_Observation", name);
//                graphDateEntry.add(readMarker(dateParser));
                readFeed2(dateParser);

            } else {
                skip(dateParser);
            }
        }
        return graphDateEntry;
    }

    private ArrayList<GraphDateEntry> readFeed2(XmlPullParser dateParser) throws XmlPullParserException, IOException {

        ArrayList<GraphDateEntry> graphDateEntry = new ArrayList<GraphDateEntry>();
        dateParser.require(XmlPullParser.START_TAG, ns, "om:OM_Observation");
        while (dateParser.next() != XmlPullParser.END_DOCUMENT) {
            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = dateParser.getName();
            // Starts by looking for the entry tag
            if (name.equals("om:result")) {
//                Log.d("om:result", name);
                readFeed3(dateParser);
            } else {
                skip(dateParser);
            }
        }
        return graphDateEntry;
    }

    private ArrayList<GraphDateEntry> readFeed3(XmlPullParser dateParser) throws XmlPullParserException, IOException {

        ArrayList<GraphDateEntry> graphDateEntry = new ArrayList<GraphDateEntry>();
        dateParser.require(XmlPullParser.START_TAG, ns, "om:result");
        while (dateParser.next() != XmlPullParser.END_DOCUMENT) {
            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = dateParser.getName();
            // Starts by looking for the entry tag
            if (name.equals("wml2:MeasurementTimeseries")) {
//                Log.d("MeasurementTimeseries", name);
                readFeed4(dateParser);
            } else {
                skip(dateParser);
            }
        }
        return graphDateEntry;
    }

    private ArrayList<GraphDateEntry> readFeed4(XmlPullParser dateParser) throws XmlPullParserException, IOException {

        ArrayList<GraphDateEntry> graphDateEntry = new ArrayList<GraphDateEntry>();
        dateParser.require(XmlPullParser.START_TAG, ns, "wml2:MeasurementTimeseries");
        while (dateParser.next() != XmlPullParser.END_DOCUMENT) {
            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = dateParser.getName();
            // Starts by looking for the entry tag
            if (name.equals("wml2:point")) {
                graphDateEntry.add(readMarker(dateParser));
                return graphDateEntry;
            } else {
                skip(dateParser);
            }
        }
        return graphDateEntry;
    }

    private GraphDateEntry readMarker(XmlPullParser dateParser) throws XmlPullParserException, IOException {
        dateParser.require(XmlPullParser.START_TAG, ns, "wml2:point");

        String date = null;

        while (dateParser.next() != XmlPullParser.END_TAG) {
            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = dateParser.getName();

            if (!(name.equals("wml2:MeasurementTVP"))) {
                skip(dateParser);
            } else {
                date = readFlow(dateParser);
            }
        }
        return new GraphDateEntry(date);

    }

    private String readFlow(XmlPullParser dateParser) throws IOException, XmlPullParserException {

        String date = null;

        while (dateParser.next() != XmlPullParser.END_TAG) {

            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = dateParser.getName();

            if (name.equals("wml2:time")) {
                String fullDate = finalFlow(dateParser);
                String[] splitDate = fullDate.split("T");
                date = splitDate[0];
                return date;
            } else {
                skip(dateParser);
            }
        }
        return date;
    }

    private String finalFlow(XmlPullParser dateParser) throws IOException, XmlPullParserException {
        dateParser.require(XmlPullParser.START_TAG, ns, "wml2:time");
        String date = readText(dateParser);
        dateParser.require(XmlPullParser.END_TAG, ns, "wml2:time");
        return date;
    }

    private String readText(XmlPullParser dateParser) throws IOException, XmlPullParserException {
        String result = "";
        if (dateParser.next() == XmlPullParser.TEXT) {
            result = dateParser.getText();
            dateParser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser dateParser) throws XmlPullParserException, IOException {
        if (dateParser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (dateParser.next()) {
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
