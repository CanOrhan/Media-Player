/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.subtitle;

import java.nio.ByteBuffer;
import java.util.PriorityQueue;
import java.util.Vector;

import android.util.Log;

public class CaptionsParser 
{
    private class cc_pkt
    {
        public cc_pkt(ByteBuffer data)
        {
            byte b = data.get();
            
            this.cc_type  = (byte)(0x3 & b);
            this.cc_valid = 0x4 == (0x4 & b);
            this.marker_bits = (byte)((b >> 3) & 0x1F);
        }
        
        public final byte cc_type;
        public final boolean cc_valid;
        public final byte marker_bits;
    }
    
    private class cc_data
    {
        public final byte cc_count;
        @SuppressWarnings("unused")
        public final boolean additional_data_flag;
        public final boolean process_cc_flag;
        @SuppressWarnings("unused")
        public final boolean process_em_flag;
        @SuppressWarnings("unused")
        public final byte em_data;
        
        public cc_data(ByteBuffer buffer)
        {
            byte b = buffer.get();
            
            this.cc_count = (byte)(b & 0x1F);
            this.additional_data_flag = 0x20 == (0x20 & b);
            this.process_cc_flag = 0x40 == (0x40 & b);
            this.process_em_flag = 0x80 == (0x80 & b);
            this.em_data = buffer.get();
        }
    }
    
    private class CaptionStream
    {
    
        private boolean hasOddParity[] = {
            false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false,
            true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true,
            true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true,
            false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false,
            true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true,
            false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false,
            false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false,
            true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true,
            true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true,
            false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false,
            false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false,
            true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true,
            false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false,
            true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true,
            true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true,
            false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false};

        private StringBuilder mCaptionData = new StringBuilder();
        private PriorityQueue<SubtitleDataSet> mCaptions = new PriorityQueue<SubtitleDataSet>();
        private PriorityQueue<CaptionsFragment> mCaptionFragments = new PriorityQueue<CaptionsFragment>();
        private boolean mIsEndofCaption = false;
        private long mCurrentFragStart;
        private long mCurrentFragDuration;
        private final Object mSyncObject = new Object();

        public PriorityQueue<SubtitleDataSet> getSubtitles()
        {
            PriorityQueue<SubtitleDataSet> retVal = null;

            synchronized (mSyncObject)
            {
                if ( mCaptions.size() > 0 )
                {
                    retVal = mCaptions;
                    mCaptions = new PriorityQueue<SubtitleDataSet>();
                }
            }
            return retVal;
        }
        
        public void startNewFragment(long fragmentStartUs, long fragmentDurationUs)
        {
            mCurrentFragStart    = fragmentStartUs;
            mCurrentFragDuration = fragmentDurationUs;
        }
        
