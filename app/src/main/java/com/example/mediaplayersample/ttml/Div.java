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

class Div extends Block
{
    protected Vector <Block> blockTable;  // *

    public Div(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        super();
        blockType = BlockType.DIV;
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
                            else if(Block.isBlock(name) == true)
                            {
                                if(blockTable == null)
                                {
                                    blockTable = new Vector <Block>();
                                }
                                Block div = Block.parse(xpp);
                                blockTable.add(div);
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

    public Vector<Block> getBlockTable() {
        return blockTable;
    }
}