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

class Head extends XML
{
    protected Vector <Metadata>   metadataTable;  // *
    protected Vector <Profile> parmetersTable; // *
    protected Styling styling; // ?
    protected Layout layout; // ?


    private Head()
    {

    }



    private void parseHead(XmlPullParser xpp) throws XmlPullParserException, IOException
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
                                Metadata metadata = Metadata.parse(xpp);
                                if(metadataTable != null && metadata != null)
                                    metadataTable.add(metadata);
                            }
                            else if(Metadata.isMetaData(name) == true)
                            {
                                Metadata metadata = Metadata.parse(xpp);
                                if(metadataTable != null && metadata != null)
                                    metadataTable.add(metadata);
                            }
                            else if(name.equals("profile") == true )  // Parameters.class
                            {
                                if(parmetersTable == null)
                                {
                                    parmetersTable = new Vector <Profile>();
                                }

                                 Profile  profile = Profile.parse(xpp);
                                 if(profile != null && parmetersTable != null)
                                     parmetersTable.add(profile);
                            }
                            else if(name.equals("styling") == true)
                            {
                                styling = Styling.parse(xpp);
                            }
                            else if(name.equals("layout") == true)
                            {
                                layout = Layout.parse(xpp);
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


    static public  Head parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        Head head = new Head();
        head.parseHead(xpp);
        return head;
    }



    public Vector<Metadata> getMetadataTable() {
        return metadataTable;
    }



    public Vector<Profile> getParmetersTable() {
        return parmetersTable;
    }



    public Styling getStyling() {
        return styling;
    }



    public Layout getLayout() {
        return layout;
    }


}
