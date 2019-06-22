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
public class ErrorParsing {
    private static final String ns = null;

    public ArrayList<ErrorEntry> parse(InputStream in) {
        ArrayList<ErrorEntry> list = null;

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            list = readFeed(parser);
            for (int i = 0; i < list.size(); i++) {
                Log.i(".......", list.get(i).errorMessage);
            }
        } catch (Exception e) {

        }
        return list;
    }

    private ArrayList<ErrorEntry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {

        String errorMessage = null;

        ArrayList<ErrorEntry> entry = new ArrayList<ErrorEntry>();
        parser.require(XmlPullParser.START_TAG, ns, "error");
//        while (parser.next() != XmlPullParser.END_DOCUMENT) {
//            if (parser.getEventType() != XmlPullParser.START_TAG) {
//                continue;
//            }

        errorMessage = readLat(parser);
        Log.d("readFeed: ", errorMessage);

        ErrorEntry thisone = new ErrorEntry(errorMessage);

        entry.add(thisone);

//        }
        return entry;
    }

    private ErrorEntry readError(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "error");
        String errorMessage = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.i("............", name);
            errorMessage = readLat(parser);

        }
        return new ErrorEntry(errorMessage);
    }

    private String readLat(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "error");
        String errorMessage = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "error");
        return errorMessage;
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