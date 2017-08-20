package io.rocketchat.core.model;

import org.json.JSONObject;

/**
 * Created by sachin on 20/8/17.
 */
public class Attachment {
    String title;
    String type;
    String description;
    String title_link;
    Boolean title_link_download;
    String image_url;
    String image_type;
    int image_size;

    Attachment(JSONObject object) {
        title = object.optString("title");
        type = object.optString("type");
        description = object.optString("description");
        title_link = object.optString("title_link");
        title_link_download = object.optBoolean("title_link_download");
        image_url = object.optString("image_url");
        image_type = object.optString("image_type");
        image_size = object.optInt("image_size");

    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle_link() {
        return title_link;
    }

    public Boolean getTitle_link_download() {
        return title_link_download;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getImage_type() {
        return image_type;
    }

    public int getImage_size() {
        return image_size;
    }

}
