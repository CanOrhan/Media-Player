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

class Block extends ExtraXML
{
    public enum BlockType
    {
        DIV,
        P
    }
    protected BlockType blockType;
    protected TTMAtribute ttmAtribute;
    protected Vector <Metadata>  metadataTable;  // *
    protected Vector <Animation> animationTable;  // *
    protected Block()
    {

    }


    static public  Block parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {

        String name = xpp.getName();
        if(name == null)
            return null;

        if(name.equals("div"))
        {
            return new Div(xpp);
        }
        else if(name.equals("p"))
        {
            return new P(xpp);
        }

        return null;
    }





    public static boolean isBlock(String name) {
        if(name.equals("div") == true ||
           name.equals("p") == true)
                return true;
            return false;
    }


    public BlockType getBlockType() {
        return blockType;
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

}


