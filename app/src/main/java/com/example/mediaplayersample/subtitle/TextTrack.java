/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.subtitle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TextTrack {
    String mID;
    List<SubtitleDataSet> mDataSetList;

    public TextTrack(String id) {
        mID = id;
        mDataSetList = new ArrayList<SubtitleDataSet>();
    }

    public void put(List<SubtitleDataSet> data) {
        mDataSetList = data;
    }

    public static List<SubtitleDataSet> get(List<TextTrack> list, String classTag) {
        for (TextTrack track : list) {
            if (track.mID.equals(classTag))
                return track.mDataSetList;
        }
        return null;
    }

    public static void put(List<TextTrack> list, String classTag, List<SubtitleDataSet> dataSetList) {
        for (TextTrack track : list) {
            if (track.mID.equals(classTag)) {
                track.mDataSetList = dataSetList;
                return;
            }
        }

        TextTrack newTrack = new TextTrack(classTag);
        newTrack.mDataSetList = dataSetList;
        list.add(newTrack);
    }

    @Override
    public String toString() {
        String s = "";
        s += "ID: " + mID + ", size=" + mDataSetList.size() + "\n";

        Iterator<SubtitleDataSet> itr = mDataSetList.iterator();
        while (itr.hasNext()) {
            SubtitleDataSet sds = itr.next();
            s += sds + "\n";
        }
        return s;
    }
}
