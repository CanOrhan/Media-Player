/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample.adapters.interfaces;

import java.io.IOException;
import java.net.URISyntaxException;

import com.microsoft.playready.DrmException;
import com.microsoft.playready2.AggregateException;
import com.microsoft.playready2.MediaException;

import android.view.SurfaceHolder;

public interface IPRMediaPlayerAdapter
{
    public interface OnSeekCompleteListener 
    {
    	public void onSeekComplete(IPRMediaPlayerAdapter mp);
	}

	public interface OnCompletionListener
    {
        public void onCompletion(IPRMediaPlayerAdapter mp);
    }

    public interface OnInfoListener
    {
        public boolean onInfo(IPRMediaPlayerAdapter mp, int what, int extra);
    }

    public interface OnErrorListener
    {
        public boolean onError(IPRMediaPlayerAdapter mp, int what, int extra);
    }

    void prepare(String contentUri) throws IllegalArgumentException,
            SecurityException, IllegalStateException, IOException, DrmException, URISyntaxException, com.microsoft.playready2.DrmException, MediaException, AggregateException;

    void play();

    void setDisplay(SurfaceHolder m_SurfaceHolder);

    void pause();

    void stop();

    void reset();

    void seek(int pos) throws IOException, com.microsoft.playready2.DrmException, MediaException, AggregateException;

    void release();

    long getDuration();

    long getCurrentPosition();

    boolean isPlaying();

    void setLicenseAcquisitionPlugin(
            com.microsoft.playready2.ILicenseAcquisitionPlugin m_laPlugin);

    void addOnCompletionListener(
            OnCompletionListener listener);

    void addOnInfoListener(OnInfoListener listener);

    void addOnErrorListener(OnErrorListener listener);

    void removeOnCompletionListener(
            OnCompletionListener listener);

    void removeOnInfoListener(OnInfoListener listener);

    void removeOnErrorListener(OnErrorListener listener);

    IMediaDescriptionAdapter getMediaDescription();

	void updateMediaSelection(IMediaDescriptionAdapter mpd) throws Exception;

	IFragmentIteratorAdapter getFragmentIterator(
            IMediaRepresentationAdapter IMediaRepresentationAdapter, long currentTime)
					throws com.microsoft.playready2.FragmentEOSException, 
					com.microsoft.playready2.FragmentBOSException, 
					com.microsoft.playready2.MediaException, 
					com.microsoft.playready2.DrmException,
					com.microsoft.playready.FragmentEOSException,
					com.microsoft.playready.FragmentBOSException,
					com.microsoft.playready.MediaException;

	IFragmentFetchDataTaskAdapter getFragmentData(IFragmentIteratorAdapter mFragmentIter);

	void addOnSeekCompleteListener(OnSeekCompleteListener listener);

	void removeOnSeekCompleteListener(OnSeekCompleteListener listener);

}
