/* Copyright 2018 Urban Airship and Contributors */

package com.urbanairship.cordova;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.urbanairship.Logger;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Config Utils.
 */
class ConfigUtils {
    private static final String UA_PREFIX = "com.urbanairship";
    private static final String SENDER_PREFIX = "sender:";

    /**
     * Convert the log level string to an int.
     *
     * @param logLevel        The log level as a string.
     * @param defaultLogLevel Default log level.
     * @return The log level.
     */
    public static int parseLogLevel(String logLevel, int defaultLogLevel) {
        if (logLevel == null || logLevel.length() == 0) {
            return defaultLogLevel;
        }
        String logString = logLevel.trim().toLowerCase();
        if (logString.equals("verbose")) {
            return Log.VERBOSE;
        } else if (logString.equals("debug")) {
            return Log.DEBUG;
        } else if (logString.equals("info")) {
            return Log.INFO;
        } else if (logString.equals("warn")) {
            return Log.WARN;
        } else if (logString.equals("error")) {
            return Log.ERROR;
        } else if (logString.equals("none")) {
            return Log.ASSERT;
        } else {
            return defaultLogLevel;
        }
    }

    /**
     * Parses the sender ID.
     *
     * @param value The value from config.
     * @return The sender ID.
     */
    public static String parseSender(String value) {
        if (value == null) {
            return null;
        }

        if (value.startsWith("sender:")) {
            return value.substring(SENDER_PREFIX.length());
        }

        return value;
    }

    /**
     * Parses the config.xml file for any Urban Airship config.
     *
     * @param context The application context.
     */
    public static Map<String, String> parseConfigXml(Context context) {
        Map<String, String> config = new HashMap<String, String>();
        int id = context.getResources().getIdentifier("config", "xml", context.getPackageName());
        if (id == 0) {
            return config;
        }

        XmlResourceParser xml = context.getResources().getXml(id);

        int eventType = -1;
        while (eventType != XmlResourceParser.END_DOCUMENT) {

            if (eventType == XmlResourceParser.START_TAG) {
                if (xml.getName().equals("preference")) {
                    String name = xml.getAttributeValue(null, "name").toLowerCase(Locale.US);
                    String value = xml.getAttributeValue(null, "value");

                    if (name.startsWith(UA_PREFIX) && value != null) {
                        config.put(name, value);
                        Logger.verbose("Found " + name + " in config.xml with value: " + value);
                    }
                }
            }

            try {
                eventType = xml.next();
            } catch (Exception e) {
                Logger.error("Error parsing config file", e);
            }
        }

        return config;
    }
}
