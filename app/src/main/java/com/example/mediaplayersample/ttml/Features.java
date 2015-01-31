/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.ttml;

import java.io.IOException;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class Features extends XML
{
    protected Vector<Metadata> metadataTable;
    protected Vector<Feature> featureTable;
    private Features()
    {

    }

    public void parseFeatures(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        String startName = xpp.getName();
        parseXML(xpp);

        int eventType = xpp.next();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            switch(eventType)
            {
                case XmlPullParser.START_TAG:
                    {
                        String name = xpp.getName();
                        if(name != null)
                        {
                            if(Metadata.isMetaData(name) == true)
                            {
                                if(metadataTable == null)
                                {
                                    metadataTable = new Vector <Metadata>();
                                }

                                Metadata metadata = Metadata.parse(xpp);
                                metadataTable.add(metadata);
                            }
                            else if(name.equals("feature") == true)
                            {
                                if(featureTable == null)
                                {
                                    featureTable = new Vector <Feature>();
                                }
                                Feature feature = Feature.parse(xpp);
                                if(feature != null && featureTable != null)
                                    featureTable.add(feature);
                            }

                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    {
                        String name = xpp.getName();
                        if(name != null)
                        {
                            if(name.equals(startName) == true)
                            {
                               return;
                            }
                        }
                    }
                    break;
            }
            eventType = xpp.next();
        }
    }


    static public  Features parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        Features features = new Features();
        features.parseFeatures(xpp);
        return features;
    }

    public Vector<Metadata> getMetadataTable() {
        return metadataTable;
    }

    public Vector<Feature> getFeatureTable() {
        return featureTable;
    }
}
