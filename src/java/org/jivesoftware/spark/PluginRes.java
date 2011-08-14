package org.jivesoftware.spark;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.spark.plugin.PluginClassLoader;

public abstract class PluginRes {
	private static PluginClassLoader classLoader;
	private static final Map<String, String> sparkResCache = new HashMap<String, String>();
	private static final Map<String, String> defaultResCache = new HashMap<String, String>();
	private static final Map<String, String> i18nResCache = new HashMap<String, String>();
	private static final Map<String, String> preferenceResCache = new HashMap<String, String>();

    public enum ResourceType {
        SPARK, DEFAULT, PREFERENCES, I18N
    }

    public static void putRes(String key, String value, ResourceType name) {
        if (name.equals(ResourceType.SPARK)) {
            putSparkRes(key, value);
        } else if (name.equals(ResourceType.DEFAULT)) {
            putDefaultRes(key, value);
        } else if (name.equals(ResourceType.PREFERENCES)) {
            putPreferenceRes(key, value);
        } else if (name.equals(ResourceType.I18N)) {
            puti18nRes(key, value);
        }
    }

	private static void putRes(String key, String value, Map<String, String> cache) {
		cache.put(key, value);
	}

	private static String getRes(String key, Map<String, String> cache) {
		return cache.get(key);
	}

	public static void setClassLoader(PluginClassLoader cl) {
		classLoader = cl;
	}

	private static URL getURL(String key, Map<String, String> cache) {
		String value = getRes(key, cache);
		return (classLoader != null && value != null) ? classLoader.getResource(value) : null;
	}

	public static String getSparkRes(String key) {
		return getRes(key, sparkResCache);
	}

	public static String getDefaultRes(String key) {
		return getRes(key, defaultResCache);
	}

	public static void putSparkRes(String key, String value) {
		putRes(key, value, sparkResCache);
	}

	public static void putDefaultRes(String key, String value) {
		putRes(key, value, defaultResCache);
	}

	public static void putPreferenceRes(String key, String value) {
	    putRes(key, value, preferenceResCache);
	}

	public static URL getSparkURL(String key) {
		return getURL(key, sparkResCache);
	}

	public static URL getDefaultURL(String key) {
		return getURL(key, defaultResCache);
	}

	public static String getPreferenceRes(String key) {
        return getRes(key, preferenceResCache);
	}

    public static String getI18nRes(String key) {
        return getRes(key, i18nResCache);
    }

    private static void puti18nRes(String key, String value) {
        putRes(key, value, i18nResCache);
    }
}