        public void processBytes( byte hi, byte lo )
        {   
            synchronized (mSyncObject)
            {
    
                ///If the second byte of a control code pair does not contain odd parity ,
                ///then the pair is ignored. The redundant transmission of the pair will be
                ///the instruction upon which the receiver acts.
                int idx = lo < 0 ? (int)(lo & 0x7F) + 0x80 : lo;
                        
                if (!hasOddParity[idx])
                {
                    return;
                }
                
                hi = (byte)(hi & 0x7F);
                lo = (byte)(lo & 0x7F);
                
                ///The first of the control code bytes is a non-printing character in the range 10h to 1Fh.
                boolean isControl = inRange( 0x10, 0x1F, hi );
                
                ///The second byte is always a printing character in the range 20h to 7Fh.
                isControl &= inRange( 0x20, 0x7F, lo );
                
                ///If the non-printing character in the pair is in the range 00h to 0Fh, that character alone will
                ///be ignored and the second character will be treated normally
                boolean isIgnorable = inRange( 0x00, 0x0F, hi );
                
                /// The first byte of  every control code pair indicates the data channel (C1/C2) to which the
                /// command applies.  Control codes which do not match the data channel selected by the user,
                /// and all subsequent data related to that control code, are ignored by the receiver.
                
                if ( isControl )
                {
                    /*
                     Skip control characters for now since the caption sample doesn't handle them anyway
                     */
                    if (   ((hi == 0x14 && lo == 0x2F) || (hi == 0x1C && lo == 0x2F))   // end of caption
                        || ((hi == 0x14 && lo == 0x25) || (hi == 0x1C && lo == 0x25))   // Rollup
                        || ((hi == 0x14 && lo == 0x26) || (hi == 0x1C && lo == 0x26))   // Rollup
                        || ((hi == 0x14 && lo == 0x27) || (hi == 0x1C && lo == 0x27)) ) // Rollup
                    {
                        mIsEndofCaption = true;
                    }
                    else if ( hi == 0x11 || hi == 0x19 ) // extended chars
                    {
                        switch (lo)
                        {
                            case 0x30:
                                mCaptionData.append('®'); // registered mark symbol
                                break;
                            case 0x31:
                                mCaptionData.append('°'); // degree sign
                                break;
                            case 0x32:
                                mCaptionData.append('½'); // 1/2
                                break;
                            case 0x33:
                                mCaptionData.append('¿'); // inverse query
                                break;
                            case 0x34:
                                mCaptionData.append('™'); // trademark symbol
                                break;
                            case 0x35:
                                mCaptionData.append('¢'); // cents sign
                                break;
                            case 0x36:
                                mCaptionData.append('£'); // Pounds Sterling sign
                                break;
                            case 0x37:
                                mCaptionData.append('♪'); // music note
                                break;
                            case 0x38:
                                mCaptionData.append('à'); // lower-case a with grave accent
                                break;
                            case 0x39:
                                mCaptionData.append(' '); // transparent space
                                break;
                            case 0x3A:
                                mCaptionData.append('è'); // lower-case e with grave accent
                                break;
                            case 0x3B:
                                mCaptionData.append('â'); // lower-case a with circumflex
                                break;
                            case 0x3C:
                                mCaptionData.append('ê'); // lower-case e with circumflex
                                break;
                            case 0x3D:
                                mCaptionData.append('î'); // lower-case i with circumflex
                                break;
                            case 0x3E:
                                mCaptionData.append('ô'); // lower-case o with circumflex
                                break;
                            case 0x3F:
                                mCaptionData.append('û'); // lower-case u with circumflex
                                break;
                        }
                    }
                    //
                    // Control characters that potentially move the cursor (so we add a space
                    // since we don't currently support UI controls).
                    //
                    else if ( hi == 0x14 || hi == 0x1C ) 
                    {
                        mCaptionData.append(' ');
                    }
                }
                else
                {
                    if (!isIgnorable)
                    {
                        processPrintCharacter(hi);
                    }
                    
                    processPrintCharacter(lo);
                }
            }
        }
    
        public void finishFragment()
        {
            synchronized (mSyncObject)
            {
                String caption = mCaptionData.toString();
                
                if ( caption.length() > 0 )
                {
                    mCaptionFragments.add( new CaptionsFragment(mCurrentFragStart, (mCurrentFragStart + mCurrentFragDuration), caption));
                    mCaptionData = new StringBuilder();
                }
                
                if ( mIsEndofCaption && mCaptionFragments.size() > 0 )
                {
                    StringBuilder sbCaption = new StringBuilder();
                    CaptionsFragment frag = mCaptionFragments.poll();
                    CaptionsFragment firstFrag;
                    CaptionsFragment lastFrag;
                    
                    firstFrag = frag;
                    lastFrag = frag;
                    
                    while (frag != null)
                    {
                        sbCaption.append( frag.getText() );
                        
                        lastFrag = frag;
                        
                        frag = mCaptionFragments.poll();
                    }
                    
                    if ( sbCaption.length() > 0 )
                    {
                        String fullCaption = sbCaption.toString();
                        // Captions appear to be about 3 seconds too late
                        final int offsetMs = -3000;
                        int startMs = (int)(firstFrag.getStartTimeUs() / 1000) + offsetMs; 
                        int endMs   = (int)(lastFrag.getEndTimeUs()    / 1000) + offsetMs;
                        
                        SubtitleDataSet set = new SubtitleDataSet(startMs, endMs, fullCaption);
                        
                        Log.w("CaptionsParser", fullCaption);
                        
                        mCaptions.add(set);
                    }
                }
                
                mIsEndofCaption = false;
                mCurrentFragDuration = 0;
                mCurrentFragStart    = -1;
            }           
        }
        
