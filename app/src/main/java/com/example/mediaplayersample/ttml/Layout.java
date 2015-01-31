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

class Layout extends XML
{

    protected Vector<Metadata> metadataTable;
    protected Vector<Region> regionTable;
    private Layout()
    {

    }


    private void parseLayout(XmlPullParser xpp) throws XmlPullParserException, IOException
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
                            if(name.equals("metadata") == true)
                            {
                                if(metadataTable == null)
                                {
                                    metadataTable = new Vector <Metadata>();
                                }
                            }
                            else if(Metadata.isMetaData(name) == true)
                            {
                                Metadata metadata = Metadata.parse(xpp);
                                if(metadataTable != null && metadata != null)
                                    metadataTable.add(metadata);
                            }
                            else if(name.equals("region") == true )  // Parameters.class
                            {
                                if(regionTable == null)
                                {
                                    regionTable = new Vector <Region>();
                                }

                                Region  region = Region.parse(xpp);
                                 if(region != null && regionTable != null)
                                     regionTable.add(region);
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


    static public  Layout parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        Layout layout = new Layout();

        layout.parseLayout(xpp);

        return layout;
    }


    public Vector<Metadata> getMetadataTable() {
        return metadataTable;
    }


    public Vector<Region> getRegionTable() {
        return regionTable;
    }

}
