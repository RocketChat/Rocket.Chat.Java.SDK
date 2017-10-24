package com.rocketchat.common.utils;

/**
 * This class has useful methods to handle URL links.
 *
 * @author Filipe de Lima Brito (filipedelimabrito@gmail.com)
 * @since 0.8.0
 */
public class Url {

    /**
     * Returns an URL with no spaces and inverted slashes.
     *
     * @param url The URL.
     * @return The URL with no spaces and inverted slashes.
     */
    public static String getSafeUrl(String url) {
        return url.replace(" ", "%20").replace("\\", "");
    }
}
