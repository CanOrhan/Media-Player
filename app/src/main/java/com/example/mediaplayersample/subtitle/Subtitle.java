/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.subtitle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

/**
 * Subtitle class parses subtitle data and provides text of given timestamp.
 *
 * <p>Subtitle class supports SMI, SRT and ASS formats and supports charset of UTF-8, UTF-16, UTF-16LE.
 *
 * <p><b>Initializing</b>
 *
 * <p>First, load your subtitle data to byte array or create {@link java.io.InputStream} from your subtitle source.
 *
 * <p>Create instance of the Subtitle.ParserOptions class.  It may be null or not required if subtitle format is SMI and encoded as UTF-8.
 * If your format is SRT or ASS, use {@link com.example.mediaplayersample.subtitle.Subtitle.ParserOptions#setFormat(com.example.mediaplayersample.subtitle.Subtitle.Format)} method to change the parser options.
 *
 * <p>Create the instance of the Subtitle class.
 * To check if the subtitle is successfully loaded, call {@link #getFormat()} method.
 * If the subtitle is successfully loaded it will return {@link com.example.mediaplayersample.subtitle.Subtitle.Format#INVALID}.
 * Otherwise, it returns the format of the subtitle data.
 *
 * <p>Call {@link #getClassTags()} method to get class tags.  If the subtitle format is SMI,  It returns the array of the language class names such as ENCC, FRFRCC, ....
 * For the other formats, it returns the String array that has one element "default".
 *
 * <p>This is the sample code for parsing the subtitle data
 * <p><pre><code>
 * byte[] rawSubtitle = ...;    // download or read the subtitle file.  use InputStream instead
 *
 * Subtitle.ParserOptions options = new Subtitle.ParserOptions();
 * options.setFormat(Subtitle.Format.SRT);
 *
 * Subtitle subtitle = new Subtitle(rawSubtitle, options);
 * Subtitle.Format format = subtitle.getFormat();
 * if (type == Subtitle.Format.INVALID)
 *   return; // failed to load
 *
 * String[] classTags = subtitle.getClassTags();
 * if (classTags.length == 0)
 *   return;  // fail.
 *
 * int languageIndex = 0;   // select your language if classTags.length is larger than 2.
 * tag = classTags.[languageIndex];
 * </code></pre>
 *
 * <p><b>Playback</b>
 * <p>{@link #getCurrentSubtitle} returns the subtitle text of a given playback position.
 * The playback position should be the return value of MediaPlayer.getCurrentPosition() method.
 *
 * <p><pre><code>
 * long curTimeMs = mMediaPlayer.getCurrentPosition();   // get position in millisecond from MediaPlayer instance
 * String currentSubtitle = subtitle.getCurrentSubtitle(tag, curTimeMs);
 * if (currentSubtitle == null)
 *   currentSubtitle = " ";
 * </code></pre>
 */
public class Subtitle {
    private final static String TAG = Subtitle.class.getSimpleName();
    /**
     * Describes format of the subtitle
     */
    public enum Format {
        /** Automatically determines the subtitle format.
         */
        AUTO_DETECT(-1),
        /** Invalid format.
         */
        INVALID(0),
        /** SMI, SAMI(Microsoft Synchronized Accessible Media Interchange) format.
         * <p>see <a href="http://msdn.microsoft.com/en-us/library/ms971327.aspx">http://msdn.microsoft.com/en-us/library/ms971327.aspx</a>
         */
        SMI(1),
        /** SRT(SubRip text) format
         */
        SRT(2),
        /** ASS(Advanced SubStation Alpha) format
         */
        ASS(3),
        /** TTML(Timed Text Makup Language) format
         */
        TTML(4);

        private int mVal;

        Format(int val) {
            mVal = val;
        }

        /**
         * Returns the integer value.
         * @return the integer value of {@link com.example.mediaplayersample.subtitle.Subtitle.Format}.
         */
        public int toInt() { return mVal; }

        /**
         * @param v     the integer value.
         * @return      {@link com.example.mediaplayersample.subtitle.Subtitle.Format} value of the integer value
         */
        public static Format fromInt(int v) {
            switch (v) {
            case -1:    return AUTO_DETECT;
            case 0:     return INVALID;
            case 1:     return SMI;
            case 2:     return SRT;
            case 3:     return ASS;
            case 4:     return TTML;
            default:    return INVALID;
            }
        }

        public static Format fromMimeType(String mimeType) {
            if (mimeType.equals("text/smi"))
                return SMI;
            if (mimeType.equals("application/x-subrip"))
                return SRT;
            else if (mimeType.equals("application/x-ass"))
                return ASS;
            else if (mimeType.equals("application/ttml+xml"))
                return TTML;
            else
                return INVALID;
        }
    }

    public static final String CHARSET_DEFAULT = "Default";

    private Format mFormat = Format.INVALID;
//    Hashtable<String, Vector<Track>> mSubtitleList;
    List<TextTrack> mSubtitleList;
    ParserOptions mParserOptions = null;

    /**
     * @param rawSubtitle   byte array for the subtitle data
     */
    public Subtitle(byte[] rawSubtitle) {
        init((ParserOptions)null);

        append(rawSubtitle, 0);
    }

