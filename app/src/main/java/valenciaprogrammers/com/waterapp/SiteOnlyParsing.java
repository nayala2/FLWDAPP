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

public class SiteOnlyParsing {
    private static final String ns = null;

    public ArrayList<SiteEntry> parse(InputStream in) {
        ArrayList<SiteEntry> list = null;

        try {
            XmlPullParser siteParser = Xml.newPullParser();
            siteParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            siteParser.setInput(in, null);
            siteParser.nextTag();
            list = readFeed(siteParser);
            for (int j = 0; j < list.size(); j++) {
                Log.i(j + ".......", list.get(j).site);
            }
        } catch (Exception e) {

        }
        return list;
    }

    private ArrayList<SiteEntry> readFeed(XmlPullParser siteParser) throws XmlPullParserException, IOException {

        ArrayList<SiteEntry> siteOnlyEntry = new ArrayList<SiteEntry>();
        siteParser.require(XmlPullParser.START_TAG, ns, "gml:FeatureCollection");
        while (siteParser.next() != XmlPullParser.END_DOCUMENT) {
            if (siteParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = siteParser.getName();
            // Starts by looking for the entry tag
            if (name.equals("gml:featureMember")) {
                siteOnlyEntry.add(readMarker(siteParser));
            } else {
                skip(siteParser);
            }
        }
        return siteOnlyEntry;
    }

    private SiteEntry readMarker(XmlPullParser siteParser) throws XmlPullParserException, IOException {
        siteParser.require(XmlPullParser.START_TAG, ns, "gml:featureMember");

        String site = null;

        while (siteParser.next() != XmlPullParser.END_TAG) {
            if (siteParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = siteParser.getName();

            if (!(name.equals("wml2:Collection"))) {
                skip(siteParser);
            } else {
                site = readFlow(siteParser);
                Log.d("site.........", site);
            }
        }
        return new SiteEntry(site);

    }

    private String readFlow(XmlPullParser siteParser) throws IOException, XmlPullParserException {

        String site = null;

        while (siteParser.next() != XmlPullParser.END_TAG) {

            if (siteParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = siteParser.getName();

            if (name.equals("gml:identifier")) {
                String fullSite = readSite(siteParser);
                String[] splitSite = fullSite.split("\\.");
                site = splitSite[1];
                return site;
            } else {
                skip(siteParser);
            }
        }
        return site;
    }


    private String readSite(XmlPullParser dateParser) throws IOException, XmlPullParserException {
        dateParser.require(XmlPullParser.START_TAG, ns, "gml:identifier");
        String site = readText(dateParser);
        dateParser.require(XmlPullParser.END_TAG, ns, "gml:identifier");
        return site;
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
