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

public class GraphValueParsing {
    private static final String ns = null;

    public ArrayList<GraphValueEntry> parse(InputStream in) {
        ArrayList<GraphValueEntry> list = null;

        try {
            XmlPullParser valueParser = Xml.newPullParser();
            valueParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            valueParser.setInput(in, null);
            valueParser.nextTag();
            list = readFeed(valueParser);
            for (int j = 0; j < list.size(); j++) {
                Log.i(j + ".......", list.get(j).value);
            }
        } catch (Exception e) {

        }
        return list;
    }

    private ArrayList<GraphValueEntry> readFeed(XmlPullParser valueParser) throws XmlPullParserException, IOException {

        ArrayList<GraphValueEntry> graphValueEntry = new ArrayList<GraphValueEntry>();
        valueParser.require(XmlPullParser.START_TAG, ns, "wml2:Collection");
        while (valueParser.next() != XmlPullParser.END_DOCUMENT) {
            if (valueParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = valueParser.getName();

            // Starts by looking for the entry tag
            if (name.equals("wml2:observationMember")) {
                graphValueEntry = readFeed1(valueParser);
                return graphValueEntry;
            } else {
                skip(valueParser);
            }
        }
        return graphValueEntry;
    }

    private ArrayList<GraphValueEntry> readFeed1(XmlPullParser valueParser) throws XmlPullParserException, IOException {

        ArrayList<GraphValueEntry> graphValueEntry = new ArrayList<GraphValueEntry>();
        valueParser.require(XmlPullParser.START_TAG, ns, "wml2:observationMember");
        while (valueParser.next() != XmlPullParser.END_DOCUMENT) {
            if (valueParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = valueParser.getName();
            // Starts by looking for the entry tag
            if (name.equals("om:OM_Observation")) {
                graphValueEntry = readFeed2(valueParser);
                return graphValueEntry;
            } else {
                skip(valueParser);
            }
        }
        return graphValueEntry;
    }

    private ArrayList<GraphValueEntry> readFeed2(XmlPullParser valueParser) throws XmlPullParserException, IOException {

        ArrayList<GraphValueEntry> graphValueEntry = new ArrayList<GraphValueEntry>();
        valueParser.require(XmlPullParser.START_TAG, ns, "om:OM_Observation");
        while (valueParser.next() != XmlPullParser.END_DOCUMENT) {
            if (valueParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = valueParser.getName();
            // Starts by looking for the entry tag
            if (name.equals("om:result")) {
                graphValueEntry = readFeed3(valueParser);
                return graphValueEntry;
            } else {
                skip(valueParser);
            }
        }
        return graphValueEntry;
    }

    private ArrayList<GraphValueEntry> readFeed3(XmlPullParser valueParser) throws XmlPullParserException, IOException {

        ArrayList<GraphValueEntry> graphValueEntry = new ArrayList<GraphValueEntry>();
        valueParser.require(XmlPullParser.START_TAG, ns, "om:result");
        while (valueParser.next() != XmlPullParser.END_DOCUMENT) {
            if (valueParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = valueParser.getName();
            // Starts by looking for the entry tag
            if (name.equals("wml2:MeasurementTimeseries")) {
                graphValueEntry = readFeed4(valueParser);
                return graphValueEntry;
            } else {
                skip(valueParser);
            }
        }
        return graphValueEntry;
    }

    private ArrayList<GraphValueEntry> readFeed4(XmlPullParser valueParser) throws XmlPullParserException, IOException {

        ArrayList<GraphValueEntry> graphValueEntry = new ArrayList<GraphValueEntry>();
        valueParser.require(XmlPullParser.START_TAG, ns, "wml2:MeasurementTimeseries");
        while (valueParser.next() != XmlPullParser.END_DOCUMENT) {
            if (valueParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = valueParser.getName();
            // Starts by looking for the entry tag
            if (name.equals("wml2:point")) {
                graphValueEntry.add(readMarker(valueParser));
            } else {
                skip(valueParser);
            }
        }
        return graphValueEntry;
    }

    private GraphValueEntry readMarker(XmlPullParser valueParser) throws XmlPullParserException, IOException {
        valueParser.require(XmlPullParser.START_TAG, ns, "wml2:point");

        String value = null;

        while (valueParser.next() != XmlPullParser.END_TAG) {
            if (valueParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = valueParser.getName();

            if (!(name.equals("wml2:MeasurementTVP"))) {
                skip(valueParser);
            } else {
                value = readValue(valueParser);
            }
        }
        return new GraphValueEntry(value);

    }

    private String readValue(XmlPullParser valueParser) throws IOException, XmlPullParserException {

        String value = null;

        while (valueParser.next() != XmlPullParser.END_TAG) {

            if (valueParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = valueParser.getName();

            if (name.equals("wml2:value")) {
                value = finalValue(valueParser);
                return value;
            } else {
                skip(valueParser);
            }
        }
        return value;
    }

    private String finalValue(XmlPullParser valueParser) throws IOException, XmlPullParserException {
        valueParser.require(XmlPullParser.START_TAG, ns, "wml2:value");
        String value = readText(valueParser);
        valueParser.require(XmlPullParser.END_TAG, ns, "wml2:value");
        return value;
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
