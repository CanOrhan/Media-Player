/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.subtitle;

import java.util.List;

public class AdvSubStationAlphaParser {
    public static List<TextTrack> parse(String sFile, int timeOffsetMs) {

        String[] lines = sFile.split("\n");
        int startPosition = -1;
        int endPosition = -1;
        int textPosition = -1;
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
                    startPosition = j;
                }
                else if(token.trim().compareToIgnoreCase("End") == 0)
                {
                    endPosition = j;
                }
                else if(token.trim().compareToIgnoreCase("Text") == 0)
                {
                    textPosition = j;
                }
            }
        }

        if(startPosition == -1 || endPosition == -1 || textPosition == -1) {
            return null;
        }

        List<TextTrack> titleTable = SubtitleHandler.getDefaultTrack();
        List<SubtitleDataSet> tempVector = TextTrack.get(titleTable, "default");
        SubtitleDataSet curDataSet = null;

        for( i = i + 2; i <  lines.length; i++)
        {

            int stime = -1;
            int etime = -1;
            String text = "";
            if (lines[i].trim().equals("")) continue;
            if(lines[i].trim().startsWith("Dialogue") == false) continue;
            String message[] = lines[i].trim().split(",");
            for(int j = 0 ; j < message.length; j++)
            {
                if(j == startPosition) {
                    stime = getASSTime(message[j].trim());
                } else if (j == endPosition) {
                    etime = getASSTime(message[j].trim());
                } else if (j == textPosition) {
                    try {

                        String temp = "";
                        for(int k = textPosition; k < message.length; k++)
                        {
                            temp += message[k];
                            if(k != message.length - 1)
                                temp += ", ";
                        }
                        temp = trimShape(temp);
                        text = getASSTrimString(temp.trim());
                    }catch(Exception e){ text = "";}

                    break;
                }
            }

            if(stime >= 0) {
                text = text.replace("\n", "<br>");

                if (curDataSet != null && curDataSet.getStartTimeMs() != stime) {
                    if (curDataSet.getEndTimeMs() == -1)
                        curDataSet.setEndTimeMs(stime);
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
        }

        return titleTable;
    }

    private static int getASSTime(String ss)
    {
        //0:00:03.38;
        String[] start = ss.split(":");
        int h = Integer.parseInt(start[0]) * 60 * 60 * 1000;
        int m = Integer.parseInt(start[1]) * 60 * 1000;
        String[] seconds = start[2].split("\\.");
        int s = Integer.parseInt(seconds[0]) * 1000;
        int ms = Integer.parseInt(seconds[1].replaceAll("[^0-9.]","")) * 10;
        return h + m + s + ms;
    }

    private static String getASSTrimString(String ss)
    {
        boolean skipTag = false;
        StringBuffer messsage = new StringBuffer();
        for(int i = 0; i < ss.length() ; i++)
        {
            if(ss.charAt(i) == '{')
            {
                skipTag = true;
            }
            else if(i + 1 < ss.length() && ss.charAt(i) == '\\' && ss.charAt(i +1 ) == 'N')
            {
                messsage.append("\n");
                i++;
            }
            else if(i + 1 < ss.length() && ss.charAt(i) == '\\' && ss.charAt(i +1 ) == 'h')
            {
                i++;
            }
            else if(ss.charAt(i) == '}')
            {
                skipTag = false;
            }
            else
            {
                if(skipTag == false)
                {
                    messsage.append(ss.charAt(i));
                }
            }
        }

        return messsage.toString();
    }

    private static String trimShape(String ss)
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
