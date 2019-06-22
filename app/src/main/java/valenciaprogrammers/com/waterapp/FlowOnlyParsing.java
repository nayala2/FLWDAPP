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
public class FlowOnlyParsing {
    private static final String ns = null;

    public ArrayList<FlowOnlyEntry> parse(InputStream in) {
        ArrayList<FlowOnlyEntry> list = null;

        try {
            XmlPullParser flowParser = Xml.newPullParser();
            flowParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            flowParser.setInput(in, null);
            flowParser.nextTag();
            list = readFeed(flowParser);
            for (int j = 0; j < list.size(); j++) {
                Log.i(j + ".......", list.get(j).flow);
            }
        } catch (Exception e) {

        }
        return list;
    }

    private ArrayList<FlowOnlyEntry> readFeed(XmlPullParser flowParser) throws XmlPullParserException, IOException {

        ArrayList<FlowOnlyEntry> flowOnlyEntry = new ArrayList<FlowOnlyEntry>();
        flowParser.require(XmlPullParser.START_TAG, ns, "gml:FeatureCollection");
        while (flowParser.next() != XmlPullParser.END_DOCUMENT) {
            if (flowParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = flowParser.getName();
            // Starts by looking for the entry tag
            if (name.equals("gml:featureMember")) {
                flowOnlyEntry.add(readMarker(flowParser));
            } else {
                skip(flowParser);
            }
        }
        return flowOnlyEntry;
    }

    private FlowOnlyEntry readMarker(XmlPullParser flowParser) throws XmlPullParserException, IOException {
        flowParser.require(XmlPullParser.START_TAG, ns, "gml:featureMember");

        String flow = null;

        while (flowParser.next() != XmlPullParser.END_TAG) {
            if (flowParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = flowParser.getName();

            if (!(name.equals("wml2:Collection"))) {
                skip(flowParser);
            } else {
                flow = readFlow(flowParser);
//                Log.d("flow.........", flow);
            }
        }
        return new FlowOnlyEntry(flow);

    }

    private String readFlow(XmlPullParser flowParser) throws IOException, XmlPullParserException {

        String flow = null;

        while (flowParser.next() != XmlPullParser.END_TAG) {

            if (flowParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = flowParser.getName();

            if (name.equals("wml2:observationMember")) {

                flow = readFlow1(flowParser);
                return flow;
            } else {
                skip(flowParser);
            }
        }
        return flow;
    }

    private String readFlow1(XmlPullParser flowParser) throws IOException, XmlPullParserException {

        String flow = null;

        while (flowParser.next() != XmlPullParser.END_TAG) {

            if (flowParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = flowParser.getName();

            if (name.equals("om:OM_Observation")) {
                flow = readNextLevelFlow(flowParser);
                return flow;
            } else {
                skip(flowParser);
            }
        }
        return flow;
    }


    private String readNextLevelFlow(XmlPullParser flowParser) throws IOException, XmlPullParserException {

        String flow = null;

        while (flowParser.next() != XmlPullParser.END_TAG) {
            if (flowParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = flowParser.getName();

            if (!(name.equals("om:result"))) {
                skip(flowParser);
            } else {
                flow = readNextLevelFlow2(flowParser);
                return flow;
            }
        }
        return flow;
    }


    private String readNextLevelFlow2(XmlPullParser flowParser) throws IOException, XmlPullParserException {
        String flow = null;

        while (flowParser.next() != XmlPullParser.END_TAG) {
            if (flowParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = flowParser.getName();

            if (!(name.equals("wml2:MeasurementTimeseries"))) {
                skip(flowParser);
            } else {
                flow = readNextLevelFlow3(flowParser);
                return flow;
            }
        }
        return flow;
    }

    private String readNextLevelFlow3(XmlPullParser flowParser) throws IOException, XmlPullParserException {

        String flow = null;

        while (flowParser.next() != XmlPullParser.END_TAG) {
            if (flowParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = flowParser.getName();

            if (!(name.equals("wml2:point"))) {
                skip(flowParser);
            } else {
                flow = readNextLevelFlow4(flowParser);
                return flow;
            }
        }
        return flow;
    }

    private String readNextLevelFlow4(XmlPullParser flowParser) throws IOException, XmlPullParserException {

        String flow = null;

        while (flowParser.next() != XmlPullParser.END_TAG) {
            if (flowParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = flowParser.getName();

            if (!(name.equals("wml2:MeasurementTVP"))) {
                skip(flowParser);
            } else {
                flow = readNextLevelFlow5(flowParser);
                return flow;
            }
        }
        return flow;
    }

    private String readNextLevelFlow5(XmlPullParser flowParser) throws IOException, XmlPullParserException {
//        flowParser.require(XmlPullParser.START_TAG, ns, "wml2:value");

        String flow = null;

        while (flowParser.next() != XmlPullParser.END_TAG) {
            if (flowParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = flowParser.getName();

            if (!(name.equals("wml2:value"))) {
                skip(flowParser);
            } else {
                flow = finalFlow(flowParser);

                return flow;
            }
        }
        return flow;
    }

    private String finalFlow(XmlPullParser flowParser) throws IOException, XmlPullParserException {
        String flow = readText(flowParser);
        flowParser.require(XmlPullParser.END_TAG, ns, "wml2:value");
        return flow;
    }

    private String readText(XmlPullParser flowParser) throws IOException, XmlPullParserException {
        String result = "";
        if (flowParser.next() == XmlPullParser.TEXT) {
            result = flowParser.getText();
            flowParser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser flowParser) throws XmlPullParserException, IOException {
        if (flowParser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (flowParser.next()) {
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