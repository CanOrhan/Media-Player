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

class Body extends ExtraXML
{

    protected TTMAtribute ttmAtribute;

    protected Vector <Metadata>  metadataTable;  // *
    protected Vector <Animation> animationTable;  // *
    protected Vector <Div> divTable;  // *

    enum TimeContainer
    {
        PAR,
        SEQ
    }

    private Body()
    {

    }


    private void parseBody(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        String startName = xpp.getName();
        parseExtraXML(xpp);

        ttmAtribute = (TTMAtribute)Util.getAttributeValues(xpp, NameSpace.TT_METADATA);

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
                            else if(name.equals("div") == true)
                            {
                                if(divTable == null)
                                {
                                    divTable = new Vector <Div>();
                                }
                                Div div = (Div)Block.parse(xpp);
                                divTable.add(div);
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


    static public  Body parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        Body body = new Body();

        body.parseBody(xpp);

        return body;
    }


    public TTMAtribute getTtmAtribute() {
        return ttmAtribute;
    }


    public Vector<Metadata> getMetadataTable() {
        return metadataTable;
    }


    public Vector<Animation> getAnimationTable() {
        return animationTable;
    }


    public Vector<Div> getDivTable() {
        return divTable;
    }

}
