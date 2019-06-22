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

public class GraphNameParsing {
    private static final String ns = null;

    public ArrayList<GraphNameEntry> parse(InputStream in) {
        ArrayList<GraphNameEntry> list = null;

        try {
            XmlPullParser valueParser = Xml.newPullParser();
            valueParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            valueParser.setInput(in, null);
            valueParser.nextTag();
            list = readFeed(valueParser);
            for (int j = 0; j < list.size(); j++) {
                Log.i(j + ".......", list.get(j).siteName);
            }
        } catch (Exception e) {

        }
        return list;
    }

    private ArrayList<GraphNameEntry> readFeed(XmlPullParser nameParser) throws XmlPullParserException, IOException {

        ArrayList<GraphNameEntry> graphNameEntry = new ArrayList<GraphNameEntry>();
        nameParser.require(XmlPullParser.START_TAG, ns, "wml2:Collection");
        while (nameParser.next() != XmlPullParser.END_DOCUMENT) {
            if (nameParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = nameParser.getName();

            // Starts by looking for the entry tag
            if (name.equals("gml:name")) {
                graphNameEntry.add(readMarker(nameParser));
                Log.d("did i make it: ", "here2");

            } else {
                skip(nameParser);
            }
        }
        return graphNameEntry;
    }

    private GraphNameEntry readMarker(XmlPullParser parser) throws XmlPullParserException, IOException {
//        parser.require(XmlPullParser.START_TAG, ns, "gml:name");
        String siteName = null;

//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.getEventType() != XmlPullParser.START_TAG) {
//                continue;
//            }
        String name = parser.getName();
        Log.i("............", name);
        if (!(name.equals("gml:name"))) {
            skip(parser);
        } else {
            String fullSiteName = readName(parser);
            String remove = "Timeseries collected at ";
            siteName = fullSiteName.replace(remove, "");
            Log.d("readMarker: ", siteName);
        }

        return new GraphNameEntry(siteName);
    }

    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "gml:name");
        String siteName = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "gml:name");
        return siteName;
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