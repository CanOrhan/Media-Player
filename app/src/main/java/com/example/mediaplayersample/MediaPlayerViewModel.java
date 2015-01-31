package com.example.mediaplayersample;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import android.app.Activity;
import android.util.Pair;
import android.view.SurfaceHolder;

import com.example.mediaplayersample.ContentCollection.ContentEntry;
import com.example.mediaplayersample.TextStreamViewModels.ITextSource;
import com.example.mediaplayersample.adapters.interfaces.IMediaDescriptionAdapter;
import com.example.mediaplayersample.adapters.interfaces.IMediaStreamAdapter;
import com.example.mediaplayersample.adapters.interfaces.IPRMediaPlayerAdapter;
import com.microsoft.playready.DrmException;
import com.microsoft.playready2.AggregateException;
import com.microsoft.playready2.MediaException;

public class MediaPlayerViewModel
{       
    private IPRMediaPlayerAdapter m_PlayerAdapter = null;
    
    public MediaPlayerViewModel( 
            Activity activity, 
            ContentEntry content, 
            SurfaceHolder surfaceHolder,
            IPRMediaPlayerAdapter.OnErrorListener errorListener,
            IPRMediaPlayerAdapter.OnCompletionListener completionListener ) 
            throws 
                IllegalArgumentException, 
                SecurityException, 
                IllegalStateException, 
                IOException, 
                DrmException, 
                URISyntaxException, 
                com.microsoft.playready2.DrmException, 
                MediaException, 
                AggregateException
    {
        MixedLibraryMediaPlayerFactory factory = new MixedLibraryMediaPlayerFactory( activity );
        MyLicenseAcquisitionPlugin laPlugin = new MyLicenseAcquisitionPlugin();
        
        String customData = content.getLicenseRequestInfo().getCustomData();
        if ( null != customData)
        {
        	laPlugin.setChallengeCustomData( customData );        
        }
        
        String urlOverride = content.getLicenseRequestInfo().getServerUrlOverride();
        if( null != urlOverride )
        {
        	laPlugin.setLicenseServerUriOverride( urlOverride );
        }        
        
        IPRMediaPlayerAdapter playerAdapter = factory.newMediaPlayer();
        
        playerAdapter.setLicenseAcquisitionPlugin( laPlugin );
        playerAdapter.addOnErrorListener( errorListener );
        playerAdapter.addOnCompletionListener( completionListener );
        
        playerAdapter.prepare( content.getUrl() );
        playerAdapter.setDisplay( surfaceHolder );
        m_PlayerAdapter = playerAdapter;
    }
    
    public void play ()
    {
        m_PlayerAdapter.play();
    }
    
    public void pause()
    {
        m_PlayerAdapter.pause();
    }
    
    public void seekTo( int desiredPosInMs ) 
            throws 
                IOException, 
                com.microsoft.playready2.DrmException, 
                MediaException, 
                AggregateException
    {
        m_PlayerAdapter.seek( desiredPosInMs );
    }
        
    public long getDuration ()
    {
        return m_PlayerAdapter.getDuration();
    }

    public void release ()
    {
        // TODO Auto-generated method stub
        m_PlayerAdapter.stop();
        m_PlayerAdapter.release();
    }

    public void seek ( int seekPosInMS ) throws IOException, com.microsoft.playready2.DrmException, MediaException, AggregateException
    {
        m_PlayerAdapter.seek( seekPosInMS );
    }

    public long getCurrentPosition()
    {
        return m_PlayerAdapter.getCurrentPosition();
    }

    public boolean isPlaying ()
    {
        return m_PlayerAdapter.isPlaying();
    }
    
    public ArrayList< Pair< String, ITextSource > > identifyTextStreams( )
    {
        return TextStreamViewModels.identifyTextStreams( m_PlayerAdapter );
    }

    public ArrayList< Pair< String, IMediaStreamAdapter > > identifyVideoStreams ()
    {
        ArrayList< Pair< String, IMediaStreamAdapter > > videoStream = new ArrayList< Pair< String, IMediaStreamAdapter >>();
        
        IMediaDescriptionAdapter mpd = m_PlayerAdapter.getMediaDescription();

        for (int i = 0; i < mpd.getStreamCount(); ++i)
        {
            IMediaStreamAdapter stream = mpd.getStreamAt(i);
            
            if( stream.getStreamType() == IMediaStreamAdapter.Type.VIDEO )
            {
                videoStream.add( new Pair< String, IMediaStreamAdapter >( stream.getName(), stream ) );
            }
        }

        return videoStream;
    }
    
    public ArrayList< Pair< String, IMediaStreamAdapter > > identifyAudioStreams ()
    {
        ArrayList< Pair< String, IMediaStreamAdapter > > audioStream = new ArrayList< Pair< String, IMediaStreamAdapter >>();
        
        IMediaDescriptionAdapter mpd = m_PlayerAdapter.getMediaDescription();

        for (int i = 0; i < mpd.getStreamCount(); ++i)
        {
            IMediaStreamAdapter stream = mpd.getStreamAt(i);
            
            if( stream.getStreamType() == IMediaStreamAdapter.Type.AUDIO )
            {
                audioStream.add( new Pair< String, IMediaStreamAdapter >( stream.getName(), stream ) );
            }
        }

        return audioStream;
    }
    
    public void selectVideoTrack ( IMediaStreamAdapter newStream ) throws Exception
    {
        IMediaDescriptionAdapter mpd = m_PlayerAdapter.getMediaDescription();

        for (int i = 0; i < mpd.getStreamCount(); ++i)
        {
            IMediaStreamAdapter stream = mpd.getStreamAt(i);
            
            if( stream.getStreamType() == IMediaStreamAdapter.Type.VIDEO )
            {
                if( stream.getName().equals( newStream.getName() ) &&
                    stream.getMediaRepresentationCount() == newStream.getMediaRepresentationCount() &&
                    stream.isCurrentlySelected() == newStream.isCurrentlySelected() )
                {
                    stream.setShouldBeSelected( true );
                }
                else
                {
                    stream.setShouldBeSelected( false );
                }
            }
        }
        
        m_PlayerAdapter.updateMediaSelection( mpd );
    }

    public void selectAudioTrack ( IMediaStreamAdapter newStream ) throws Exception
    {
        IMediaDescriptionAdapter mpd = m_PlayerAdapter.getMediaDescription();

        for (int i = 0; i < mpd.getStreamCount(); ++i)
        {
            IMediaStreamAdapter stream = mpd.getStreamAt(i);
            
            if( stream.getStreamType() == IMediaStreamAdapter.Type.AUDIO )
            {
                if( stream.getName().equals( newStream.getName() ) &&
                    stream.getMediaRepresentationCount() == newStream.getMediaRepresentationCount() &&
                    stream.isCurrentlySelected() == newStream.isCurrentlySelected() )
                {
                    stream.setShouldBeSelected( true );
                }
                else
                {
                    stream.setShouldBeSelected( false );
                }
            }
        }
        
        m_PlayerAdapter.updateMediaSelection( mpd );
    }
    
    public void clearSurface()
    {
    	m_PlayerAdapter.setDisplay(null);
    }
}