    /**
     * @param rawSubtitle   InputStream for the subtitle data
     * @throws java.io.IOException
     */
    public Subtitle(InputStream rawSubtitle) throws IOException {
        init((ParserOptions)null);

        byte[] buf = null;
        buf = getBytesFromInputStream(rawSubtitle);
        append(buf, 0);
    }

    /**
     * @param rawSubtitle   byte array for the subtitle data
     * @param options       options used to parsing the subtitle
     */
    public Subtitle(byte[] rawSubtitle, ParserOptions options) {
        init(options);

        append(rawSubtitle, 0);
    }

    public Subtitle(ParserOptions options) {
        init(options);
    }

    /**
     * @param rawSubtitle   InputStream for the subtitle data
     * @param options       options used to parsing the subtitle
     * @throws java.io.IOException
     */
    public Subtitle(InputStream rawSubtitle, ParserOptions options) throws IOException{
        init(options);

        byte[] buf = null;
        buf = getBytesFromInputStream(rawSubtitle);
        append(buf, 0);
    }

    private void init(ParserOptions options) {
        mSubtitleList = new ArrayList<TextTrack>();
        if (options == null)
            mParserOptions = new ParserOptions();
        else
            mParserOptions = options.clone();
    }

    public void append(InputStream rawSubtitle) throws IOException {
        byte[] buf = null;
        buf = getBytesFromInputStream(rawSubtitle);
        append(buf, 0);
    }

    public void append(byte[] rawSubtitle, int timeOffsetMs) {
        if (rawSubtitle == null)
            return;

        appendInternal(rawSubtitle, timeOffsetMs);
    }

    private void appendInternal(byte[] rawSubtitle, int timeOffsetMs) {
        // Charset ?? ????? ?? ??? ???? ????.
        if (mParserOptions.mCharset == null) {
            Charset charset = guessCharset(rawSubtitle);
            if (charset == null)
                return;
            mParserOptions.mCharset = charset;
        }

        String charset;
        if (mParserOptions.mCharset == null)
            charset = "Default";
        else
            charset = mParserOptions.mCharset.name();
        SubtitleHandler subtitleHandler = new SubtitleHandler(mParserOptions.mFormat, charset, false, timeOffsetMs);
        mFormat = subtitleHandler.parse(rawSubtitle);

        List<TextTrack> subtitleList;
        subtitleList = subtitleHandler.getData();
        //Log.d(TAG, "append: @" + timeOffsetMs + ", " + subtitleListToString(subtitleList));
        appendSubtitleList(subtitleList);
    }

    private String subtitleListToString(List<TextTrack> subtitleList) {
        String ret = "";
        Iterator<TextTrack> itr = subtitleList.iterator();
        while(itr.hasNext()) {
            TextTrack track = itr.next();
            ret += "Track: " + track.mID;
            Iterator<SubtitleDataSet> sub = track.mDataSetList.iterator();
            while (sub.hasNext()) {
                SubtitleDataSet item = sub.next();

                ret += "\n | " + item.toString();
            }
        }
        return ret;
    }

    private void appendSubtitleList(List<TextTrack> list) {
        if (mSubtitleList.size() == 0) {
            mSubtitleList = list;
            return;
        }

        for (TextTrack track : list) {
            String trackID = track.mID;
            List<SubtitleDataSet> matchingDataSet = TextTrack.get(mSubtitleList, trackID);
            List<SubtitleDataSet> srcList = TextTrack.get(list, trackID);
            if (matchingDataSet != null) {
                List<SubtitleDataSet> dstList = TextTrack.get(mSubtitleList, trackID);
                joinSubtitleList(dstList, srcList);
            } else {
                TextTrack.put(mSubtitleList, trackID, srcList);
            }
        }
    }

    private static void joinSubtitleList(List<SubtitleDataSet> dst, List<SubtitleDataSet> src) {
        dst.addAll(src);
        Collections.sort(dst, SubtitleDataSet.getComparator());
    }

    /**
     * check if the given subtitle is valid
     * @return true if the subtitle is valid, otherwise false
     */
    public boolean isValid() {
        if (mFormat == Format.INVALID)
            return false;
        else
            return true;
    }

    /**
     * Returns the format of the subtitle
     * @return the format of the subtitle
     * @see com.example.mediaplayersample.subtitle.Subtitle.Format
     */
    public Format getFormat() {
        return mFormat;
    }

    /**
     * Returns the language tag.
     *
     * <p> If the subtitle format is SMI this method returns the names class tag that have language code.
     * If the subtitle format is SRT or ASS, getClassTags returns string array that has "default" element.
     *
     * <p>{@link #getClassTags()} does not return null.
     *
     * @return array of the language tags
     */
    public String[] getClassTags() {
        String[] tags = new String[mSubtitleList.size()];

        Iterator<TextTrack> itr = mSubtitleList.iterator();
        int index = 0;
        while (itr.hasNext()) {
            tags[index++] = itr.next().mID;
        }

        return tags;
    }

