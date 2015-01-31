/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.subtitle;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.example.mediaplayersample.ttml.TTMLParser;

public class SubtitleHandler
{

    static public int CLEARTIME = 7000;

    List<TextTrack> titleTable;
    String mEncoding = "Default";
    Subtitle.Format mType = Subtitle.Format.INVALID;
    boolean bAutoDetected = false;
    boolean mSubtitleMode = false;
    int mTimeOffsetMs = 0;

    int mStartPosition = -1;
    int mEndPosition = -1;
    int mTextPosition = -1;

    public SubtitleHandler(Subtitle.Format type, String subtitleInfo, String encoding, int timeOffsetMs)
    {
        mStartPosition = -1;
        mEndPosition = -1;
        mTextPosition = -1;

        this.mType = type;
        this.mEncoding = encoding;
        this.mSubtitleMode = false;
        mTimeOffsetMs = timeOffsetMs;
        bAutoDetected = false;

        switch(mType)
        {
        case SRT:
            break;
        case ASS:
            {
                String[] lines = subtitleInfo.split("\n");
                String event = null;;
                int i = 0;
                for(i = 0; i < lines.length; i++)
                {
                    if (lines[i].trim().equals("")) continue;
                    if(lines[i].trim().compareToIgnoreCase("[Events]") == 0)
                    {
                        event = lines[i + 1].trim();
                        break;
                    }
                }
                if(event != null)
                {
                    event = event.substring(event.indexOf(":") +1);
                    String[] events = event.split(",");
                    for(int j = 0; j < events.length; j++)
                    {
                        String token = events[j].trim();
                        if(token.compareToIgnoreCase("Start") == 0)
                        {
                            mStartPosition = j;
                        }
                        else if(token.trim().compareToIgnoreCase("End") == 0)
                        {
                            mEndPosition = j;
                        }
                        else if(token.trim().compareToIgnoreCase("Text") == 0)
                        {
                            mTextPosition = j;
                        }
                    }
                }
            }
            break;
        }
    }


    public SubtitleHandler(Subtitle.Format type, String encoding, boolean subtitleMode, int timeOffsetMs)
    {
        titleTable = new ArrayList<TextTrack>();

        this.mEncoding = encoding;
        this.mType = type;
        this.mSubtitleMode = subtitleMode;
        mTimeOffsetMs = timeOffsetMs;
        bAutoDetected = false;
    }

    public void clear()
    {
        if(titleTable != null)
            titleTable.clear();

    }

    byte[] buffer = null;

    public static Subtitle.Format checkType(String ss, Subtitle.Format defaultType)
    {
        Subtitle.Format type = Subtitle.Format.INVALID;
        try
        {
            String[] lines = ss.split("\n");
            for(int i = 0; i < lines.length; i++)
            {
                String line = lines[i].toLowerCase().trim();
                if(line.startsWith("[script info]"))
                {
                    type = Subtitle.Format.ASS;
                    break;
                }
                else if(line.startsWith("[events]"))
                {
                    type = Subtitle.Format.ASS;
                    break;
                }
                else if(line.startsWith("<sami>"))
                {
                    type = Subtitle.Format.SMI;
                    break;
                }
                else if(line.startsWith("<sync start="))
                {
                    type = Subtitle.Format.SMI;
                    break;
                }
                else if(line.contains("-->"))
                {
                    try
                    {
                        SRTParser.getSrtTime(line, true);
                        SRTParser.getSrtTime(line, false);
                        type = Subtitle.Format.SRT;
                        break;
                    }
                    catch (Exception e){}
                }
                else if(i > 30)
                {
                    break;
                }
            }

            if (type == Subtitle.Format.INVALID)
            {
                // new Sniffing interface
                if (TTMLParser.sniff(ss))
                {
                    type = Subtitle.Format.TTML;
                }
            }
        }catch(Exception e)
        {
            type = Subtitle.Format.INVALID;
        }

        return type;
    }