        private char dissassembleChar( byte b )
        {
            char convertedByte = (char)0x00; // placeholder for failed match
            
            switch (b)
            {
                case 0x00: convertedByte = '_'; break;
                case 0x20: convertedByte = ' '; break;
                case 0x21: convertedByte = '!'; break;
                case 0x22: convertedByte = '\"'; break;
                case 0x23: convertedByte = '#'; break;
                case 0x24: convertedByte = '$'; break;
                case 0x25: convertedByte = '%'; break;
                case 0x26: convertedByte = '&'; break;
                case 0x27: convertedByte = '\''; break;
                case 0x28: convertedByte = '('; break;
                case 0x29: convertedByte = ')'; break;
                case 0x2a: convertedByte = '*'; break;
                case 0x2b: convertedByte = '+'; break;
                case 0x2c: convertedByte = ','; break;
                case 0x2d: convertedByte = '-'; break;
                case 0x2e: convertedByte = '.'; break;
                case 0x2f: convertedByte = '/'; break;
                case 0x30: convertedByte = '0'; break;
                case 0x31: convertedByte = '1'; break;
                case 0x32: convertedByte = '2'; break;
                case 0x33: convertedByte = '3'; break;
                case 0x34: convertedByte = '4'; break;
                case 0x35: convertedByte = '5'; break;
                case 0x36: convertedByte = '6'; break;
                case 0x37: convertedByte = '7'; break;
                case 0x38: convertedByte = '8'; break;
                case 0x39: convertedByte = '9'; break;
                case 0x3a: convertedByte = ':'; break;
                case 0x3b: convertedByte = ';'; break;
                case 0x3c: convertedByte = '<'; break;
                case 0x3d: convertedByte = '='; break;
                case 0x3e: convertedByte = '>'; break;
                case 0x3f: convertedByte = '?'; break;
                case 0x40: convertedByte = '@'; break;
                case 0x41: convertedByte = 'A'; break;
                case 0x42: convertedByte = 'B'; break;
                case 0x43: convertedByte = 'C'; break;
                case 0x44: convertedByte = 'D'; break;
                case 0x45: convertedByte = 'E'; break;
                case 0x46: convertedByte = 'F'; break;
                case 0x47: convertedByte = 'G'; break;
                case 0x48: convertedByte = 'H'; break;
                case 0x49: convertedByte = 'I'; break;
                case 0x4a: convertedByte = 'J'; break;
                case 0x4b: convertedByte = 'K'; break;
                case 0x4c: convertedByte = 'L'; break;
                case 0x4d: convertedByte = 'M'; break;
                case 0x4e: convertedByte = 'N'; break;
                case 0x4f: convertedByte = 'O'; break;
                case 0x50: convertedByte = 'P'; break;
                case 0x51: convertedByte = 'Q'; break;
                case 0x52: convertedByte = 'R'; break;
                case 0x53: convertedByte = 'S'; break;
                case 0x54: convertedByte = 'T'; break;
                case 0x55: convertedByte = 'U'; break;
                case 0x56: convertedByte = 'V'; break;
                case 0x57: convertedByte = 'W'; break;
                case 0x58: convertedByte = 'X'; break;
                case 0x59: convertedByte = 'Y'; break;
                case 0x5a: convertedByte = 'Z'; break;
                case 0x5b: convertedByte = '['; break;
                case 0x5c: convertedByte = '\\'; break;
                case 0x5d: convertedByte = ']'; break;
                case 0x5e: convertedByte = '^'; break;
                case 0x5f: convertedByte = '_'; break;
                case 0x60: convertedByte = '`'; break;
                case 0x61: convertedByte = 'a'; break;
                case 0x62: convertedByte = 'b'; break;
                case 0x63: convertedByte = 'c'; break;
                case 0x64: convertedByte = 'd'; break;
                case 0x65: convertedByte = 'e'; break;
                case 0x66: convertedByte = 'f'; break;
                case 0x67: convertedByte = 'g'; break;
                case 0x68: convertedByte = 'h'; break;
                case 0x69: convertedByte = 'i'; break;
                case 0x6a: convertedByte = 'j'; break;
                case 0x6b: convertedByte = 'k'; break;
                case 0x6c: convertedByte = 'l'; break;
                case 0x6d: convertedByte = 'm'; break;
                case 0x6e: convertedByte = 'n'; break;
                case 0x6f: convertedByte = 'o'; break;
                case 0x70: convertedByte = 'p'; break;
                case 0x71: convertedByte = 'q'; break;
                case 0x72: convertedByte = 'r'; break;
                case 0x73: convertedByte = 's'; break;
                case 0x74: convertedByte = 't'; break;
                case 0x75: convertedByte = 'u'; break;
                case 0x76: convertedByte = 'v'; break;
                case 0x77: convertedByte = 'w'; break;
                case 0x78: convertedByte = 'x'; break;
                case 0x79: convertedByte = 'y'; break;
                case 0x7a: convertedByte = 'z'; break;
                case 0x7b: convertedByte = '{'; break;
                case 0x7c: convertedByte = '|'; break;
                case 0x7d: convertedByte = '}'; break;
                case 0x7e: convertedByte = '~'; break;
                default: convertedByte = (char)0x00; break;
            }
            
            return convertedByte;
        }
    