    /**
     * Returns the subtitle for given language code and playback position.
     * @param classTag language code
     * @param currentTimeMs current playback position in millisecond
     * @return the subtitle
     */
    public String getCurrentSubtitle(String classTag, long currentTimeMs) {
        List<SubtitleDataSet> dataset = TextTrack.get(mSubtitleList, classTag);
        return getSubtitleInternal(dataset, currentTimeMs);
    }

    public String getCurrentSubtitle(int trackIndex, long currentTimeMs) {
        List<SubtitleDataSet> dataset = mSubtitleList.get(trackIndex).mDataSetList;
        return getSubtitleInternal(dataset, currentTimeMs);
    }

    private String getSubtitleInternal(List<SubtitleDataSet> datasetList, long currentTimeMs) {
        int size = datasetList.size();
        for (int i=0; i<size; i++) {
            SubtitleDataSet data = datasetList.get(i);
            if (data.getStartTimeMs() <= currentTimeMs && currentTimeMs < data.getEndTimeMs()) {
                String text = data.getText();
                if (text == null)
                    return "";
                return text;
            }
        }
        return "";
    }

    @SuppressWarnings("unused")
    private String getSubtitleInternal_old(List<SubtitleDataSet> datasetList, long currentTimeMs) {
        int curTime = (int) currentTimeMs;
        int min = 0, max = datasetList.size() - 1 - 1;
        int mid;
        while (min <= max){
            mid = (min + max) / 2;
            if (datasetList.get(mid+1).getStartTimeMs() < curTime )
                min = mid + 1;
            else if (curTime < datasetList.get(mid).getStartTimeMs())
                max = mid - 1;
            else {  // found
                String title = datasetList.get(mid).getText();
                // check EndTime
                if (currentTimeMs >= datasetList.get(mid).getEndTimeMs())
                    return "";
                return title;
            }
        }
        return "";
    }

    /**
     * Returns all subtitles for a given language code.
     * @param classTag language code
     * @return list of subtitles
     */
    public List<SubtitleDataSet> getSubtitles(String classTag) {
        List<SubtitleDataSet> datasetList = TextTrack.get(mSubtitleList, classTag);
        if (datasetList == null || datasetList.size() == 0) {
            if (mSubtitleList.size() > 0) {
                datasetList = mSubtitleList.get(0).mDataSetList;
            }
        }

        return datasetList;
    }

    /**
     * Returns all subtitles for a given track Index
     * @param trackIndex
     * @return list of subtitles
     */
    public List<SubtitleDataSet> getSubtitles(int trackIndex) {

        List<SubtitleDataSet> datasetList = null;
        if( ( mSubtitleList != null ) && ( mSubtitleList.size() > 0) )
        {
            TextTrack subtitleTrack = mSubtitleList.get(trackIndex);
            if ( subtitleTrack != null )
            {
                datasetList = mSubtitleList.get(trackIndex).mDataSetList;
            }
        }

        if (datasetList == null || datasetList.size() == 0) {
            if (mSubtitleList.size() > 0) {
                datasetList = mSubtitleList.get(0).mDataSetList;
            }
        }

        return datasetList;
    }

    /**
     * Guess the charset of a given subtitle.
     *
     * <p>This method detects UTF-8, UTF-16, UTF-16LE.
     *
     * @param rawSubtitle subtitle
     * @return charset
     */
    private static Charset guessCharset(byte[] rawSubtitle) {
        Charset guessedCharset = null;
        try {
            guessedCharset = CharsetToolkit.guessEncoding(rawSubtitle, 4096<rawSubtitle.length ? 4096 : rawSubtitle.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (guessedCharset == null)
            return null;
        else
            return guessedCharset;
    }

    /**
     * Used as a parameter of the constructor of {@link com.example.mediaplayersample.subtitle.Subtitle} class.
     */
    public static class ParserOptions{
        Charset mCharset;
        Format mFormat;

        /**
         * Create class instance in default attributes.
         * <p>If the charset is not set, auto-detect the charset.
         * <p>The default format is {@link com.example.mediaplayersample.subtitle.Subtitle.Format#AUTO_DETECT}.
         */
        public ParserOptions() {
            mCharset = null;  // auto-detect
            mFormat = Format.AUTO_DETECT;
        }

        /**
         * Set charset.
         * @param charset charset
         */
        public void setCharset(Charset charset) {
            mCharset = charset;
        }

        /**
         * Set format
         * @param format the format of the subtitle
         * @see com.example.mediaplayersample.subtitle.Subtitle.Format
         */
        public void setFormat(Format format) {
            mFormat = format;
        }

        @Override
        public ParserOptions clone() {
            ParserOptions c = new ParserOptions();
            c.mCharset = mCharset;
            c.mFormat = mFormat;
            return c;
        }
    }

    private static byte[] getBytesFromInputStream(InputStream is) throws IOException {
        int len;
        int size = 1024;
        byte[] buf;
        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1)
                bos.write(buf, 0, len);
            buf = bos.toByteArray();
        }
        return buf;
    }

    public void dump() {
        String toDump = subtitleListToString(mSubtitleList);
        Log.d(TAG, toDump);
    }
}
