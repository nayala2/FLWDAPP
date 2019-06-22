package valenciaprogrammers.com.waterapp;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by nayala2 on 9/22/17.
 */
public class TidalParsing {
    private static final String ns = null;

    public ArrayList<TidalEntry> parse(InputStream in) {
        ArrayList<TidalEntry> list = null;

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            list = readFeed(parser);
            for (int i = 0; i < list.size(); i++) {
                Log.i(".......", list.get(i).date);
                Log.i(".......", list.get(i).tidePrediction);
                Log.i(".......", list.get(i).highLow);
            }

        } catch (Exception e) {

        }
        return list;
    }

    private ArrayList<TidalEntry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {

        String date = null;
        String tidePrediction = null;
        String highLow = null;

        ArrayList<TidalEntry> entry = new ArrayList<TidalEntry>();
        parser.require(XmlPullParser.START_TAG, ns, "data");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            Log.i("first..........", name);

            // Starts by looking for the entry tag
            if (name.equals("pr")) {
//
                date = "" + parser.getAttributeValue(0);
                tidePrediction = "" + parser.getAttributeValue(1);
                highLow = "" + parser.getAttributeValue(2);
                TidalEntry thisone = new TidalEntry(date, tidePrediction, highLow);

                entry.add(thisone);
            } else {
                skip(parser);
            }
        }
        return entry;
    }

    private TidalEntry readMarker(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "pr");

        String date = null;
        String tidePrediction = null;
        String highLow = null;

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.i("Attribute name........", name);

            if (name.equals("pr")) {

                date = readDate(parser);
                tidePrediction = readTide(parser);
                highLow = readHighLow(parser);

            } else {
                skip(parser);
            }
        }
        return new TidalEntry(date, tidePrediction, highLow);

    }

    private String readDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        String date = "" + parser.getAttributeValue(0);

        return date;
    }

    private String readTide(XmlPullParser parser) throws IOException, XmlPullParserException {
        String tide = "" + parser.getAttributeValue(1);

        return tide;
    }

    private String readHighLow(XmlPullParser parser) throws IOException, XmlPullParserException {
        String highLow = "" + parser.getAttributeValue(2);

        return highLow;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
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