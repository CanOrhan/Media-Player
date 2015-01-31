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

import com.example.mediaplayersample.ContentCollection.ContentEntry;
import com.example.mediaplayersample.TextStreamViewModels.ITextSource;
import com.example.mediaplayersample.adapters.interfaces.IMediaStreamAdapter;
import com.example.mediaplayersample.adapters.interfaces.IPRMediaPlayerAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity
{
    private static final String s_Tag = "Main";

    private static final boolean s_AutoStartPlayback = false;
    private static final int s_AutoStartDelayInMs = 5000;
    private static final int s_StateIntervalInMs = 1000;

    private SurfaceView m_SurfaceView = null;
    private SurfaceHolder m_SurfaceHolder = null;
    private FrameLayout m_VideoFrame = null;
    private AudioManager m_AudioManager = null;
    private Timer m_AutoStartTimer = null;
    private Timer m_StateTimer = null;

    private ContentCollection m_ContentCollection = null;
    private ContentEntry m_ContentEntry = null;

    private ListView m_ContentList = null;
    
    private static class CustomArrayPairAdapter< T > extends ArrayAdapter< Pair< String, T> >
    {
        private ArrayList< Pair< String, T> > mArrayList;
        
        public CustomArrayPairAdapter(Context ctx, ArrayList< Pair< String, T> > arrayList)
        {
            super(ctx, android.R.layout.simple_list_item_activated_1, arrayList);
            mArrayList = arrayList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            ((TextView) v).setText( mArrayList.get( position ).first );
            ((TextView) v).setTextColor(Color.WHITE);
            return v;
        }
        
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            ((TextView) v).setText( mArrayList.get( position ).first );
            return v;
        }
    }
    
    private TextView mSubtitleTextView;
    
    private MediaPlayerViewModel mMPViewModel = null;
    private ITextSource mSubtitleTextModel = null;

    private Spinner mVideoTrackSpinner;
    private Spinner mAudioTrackSpinner;
    private Spinner mSubtitleTrackSpinner;

    private CustomArrayPairAdapter< IMediaStreamAdapter > mVideoTrackSpinnerAdapter;
    private CustomArrayPairAdapter< IMediaStreamAdapter > mAudioTrackSpinnerAdapter;
    private CustomArrayPairAdapter< ITextSource > mSubtitleTrackSpinnerAdapter;
    
    private void autoStartTimerMethod ()
    {
        m_AutoStartTimer.cancel();
        runOnUiThread( autoStartTimerTick );
    }

    private Runnable autoStartTimerTick = new Runnable()
    {
        public void run ()
        {
            play();
        }
    };

    private Runnable stateTimerTick = new Runnable()
    {
        public void run ()
        {
            updateStateText();
        }
    };

    private void stateTimerMethod ()
    {
        runOnUiThread( stateTimerTick );
    }

    private ArrayList< String > getContentTitles ()
    {
        ArrayList< String > titles = new ArrayList< String >();

        for ( int i = 0; i < m_ContentCollection.getContentCount(); i++ )
        {
            titles.add( m_ContentCollection.getContentAtIndex( i ).getTitle() );
        }
        return titles;
    }

    private void showContentList ( Boolean show )
    {
        View widget = findViewById( R.id.playButton );
        widget.setVisibility( show ? View.INVISIBLE : View.VISIBLE );

        widget = findViewById( R.id.pauseButton );
        widget.setVisibility( show ? View.INVISIBLE : View.VISIBLE );

        widget = findViewById( R.id.stopButton );
        widget.setVisibility( show ? View.INVISIBLE : View.VISIBLE );

        widget = findViewById( R.id.contentButton );
        widget.setVisibility( show ? View.INVISIBLE : View.VISIBLE );

        widget = findViewById( R.id.positionText );
        widget.setVisibility( show ? View.INVISIBLE : View.VISIBLE );

        widget = findViewById( R.id.stateText );
        widget.setVisibility( show ? View.INVISIBLE : View.VISIBLE );

        widget = findViewById( R.id.seekBar );
        widget.setVisibility( show ? View.INVISIBLE : View.VISIBLE );

        widget = findViewById( R.id.volDownButton );
        widget.setVisibility( show ? View.INVISIBLE : View.VISIBLE );

        widget = findViewById( R.id.volUpButton );
        widget.setVisibility( show ? View.INVISIBLE : View.VISIBLE );
        
        widget = findViewById( R.id.videoTrackSpinner );
        widget.setVisibility( show ? View.INVISIBLE : View.VISIBLE );
        
        widget = findViewById( R.id.audioTrackSpinner );
        widget.setVisibility( show ? View.INVISIBLE : View.VISIBLE );
        
        widget = findViewById( R.id.subtitleTrackSpinner );
        widget.setVisibility( show ? View.INVISIBLE : View.VISIBLE );
        
        widget = findViewById( R.id.subtitleText );
        widget.setVisibility( show ? View.INVISIBLE : View.VISIBLE );

        if ( m_SurfaceView != null )
        {
            m_SurfaceView.setVisibility( show ? View.GONE : View.VISIBLE );
        }

        m_ContentList.setVisibility( show ? View.VISIBLE : View.INVISIBLE );

        if ( show )
        {
            m_ContentList.bringToFront();
        }
    }

    @Override
    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );

        setContentView( R.layout.activity_main );

        m_VideoFrame = ( FrameLayout ) findViewById( R.id.video_frame );

        m_SurfaceView = new SurfaceView( this );
        m_SurfaceHolder = m_SurfaceView.getHolder();
        m_SurfaceHolder.addCallback( m_SurfaceHolderCallback );
        m_VideoFrame.addView( m_SurfaceView );

        m_AudioManager = ( AudioManager ) getSystemService( Context.AUDIO_SERVICE );

        if ( s_AutoStartPlayback )
        {
            m_AutoStartTimer = new Timer();
            m_AutoStartTimer.schedule( new TimerTask()
            {
                @Override
                public void run ()
                {
                    autoStartTimerMethod();
                }
            }, s_AutoStartDelayInMs );
        }

        m_ContentCollection = new ContentCollection();


        Button playButton = ( Button ) findViewById( R.id.playButton );
        playButton.setOnClickListener( new View.OnClickListener()
        {
            public void onClick ( View v )
            {
                play();
            }
        } );

        Button pauseButton = ( Button ) findViewById( R.id.pauseButton );
        pauseButton.setOnClickListener( new View.OnClickListener()
        {
            public void onClick ( View v )
            {
                pause();
            }
        } );

        Button stopButton = ( Button ) findViewById( R.id.stopButton );
        stopButton.setOnClickListener( new View.OnClickListener()
        {
            public void onClick ( View v )
            {
                stop();
            }
        } );

        SeekBar seekBar = ( SeekBar ) findViewById( R.id.seekBar );
        seekBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener()
        {
            private Boolean mTracking = false;

            public void onProgressChanged ( SeekBar seekBar, int progress,
                    boolean fromUser )
            {
                if ( mTracking )
                {
                    seekTo( progress );
                }
            }

            public void onStartTrackingTouch ( SeekBar seekBar )
            {
                mTracking = true;
            }

            public void onStopTrackingTouch ( SeekBar seekBar )
            {
                mTracking = false;
            }
        } );

        Button volDownButton = ( Button ) findViewById( R.id.volDownButton );
        volDownButton.setOnClickListener( new View.OnClickListener()
        {
            public void onClick ( View v )
            {
                int vol = m_AudioManager
                        .getStreamVolume( AudioManager.STREAM_MUSIC );
                if ( vol / 2 > 0 )
                {
                    m_AudioManager.setStreamVolume( AudioManager.STREAM_MUSIC,
                            vol / 2, 0 );
                }
            }
        } );

        Button volUpButton = ( Button ) findViewById( R.id.volUpButton );
        volUpButton.setOnClickListener( new View.OnClickListener()
        {
            public void onClick ( View v )
            {
                int vol = m_AudioManager
                        .getStreamVolume( AudioManager.STREAM_MUSIC );
                m_AudioManager.setStreamVolume( AudioManager.STREAM_MUSIC,
                        vol * 2, 0 );
            }
        } );

        Button contentButton = ( Button ) findViewById( R.id.contentButton );
        contentButton.setOnClickListener( new View.OnClickListener()
        {
            public void onClick ( View v )
            {
                stop();
            	showContentList( true );
            }
        } );

        ArrayAdapter< String > adapter = new ArrayAdapter< String >( this,
                android.R.layout.simple_list_item_single_choice,
                getContentTitles() );

        m_ContentList = ( ListView ) findViewById( R.id.contentList );
        m_ContentList.setAdapter( adapter );
        m_ContentList.setChoiceMode( ListView.CHOICE_MODE_NONE );
        m_ContentList.setOnItemClickListener( new OnItemClickListener()
        {
            public void onItemClick ( AdapterView< ? > parent, View view,
                    int position, long id )
            {

                stop();
                Log.d( s_Tag,
                        "Content selected: "
                                + m_ContentCollection
                                        .getContentAtIndex( position ) );

                showContentList( false );
                
                m_ContentEntry = m_ContentCollection.getContentAtIndex( position );
                play();
            }
        } );
        m_ContentList.setVisibility( View.INVISIBLE );

        m_StateTimer = new Timer();
        m_StateTimer.schedule( new TimerTask()
        {
            @Override
            public void run ()
            {
                stateTimerMethod();
            }
        }, 0, s_StateIntervalInMs );
        
        mSubtitleTextView = (TextView)findViewById(R.id.subtitleText);
        mSubtitleTextView.setTextColor(Color.YELLOW);        
    }

    @Override
    public void onDestroy ()
    {
        super.onDestroy();
        
        if( mMPViewModel != null )
        {
            mMPViewModel.release();
            mMPViewModel = null;
        }
    }
    
    void releaseMediaPlayer()
    {
        if( mMPViewModel != null )
        {
        	mMPViewModel.clearSurface();
        	mMPViewModel.release();
            mMPViewModel = null;
        }
    }
    
    void createMediaPlayer( ContentEntry content ) throws Exception
    {        
        releaseMediaPlayer();
        
        m_SurfaceView.setVisibility( content.getContentType() == ContentEntry.ContentType.AUDIO ? View.GONE : View.VISIBLE );
        
        mMPViewModel = new MediaPlayerViewModel( 
                this, 
                content, 
                m_SurfaceHolder,
                new IPRMediaPlayerAdapter.OnErrorListener()
                {
                    @Override
                    public boolean onError ( IPRMediaPlayerAdapter mp,
                            final int what, final int extra )
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run ()
                            {
                                new AlertDialog.Builder(MainActivity.this)
                                .setTitle("An error ocurred during palyback. ")
                                .setMessage(" What: " + what + " Extra: " + extra )
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    { 
                                        dialog.dismiss();
                                    }
                                 })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .create()
                                .show();
                            }
                        } );
 
                        return false;
                    }
                },
                new IPRMediaPlayerAdapter.OnCompletionListener()
                {
                    @Override
                    public void onCompletion ( IPRMediaPlayerAdapter mp )
                    {
                    	stop();                    	
                    }
                } );
        
        mSubtitleTrackSpinner = (Spinner) findViewById(R.id.subtitleTrackSpinner);
        mSubtitleTrackSpinnerAdapter = new CustomArrayPairAdapter<ITextSource>(this, mMPViewModel.identifyTextStreams() );
        mSubtitleTrackSpinner.setAdapter(mSubtitleTrackSpinnerAdapter);
        for( int i = 0 ; i < mSubtitleTrackSpinnerAdapter.getCount() ; i++ )
        {
            if( mSubtitleTrackSpinnerAdapter.getItem( i ).second == null )
            {
                mSubtitleTrackSpinner.setSelection( i, false );
            }
        }
        mSubtitleTrackSpinner.setOnItemSelectedListener(mSubtitleTrackSelectedListener);
                    
        mVideoTrackSpinner = (Spinner) findViewById(R.id.videoTrackSpinner);
        mVideoTrackSpinnerAdapter = new CustomArrayPairAdapter<IMediaStreamAdapter>(this, mMPViewModel.identifyVideoStreams());
        mVideoTrackSpinner.setAdapter(mVideoTrackSpinnerAdapter);
        for( int i = 0 ; i < mVideoTrackSpinnerAdapter.getCount() ; i++ )
        {
            if( mVideoTrackSpinnerAdapter.getItem( i ).second != null &&
                mVideoTrackSpinnerAdapter.getItem( i ).second.isCurrentlySelected() )
            {
                mVideoTrackSpinner.setSelection( i, false );
            }
        }
        mVideoTrackSpinner.setOnItemSelectedListener(mVideoTrackSelectedListener);
        
        mAudioTrackSpinner = (Spinner) findViewById(R.id.audioTrackSpinner);
        mAudioTrackSpinnerAdapter = new CustomArrayPairAdapter<IMediaStreamAdapter>(this, mMPViewModel.identifyAudioStreams());
        mAudioTrackSpinner.setAdapter(mAudioTrackSpinnerAdapter);
        for( int i = 0 ; i < mAudioTrackSpinnerAdapter.getCount() ; i++ )
        {
            if( mAudioTrackSpinnerAdapter.getItem( i ).second != null &&
                mAudioTrackSpinnerAdapter.getItem( i ).second.isCurrentlySelected() )
            {
                mAudioTrackSpinner.setSelection( i, false );
            }
        }
        mAudioTrackSpinner.setOnItemSelectedListener(mAudioTrackSelectedListener);
        
        SeekBar seekBar = ( SeekBar ) findViewById( R.id.seekBar );
        long contentLength = mMPViewModel.getDuration();
        seekBar.setMax( ( int ) ( contentLength ) );
       
    }

    void play ()
    {
        try
        {
            if( m_ContentEntry != null )
            {
                if( mMPViewModel == null )
                {
                    createMediaPlayer( m_ContentEntry );
                }
                mMPViewModel.play();
            }
        } 
        catch (Exception e)
        {
            showErrorDialog( e );
        }
    }

    void pause ()
    {
        try
        {
            if( mMPViewModel != null )
            {
                mMPViewModel.pause();
            }
        } 
        catch (Exception e)
        {
            showErrorDialog( e );
        }
    }

    void stop ()
    {
        try
        {
            releaseMediaPlayer();
            
            SeekBar seekBar = ( SeekBar ) findViewById( R.id.seekBar );
            seekBar.setProgress( 0 );
        } 
        catch (Exception e)
        {
            showErrorDialog( e );
        }
    }

    void seekTo( int pos )
    {
        try
        {
            if( mMPViewModel != null )
            {
                mMPViewModel.seek( pos );
            }
        } 
        catch (Exception e)
        {
            showErrorDialog( e );
        }
    }

    private void updateStateText ()
    {
        if( mMPViewModel != null )
        {
            TextView positionText = ( TextView ) findViewById( R.id.positionText );
            long posInMS = mMPViewModel.getCurrentPosition();
            long posSeconds = posInMS / 1000;
            long posHours = posSeconds / ( 24 * 60 );
            posSeconds -= ( posHours * ( 24 * 60 ) );
            long posMinutes = posSeconds / 60;
            posSeconds -= ( posMinutes * 60 );
    
            String currPosition = String.format( "%02d:%02d:%02d", posHours,
                    posMinutes, posSeconds );
            positionText.setText( currPosition );
    
            SeekBar seekBar = ( SeekBar ) findViewById( R.id.seekBar );
            seekBar.setProgress( ( int ) ( posInMS ) );
    
            TextView stateText = ( TextView ) findViewById( R.id.stateText );
            stateText.setText( mMPViewModel.isPlaying() ? "Playing"
                    : "Not playing" );
            
            //
            // Update subtitles
            //
            
            if( mSubtitleTextModel != null )
            {
                String subtitle = mSubtitleTextModel.GetTextForTime( posInMS );
                        
                if ( subtitle.startsWith("http" ) )
                {
                    mSubtitleTextView.setText(Html.fromHtml(subtitle));
                }
                else
                {
                    mSubtitleTextView.setText( subtitle );
                }
            }
        }
    }

    SurfaceHolder.Callback m_SurfaceHolderCallback = new SurfaceHolder.Callback()
    {
        public void surfaceCreated ( SurfaceHolder holder )
        {
            m_SurfaceHolder = holder;
        }

        public void surfaceChanged ( SurfaceHolder holder, int format,
                int width, int height )
        {
        }

        public void surfaceDestroyed ( SurfaceHolder holder )
        {
            if(mMPViewModel != null)
            {
            	stop();
            }
        	m_SurfaceHolder = null;
        }
    };
    
    private void showErrorDialog( Exception exception )
    {
        exception.printStackTrace();
        new AlertDialog.Builder(this)
            .setTitle("An Exception has ocurred")
            .setMessage(exception.toString())
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                { 
                    dialog.dismiss();
                }
             })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .create()
            .show();
    }
    
    
    private OnItemSelectedListener mVideoTrackSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapter, View view, int position,
                long id) 
        {
        	try 
        	{
        	    IMediaStreamAdapter stream = mVideoTrackSpinnerAdapter.getItem( position ).second;
                if( !stream.isCurrentlySelected() )
                {
                    MainActivity.this.mMPViewModel.selectVideoTrack( stream );
                }
			} 
        	catch (Exception e)
        	{
        	    showErrorDialog( e );
			}
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0)
        {
        }

    };

    private OnItemSelectedListener mAudioTrackSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapter, View view, int position,
                long id) 
        {
            try 
            {
                IMediaStreamAdapter stream = mAudioTrackSpinnerAdapter.getItem( position ).second;
                if( !stream.isCurrentlySelected() )
                {
                    MainActivity.this.mMPViewModel.selectAudioTrack( stream );
                }
            } 
            catch (Exception e)
            {
                showErrorDialog( e );
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0)
        {
        }

    };

    private OnItemSelectedListener mSubtitleTrackSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapter, View view, int position,
                long id) 
        {
            try 
            {
                ITextSource source = mSubtitleTrackSpinnerAdapter.getItem( position ).second;
                
                if( mSubtitleTextModel != null )
                {
                    mSubtitleTextModel.DisableSource();
                    mSubtitleTextModel = null;
                }
                
                if( source != null )
                {
                    source.EnableSource();
                    mSubtitleTextModel = source;
                }
            } 
            catch (Exception e)
            {
                showErrorDialog( e );
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0)
        {
        }

    };
}
