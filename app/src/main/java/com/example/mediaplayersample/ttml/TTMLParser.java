/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.ttml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.example.mediaplayersample.subtitle.SubtitleDataSet;
import com.example.mediaplayersample.subtitle.TextTrack;
import com.example.mediaplayersample.ttml.Block.BlockType;

public class TTMLParser
{
    static final String TTM_UNKNOW = "Unknown";
    public TTML ttml;
    public TTMLParser()
    {
    }

    public boolean parse(InputStream stream)
    {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(stream, null);
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                switch(eventType)
                {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String name = xpp.getName();
                        if(name != null && name.equals("tt"))
                        {
                            ttml = TTML.parse(xpp);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        break;
                }
                eventType = xpp.next();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public Vector<Region> getRegionTable()
    {
        if(ttml == null || ttml.getHead() == null)
            return null;

        Head head = ttml.getHead();
        if(head.layout == null)
        {
            return null;
        }

        return head.layout.regionTable;
    }


    public Vector<Style> getStyleTable()
    {
        if(ttml == null || ttml.getHead() == null)
            return null;

        Head head = ttml.getHead();
        if(head.styling == null)
        {
            return null;
        }

        return head.styling.styleTable;
    }

    public Region getRegion(String regionID)
    {
        Vector<Region> regionTable  = getRegionTable();
        if(regionTable == null)
            return null;

        for(int i = 0 ; i < regionTable.size(); i++)
        {
            Region r = regionTable.get(i);
            if(r == null || r.id == null)
                continue;

            if(r.id.equals(regionID))
            {
                return r;
            }
        }
        return null;
    }


    public Style getStyle(String styleID)
    {
        Vector<Style> styleTable  = getStyleTable();
        if(styleTable == null)
            return null;

        for(int i = 0 ; i < styleTable.size(); i++)
        {
            Style s = styleTable.get(i);
            if(s == null || s.id == null)
                continue;

            if(s.id.equals(styleID))
            {
                return s;
            }
        }
        return null;
    }

    private Vector <Div> getDivTable()
    {
        if(ttml == null || ttml.getBody() == null)
            return null;

        Body body = ttml.getBody();
        return body.divTable;
    }

    public String getDivLanguage(Div div)
    {
        String language = null;
        if(div == null)
            return language;


        language = div.getLanguge();
        if(language != null)
        {
            return language;
        }

        if(div.region != null)
        {
            Region r = getRegion(div.region);
            if(r == null)
            {
                return null;
            }

            language = r.getLanguge();
            if(language != null)
            {
                return language;
            }

            if(r.styleID != null)
            {
                Style s = getStyle(r.styleID);
                if(s != null)
                {
                    language = s.getLanguge();
                    if(language != null)
                    {
                        return language;
                    }
                }
            }
        }

        return language;
    }


    public Vector<String> getLanguages()
    {
       if(ttml == null || ttml.getBody() == null)
           return null;

       Vector<String> languageTable = new Vector<String>();

       String language = null;

       Body body = ttml.getBody();

       Vector <Div> divTable = body.divTable;
       if(divTable != null && divTable.size() > 0)
       {
           for(int i =0 ; i < divTable.size(); i++)
           {
               language = getDivLanguage(divTable.get(i));
               if(language != null && languageTable != null)
               {
                   languageTable.add(language);
               }
           }
       }


       if(languageTable.size() == 0)
       {
           language = body.getLanguge();
           if(language!= null)
           {
               languageTable.add(language);
           }

           language = ttml.getLanguge();
           if(language != null)
           {
               languageTable.add(language);
           }
           else
           {
               languageTable.add(TTM_UNKNOW);
           }
       }

       return languageTable;
    }
    private void convertDivTable(List<SubtitleDataSet> subtitleDataSetTable, Div div)
    {
        if(div == null || div.blockTable == null || div.blockTable.size() == 0)
        {
            return;
        }


        Vector <Block> blockTable = div.blockTable;
        if(blockTable == null)
        {
            return;
        }

        String curString ="";
        long curStartTime = -1;
        long curEndTime = -1;

        long divBeginOffset = div.getBeginClockTimsMS();
        long divLastOffset = div.getLastClockTimsMS();

        for(int i = 0; i < blockTable.size(); i++)
        {
            Block block = blockTable.get(i);
            if(block == null)
            {
                continue;
            }

            if(block.blockType == BlockType.DIV)
            {
                convertDivTable(subtitleDataSetTable,  (Div)block);
                continue;
            }

            P p = (P)block;

            if(curStartTime == -1)
            {
                curStartTime = p.getBeginClockTimsMS() + divBeginOffset;
                curEndTime = p.getLastClockTimsMS() + divBeginOffset + divLastOffset;
                if(curEndTime <= curStartTime)
                    curEndTime = curStartTime + ExtraXML.CLEARTIME;
            }

            if(curStartTime >= 0 && curStartTime != (p.getBeginClockTimsMS() + divBeginOffset))
            {
                subtitleDataSetTable.add(new SubtitleDataSet((int)curStartTime, (int)curEndTime, curString));
                curStartTime = p.getBeginClockTimsMS() + divBeginOffset;
                curEndTime = p.getLastClockTimsMS() + divBeginOffset + divLastOffset;
                if(curEndTime <= curStartTime)
                    curEndTime = curStartTime + ExtraXML.CLEARTIME;
                curString = p.getText();
            }
            else
            {
                curString += p.getText();
            }
        }

        if(curStartTime > 0)
        {
            subtitleDataSetTable.add(new SubtitleDataSet((int)curStartTime, (int)curEndTime, curString));
        }
    }

    public  List<SubtitleDataSet> createSubtitleDataSet(String language)
    {
        List<SubtitleDataSet> subtitleDataSetTable = new ArrayList<SubtitleDataSet>();


        Vector <Div> divTable = getDivTable();
        if(divTable == null)
        {
            return null;
        }

        String lang = null;
        for(int i= 0 ;i < divTable.size(); i++)
        {
            lang = getDivLanguage(divTable.get(i));
            if(lang != null)
            {
                if(language.equals(lang))
                {
                    Div div = divTable.get(i);
                    convertDivTable(subtitleDataSetTable, div);
                }
            }
            else if(language.equals(TTM_UNKNOW))
            {
                Div div = divTable.get(i);
                convertDivTable(subtitleDataSetTable, div);
            }
        }

        if(language != null && language.length() > 0 && subtitleDataSetTable.size() == 0)
        {
            for(int i= 0 ;i < divTable.size(); i++)
            {
                lang = getDivLanguage(divTable.get(i));
                if(lang == null)
                {
                    Div div = divTable.get(i);
                    convertDivTable(subtitleDataSetTable, div);
                }
            }
        }

        if(subtitleDataSetTable.size() == 0)
        {
            subtitleDataSetTable = null;
            return subtitleDataSetTable;
        }

        Collections.sort(subtitleDataSetTable, SubtitleDataSet.getComparator());

        return subtitleDataSetTable;
    }


    public List<TextTrack> createSubtitleDataSetList(int timeOffsetMs)
    {

        Vector<String> langTable = getLanguages();

        if(langTable == null || langTable.size() <= 0)
        {
            return null;
        }

        Vector <Div> divTable = getDivTable();
        if(divTable == null)
        {
            return null;
        }

        List <TextTrack> dataTable = new ArrayList <TextTrack>();

        for(int i = 0; i < langTable.size(); i++)
        {
            String lang = langTable.get(i);

            List<SubtitleDataSet> dataSetTable = createSubtitleDataSet(lang);
            if(dataSetTable != null)
            {
                Iterator<SubtitleDataSet> itr = dataSetTable.iterator();
                while (itr.hasNext()) {
                    SubtitleDataSet ds = itr.next();
                    ds.adjustOffsetMs(timeOffsetMs);
                }

                TextTrack.put(dataTable, lang, dataSetTable);
            }
        }

        if(dataTable.size() == 0)
        {
            dataTable = null;
        }

        return dataTable;
    }

    public static boolean sniff(String ss) {
        BufferedReader sr = null;
        boolean tt_found = false;
        boolean xmlns_found = false;
        boolean namespace_found = false;
        int lineNum = 0;
        try {
            sr = new BufferedReader(new StringReader(ss));
            String line;
            while ((line=sr.readLine()) != null) {
                if (line.contains("<tt "))
                    tt_found = true;

                if (line.contains("xmlns"))
                    xmlns_found = true;
                if (xmlns_found
                    && (line.contains("http://www.w3.org/ns/ttml")
                        || line.contains("http://www.w3.org/2006/04/ttaf1")
                        || line.contains("http://www.w3.org/2006/10/ttaf1")
                    )) {
                    namespace_found = true;
                }

                if (tt_found && namespace_found) {
                    return true;
                }

                if (lineNum++ > 30)
                    return false;
            }
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (sr != null)
                    sr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}



