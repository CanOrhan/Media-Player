/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample.adapters.v1;

import java.io.IOException;
import java.util.ArrayList;

import com.example.mediaplayersample.adapters.interfaces.IFragmentFetchDataTaskAdapter;
import com.example.mediaplayersample.adapters.interfaces.IFragmentIteratorAdapter;
import com.example.mediaplayersample.adapters.interfaces.IMediaDescriptionAdapter;
import com.example.mediaplayersample.adapters.interfaces.IMediaRepresentationAdapter;
import com.example.mediaplayersample.adapters.interfaces.IPRMediaPlayerAdapter;
import com.microsoft.playready.DrmException;
import com.microsoft.playready.FragmentBOSException;
import com.microsoft.playready.FragmentEOSException;
import com.microsoft.playready.FragmentIterator;
import com.microsoft.playready.MediaDescription;
import com.microsoft.playready.MediaException;
import com.microsoft.playready.MediaRepresentation;


import android.util.Log;
import android.view.SurfaceHolder;

public class V1MediaPlayerAdapter implements IPRMediaPlayerAdapter
{
    //
    // Adapter class to adapt LicenseAcquisitionPlugins written for the V2
    // libraries to work against V1
    //
    private class V1LicenseAcquisitionPluginAdapter implements
            com.microsoft.playready.ILicenseAcquisitionPlugin
    {
        private com.microsoft.playready2.ILicenseAcquisitionPlugin mInner;

        public V1LicenseAcquisitionPluginAdapter (
                com.microsoft.playready2.ILicenseAcquisitionPlugin inner )
        {
            mInner = inner;
        }

        @Override
        public byte[] doLicenseRequest ( byte[] arg0, String arg1 )
                throws Exception
        {
            return mInner.doLicenseRequest( arg0, arg1 );
        }

        @Override
        public String getChallengeCustomData ()
        {
            return mInner.getChallengeCustomData();
        }

        @Override
        public com.microsoft.playready.DomainInfo getCurrentDomainInfo ()
        {

            //
            // Adapt between the two DomainInfo types
            //
            com.microsoft.playready.DomainInfo v1DomainInfo = null;
            com.microsoft.playready2.DomainInfo v2DomainInfo = mInner
                    .getCurrentDomainInfo();

            if ( v2DomainInfo != null )
            {
                v1DomainInfo = new com.microsoft.playready.DomainInfo(
                        v2DomainInfo.getServiceID(),
                        v2DomainInfo.getAccountID(),
                        v2DomainInfo.getFriendlyName(),
                        v2DomainInfo.getRevision() );
            }

            return v1DomainInfo;
        }

    }

