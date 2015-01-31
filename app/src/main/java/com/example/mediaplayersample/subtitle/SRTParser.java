/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.subtitle;

import java.util.List;

public class SRTParser {

    public static List<TextTrack> parse(String sFile, int timeOffsetMs) {
        List<TextTrack> titleTable = SubtitleHandler.getDefaultTrack();
        String[] lines = sFile.split("\n");
        @SuppressWarnings("unused")
        int number = 0;
        int stime = 0, etime = 0;
        String text;
        SubtitleDataSet curDataSet = null;
        List<SubtitleDataSet> tempVector = TextTrack.get(titleTable, "default");


        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().equals("")) continue;
            try {
                number = Integer.parseInt(lines[i].replaceAll("[^0-9.]",""));
            } catch (Exception e) {
                continue;
            }
            boolean bAddString = true;
            try
            {
                stime = getSrtTime(lines[i + 1].trim(), true);
                etime = getSrtTime(lines[i + 1].trim(), false);
            }
            catch(Exception e) { bAddString = false;}
            text = "";
            int j = 0;
            for (j = 0; ; j++) {
                if (i + j + 2 >= lines.length) break;
                if (lines[i + j + 2].trim().equals("") && j != 0) break;
                text += lines[i + j + 2].trim() + "\n";
            }

            text = trimShape(text);

            if(bAddString) {
                text = text.replace("\n", "<br>");

                if (curDataSet != null && curDataSet.getStartTimeMs() != stime) {
                    tempVector.add(curDataSet);
                    curDataSet = null;
                }

                if (curDataSet != null) {
                    String newText = curDataSet.getText() + "<br>" + text;
                    curDataSet.setText(newText);
                } else {
                    curDataSet = new SubtitleDataSet(stime, etime, text);
                }
            }

            i = i + j + 2;
        }

        if (curDataSet != null) {   // last element
            tempVector.add(curDataSet);
        }

        return titleTable;
    }

    public static int getSrtTime(String ss, boolean flag)
    {
        String[] times = ss.split("-->");
        String[] start = times[flag ? 0 : 1].trim().split(":");
        int h = Integer.parseInt(start[0]) * 60 * 60 * 1000;
        int m = Integer.parseInt(start[1]) * 60 * 1000;
        String[] seconds = start[2].split(",");
        int s = Integer.parseInt(seconds[0]) * 1000;

        return h + m + s + Integer.parseInt(seconds[1].substring(0, 3));
    }


    static String trimShape(String ss)
    {
        try
        {
            if(ss.startsWith("m ") == true)
            {
                String[] lines = ss.split(" ");

                if(lines.length > 10)
                {
                    if(lines[1].matches("[0-9]*") == true &&
                       lines[2].matches("[0-9]*") == true &&
                       lines[4].matches("[0-9]*") == true &&
                       lines[5].matches("[0-9]*") == true &&
                       lines[6].matches("[0-9]*") == true &&
                       lines[7].matches("[0-9]*") == true)
                    {
                       return "";
                    }
                }
            }
        }catch(Exception e){}

        return ss;
    }

}
