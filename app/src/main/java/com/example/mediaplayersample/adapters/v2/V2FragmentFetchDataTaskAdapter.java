/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample.adapters.v2;

import java.util.ArrayList;

import android.util.Log;

import com.example.mediaplayersample.adapters.interfaces.IFragmentFetchDataListenerAdapter;
import com.example.mediaplayersample.adapters.interfaces.IFragmentFetchDataTaskAdapter;
import com.example.mediaplayersample.adapters.interfaces.IFragmentInfoAdapter;
import com.microsoft.playready2.IFragmentFetchDataListener;
import com.microsoft.playready2.IFragmentFetchDataTask;

public class V2FragmentFetchDataTaskAdapter implements
		IFragmentFetchDataTaskAdapter 
{

	IFragmentFetchDataTask mTask;
	
	class EventAdapter implements IFragmentFetchDataListener 
	{

		private final static String TAG = "V2FragmentFetchDataTaskAdapter.EventAdapter";
		
		private ArrayList<IFragmentFetchDataListenerAdapter> mListeners = new ArrayList<IFragmentFetchDataListenerAdapter>();
		private Object mOnFragmentFetchDataCompleteLockObj = new Object();
		
		@Override
		public void onFragmentFetchDataComplete(IFragmentFetchDataTask task) 
		{
			final ArrayList<IFragmentFetchDataListenerAdapter> listeners = new ArrayList<IFragmentFetchDataListenerAdapter>();
			synchronized ( mOnFragmentFetchDataCompleteLockObj )
            {
				listeners.addAll(mListeners);
            }
			
			for(IFragmentFetchDataListenerAdapter listener : listeners) 
			{
				try
                {
					listener.onFragmentFetchDataComplete(new V2FragmentFetchDataTaskAdapter(task));
                }
                catch ( Exception e )
                {
                    Log.w( TAG,
                            "Uncaught exception thrown during onFragmentFetchDataComplete callback",
                            e );
                }
			}
			
		}

		public void addFragmentFetchDataListener(
				IFragmentFetchDataListenerAdapter listener) 
		{
			synchronized ( mOnFragmentFetchDataCompleteLockObj )
            {
				mListeners.add( listener );
				if(mListeners.size() == 1) {
					
					mTask.addFragmentFetchDataListener(this);
				}
            }
			
		}

		public void removeFragmentFetchDataListener(IFragmentFetchDataListenerAdapter listener) 
		{
			synchronized ( mOnFragmentFetchDataCompleteLockObj )
            {
				mListeners.remove( listener );
				if(mListeners.size() == 0) {
					mTask.removeFragmentFetchDataListener(this);
				}
            }
			
		}
		
	}
	
	private EventAdapter mEventAdapter = new EventAdapter();
	
	public V2FragmentFetchDataTaskAdapter(IFragmentFetchDataTask task) 
	{
		mTask = task;
	}

	@Override
	public void cancel(boolean b) 
	{
		mTask.cancel(b);

	}

	@Override
	public byte[] fragmentData() throws Exception 
	{
		return mTask.fragmentData();
	}

	@Override
	public IFragmentInfoAdapter fragmentInfo() 
	{
		return new V2FragmentInfoAdapter(mTask.fragmentInfo());
	}

	@Override
	public void removeFragmentFetchDataListener(
			IFragmentFetchDataListenerAdapter listener) 
	{
		mEventAdapter.removeFragmentFetchDataListener(listener);
		
	}

	@Override
	public void addFragmentFetchDataListener(
			IFragmentFetchDataListenerAdapter listener) 
	{
		mEventAdapter.addFragmentFetchDataListener(listener);
		
	}

	@Override
	public Object get() 
	{
		return mTask;
	}

}
