/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.subtitle;

import java.util.List;
import java.util.Vector;

import android.util.Log;

public class SMIParser {
    private static final String TAG = SMIParser.class.getSimpleName();
    public static int CLEARTIME = 7000;

    public static List<TextTrack> parse(String ss)
    {
        List<TextTrack> titleTable = SubtitleHandler.getDefaultTrack();

        String classTag = "default";
        int pretime =0;
        int time = 0;
        try {
            ss = removeMessage(ss, "<!--", "-->" );
            ss = ss.replace("\r", "");
            StringBuffer logMessage = new StringBuffer();
            String[] lines = ss.split("\n");
            String endTag ="";
            SubtitleDataSet curDataSet = null;
            setFirstClassTag(ss.toLowerCase(), titleTable);
            for(int i = 0; i < lines.length; i++)
            {
                String[] tags = splitTag(lines[i]);

                if(tags == null)
                    continue;

                for(int j =0 ; j < tags.length ; j++)
                {
                    if(tags[j].trim().startsWith("<") == true)
                    {
                        String tag = tags[j].toLowerCase().trim();
                        tag = tag.replace("\"", "");
                        tag = tag.replace("\'", "");
                        if(tag.contains("<sync start") == true)
                        {

                            if(logMessage.length() > 0)
                            {
                                List<SubtitleDataSet> tempVector = TextTrack.get(titleTable, classTag);
                                if(tempVector != null)
                                {
                                    if(pretime != 0 && pretime + CLEARTIME < time)
                                    {
                                        if (curDataSet == null) {
                                            Log.w(TAG, "end-time is earlier than start time");
                                            curDataSet = new SubtitleDataSet(0, -1, "");
                                        }

                                        curDataSet.setEndTimeMs(pretime + CLEARTIME);
                                        tempVector.add(curDataSet);
                                        curDataSet = null;
                                    }

                                    if (curDataSet != null) {
                                        curDataSet.setEndTimeMs(time);
                                        tempVector.add(curDataSet);
                                        curDataSet = null;
                                    }

                                    logMessage.append(endTag);
                                    endTag ="";

                                    curDataSet = new SubtitleDataSet(time, -1, logMessage.toString());
                                    pretime = time;
                                }
                            }
                            try
                            {
                                String timeString = tag.toLowerCase().substring(tag.indexOf("=") + 1, tag.length() -1).trim();

                                int endIndex = timeString.indexOf("end");
                                if(endIndex > 0)
                                    timeString = timeString.substring(0, endIndex).trim();

                                if(timeString.endsWith("ms"))
                                {
                                    timeString = timeString.substring(0, timeString.length() - 2);
                                }
                                int curtime = Integer.parseInt(timeString);
                                time = curtime;
                            }catch(Exception e)
                            {
                                time += 500;
                            }


                            logMessage = new StringBuffer();    // reset buffer
                        }
                        else if(tag.contains("<p class") == true)
                        {
                            try
                            {
                                int index = tag.indexOf("=");
                                if(index > 0)
                                {
                                    classTag = tag.toLowerCase().substring(index + 1, tag.length() -1).trim();
                                }
                                else
                                {
                                    classTag = "default";
                                }

                                List<SubtitleDataSet> tempVector = TextTrack.get(titleTable, classTag);
                                if(tempVector == null)
                                {
                                    tempVector = new Vector<SubtitleDataSet>();
                                    TextTrack.put(titleTable, classTag, tempVector);
                                }
                            }catch(Exception e){}
                        }
                        else if(tag.contains("<smi>") == true
                                || tag.contains("<sami>") == true
                                || tag.contains("</sami>") == true
                                || tag.contains("<head>") == true
                                || tag.contains("</head>") == true
                                || tag.contains("<title>") == true
                                || tag.contains("</title>") == true
                                || tag.contains("<body>") == true
                                || tag.contains("</body>") == true
                                || tag.contains("<style") == true
                                || tag.contains("</style>") == true)
                        {
                            break; // next tag
                        }
                        else if(tag.contains("<font") == true)
                        {
                            try
                            {
                                int index = tag.indexOf("=");
                                if(index > 0)
                                {
                                    String colorTag = tag.substring(index + 1, tag.length() -1).trim();
                                    HtmlColor color = HtmlColor.fromString(colorTag);
                                    if(color != null)
                                    {
                                        tag = tag.replace(colorTag, "#" +  Integer.toHexString(color.getInt()));
                                    }

                                    logMessage.append(tag);
                                    endTag += addEndTag(tag);
                                }
                            }catch(Exception e){}
                        }
                        else if(tag.contains("<br>") == true)
                        {
                            logMessage.append("<br>");
                        }
                        else
                        {
                            logMessage.append(tag);
                            endTag += addEndTag(tag);
                        }
                    }
                    else
                    {
                        logMessage.append(tags[j]);
                    }
                }
            } // end of for loop

            logMessage.append(endTag);

            List<SubtitleDataSet> tempVector = TextTrack.get(titleTable, classTag);
            if(tempVector != null)
            {
                if(pretime != 0 && pretime + CLEARTIME < time)
                {
                    if (curDataSet == null) {
                        Log.w(TAG, "end-time is earlier than start time");
                        curDataSet = new SubtitleDataSet(0, -1, "");
                    }

                    curDataSet.setEndTimeMs(pretime + CLEARTIME);
                    tempVector.add(curDataSet);
                    curDataSet = null;
                }
                tempVector.add(new SubtitleDataSet(time, 0x7fffffff, logMessage.toString()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return titleTable;
    }



    private static String[] splitTag(String message)
    {

        Vector <String> tempVector = new Vector<String> ();
        StringBuffer temp = new StringBuffer();

        int index = 0;
        boolean bFirst = false;

        for(int i = 0 ; i < message.length(); i++)
        {
            if(message.charAt(i) == '<')
            {
                if(bFirst == true)
                {
                    tempVector.add(temp.toString());
                    temp = new StringBuffer();  // reset
                    index++;
                }
                else
                {
                    bFirst = true;
                }
                temp.append(message.charAt(i));
            }
            else if(message.charAt(i) == '>')
            {
                temp.append(message.charAt(i));
                index++;
                tempVector.add(temp.toString());
                temp = new StringBuffer();  // reset
            }
            else
            {
                temp.append(message.charAt(i));
            }

        }
        if(temp.length() > 0)
        {
            tempVector.add(temp.toString());
            temp = new StringBuffer();  // reset
        }

        String[] outMessage = new String[tempVector.size()];
        for(int i = 0; i< tempVector.size(); i++)
        {
            outMessage[i] = tempVector.get(i);
        }

        return outMessage;
    }

    private static void setFirstClassTag(String message, List<TextTrack> titleTable)
    {
        int startIndex = message.indexOf("<p class");
        startIndex = message.indexOf("=", startIndex);
        int endIndex = message.indexOf(">", startIndex);

        String classTag = message.substring(startIndex + 1, endIndex);
        List<SubtitleDataSet> tempVector = TextTrack.get(titleTable, classTag);
        if(tempVector == null)
        {
            tempVector = new Vector<SubtitleDataSet>();
            TextTrack.put(titleTable, classTag, tempVector);
        }
    }

    private static String removeMessage(String message, String startMessage, String endMessage)
    {
        String outMessage = "";
        String[] lines =  message.split(startMessage);

        for(int i = 0 ; i < lines.length; i++)
        {
            int lastIndex = lines[i].indexOf(endMessage);
            if(lastIndex > 0)
                  outMessage += lines[i].substring(lastIndex + endMessage.length());
            else
                outMessage += lines[i];
        }

        return outMessage;
    }


    private static String addEndTag(String message)
    {

        if(message.length() <= 1)
        {
            return "";
        }

        String outMessage =  "";
        int lastIndex = message.indexOf(" ");
        if(lastIndex < 0)
        {
            return "";
        }
        outMessage =  "</" + message.substring(1, lastIndex) + ">";
        return outMessage;
    }
}
