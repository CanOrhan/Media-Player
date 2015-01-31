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

class Styling extends XML
{
    protected Vector<Metadata> metadataTable;
    protected Vector<Style> styleTable;

    private Styling()
    {

    }


    private void parseStyling(XmlPullParser xpp) throws XmlPullParserException, IOException
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
                            else if(name.equals("style") == true )  // Parameters.class
                            {
                                if(styleTable == null)
                                {
                                    styleTable = new Vector <Style>();
                                }

                                Style  style = Style.parse(xpp);
                                 if(style != null && styleTable != null)
                                     styleTable.add(style);
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


    static public  Styling parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        Styling styling = new Styling();

        styling.parseStyling(xpp);

        return styling;
    }


    public Vector<Metadata> getMetadataTable() {
        return metadataTable;
    }


    public Vector<Style> getStyleTable() {
        return styleTable;
    }
}
