package io.rocketchat.core.model;

import org.json.JSONArray;
import org.json.JSONObject;

import io.rocketchat.common.data.model.Message;

/**
 * Created by sachin on 21/7/17.
 */

/**
 * // TODO: 21/7/17 Convert members to strict data
 */

public class RocketChatMessage extends Message {

    private JSONArray mentions;
    private JSONArray channels;
    private Boolean groupable;  //Boolean that states whether or not this message should be grouped together with other messages from the same userBoolean that states whether or not this message should be grouped together with other messages from the same user
    private JSONArray urls; //A collection of URLs metadata. Available when the message contains at least one URL
    private JSONArray attachments; //A collection of attachment objects, available only when the message has at least one attachment
    private String avatar; //A url to an image, that is accessible to anyone, to display as the avatar instead of the message user’s account avatar
    private Boolean parseUrls; //Whether Rocket.Chat should try and parse the urls or not
    private JSONObject translations;

    //This is required for message pin and unpin
    private JSONObject rawMessage;

    public RocketChatMessage(JSONObject object) {
        super(object);
        mentions = object.optJSONArray("mentions");
        channels = object.optJSONArray("channels");
        groupable = object.optBoolean("groupable");
        urls = object.optJSONArray("urls");
        attachments = object.optJSONArray("attachments");
        avatar = object.optString("avatar");
        parseUrls = object.optBoolean("parseUrls");
        translations = object.optJSONObject("translations");

        rawMessage = object;
    }

    public JSONArray getMentions() {
        return mentions;
    }

    public JSONArray getChannels() {
        return channels;
    }

    public Boolean getGroupable() {
        return groupable;
    }

    public JSONArray getUrls() {
        return urls;
    }

    public JSONArray getAttachments() {
        return attachments;
    }

    public String getAvatar() {
        return avatar;
    }

    public Boolean getParseUrls() {
        return parseUrls;
    }

    public JSONObject getTranslations() {
        return translations;
    }

    public JSONObject getRawJsonObject() {
        return rawMessage;
    }
}
