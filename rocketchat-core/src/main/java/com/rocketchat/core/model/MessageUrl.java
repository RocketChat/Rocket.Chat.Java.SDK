package com.rocketchat.core.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 22/8/17.
 */

public class MessageUrl {
    private String url;
    private Boolean ignoreParse;
    private Meta meta;
    private ParsedUrl parsedUrl;

    public MessageUrl (JSONObject object) {
        url = object.optString("url");
        ignoreParse = object.optBoolean("ignoreParse");
        try {
            if (object.opt("meta") != null) {
                meta = new Meta(object.getJSONObject("meta"));
            }
            if (object.opt("parsedUrl") != null) {
                parsedUrl = new ParsedUrl(object.getJSONObject("parsedUrl"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    public Meta getMeta() {
        return meta;
    }

    public ParsedUrl getParsedUrl() {
        return parsedUrl;
    }

    public Boolean getIgnoreParse() {
        return ignoreParse;
    }

    public class Meta {
        private String pageTitle;
        private String fbAppId;
        private String description;
        private String ogImage;
        private String ogSiteName;
        private String ogType;
        private String ogTitle;
        private String ogUrl;
        private String ogDescription;

        public Meta (JSONObject object) {
            try {
                pageTitle = object.getString("pageTitle");
                fbAppId = object.getString("fbAppId");
                description = object.getString("description");
                ogImage = object.getString("ogImage");
                ogSiteName = object.getString("ogSiteName");
                ogType = object.getString("ogType");
                ogTitle = object.getString("ogTitle");
                ogUrl = object.getString("ogUrl");
                ogDescription = object.getString("ogDescription");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getPageTitle() {
            return pageTitle;
        }

        public String getFbAppId() {
            return fbAppId;
        }

        public String getDescription() {
            return description;
        }

        public String getOgImage() {
            return ogImage;
        }

        public String getOgSiteName() {
            return ogSiteName;
        }

        public String getOgType() {
            return ogType;
        }

        public String getOgTitle() {
            return ogTitle;
        }

        public String getOgUrl() {
            return ogUrl;
        }

        public String getOgDescription() {
            return ogDescription;
        }
    }

    public class ParsedUrl {
        private String host;
        private String hash;
        private String pathname;
        private String protocol;
        private Object port;
        private Object query;
        private Object search;
        private String hostname;

        public ParsedUrl (JSONObject object) {
            try {
                host = object.getString("host");
                hash = object.getString("hash");
                pathname = object.getString("pathname");
                protocol = object.getString("protocol");
                port = object.opt("port");
                query = object.opt("query");
                search = object.opt("search");
                hostname = object.optString("hostname");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getHost() {
            return host;
        }

        public String getHash() {
            return hash;
        }

        public String getPathname() {
            return pathname;
        }

        public String getProtocol() {
            return protocol;
        }

        public Object getPort() {
            return port;
        }

        public Object getQuery() {
            return query;
        }

        public Object getSearch() {
            return search;
        }

        public String getHostname() {
            return hostname;
        }
    }
}
