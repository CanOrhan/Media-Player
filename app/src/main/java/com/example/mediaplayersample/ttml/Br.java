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

class Br extends Inline
{
    protected Vector <Metadata>  metadataTable;  // *
    protected Vector <Animation> animationTable;  // *
    protected Br(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        super(xpp);
        inlineType = InlineType.BR;
        String startName = xpp.getName();
        parseExtraXML(xpp);
        ttmAtribute = (TTMAtribute) Util.getAttributeValues(xpp, NameSpace.TT_METADATA);

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

                                if(metadataTable != null && metadata != null)
                                {
                                    metadataTable.add(metadata);
                                }
                            }
                            else if(Animation.isAnimation(name) == true)
                            {
                                if(animationTable == null)
                                {
                                    animationTable = new Vector <Animation>();
                                }

                                Animation animation = Animation.parse(xpp);
                                if(animationTable != null && animation != null)
                                {
                                    animationTable.add(animation);
                                }

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
    public Vector<Metadata> getMetadataTable() {
        return metadataTable;
    }
    public Vector<Animation> getAnimationTable() {
        return animationTable;
    }

}