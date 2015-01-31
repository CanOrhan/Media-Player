/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.adapters.v2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.example.mediaplayersample.adapters.interfaces.IFragmentFetchDataTaskAdapter;
import com.example.mediaplayersample.adapters.interfaces.IFragmentIteratorAdapter;
import com.example.mediaplayersample.adapters.interfaces.IMediaDescriptionAdapter;
import com.example.mediaplayersample.adapters.interfaces.IMediaRepresentationAdapter;
import com.example.mediaplayersample.adapters.interfaces.IPRMediaPlayerAdapter;
import com.microsoft.playready2.AggregateException;
import com.microsoft.playready2.DrmException;
import com.microsoft.playready2.FragmentBOSException;
import com.microsoft.playready2.FragmentEOSException;
import com.microsoft.playready2.FragmentIterator;
import com.microsoft.playready2.MediaDescription;
import com.microsoft.playready2.MediaException;
import com.microsoft.playready2.MediaRepresentation;

import android.util.Log;
import android.view.SurfaceHolder;

public class V2MediaPlayerAdapter implements IPRMediaPlayerAdapter
{
    //
    // Adapter that translates some V2 events into the abstracted IPRMediaPlayerAdapter events
    //
    private class V2EventAdapter implements
        com.microsoft.playready2.PRMediaPlayer.OnErrorListener,
        com.microsoft.playready2.PRMediaPlayer.OnInfoListener,
        com.microsoft.playready2.PRMediaPlayer.OnCompletionListener,
        com.microsoft.playready2.PRMediaPlayer.OnSeekCompleteListener
    {
        private final static String TAG = "V2EventAdapter";
        
        private ArrayList< IPRMediaPlayerAdapter.OnCompletionListener > mOnCompletionListeners = 
                new ArrayList< IPRMediaPlayerAdapter.OnCompletionListener >();
        private Object mOnCompletionListenerLockObj = new Object();

        private ArrayList< IPRMediaPlayerAdapter.OnInfoListener > mOnInfoListeners = 
                new ArrayList< IPRMediaPlayerAdapter.OnInfoListener >();
        private Object mOnInfoListenerLockObj = new Object();

        private ArrayList< IPRMediaPlayerAdapter.OnErrorListener > mOnErrorListeners = 
                new ArrayList< IPRMediaPlayerAdapter.OnErrorListener >();
        private Object mOnErrorListenerLockObj = new Object();
        
        private ArrayList< IPRMediaPlayerAdapter.OnSeekCompleteListener > mOnSeekCompleteListeners = 
                new ArrayList< IPRMediaPlayerAdapter.OnSeekCompleteListener >();
        private Object mOnSeekCompleteListenerLockObj = new Object();
        
        IPRMediaPlayerAdapter mMPToReport = null; 
        
        public V2EventAdapter( IPRMediaPlayerAdapter mpToReport )
        {
            mMPToReport = mpToReport;
        }
                
        public void addOnCompletionListener ( IPRMediaPlayerAdapter.OnCompletionListener listener )
        {
            synchronized ( mOnCompletionListenerLockObj )
            {
                mOnCompletionListeners.add( listener );
            }
        }

        public void removeOnCompletionListener ( IPRMediaPlayerAdapter.OnCompletionListener listener )
        {
            synchronized ( mOnCompletionListenerLockObj )
            {
                mOnCompletionListeners.remove( listener );
            }
        }

        @Override
        public void onCompletion ( com.microsoft.playready2.PRMediaPlayer arg0 )
        {
            final ArrayList< IPRMediaPlayerAdapter.OnCompletionListener > listeners = 
                    new ArrayList< IPRMediaPlayerAdapter.OnCompletionListener >();
            
            synchronized ( mOnCompletionListenerLockObj )
            {
                listeners.addAll( mOnCompletionListeners );
            }

            for ( IPRMediaPlayerAdapter.OnCompletionListener listener : listeners )
            {
                try
                {
                    listener.onCompletion( mMPToReport );
                }
                catch ( Exception e )
                {
                    Log.w( TAG,
                            "Uncaught exception thrown during OnCompletionListener callback",
                            e );
                }
            }            
        }
        
        public void addOnInfoListener ( IPRMediaPlayerAdapter.OnInfoListener listener )
        {
            synchronized ( mOnInfoListenerLockObj )
            {
                mOnInfoListeners.add( listener );
            }
        }

        public void removeOnInfoListener ( IPRMediaPlayerAdapter.OnInfoListener listener )
        {
            synchronized ( mOnInfoListenerLockObj )
            {
                mOnInfoListeners.remove( listener );
            }
        }

        @Override
        public boolean onInfo ( com.microsoft.playready2.PRMediaPlayer arg0,
                int arg1, int arg2 )
        {
            boolean handled = false;
            final ArrayList< IPRMediaPlayerAdapter.OnInfoListener > listeners = 
                    new ArrayList< IPRMediaPlayerAdapter.OnInfoListener >();

            synchronized ( mOnInfoListenerLockObj )
            {
                listeners.addAll( mOnInfoListeners );
            }

            for ( IPRMediaPlayerAdapter.OnInfoListener listener : listeners )
            {
                try
                {
                    if ( listener.onInfo( mMPToReport, arg1, arg2 ) )
                    {
                        handled = true;
                    }
                }
                catch ( Exception e )
                {
                    Log.w( TAG,
                            "Uncaught exception thrown during OnInfoListener callback",
                            e );
                }
            }

            return handled;
        }
        
        public void addOnErrorListener ( IPRMediaPlayerAdapter.OnErrorListener listener )
        {
            synchronized ( mOnErrorListenerLockObj )
            {
                mOnErrorListeners.add( listener );
            }
        }

        public void removeOnErrorListener ( IPRMediaPlayerAdapter.OnErrorListener listener )
        {
            synchronized ( mOnErrorListenerLockObj )
            {
                mOnErrorListeners.remove( listener );
            }
        }

        @Override
        public boolean onError ( com.microsoft.playready2.PRMediaPlayer arg0,
                int arg1, int arg2 )
        {
            boolean handled = false;
            ArrayList< IPRMediaPlayerAdapter.OnErrorListener > listeners = 
                    new ArrayList< IPRMediaPlayerAdapter.OnErrorListener >();

            synchronized ( mOnErrorListenerLockObj )
            {
                listeners.addAll( mOnErrorListeners );
            }

            for ( IPRMediaPlayerAdapter.OnErrorListener listener : listeners )
            {
                try
                {
                    if ( listener.onError( mMPToReport, arg1, arg2 ) )
                    {
                        handled = true;
                        break;
                    }
                }
                catch ( Exception e )
                {
                    Log.w( TAG,
                            "Uncaught exception thrown during OnErrorListener callback",
                            e );
                }
            }

            return handled;
        }

		@Override
		public void onSeekComplete(com.microsoft.playready2.PRMediaPlayer arg0) {
            final ArrayList< IPRMediaPlayerAdapter.OnSeekCompleteListener > listeners = 
                    new ArrayList< IPRMediaPlayerAdapter.OnSeekCompleteListener >();

            synchronized ( mOnSeekCompleteListenerLockObj )
            {
                listeners.addAll( mOnSeekCompleteListeners );
            }

            for ( IPRMediaPlayerAdapter.OnSeekCompleteListener listener : listeners )
            {
                try
                {
                    listener.onSeekComplete( mMPToReport );
                }
                catch ( Exception e )
                {
                    Log.w( TAG,
                            "Uncaught exception thrown during OnSeekCompleteListener callback",
                            e );
                }
            }
			
		}

		public void removeOnSeekCompleteListener(IPRMediaPlayerAdapter.OnSeekCompleteListener listener) {
			synchronized ( mOnSeekCompleteListenerLockObj )
            {
				mOnSeekCompleteListeners.remove( listener );
            }
			
		}

		public void addOnSeekCompleteListener(IPRMediaPlayerAdapter.OnSeekCompleteListener listener) {
			synchronized ( mOnSeekCompleteListenerLockObj )
            {
				mOnSeekCompleteListeners.add( listener );
            }
			
		}

		
        
    }
    
