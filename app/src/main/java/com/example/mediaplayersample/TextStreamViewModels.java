package com.example.mediaplayersample;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import android.util.Pair;

import com.example.mediaplayersample.TextStreamHandler.ITextListener;
import com.example.mediaplayersample.adapters.interfaces.IFragmentInfoAdapter;
import com.example.mediaplayersample.adapters.interfaces.IMediaDescriptionAdapter;
import com.example.mediaplayersample.adapters.interfaces.IMediaRepresentationAdapter;
import com.example.mediaplayersample.adapters.interfaces.IMediaStreamAdapter;
import com.example.mediaplayersample.adapters.interfaces.IPRMediaPlayerAdapter;
import com.example.mediaplayersample.subtitle.CaptionsParser;
import com.example.mediaplayersample.subtitle.Subtitle;
import com.example.mediaplayersample.subtitle.SubtitleDataSet;

public class TextStreamViewModels
{   
    //
    // Interface that a View can use to poll a subtitle or caption stream
    //
    public interface ITextSource
    {
        void EnableSource() throws Exception;
        
        String GetTextForTime(long timeMS);
        
        void DisableSource() throws Exception;
    }
    
    //
    // Private interface used to enable parsing of
    // different types of subtitle or caption streams.
    //
    private interface ITextParser
    {
        PriorityQueue<SubtitleDataSet> Parse(
                IMediaRepresentationAdapter textStream,
                IFragmentInfoAdapter info,
                byte[] rawFragmentData);
    }
    
    //
    // Internal class that uses the TextStreamHandler class
    // and an ISubtitleOrCaptionParser to collect text information
    //
    private static class TextSourcePump
    {
        private final PriorityQueue<SubtitleDataSet> mSubtitles = new PriorityQueue<SubtitleDataSet>();
        private final Object mSubtitleLock = new Object();
        
        private TextStreamHandler mTextStreamHandler = null;
        private IPRMediaPlayerAdapter mMediaPlayer = null;
        private IMediaRepresentationAdapter mTargetRepresentation = null;
        
        private ITextParser mParser = null;
        
        private ITextListener mTextStreamListener = new ITextListener()
        {
            
            @Override
            public void onText ( IMediaRepresentationAdapter textStream,
                    IFragmentInfoAdapter info, byte[] rawFragmentData )
            {
                synchronized( mSubtitleLock )
                {
                    PriorityQueue<SubtitleDataSet> newSubtitles = mParser.Parse( textStream, info, rawFragmentData );
                    if( (newSubtitles != null) && (newSubtitles.size() > 0))
                    {
                        mSubtitles.addAll( newSubtitles );
                    }
                }
            }
        };
        
        public TextSourcePump( 
                IPRMediaPlayerAdapter mediaPlayer,
                IMediaRepresentationAdapter representation,
                ITextParser parser )
        {
            mMediaPlayer = mediaPlayer;
            mTargetRepresentation = representation;
            mParser = parser;
        }
        
        public void StartPump () throws Exception
        {
            synchronized( mSubtitleLock )
            {
                if( mTextStreamHandler != null )
                {
                    mTextStreamHandler.stop();
                    mTextStreamHandler = null;
                }
                
                mSubtitles.clear();
                
                mTextStreamHandler = new TextStreamHandler();
                mTextStreamHandler.start( mMediaPlayer, mTargetRepresentation );
                mTextStreamHandler.addTextListener( mTextStreamListener );
            }
        }

        public void StopPump () throws Exception
        {
            synchronized( mSubtitleLock )
            {
                if( mTextStreamHandler != null )
                {
                    mTextStreamHandler.removeTextListener( mTextStreamListener );
                    mTextStreamHandler.stop();
                    mTextStreamHandler = null;
                }
                
                mSubtitles.clear();
            }
            
        }
        
        public String GetSubtitlesOrCaptionForTime ( long timeMS )
        {
            String currSubtitle = "";

            synchronized( mSubtitleLock )
            {
                if (mSubtitles.size() > 0)
                {
                    SubtitleDataSet nextSubtitle = mSubtitles.peek();
    
                    while( ( nextSubtitle != null ) && ( nextSubtitle.getEndTimeMs() < timeMS ) )
                    {
                        //
                        // Remove the old caption
                        //
                        mSubtitles.poll();
    
                        //
                        // Just peek for current captions because the timer handler requires the active
                        // subtitle to be in the front of the queue
                        //
                        nextSubtitle = mSubtitles.peek();
                    }
    
                    if( ( nextSubtitle != null ) &&
                        ( nextSubtitle.getStartTimeMs() <= timeMS ) &&
                        ( timeMS < nextSubtitle.getEndTimeMs() ) )
                    {
                        
                       currSubtitle = nextSubtitle.getText();
                    }
                } 
            }
        
            return currSubtitle;
        }
    }
    
    //
    // Implementation
    //
    private static class CaptionStream implements ITextSource
    {
        private CaptionsParser mCaptionsParser = new CaptionsParser();
        private TextSourcePump mCaptionPump = null;
        
        private int mCaptionIndex = 0;
        
