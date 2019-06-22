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

////waterservices.usgs.gov/nwis/iv/?format=waterml,2.0&stateCd=fl&parameterCd=00060&siteType=ST,ST-CA,ST-DCH,ST-TS&siteStatus=all

public class NameCoordsParsing {
    private static final String ns = null;

    public ArrayList<NameCoordsEntry> parse(InputStream in) {
        ArrayList<NameCoordsEntry> list = null;

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            list = readFeed(parser);
            for (int i = 0; i < list.size(); i++) {
                Log.i(i + ".......", list.get(i).coordinates);
                Log.i(i + ".......", list.get(i).siteName);
            }
        } catch (Exception e) {

        }
        return list;
    }

    private ArrayList<NameCoordsEntry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {

        ArrayList<NameCoordsEntry> streamFlowEntry = new ArrayList<NameCoordsEntry>();
        parser.require(XmlPullParser.START_TAG, ns, "gml:FeatureCollection");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("gml:featureMember")) {
                streamFlowEntry.add(readMarker(parser));
            } else {
                skip(parser);
            }
        }
        return streamFlowEntry;
    }

    private NameCoordsEntry readMarker(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "gml:featureMember");
        String coordinates = null;
        String siteName = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("wml2:Collection")) {
                siteName = readName(parser);
            } else if (name.equals("wml2:observationMember")) {
                coordinates = readCoordinates(parser);
            } else {
                skip(parser);
            }
        }
        return new NameCoordsEntry(coordinates, siteName);

    }

    private String readCoordinates(XmlPullParser parser) throws IOException, XmlPullParserException {

        String coordinates = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals("om:OM_Observation")) {
                coordinates = nextLevelCoords(parser);
                return coordinates;
            } else {
                skip(parser);
            }
        }
        return coordinates;
    }

    private String readSiteName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "gml:name");
        String siteName = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "gml:name");
        return siteName;
    }

    private String readCoords(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "gml:pos");
        String coords = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "gml:pos");
        return coords;
    }

    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {

        String siteName = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (!(name.equals("gml:name"))) {
                skip(parser);
            } else {
                String fullSiteName = readSiteName(parser);
                String remove = "Timeseries collected at ";
                siteName = fullSiteName.replace(remove, "");
                return siteName;
            }
        }
        return siteName;
    }

    private String nextLevelCoords(XmlPullParser parser) throws IOException, XmlPullParserException {

        String coords = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (!(name.equals("om:featureOfInterest"))) {
                skip(parser);
            } else {
                coords = readNextLevelCoords2(parser);
                return coords;
            }
        }
        return coords;
    }

    private String readNextLevelCoords2(XmlPullParser parser) throws IOException, XmlPullParserException {
        String coords = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (!(name.equals("wml2:MonitoringPoint"))) {
                skip(parser);
            } else {
                coords = readNextLevelCoords3(parser);
                return coords;
            }
        }
        return coords;
    }

    private String readNextLevelCoords3(XmlPullParser parser) throws IOException, XmlPullParserException {
        String coords = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (!(name.equals("sams:shape"))) {
                skip(parser);
            } else {
                coords = readNextLevelCoords4(parser);
                return coords;
            }
        }
        return coords;
    }

    private String readNextLevelCoords4(XmlPullParser parser) throws IOException, XmlPullParserException {
        String coords = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (!(name.equals("gml:Point"))) {
                skip(parser);
            } else {
                coords = readNextLevelCoords5(parser);
                return coords;
            }
        }
        return coords;
    }

    private String readNextLevelCoords5(XmlPullParser parser) throws IOException, XmlPullParserException {
        String coords = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (!(name.equals("gml:pos"))) {
                skip(parser);
            } else {
                coords = readCoords(parser);

                return coords;
            }
        }
        return coords;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
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