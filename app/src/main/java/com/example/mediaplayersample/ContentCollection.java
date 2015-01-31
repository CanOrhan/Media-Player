/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample;

import java.util.ArrayList;

import com.example.mediaplayersample.ContentCollection.ContentEntry.ContentType;
import com.example.mediaplayersample.ContentCollection.ContentEntry.LicenseRequestInformation;

public class ContentCollection
{
	public static class ContentEntry
	{
		public static class LicenseRequestInformation
		{
			private String m_ServerUrlOverrideUrl;
			private String m_CustomData;

			public LicenseRequestInformation(String serverUrlOverride, String CustomData)
			{
				m_ServerUrlOverrideUrl = serverUrlOverride;
				m_CustomData = CustomData;
			}

			public String getServerUrlOverride()
			{
				return m_ServerUrlOverrideUrl;
			}

			public String getCustomData()
			{
				return m_CustomData;
			}
		}

		public enum ContentType
		{
			AUDIO, VIDEO
		};

		private String m_title;
		private String m_url;
		private ContentType m_contentType;

		private LicenseRequestInformation m_licenseRequestInfo = null;

		public ContentEntry(String title, String url, ContentType contentType, LicenseRequestInformation licenseInfo)
		{
			m_title = title;
			m_url = url;
			m_contentType = contentType;
			m_licenseRequestInfo = licenseInfo;
		}

		public String getTitle()
		{
			return m_title;
		}

		public String getUrl()
		{
			return m_url;
		}

		public ContentType getContentType()
		{
			return m_contentType;
		}

		public LicenseRequestInformation getLicenseRequestInfo()
		{
			return m_licenseRequestInfo;
		}
	};

	private ArrayList<ContentEntry> m_Contents = new ArrayList<ContentEntry>();

	public ContentCollection()
	{

		LicenseRequestInformation defaultInternalLicenseRequestInfo = new LicenseRequestInformation(
				"http://playready.directtaps.net/pr/svc/rightsmanager.asmx?PlayEnablers=786627D8-C2A6-44BE-8F88-08AE255B01A7",
				null);

		m_Contents.add(new ContentEntry("SuperSpeedway_720 (Smooth Streaming)",
				"http://playready.directtaps.net/smoothstreaming/SSWSS720H264/SuperSpeedway_720.ism/Manifest", 
				ContentType.VIDEO,
				defaultInternalLicenseRequestInfo));
		m_Contents.add(new ContentEntry("SuperSpeedway_720 Encrypted (Smooth Streaming)",
				"http://playready.directtaps.net/smoothstreaming/SSWSS720H264PR/SuperSpeedway_720.ism/Manifest", 
				ContentType.VIDEO,
				defaultInternalLicenseRequestInfo));

		m_Contents.add(new ContentEntry("sintel_trailer-720p (Smooth Streaming)",
				"http://playready.directtaps.net/smoothstreaming/WP7/sintel_trailer-720p.ism/Manifest", 
				ContentType.VIDEO, 
				defaultInternalLicenseRequestInfo));

		m_Contents.add(new ContentEntry("Adaptive (480p down to 240p) Dash",
				"http://playready.directtaps.net/media/dash/DASHSDH264AAC/sintel_trailer.SD.adapt.dash_ond.mpd", 
				ContentType.VIDEO,
				defaultInternalLicenseRequestInfo));
		m_Contents.add(new ContentEntry("Adaptive (480p down to 240p) Dash Encrypted",
				"http://playready.directtaps.net/media/dash/DASHSDH264AACENC/sintel_trailer.SD.adapt.dash_ond.mpd", 
				ContentType.VIDEO,
				defaultInternalLicenseRequestInfo));
		m_Contents.add(new ContentEntry("Video only (480p) Dash",
				"http://playready.directtaps.net/media/dash/DASHSDVIDEO/sintel_trailer.640x480.1000.b30.h264.dash_ond.mpd", 
				ContentType.VIDEO,
				defaultInternalLicenseRequestInfo));

		m_Contents.add(new ContentEntry(
				"1a-Netflix-1 Dash", 
				"http://dash.edgesuite.net/dash264/TestCases/1a/netflix/exMPD_BIP_TC1.mpd", 
				ContentType.VIDEO,
				defaultInternalLicenseRequestInfo));
		m_Contents.add(new ContentEntry(
				"1a-Sony-2 Dash", 
				"http://dash.edgesuite.net/dash264/TestCases/1a/sony/SNE_DASH_SD_CASE1A_REVISED.mpd",
				ContentType.VIDEO, 
				defaultInternalLicenseRequestInfo));
		m_Contents.add(new ContentEntry(
				"1b-Envivio-1 Dash", 
				"http://dash.edgesuite.net/dash264/TestCases/1b/envivio/manifest.mpd", 
				ContentType.VIDEO,
				defaultInternalLicenseRequestInfo));
		m_Contents.add(new ContentEntry(
				"4b-Sony-1 Dash", 
				"http://dash.edgesuite.net/dash264/TestCases/4b/sony/SNE_DASH_CASE4B_SD_REVISED.mpd",
				ContentType.VIDEO, 
				defaultInternalLicenseRequestInfo));
	}

	public int getContentCount()
	{
		return m_Contents.size();
	}

	public ContentEntry getContentAtIndex(int index)
	{
		return m_Contents.get(index);
	}
}
