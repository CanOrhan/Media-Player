/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.ttml;

import java.io.IOException;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class Profile
{
    protected String use;
    protected XML.Id id;
    protected Vector<Metadata> metadataTable;
    protected Vector<Features> featuresTable;
    protected Vector<Extensions> extensionsTable;
    private Profile()
    {

    }


    private void parseParameters(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        String startName = xpp.getName();

        use = Util.getAttributeValue(xpp, "use", null);


        int eventType = xpp.next();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            switch(eventType)
            {
                case XmlPullParser.START_TAG:
                    {
                        String name = xpp.getName();
                        if(name != null)
                        {
                            if(Metadata.isMetaData(name) == true)
                            {
                                if(metadataTable == null)
                                {
                                    metadataTable = new Vector <Metadata>();
                                }

                                Metadata metadata = Metadata.parse(xpp);
                                metadataTable.add(metadata);
                            }
                            else if(name.equals("features"))
                            {
                                if(featuresTable == null)
                                {
                                    featuresTable = new Vector <Features>();
                                }

                                Features features = Features.parse(xpp);
                                if(features != null && featuresTable != null)
                                     featuresTable.add(features);
                            }
                            else if(name.equals("extensions") == true)
                            {
                                if(extensionsTable == null)
                                {
                                    extensionsTable = new Vector <Extensions>();
                                }
                                Extensions extensions = Extensions.parse(xpp);
                                if(extensions != null && extensionsTable != null)
                                    extensionsTable.add(extensions);
                            }

                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    {
                        String name = xpp.getName();
                        if(name != null)
                        {
                            if(name.equals(startName) == true)
                            {
                               return;
                            }
                        }
                    }
                    break;
            }
            eventType = xpp.next();
        }


    }


    static public  Profile parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        Profile parameters = new Profile();

        parameters.parseParameters(xpp);

        return parameters;
    }


    public String getUse() {
        return use;
    }


    public XML.Id getId() {
        return id;
    }


    public Vector<Metadata> getMetadataTable() {
        return metadataTable;
    }


    public Vector<Features> getFeaturesTable() {
        return featuresTable;
    }


    public Vector<Extensions> getExtensionsTable() {
        return extensionsTable;
    }



}
