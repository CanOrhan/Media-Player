/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample.adapters.v1;

import java.util.ArrayList;

import android.util.Log;

import com.example.mediaplayersample.adapters.interfaces.IFragmentFetchDataListenerAdapter;
import com.example.mediaplayersample.adapters.interfaces.IFragmentFetchDataTaskAdapter;
import com.example.mediaplayersample.adapters.interfaces.IFragmentInfoAdapter;
import com.microsoft.playready.IFragmentFetchDataTask;
import com.microsoft.playready.IFragmentFetchDataListener;

public class V1FragmentFetchDataTaskAdapter implements
		IFragmentFetchDataTaskAdapter 
{

	IFragmentFetchDataTask mTask;
	
	class EventAdapter implements IFragmentFetchDataListener 
	{

		private final static String TAG = "V1FragmentFetchDataTaskAdapter.EventAdapter";
		
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
					listener.onFragmentFetchDataComplete(new V1FragmentFetchDataTaskAdapter(task));
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
				if(mListeners.size() == 0) {
					
					mTask.addFragmentFetchDataListener(this);
				}
				mListeners.add( listener );
            }
			
		}

		public void removeFragmentFetchDataListener(IFragmentFetchDataListenerAdapter listener) 
		{
			synchronized ( mOnFragmentFetchDataCompleteLockObj )
            {
				mListeners.remove( listener );
				if(mListeners.size() == 0) 
				{
					mTask.removeFragmentFetchDataListener(this);
				}
            }
			
		}
		
	}
	
	private EventAdapter mEventAdapter = new EventAdapter();
	
	public V1FragmentFetchDataTaskAdapter(IFragmentFetchDataTask task) 
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
		return new V1FragmentInfoAdapter(mTask.fragmentInfo());
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