        private ITextParser mParser = new ITextParser()
        {
            @Override
            public PriorityQueue< SubtitleDataSet > Parse (
                    IMediaRepresentationAdapter textStream,
                    IFragmentInfoAdapter info, byte[] rawFragmentData )
            {
                String fourCC = textStream.getFourCC();
                PriorityQueue<SubtitleDataSet> newSubtitles = null;

                mCaptionsParser.parse(
                    mCaptionIndex,
                    info.getStartTimeInUs(),
                    info.getDurationInUs(),
                    rawFragmentData,
                    fourCC);

                newSubtitles = mCaptionsParser.getCaptions( mCaptionIndex );
                
                return newSubtitles;
            }
        };
               
        CaptionStream( 
                IPRMediaPlayerAdapter mp, 
                IMediaRepresentationAdapter captionRepresentation, 
                int captionIndex )
        {
            mCaptionIndex = captionIndex;
            mCaptionPump = new TextSourcePump( mp, captionRepresentation, mParser);
        }

        @Override
        public void EnableSource () throws Exception
        {
            mCaptionPump.StartPump();
        }

        @Override
        public String GetTextForTime ( long timeMS )
        {
            return mCaptionPump.GetSubtitlesOrCaptionForTime( timeMS );
        }

        @Override
        public void DisableSource () throws Exception
        {
            mCaptionPump.StopPump();
        }
    }
    
    private static class SubtitleStream implements ITextSource
    {
        private TextSourcePump mSubtitlePump = null;
        
        private ITextParser mParser = new ITextParser()
        {
            @Override
            public PriorityQueue< SubtitleDataSet > Parse (
                    IMediaRepresentationAdapter textStream,
                    IFragmentInfoAdapter info, byte[] rawFragmentData )
            {
                PriorityQueue<SubtitleDataSet> newSubtitles = new PriorityQueue<SubtitleDataSet>( );
                
                Subtitle subtitle = new Subtitle((Subtitle.ParserOptions)null);
                subtitle.append(rawFragmentData, (int) info.getStartTimeInMs());
                List< SubtitleDataSet > newSubtitleList = subtitle.getSubtitles( 0 );
                
                if( newSubtitleList != null )
                {
                    newSubtitles.addAll( newSubtitleList );
                }
                
                return newSubtitles;
            }
        };
        
        SubtitleStream( 
                IPRMediaPlayerAdapter mp, 
                IMediaRepresentationAdapter textRepresentation )
        {
            mSubtitlePump = new TextSourcePump( mp, textRepresentation, mParser );
        }

        @Override
        public void EnableSource () throws Exception
        {
            mSubtitlePump.StartPump();
        }

        @Override
        public String GetTextForTime ( long timeMS )
        {
            return mSubtitlePump.GetSubtitlesOrCaptionForTime( timeMS );
        }

        @Override
        public void DisableSource () throws Exception
        {
            mSubtitlePump.StopPump();
        }
    }
    
    public static ArrayList< Pair< String, ITextSource > > identifyTextStreams( 
            IPRMediaPlayerAdapter mediaPlayer )
    {
        IMediaDescriptionAdapter mpd = mediaPlayer.getMediaDescription();
        
        ArrayList< Pair< String, ITextSource > > streams = 
                new ArrayList< Pair< String, ITextSource > >();
        
        streams.add( new Pair< String, ITextSource >("No Subtitles", null) );
        
        for( int i = 0 ; i < mpd.getStreamCount() ; i++ )
        {
            IMediaStreamAdapter stream = mpd.getStreamAt( i );
            
            switch( stream.getStreamType() )
            {
            case METADATA:
                {
                    IMediaRepresentationAdapter rep = stream.getMediaRepresentationAt( 0 );
                    String fourCC = rep.getFourCC();
                    
                    if ( fourCC.equalsIgnoreCase( "atsc" ) ||
                         fourCC.equalsIgnoreCase( "scte" ) ||
                         fourCC.equalsIgnoreCase( "dvb1" ) ||
                         fourCC.equalsIgnoreCase( "dvd1" ) )
                    {
                        //
                        // Caption streams are assumed to have 4 Closed Captioning
                        // channels
                        //
                        
                        streams.add( new Pair< String, ITextSource >( 
                                stream.getName() + " CC1", 
                                new CaptionStream( mediaPlayer, rep, 0 ) ) );
                        
                        streams.add( new Pair< String, ITextSource >( 
                                stream.getName() + " CC2", 
                                new CaptionStream( mediaPlayer, rep, 1 ) ) );
                        
                        streams.add( new Pair< String, ITextSource >( 
                                stream.getName() + " CC3", 
                                new CaptionStream( mediaPlayer, rep, 2 ) ) );
                        
                        streams.add( new Pair< String, ITextSource >( 
                                stream.getName() + " CC4", 
                                new CaptionStream( mediaPlayer, rep, 3 ) ) );
                    }
                    
                    break;
                }
            case TEXT:
                {
                    IMediaRepresentationAdapter rep = stream.getMediaRepresentationAt( 0 );
                    
                    streams.add( new Pair< String, ITextSource >( 
                            stream.getName(), 
                            new SubtitleStream( mediaPlayer, rep ) ) );
                    
                    break;
                }
            default:
                break;
            
            }
        }
        
        return streams;
    }
}