    //
    // Adapter that translates some V1 events into the abstracted
    // IPRMediaPlayerAdapter events
    //
    private class V1EventAdapter implements
            com.microsoft.playready.MediaPlayer.OnErrorListener,
            com.microsoft.playready.MediaPlayer.OnInfoListener,
            com.microsoft.playready.MediaPlayer.OnCompletionListener,
            com.microsoft.playready.MediaPlayer.OnSeekCompleteListener
    {
        private final static String TAG = "V1EventAdapter";

        private ArrayList< IPRMediaPlayerAdapter.OnCompletionListener > mOnCompletionListeners = new ArrayList< IPRMediaPlayerAdapter.OnCompletionListener >();
        private Object mOnCompletionListenerLockObj = new Object();

        private ArrayList< IPRMediaPlayerAdapter.OnInfoListener > mOnInfoListeners = new ArrayList< IPRMediaPlayerAdapter.OnInfoListener >();
        private Object mOnInfoListenerLockObj = new Object();

        private ArrayList< IPRMediaPlayerAdapter.OnErrorListener > mOnErrorListeners = new ArrayList< IPRMediaPlayerAdapter.OnErrorListener >();
        private Object mOnErrorListenerLockObj = new Object();

        private ArrayList< IPRMediaPlayerAdapter.OnSeekCompleteListener > mOnSeekCompleteListeners = 
                new ArrayList< IPRMediaPlayerAdapter.OnSeekCompleteListener >();
        private Object mOnSeekCompleteListenerLockObj = new Object();
        
        IPRMediaPlayerAdapter mMPToReport = null;

        public V1EventAdapter ( IPRMediaPlayerAdapter mpToReport )
        {
            mMPToReport = mpToReport;
        }

        public void addOnCompletionListener (
                IPRMediaPlayerAdapter.OnCompletionListener listener )
        {
            synchronized ( mOnCompletionListenerLockObj )
            {
                mOnCompletionListeners.add( listener );
            }
        }

        public void removeOnCompletionListener (
                IPRMediaPlayerAdapter.OnCompletionListener listener )
        {
            synchronized ( mOnCompletionListenerLockObj )
            {
                mOnCompletionListeners.remove( listener );
            }
        }

        @Override
        public void onCompletion ( com.microsoft.playready.MediaPlayer arg0 )
        {
            final ArrayList< IPRMediaPlayerAdapter.OnCompletionListener > listeners = new ArrayList< IPRMediaPlayerAdapter.OnCompletionListener >();

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

        public void addOnInfoListener (
                IPRMediaPlayerAdapter.OnInfoListener listener )
        {
            synchronized ( mOnInfoListenerLockObj )
            {
                mOnInfoListeners.add( listener );
            }
        }

        public void removeOnInfoListener (
                IPRMediaPlayerAdapter.OnInfoListener listener )
        {
            synchronized ( mOnInfoListenerLockObj )
            {
                mOnInfoListeners.remove( listener );
            }
        }

        @Override
        public boolean onInfo ( com.microsoft.playready.MediaPlayer arg0,
                int arg1, int arg2 )
        {
            boolean handled = false;
            final ArrayList< IPRMediaPlayerAdapter.OnInfoListener > listeners = new ArrayList< IPRMediaPlayerAdapter.OnInfoListener >();

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

        public void addOnErrorListener (
                IPRMediaPlayerAdapter.OnErrorListener listener )
        {
            synchronized ( mOnErrorListenerLockObj )
            {
                mOnErrorListeners.add( listener );
            }
        }

        public void removeOnErrorListener (
                IPRMediaPlayerAdapter.OnErrorListener listener )
        {
            synchronized ( mOnErrorListenerLockObj )
            {
                mOnErrorListeners.remove( listener );
            }
        }

        @Override
        public boolean onError ( com.microsoft.playready.MediaPlayer arg0,
                int arg1, int arg2 )
        {
            boolean handled = false;
            ArrayList< IPRMediaPlayerAdapter.OnErrorListener > listeners = new ArrayList< IPRMediaPlayerAdapter.OnErrorListener >();

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
		public void onSeekComplete(com.microsoft.playready.MediaPlayer mp) {
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

    private com.microsoft.playready.PRMediaPlayer m_mediaPlayer = null;
    private V1EventAdapter m_eventAdapter = null;

    public V1MediaPlayerAdapter (
            com.microsoft.playready.PRMediaPlayer mediaPlayer )
    {
        m_eventAdapter = new V1EventAdapter( this );
        m_mediaPlayer = mediaPlayer;

        m_mediaPlayer.addOnCompletionListener( m_eventAdapter );
        m_mediaPlayer.addOnInfoListener( m_eventAdapter );
        m_mediaPlayer.addOnErrorListener( m_eventAdapter );
    }

    @Override
    public void prepare ( String contentUri ) throws IllegalArgumentException,
            SecurityException, IllegalStateException, IOException, DrmException
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
    public void seek ( int pos )
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
            com.microsoft.playready2.ILicenseAcquisitionPlugin laPlugin )
    {
        //
        // We need to adapt between the two Plugin Types (v1 and V2)
        //

        m_mediaPlayer
                .setLicenseAcquisitionPlugin( new V1LicenseAcquisitionPluginAdapter(
                        laPlugin ) );
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
		return new V1MediaDescriptionAdapter(m_mediaPlayer.getCurrentMediaDescription());
	}

	@Override
	public void updateMediaSelection(IMediaDescriptionAdapter mpd) throws Exception {
		m_mediaPlayer.updateMediaSelection((MediaDescription)(mpd.get()));
		
	}

	@Override
	public IFragmentIteratorAdapter getFragmentIterator(
			IMediaRepresentationAdapter IMediaRepresentationAdapter,
			long currentTime) throws FragmentEOSException,
			FragmentBOSException, MediaException {
		return new V1FragmentIteratorAdapter(m_mediaPlayer.getFragmentIterator((MediaRepresentation)IMediaRepresentationAdapter.get(), currentTime));
	}

	@Override
	public IFragmentFetchDataTaskAdapter getFragmentData(
			IFragmentIteratorAdapter mFragmentIter) {
		
		return new V1FragmentFetchDataTaskAdapter(m_mediaPlayer.getFragmentData((FragmentIterator)mFragmentIter.get()));
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
