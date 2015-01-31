/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import com.example.mediaplayersample.adapters.interfaces.IFragmentFetchDataListenerAdapter;
import com.example.mediaplayersample.adapters.interfaces.IFragmentFetchDataTaskAdapter;
import com.example.mediaplayersample.adapters.interfaces.IFragmentInfoAdapter;
import com.example.mediaplayersample.adapters.interfaces.IFragmentIteratorAdapter;
import com.example.mediaplayersample.adapters.interfaces.IMediaRepresentationAdapter;
import com.example.mediaplayersample.adapters.interfaces.IPRMediaPlayerAdapter;

import android.util.Log;

/**
 * This class show one method of handling captions and subtitles for both Video on Demand
 * and Live streaming scenarios. It functions entirely through listener callbacks.
 *
 * By default the text stream Hander will start at the current player time,
 * and sequentially download every fragment until it reaches the end
 * of the stream, at which point it will wait for new fragments to be
 * added (as notified by the OnStreamUpdate callback).
 *
 * At any time the Text handler can be paused/unpaused, which allows
 * consumers to gate, or regulate how far ahead in the stream they receive
 * new fragments.
 */
public class TextStreamHandler implements
            IPRMediaPlayerAdapter.OnSeekCompleteListener,
            IFragmentFetchDataListenerAdapter
{
    /**
     *
     * Interface for Listeners Called any time a new Text fragment is donwloaded.
     */
    public interface ITextListener
    {
        /**
         * Called any time a new Text fragment is downloaded.
         * @param TextStream the TextInfo stream the fragment belongs to
         * @param info FragmentInfo struct for the just-downloaded fragment, indicating time and duration
         * @param rawFragmentData the raw byte array of the just-downloaded fragment.
         */
        public void onText(IMediaRepresentationAdapter Texteam, IFragmentInfoAdapter info, byte[] rawFragmentData);
    }

    /**
     * Enum describing the states of the TextHandler
     * (also used internally to track those states)
     */
    public enum TextHandlerState
    {
        NOT_STARTED,
        FETCHING_FRAGMENT,
        WAITING_FOR_NEW_FRAGMENTS,
        PAUSED,
        STOPPED,
        EXCEPTION
    };

    private class TextParserTask extends TimerTask
    {
        @Override
        public void run() 
        {
            synchronized ( mLockObj )
            {
                if (mCurrentState == TextHandlerState.WAITING_FOR_NEW_FRAGMENTS)
                {
                    try 
                    {
                        startNextFragmentFetch();
                    } 
                    catch (Exception e) 
                    {
                        // eat exception during polling
                    }
                }
            }
        }
    }


    private final ArrayList<ITextListener> mListeners = new ArrayList<ITextListener>();

    private IPRMediaPlayerAdapter mMediaPlayer = null;
    private IMediaRepresentationAdapter mTextRepresentation = null;

    private IFragmentIteratorAdapter mFragmentIter = null;
    private long mFirstFragmentStartTime = 0;
    private long mLastFragmentEndTime = 0;
    private IFragmentFetchDataTaskAdapter mCurrentFragmentFetchTask = null;

    private final Object mLockObj = new Object();
    private TextHandlerState mCurrentState = TextHandlerState.NOT_STARTED;
    private Exception mFinalException = null;
    
    private Timer mTextTimer = null;
    private boolean mIsPollingIterator = false;

    /**
     * Constructor (Empty)
     */
    public TextStreamHandler( ) {}

    /**
     * Starts the TextHander into the loop of downloading fragments.
     *
     * @param mp MediaPlayer to attach to and use as a reference for current playback position.
     * @param textRepresentation the textStream whose fragments are to be downloaded.
     * @throws IllegalStateException if called after previously started
     * @throws Exception may throw an exception if there is an error looking up the
     *      TextStream or retrieving the fragmentInfo for the very first fragment.
     */
    public void start( final IPRMediaPlayerAdapter mp, final IMediaRepresentationAdapter textRepresentation ) throws Exception
    {
        synchronized ( mLockObj )
        {
            if( mCurrentState == TextHandlerState.NOT_STARTED )
            {
                Log.d( "TextHandler", "Starting" );

                mMediaPlayer = mp;
                mTextRepresentation = textRepresentation;

                //
                // These listeners will drive the TextHandler to respond to seeks and stream updates.
                //
                mMediaPlayer.addOnSeekCompleteListener( this );

                String fourCC = textRepresentation.getFourCC();

                if ( fourCC.equalsIgnoreCase( "atsc" ) || 
                     fourCC.equalsIgnoreCase( "scte" ) || 
                     fourCC.equalsIgnoreCase( "dvb1" ) || 
                     fourCC.equalsIgnoreCase( "dvd1" ) )
                {
                    mIsPollingIterator = true;
                }

                //
                // Kick off the first fragment fetch
                //
                startFragmentFetchAtCurrentTime();
            }
            else
            {
                throw new IllegalStateException("TextHandler can not be re-used, please create a new instance");
            }
        }

    }

    /**
     * Stops the Text Handler once started (the Text handler should not be restarted,
     * after calling stop() please create a new Text Handler).
     *
     * @throws IllegalStateException if called before the Text Handler is started
     * @throws Exception will re-throw any exceptions that might have caused the TextHandler to
     *          cease downloading fragments.
     */
    public void stop() throws Exception
    {
        synchronized ( mLockObj )
        {
            if( mCurrentState != TextHandlerState.STOPPED )
            {
                Log.d( "TextHandler", "Stopping" );

                stopCurrentFragmentFetch();

                mMediaPlayer.removeOnSeekCompleteListener( this );

                if ( mCurrentState == TextHandlerState.EXCEPTION )
                {
                    try
                    {
                        throw mFinalException;
                    }
                    catch( InterruptedException ix )
                    {
                        //
                        // Ignore, expected if the download task was canceled mid-download
                        //
                    }
                    catch( CancellationException cx )
                    {
                        //
                        // Also Ignore, another potential result of canceling download tasks
                        //
                    }
                    catch( ExecutionException ee )
                    {
                        //
                        // If the download task actually failed (not just canceled), it will
                        // wrap the real exception up with an ExecutionException. Unwrap
                        // and throw the root if possible.
                        //
                        Throwable cause = ee.getCause();
                        if( ( cause != null ) && ( cause instanceof Exception ) )
                        {
                            throw ( Exception )cause;
                        }
                        else
                       {
                            throw ee;
                       }
                    }
                }

            }
            else
            {
                throw new IllegalStateException("TextHandler can not be re-used, please create a new instance");
            }
        }
    }

    /**
     * Pauses the Text Handler, causing it to stop downloading fragments until Unpause is called.
     *
     * @throws IllegalStateException if called on a TextHander that is stopped, not yet started, or
     *  paused, or otherwise in a bad state.
     */
    public void pause()
    {
        synchronized ( mLockObj )
        {
            if( ( mCurrentState == TextHandlerState.FETCHING_FRAGMENT ) ||
                ( mCurrentState == TextHandlerState.WAITING_FOR_NEW_FRAGMENTS ) )
            {
                Log.d( "TextHandler", "pausing" );

                if ( mTextTimer != null )
                {
                    mTextTimer.cancel();
                    mTextTimer.purge();
                    mTextTimer = null;
                }

                mCurrentState = TextHandlerState.PAUSED;
            }
            else
            {
                throw new IllegalStateException("TextHandler can not be paused at this time.");
            }
        }
    }

    /**
     * Unpauses the Text handler, causing it to resume downloading fragments. When unpaused
     * the FragmentHandler will attempt to determine if the associated MediaPlayer has seeked or
     * played past it's most recently downloaded fragment, and will resync if needed,
     * otherwise it will resume downloading fragments where it left off.
     *
     * @throws IllegalStateException if called on a TextHander that is stopped, not yet started
     * or otherwise in a bad state.
     */
    public void unpause()
    {
        synchronized ( mLockObj )
        {
            switch ( mCurrentState )
            {

            case FETCHING_FRAGMENT:
            case WAITING_FOR_NEW_FRAGMENTS:
                //
                // do nothing
                //
                break;

            case PAUSED:
                try
                {
                    Log.d( "TextHandler", "unpausing" );

                    long currentTime = mMediaPlayer.getCurrentPosition();
                    if ( ( currentTime < mFirstFragmentStartTime ) ||
                         ( currentTime > mLastFragmentEndTime ) )
                    {
                        startFragmentFetchAtCurrentTime();
                    }
                    else
                    {
                        startNextFragmentFetch();
                    }
                }
                catch ( Exception e )
                {
                    Log.w( "TextHandler", "Exception occured resuming after a pause", e );
                    mFinalException = e;
                    mCurrentState = TextHandlerState.EXCEPTION;
                }
                break;

            default:
                throw new IllegalStateException("TextHandler can not be unpaused at this time.");

            }
        }
    }

    /**
     * Forces the TextHandler to re-sync to the current mediaPlayers position, discarding
     * all previously downloaded fragments and start downloading them from the new position.
     *
     * This should never really be needed, but is avaiable just in case.
     *
     * @throws IllegalStateException if called on a TextHander that is stopped, not yet started,
     * paused, or otherwise in a bad state.
     */
    public void resync()
    {
        synchronized ( mLockObj )
        {
            if( ( mCurrentState == TextHandlerState.FETCHING_FRAGMENT ) ||
                ( mCurrentState == TextHandlerState.WAITING_FOR_NEW_FRAGMENTS ) )
            {
                Log.d( "TextHandler", "resyncing" );

                try
                {

                    startFragmentFetchAtCurrentTime();
                }
                catch ( Exception e )
                {
                    Log.w( "TextHandler", "Exception occured resuming after a pause", e );
                    mFinalException = e;
                    mCurrentState = TextHandlerState.EXCEPTION;
                }
            }
            else
            {
                throw new IllegalStateException("TextHandler can not resync at this time.");
            }
        }
    }

    /**
     * Returns the current state of the Text handler
     * (you may want to use one of the helper functions below)
     * @return the current state of the Text handler
     */
    public TextHandlerState getState()
    {
        return mCurrentState;
    }

    /**
     * Returns true if the Text handler was started
     * and has not yet been stopped or encountered an error
     * @return Returns true if the Text handler is running or paused
     */
    public boolean isRunning()
    {
        switch (mCurrentState)
        {
        case FETCHING_FRAGMENT:
        case WAITING_FOR_NEW_FRAGMENTS:
        case PAUSED:
            return true;
        case EXCEPTION:
        case NOT_STARTED:
        case STOPPED:
        default:
            return false;

        }
    }

    /**
     * Returns true if the Text handler is paused
     * @return true if the Text handler is paused
     */
    public boolean isPaused()
    {
        switch (mCurrentState)
        {
        case PAUSED:
            return true;
        case FETCHING_FRAGMENT:
        case WAITING_FOR_NEW_FRAGMENTS:
        case EXCEPTION:
        case NOT_STARTED:
        case STOPPED:
        default:
            return false;

        }
    }

    public void addTextListener( ITextListener listener )
    {
        synchronized (this)
        {
            mListeners.add(listener);
        }
    }

    public void removeTextListener( ITextListener listener )
    {
        synchronized (this)
        {
            mListeners.remove(listener);
        }
    }

    //
    // Private Methods
    //


    private void stopCurrentFragmentFetch()
    {
       synchronized ( mLockObj )
        {
            if( mCurrentFragmentFetchTask != null )
            {
                //
                // Make sure to unsubscribe from the listener before canceling, that
                // way we will not receive the callback.
                //
                mCurrentFragmentFetchTask.removeFragmentFetchDataListener( this );
                mCurrentFragmentFetchTask.cancel( true );
                mCurrentFragmentFetchTask = null;
            }
            
            if ( mTextTimer != null )
            {
                mTextTimer.cancel();
                mTextTimer.purge();
                mTextTimer = null;
            }
        }
    }


    private void startFragmentFetchAtCurrentTime() throws Exception
    {
        synchronized ( mLockObj )
        {
            stopCurrentFragmentFetch();

            long currentTime = mMediaPlayer.getCurrentPosition();
            mFirstFragmentStartTime = currentTime;
            mLastFragmentEndTime = currentTime;

            Log.d( "TextHandler", "Iterator re-started at time: " + currentTime );
            try
            {
                mCurrentState = TextHandlerState.FETCHING_FRAGMENT;

                mFragmentIter = mMediaPlayer.getFragmentIterator( mTextRepresentation, currentTime );
                mCurrentFragmentFetchTask = mMediaPlayer.getFragmentData( mFragmentIter );
                mCurrentFragmentFetchTask.addFragmentFetchDataListener( this );

                //
                // Execution will be continued in the onFragmentFetchDataComplete callback
                //
            }
            catch ( com.microsoft.playready2.FragmentEOSException eosx )
            {
                Log.d( "TextHandler", "No fragments avaiable in stream, waiting for update." );

                mCurrentState = TextHandlerState.WAITING_FOR_NEW_FRAGMENTS;

                //
                // Execution will be continued in the onStreamUpdate callback
                //
            }
            catch ( com.microsoft.playready.FragmentEOSException eosx )
            {
                Log.d( "TextHandler", "No fragments avaiable in stream, waiting for update." );

                mCurrentState = TextHandlerState.WAITING_FOR_NEW_FRAGMENTS;

                //
                // Execution will be continued in the onStreamUpdate callback
                //
            }

            if ( mIsPollingIterator && mTextTimer == null )
            {
                mTextTimer = new Timer();
                mTextTimer.schedule(new TextParserTask(), 500, 500);
            }   
        }
    }

    private void startNextFragmentFetch() throws Exception
    {
        if ( ( mFragmentIter == null ) || ( !mFragmentIter.isValid() ) )
        {
            startFragmentFetchAtCurrentTime();
        }
        else
        {
            if( mFragmentIter.next() )
            {
                mCurrentState = TextHandlerState.FETCHING_FRAGMENT;

                mCurrentFragmentFetchTask = mMediaPlayer.getFragmentData( mFragmentIter );
                mCurrentFragmentFetchTask.addFragmentFetchDataListener( this );

                //
                // Execution will be continued in the onFragmentFetchDataComplete callback
                //
            }
            else
            {
                Log.d( "TextHandler", "Reached the end of known fragments, waiting for more." );

                mCurrentState = TextHandlerState.WAITING_FOR_NEW_FRAGMENTS;

                //
                // Execution will be continued in the onStreamUpdate callback
                //

                if ( mIsPollingIterator && mTextTimer == null )
                {
                    mTextTimer = new Timer();
                    mTextTimer.schedule(new TextParserTask(), 500, 500);
                }
            }
        }
    }

    void notifyTextListeners( IMediaRepresentationAdapter textStream, IFragmentInfoAdapter info, byte[] rawFragmentData )
    {
        synchronized (this)
        {
            for (ITextListener listener : mListeners)
            {
                try
                {
                    listener.onText(textStream, info, rawFragmentData);
                }
                catch (Exception e)
                {
                    Log.w( "TextHandler",
                            "An ITextListner threw an exception when called! Ignoring it.", e );

                }
            }
        }
    }


    //
    // Listeners
    //

    @Override
    public void onSeekComplete( IPRMediaPlayerAdapter arg0 )
    {

        synchronized ( mLockObj )
        {
            Log.d( "TextHandler", "Seek detected!" );

            if( ( mCurrentState == TextHandlerState.WAITING_FOR_NEW_FRAGMENTS ) ||
                ( mCurrentState == TextHandlerState.FETCHING_FRAGMENT ))
            {
                long currentTime = mMediaPlayer.getCurrentPosition();
                if ( ( currentTime < mFirstFragmentStartTime ) ||
                     ( currentTime > mLastFragmentEndTime ) )
                {
                    Log.d( "TextHandler",
                            "New position outside of fragments already downloaded, resetting iterator." );
                    try
                    {
                        startFragmentFetchAtCurrentTime();
                    }
                    catch ( Exception e )
                    {
                        Log.w( "TextHandler", "Exception occured resetting iterator after seek.", e );
                        mFinalException = e;
                        mCurrentState = TextHandlerState.EXCEPTION;
                    }
                }
                else
                {
                    Log.d( "TextHandler",
                            "New position inside of downloaded fragment range, nothing to do." );
                }
            }
        }

    }

    @Override
    public void onFragmentFetchDataComplete( IFragmentFetchDataTaskAdapter completedTask )
    {
        synchronized ( mLockObj )
        {
            Log.d( "TextHandler", "Fragment Fetch complete. " );

            if ( mCurrentFragmentFetchTask.get() == completedTask.get() )
            {
                mCurrentFragmentFetchTask = null;

                try
                {
                    IFragmentInfoAdapter fragInfo = completedTask.fragmentInfo();
                    
                    notifyTextListeners(
                            mTextRepresentation,
                            fragInfo,
                            completedTask.fragmentData());

                    //
                    // Keep track of contigious block of fragments
                    // downloaded by recording the first startime and most
                    // recent end-time, this helps us figure out what to do
                    // in event of a seek.
                    //
                    mLastFragmentEndTime =
                            (fragInfo.getStartTimeInMs() +
                             fragInfo.getDurationInMs());

                    Log.d( "TextHandler", "Fragment sucessfully downloaded for time " +
                                               completedTask.fragmentInfo().getStartTimeInMs() );

                    if ( mCurrentState == TextHandlerState.FETCHING_FRAGMENT )
                    {
                        startNextFragmentFetch();
                    }
                }
                catch ( Exception e )
                {
                    Log.w( "TextHandler",
                            "Exception occured when downloading a fragment. ", e );

                    mFinalException = e;
                    mCurrentState = TextHandlerState.EXCEPTION;
                }
            }
            else
            {
                Log.w( "TextHandler",
                        "onFragmentFetchDataComplete called when not expected! " );
            }
        }
    }
}

