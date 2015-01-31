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

import android.sax.StartElementListener;

import com.example.mediaplayersample.ttml.Inline.InlineType;

class P extends Block
{
    protected Vector <Inline> inlineTable;  // *
    protected Vector <PCDATA> textTable;
    private String onlyText;

    public P(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        super();
        blockType = BlockType.P;
        String startName = xpp.getName();
        parseExtraXML(xpp);
        ttmAtribute = (TTMAtribute) Util.getAttributeValues(xpp, NameSpace.TT_METADATA);

        int mdataIndex = -1;
        int animationIndex = -1;
        int inlineIndex =-1;
        onlyText = "";

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
                                    mdataIndex  = metadataTable.size() - 1;
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
                                    animationIndex  = animationTable.size() - 1;
                                }

                            }
                            else if(Inline.isInline(name) == true)
                            {
                                if(inlineTable == null)
                                {
                                    inlineTable = new Vector <Inline>();
                                }

                                Inline inline = Inline.parse(xpp);
                                if(inlineTable != null && inline != null)
                                {
                                    inlineTable.add(inline);
                                    inlineIndex  = inlineTable.size() - 1;

                                    if(inline.inlineType == InlineType.BR)
                                    {
                                        onlyText += "<br>";
                                    }
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

                case XmlPullParser.TEXT:
                    {
                        if(textTable == null)
                        {
                            textTable = new Vector <PCDATA>();
                        }
                        PCDATA data = new PCDATA(xpp.getText(), mdataIndex, animationIndex, inlineIndex);

                        if(textTable != null && data != null)
                        {
                            textTable.add(data);
                        }


                        onlyText += xpp.getText();

                    }
                    break;

            }
            eventType = xpp.next();
        }
    }



    public String getText()
    {
        if(onlyText == null)
            onlyText = "";

        return onlyText;
    }


    public Vector<Inline> getInlineTable() {
        return inlineTable;
    }


    public Vector<PCDATA> getTextTable() {
        return textTable;
    }

    @Override
    public String toString() {
        String s = "";
        if (begin != null || end != null) {
            s += "[";
            if (begin != null)
                s += begin;
            if (end != null)
                s += end;
            s += "] ";
        }
        if (onlyText != null) {
            s += onlyText;
        } else if (inlineTable != null){
            s += inlineTable.toString();
        } else if (textTable != null) {
            s += textTable.toString();
        } else {
            s = "<empty>";
        }

        return s;
    }
}
