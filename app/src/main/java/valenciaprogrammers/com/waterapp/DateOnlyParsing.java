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

public class DateOnlyParsing {
    private static final String ns = null;

    public ArrayList<DateEntry> parse(InputStream in) {
        ArrayList<DateEntry> list = null;

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

    private ArrayList<DateEntry> readFeed(XmlPullParser dateParser) throws XmlPullParserException, IOException {

        ArrayList<DateEntry> dateOnlyEntry = new ArrayList<DateEntry>();
        dateParser.require(XmlPullParser.START_TAG, ns, "gml:FeatureCollection");
        while (dateParser.next() != XmlPullParser.END_DOCUMENT) {
            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = dateParser.getName();
            // Starts by looking for the entry tag
            if (name.equals("gml:featureMember")) {
                dateOnlyEntry.add(readMarker(dateParser));
            } else {
                skip(dateParser);
            }
        }
        return dateOnlyEntry;
    }

    private DateEntry readMarker(XmlPullParser dateParser) throws XmlPullParserException, IOException {
        dateParser.require(XmlPullParser.START_TAG, ns, "gml:featureMember");

        String date = null;

        while (dateParser.next() != XmlPullParser.END_TAG) {
            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = dateParser.getName();

            if (!(name.equals("wml2:Collection"))) {
                skip(dateParser);
            } else {
                date = readFlow(dateParser);
//                Log.d("date.........", date);
            }
        }
        return new DateEntry(date);

    }

    private String readFlow(XmlPullParser dateParser) throws IOException, XmlPullParserException {

        String date = null;

        while (dateParser.next() != XmlPullParser.END_TAG) {

            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = dateParser.getName();

            if (name.equals("wml2:observationMember")) {

                date = readFlow1(dateParser);
                return date;
            } else {
                skip(dateParser);
            }
        }
        return date;
    }

    private String readFlow1(XmlPullParser dateParser) throws IOException, XmlPullParserException {

        String date = null;

        while (dateParser.next() != XmlPullParser.END_TAG) {

            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = dateParser.getName();

            if (name.equals("om:OM_Observation")) {
                date = readNextLevelFlow(dateParser);
                return date;
            } else {
                skip(dateParser);
            }
        }
        return date;
    }


    private String readNextLevelFlow(XmlPullParser dateParser) throws IOException, XmlPullParserException {

        String date = null;

        while (dateParser.next() != XmlPullParser.END_TAG) {
            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = dateParser.getName();

            if (!(name.equals("om:result"))) {
                skip(dateParser);
            } else {
                date = readNextLevelFlow2(dateParser);
                return date;
            }
        }
        return date;
    }


    private String readNextLevelFlow2(XmlPullParser dateParser) throws IOException, XmlPullParserException {
        String date = null;

        while (dateParser.next() != XmlPullParser.END_TAG) {
            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = dateParser.getName();

            if (!(name.equals("wml2:MeasurementTimeseries"))) {
                skip(dateParser);
            } else {
                date = readNextLevelFlow3(dateParser);
                return date;
            }
        }
        return date;
    }

    private String readNextLevelFlow3(XmlPullParser dateParser) throws IOException, XmlPullParserException {

        String date = null;

        while (dateParser.next() != XmlPullParser.END_TAG) {
            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = dateParser.getName();

            if (!(name.equals("wml2:point"))) {
                skip(dateParser);
            } else {
                date = readNextLevelFlow4(dateParser);
                return date;
            }
        }
        return date;
    }

    private String readNextLevelFlow4(XmlPullParser dateParser) throws IOException, XmlPullParserException {

        String date = null;

        while (dateParser.next() != XmlPullParser.END_TAG) {
            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = dateParser.getName();

            if (!(name.equals("wml2:MeasurementTVP"))) {
                skip(dateParser);
            } else {
                date = readNextLevelFlow5(dateParser);
                return date;
            }
        }
        return date;
    }

    private String readNextLevelFlow5(XmlPullParser dateParser) throws IOException, XmlPullParserException {

        String date = null;

        while (dateParser.next() != XmlPullParser.END_TAG) {
            if (dateParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = dateParser.getName();

            if (!(name.equals("wml2:time"))) {
                skip(dateParser);
            } else {
                String fullDate = finalFlow(dateParser);
                String[] splitDate = fullDate.split("T");
                //T05:00:00-05:00
                String[] splitTime = splitDate[1].split(":");
                date = splitDate[0] + " " + splitTime[0] + ":" + splitTime[1];
                return date;
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
