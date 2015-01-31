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

import com.example.mediaplayersample.ttml.Body.TimeContainer;

class Region extends ExtraXML
{
    protected Vector <Metadata>  metadataTable;  // *
    protected Vector <Animation> animationTable;  // *
    protected Vector<Style> styleTable; // *
    protected String role;
    protected String styleID;

    private Region()
    {

    }

    public void parseRegionAtribute(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        parseXML(xpp);
        ttsAtribute = (TTSAtribute)Util.getAttributeValues(xpp, NameSpace.TT_STYLE);
        int count = xpp.getAttributeCount();
        for(int i = 0; i < count ; i++)
        {
            String name = xpp.getAttributeName(i);
            if(name != null)
            {
                if(name.equals("begin") == true)
                {
                    begin = TimeExpression.parse(xpp.getAttributeValue(i));
                }
                else if(name.equals("dur") == true)
                {
                    dur = TimeExpression.parse(xpp.getAttributeValue(i));
                }
                else if(name.equals("end") == true)
                {
                    end = TimeExpression.parse(xpp.getAttributeValue(i));
                }
                else if(name.equals("style") == true)
                {
                    styleID = xpp.getAttributeValue(i);
                }
                else if(name.equals("role") == true)
                {
                    role = xpp.getAttributeValue(i);
                }
                else if(name.equals("timeContainer") == true)
                {
                    String value = xpp.getAttributeValue(i);
                    if(value != null)
                    {
                        if(value.equals("par") == true)
                        {
                            timeContainer = TimeContainer.PAR;
                        }
                        else if(value.equals("seq") == true)
                        {
                            timeContainer = TimeContainer.SEQ;
                        }
                    }
                }
            }
        }
    }

    public void parseRegion(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        String startName = xpp.getName();
        parseRegionAtribute(xpp);

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
                            else if(Animation.isAnimation(name) == true)
                            {
                                if(animationTable == null)
                                {
                                    animationTable = new Vector <Animation>();
                                }

                                Animation animation = Animation.parse(xpp);
                                animationTable.add(animation);
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


    static public  Region parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        Region region = new Region();
        region.parseRegion(xpp);
        return region;
    }

    public Vector<Metadata> getMetadataTable() {
        return metadataTable;
    }

    public Vector<Animation> getAnimationTable() {
        return animationTable;
    }

    public Vector<Style> getStyleTable() {
        return styleTable;
    }

    public String getRole() {
        return role;
    }

    public String getStyleID() {
        return styleID;
    }

}
