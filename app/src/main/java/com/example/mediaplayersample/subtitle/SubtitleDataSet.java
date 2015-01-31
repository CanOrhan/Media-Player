/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.subtitle;

import java.util.Comparator;

public class SubtitleDataSet implements Comparable<SubtitleDataSet>
{
    private int startTimeMs = 0;
    private int endTimeMs = -1;
    private String text;

    public SubtitleDataSet(int startTimeMs, int endTimeMs, String text) {
        this.startTimeMs = startTimeMs;
        this.endTimeMs = endTimeMs;
        if(text == null)
            text ="";
        this.text = text;
    }

    public int getStartTimeMs() {
        return startTimeMs;
    }

    public int getEndTimeMs() {
        return endTimeMs;
    }

    public void setEndTimeMs(int endTimeMs) {
        this.endTimeMs = endTimeMs;
    }

    public void adjustOffsetMs(int timeOffsetMs) {
        startTimeMs += timeOffsetMs;
        endTimeMs += timeOffsetMs;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private final static Comparator<SubtitleDataSet> subtitleDataSetComparator = new Comparator<SubtitleDataSet>() {
        public int compare(SubtitleDataSet info1, SubtitleDataSet info2)
        {
            int time1 = info1.startTimeMs;
            int time2 = info2.startTimeMs;
            
            int endTime1 = info1.endTimeMs;
            int endTime2 = info2.endTimeMs;
            
            if (time1 < time2) 
            {
                if ( time2 < endTime1 )
                {
                    info1.setEndTimeMs(time2);
                }
                return -1;
            } 
            else if (time1 > time2) 
            {
                if ( time1 < endTime2 )
                {
                    info2.setEndTimeMs( time1 );
                }
                
                return 1;
            }
            return 0;
        }
    };

    public final static Comparator<SubtitleDataSet> getComparator() {
        return subtitleDataSetComparator;
    }

    private static String timestampMsToString(int timestampMs) {
        int hh = timestampMs/3600000;
        timestampMs = timestampMs % 3600000;
        int mm = timestampMs/60000;
        timestampMs = timestampMs % 60000;
        int ss = timestampMs / 1000;
        timestampMs = timestampMs % 1000;
        int uuu = timestampMs;
        return String.format("%d:%02d:%02d:%03d", hh, mm, ss, uuu);
    }

    @Override
    public String toString() {
        return "[" + timestampMsToString(startTimeMs) + "-" + timestampMsToString(endTimeMs) + "]" + text;
    }

    @Override
    public int compareTo( SubtitleDataSet arg0 )
    {
        return subtitleDataSetComparator.compare( this, arg0 );
    }
}