    private com.microsoft.playready2.PRMediaPlayer m_mediaPlayer = null;
    private V2EventAdapter m_eventAdapter = null;

    public V2MediaPlayerAdapter ( com.microsoft.playready2.PRMediaPlayer mediaPlayer )
    {
        m_eventAdapter = new V2EventAdapter( this );
        m_mediaPlayer = mediaPlayer;
        
        m_mediaPlayer.addOnCompletionListener( m_eventAdapter );
        m_mediaPlayer.addOnInfoListener( m_eventAdapter );
        m_mediaPlayer.addOnErrorListener( m_eventAdapter );
        m_mediaPlayer.addOnSeekCompleteListener( m_eventAdapter );
    }

    @Override
    public void prepare ( String contentUri ) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException, URISyntaxException, DrmException, MediaException, AggregateException
    {
        m_mediaPlayer.setDataSource( contentUri );
        m_mediaPlayer.prepare();
    }

    @Override
    public void setDisplay ( SurfaceHolder sh )
    {
        m_mediaPlayer.setDisplay( sh );
    }

    @Override
    public void play ()
    {
        m_mediaPlayer.start();

    }

    @Override
    public void pause ()
    {
        m_mediaPlayer.pause();
    }

    @Override
    public void stop ()
    {
        m_mediaPlayer.stop();
    }