    public Subtitle.Format parse(byte[] buffer)
    {
        Subtitle.Format ret = Subtitle.Format.SMI;
        clear();

        if(bAutoDetected == false)
        {
            String sample;
            try {
                sample = new String(buffer, 0, buffer.length, mEncoding);
                mType = checkType(sample, mType);
            } catch (UnsupportedEncodingException e) {

            }
            bAutoDetected = true;
        }

        if(mType == Subtitle.Format.INVALID)
            return Subtitle.Format.INVALID;

        if (mType == Subtitle.Format.SMI) {
              String ss = null;
              try {
                  if (mEncoding.equals("Default")) {
                      ss = new String(buffer, 0, buffer.length, "UTF-8");
                  } else {
                      ss = new String(buffer, 0, buffer.length, mEncoding);
                  }
              } catch (UnsupportedEncodingException e2) {
                  return Subtitle.Format.INVALID;
              }

              titleTable = SMIParser.parse(ss);

        }
        else if (mType == Subtitle.Format.SRT) {
            String sFile;
            try {
                sFile = new String(buffer, mEncoding);
            } catch (UnsupportedEncodingException e) {
                return ret;
            }

            titleTable = SRTParser.parse(sFile, mTimeOffsetMs);

        }
        else if(mType == Subtitle.Format.ASS)
        {
            String sFile;
            try {
                sFile = new String(buffer, mEncoding);
            } catch (UnsupportedEncodingException e) {
                return ret;
            }
            titleTable = AdvSubStationAlphaParser.parse(sFile, mTimeOffsetMs);
            if(titleTable == null) {
                return Subtitle.Format.INVALID;
            }

        }
        else if(mType == Subtitle.Format.TTML)
        {
            TTMLParser ttmParser = new TTMLParser();
            ByteArrayInputStream bs = new ByteArrayInputStream(buffer);

            boolean bRet = ttmParser.parse(bs);
            if(bRet == false)
                return Subtitle.Format.INVALID;

            titleTable = ttmParser.createSubtitleDataSetList(mTimeOffsetMs);
            if(titleTable == null)
            {
                return Subtitle.Format.INVALID;
            }
        }

        if (titleTable.size() > 1) {
            TextTrack tt = titleTable.get(0);
            if (tt.mID.equals("default") && tt.mDataSetList.size() == 0) {
                titleTable.remove(0);
            }
        }

        return ret;
    }

    private void convertLegacyDataSetList() {
        for (int trackIndex=0; trackIndex<titleTable.size(); trackIndex++) {
            TextTrack track = titleTable.get(trackIndex);
            List<SubtitleDataSet> newList = new ArrayList<SubtitleDataSet>();

            List<SubtitleDataSet> subtitleList = track.mDataSetList;
            int numSubtitles = subtitleList.size();

            SubtitleDataSet toAdd = null;
            for (int i=0; i<numSubtitles; i++) {
                SubtitleDataSet cur = subtitleList.get(i);

                if (toAdd != null) {
                    toAdd.setEndTimeMs(cur.getStartTimeMs());
                    newList.add(toAdd);
                    toAdd = null;
                }

                if (cur.getText().equals(""))
                    continue;

                if (cur.getEndTimeMs() == -1) {
                    toAdd = cur;
                } else {
                    newList.add(cur);
                }
            }
            if (toAdd != null) {
                if (toAdd.getEndTimeMs() == -1)
                    toAdd.setEndTimeMs(0x7fffffff);

                newList.add(toAdd);
            }

            track.mDataSetList = newList;
        }

    }

    public List<TextTrack> getData() {
        if (titleTable == null)
            return new ArrayList<TextTrack>();

        return titleTable;
    }

    public static List<TextTrack> getDefaultTrack() {
        List<TextTrack> titleTable = new ArrayList<TextTrack>();
        List<SubtitleDataSet> defaultVector = new Vector<SubtitleDataSet>();
        TextTrack.put(titleTable, "default", defaultVector);
        return titleTable;
    }
}



