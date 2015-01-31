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

class Span extends Inline
{
    protected Vector <Metadata>  metadataTable;  // *
    protected Vector <Animation> animationTable;  // *
    protected Vector <Inline>    inlineTable;  // *
    protected Span(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        super(xpp);
        inlineType = InlineType.SPAN;
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
                            else if(Inline.isInline(name) == true)
                            {
                                if(inlineTable == null)
                                {
                                    inlineTable = new Vector <Inline>();
                                }

                                Inline div = (Inline)Inline.parse(xpp);
                                if(inlineTable != null && div != null)
                                {
                                    inlineTable.add(div);
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
    public Vector<Inline> getInlineTable() {
        return inlineTable;
    }
}