    @Override
    public void reset ()
    {
        m_mediaPlayer.reset();
    }

    @Override
    public void seek ( int pos ) throws IOException, DrmException, MediaException, AggregateException
    {
        m_mediaPlayer.seekTo( pos );
    }

    @Override
    public void release ()
    {
        m_mediaPlayer.removeOnCompletionListener( m_eventAdapter );
        m_mediaPlayer.removeOnInfoListener( m_eventAdapter );
        m_mediaPlayer.removeOnErrorListener( m_eventAdapter );
        m_mediaPlayer.removeOnSeekCompleteListener( m_eventAdapter );
        m_mediaPlayer.release();
    }

    @Override
    public long getDuration ()
    {
        return m_mediaPlayer.getDuration();
    }

    @Override
    public long getCurrentPosition ()
    {
        return m_mediaPlayer.getCurrentPosition();
    }

    @Override
    public boolean isPlaying ()
    {
        return m_mediaPlayer.isPlaying();
    }

    @Override
    public void setLicenseAcquisitionPlugin (
            com.microsoft.playready2.ILicenseAcquisitionPlugin  laPlugin )
    {
        m_mediaPlayer.setLicenseAcquisitionPlugin( laPlugin );
    }

    @Override
    public void addOnCompletionListener ( OnCompletionListener listener )
    {
        m_eventAdapter.addOnCompletionListener( listener );
    }

    @Override
    public void addOnInfoListener ( OnInfoListener listener )
    {
        m_eventAdapter.addOnInfoListener( listener );
    }

    @Override
    public void addOnErrorListener ( OnErrorListener listener )
    {
        m_eventAdapter.addOnErrorListener( listener );
    }

    @Override
    public void removeOnCompletionListener ( OnCompletionListener listener )
    {
        m_eventAdapter.removeOnCompletionListener( listener );
    }

    @Override
    public void removeOnInfoListener ( OnInfoListener listener )
    {
        m_eventAdapter.removeOnInfoListener( listener );
    }

    @Override
    public void removeOnErrorListener ( OnErrorListener listener )
    {
        m_eventAdapter.removeOnErrorListener( listener );
    }

	@Override
	public IMediaDescriptionAdapter getMediaDescription() {
		return new V2MediaDescriptionAdapter(m_mediaPlayer.getMediaDescription());
	}

	@Override
	public void updateMediaSelection(IMediaDescriptionAdapter mpd) throws Exception {
		m_mediaPlayer.updateMediaSelection((MediaDescription)mpd.get());
		
	}

	@Override
	public IFragmentIteratorAdapter getFragmentIterator(
			IMediaRepresentationAdapter IMediaRepresentationAdapter,
			long currentTime) throws FragmentEOSException, FragmentBOSException, MediaException, DrmException {
		return new V2FragmentIteratorAdapter(m_mediaPlayer.getFragmentIterator((MediaRepresentation)IMediaRepresentationAdapter.get(), currentTime));
	}

	@Override
	public IFragmentFetchDataTaskAdapter getFragmentData(
			IFragmentIteratorAdapter mFragmentIter) {
		
		return new V2FragmentFetchDataTaskAdapter(m_mediaPlayer.getFragmentData((FragmentIterator)mFragmentIter.get()));
	}

	@Override
	public void addOnSeekCompleteListener(OnSeekCompleteListener listener) {
		m_eventAdapter.addOnSeekCompleteListener( listener );
		
	}

	@Override
	public void removeOnSeekCompleteListener(OnSeekCompleteListener listener) {
		m_eventAdapter.removeOnSeekCompleteListener( listener );
		
	}
}