        private void processPrintCharacter( byte b )
        {
            if (b != 0x00) // filler character ignored.
            {
                char c = dissassembleChar( b );
                
                if (c != (char)0x00)
                {
                    mCaptionData.append(c);
                }
            }
        }
    
        private boolean inRange( int lowerBound, int upperBound, byte target )
        {
            return lowerBound <= target && target <= upperBound;
        }
    
    }

    private final Vector<CaptionStream> mCaptions = new Vector<CaptionStream>(4);
    
    public CaptionsParser()
    {
        for( int i=0; i<4; i++)
        {
            mCaptions.add(new CaptionStream());
        }
    }
    
    public void parse(int captionIndex, long timeOffsetUs, long durationUs, byte[] rawData, String fourCC)
    {
        if ( durationUs == 0 )
        {
            durationUs = 4000000;
        }

        parseCaptionFragment(captionIndex, timeOffsetUs, durationUs, rawData, fourCC);
    }
    
    public PriorityQueue<SubtitleDataSet> getCaptions( int captionTrackIndex )
    {
        PriorityQueue<SubtitleDataSet> retVal = null;
        
        if ( captionTrackIndex < mCaptions.size() )
        {
            retVal = mCaptions.get(captionTrackIndex).getSubtitles();
        }
        
        return retVal;
    }

    private void parseCaptionFragment( int captionIndex, long fragmentStartTimeUs, long fragmentDurationUs, byte[] fragmentData, String fourCC )
    {
        ByteBuffer fragBuffer = ByteBuffer.wrap(fragmentData);
    
        cc_data pcc = new cc_data(fragBuffer);
        
        if ( !pcc.process_cc_flag )
        {
            return;
        }
        
        int cPkts = pcc.cc_count;
        
        mCaptions.get(captionIndex).startNewFragment(fragmentStartTimeUs, fragmentDurationUs);
       
        for ( int i = 0; i < cPkts; i++ )
        {
            cc_pkt ppkt = new cc_pkt(fragBuffer);
            
            if ( !ppkt.cc_valid || ppkt.marker_bits != 0x1F )
            {
                continue;
            }
            
            CaptionStream stream = this.mCaptions.get(ppkt.cc_type);
            
            // EIA-608
            if ( ppkt.cc_type == 0 || ppkt.cc_type == 1 )
            {
                if ( captionIndex == ppkt.cc_type )
                {
                    stream.processBytes(fragBuffer.get(), fragBuffer.get());
                }
                else
                {
                    fragBuffer.get();
                    fragBuffer.get();
                }
            }
        }

        mCaptions.get(captionIndex).finishFragment();
    }
}
