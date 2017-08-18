package io.rocketchat.core.model;

import io.rocketchat.common.data.model.Message;
import org.json.JSONArray;
import org.json.JSONObject;

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


    private static final String TYPE_MESSAGE_REMOVED = "rm";
    private static final String TYPE_ROOM_NAME_CHANGED = "r";
    private static final String TYPE_ROOM_ARCHIVED = "room-archived";
    private static final String TYPE_ROOM_UNARCHIVED = "room-unarchived";
    private static final String TYPE_USER_ADDED = "au";
    private static final String TYPE_USER_REMOVED = "ru";
    private static final String TYPE_USER_JOINED = "uj";
    private static final String TYPE_USER_LEFT = "ul";
    private static final String TYPE_USER_MUTED = "user-muted";
    private static final String TYPE_USER_UNMUTED = "user-unmuted";
    private static final String TYPE_WELCOME = "wm";
    private static final String TYPE_SUBSCRIPTION_ROLE_ADDED = "subscription-role-added";
    private static final String TYPE_SUBSCRIPTION_ROLE_REMOVED = "subscription-role-removed";


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

    enum Type {
        TEXT,
        TEXT_ATTACHMENT,
        IMAGE,
        AUDIO,
        VIDEO,
        URL,
        MESSAGE_REMOVED,
        ROOM_NAME_CHANGED,
        ROOM_ARCHIVED,
        ROOM_UNARCHIVED,
        USER_ADDED,
        USER_REMOVED,
        USER_JOINED,
        USER_LEFT,
        USER_MUTED,
        USER_UNMUTED,
        WELCOME,
        SUBSCRIPTION_ROLE_ADDED,
        SUBSCRIPTION_ROLE_REMOVED,
        OTHER
    }

    public static Type getType(String s) {
        if (s.equals(TYPE_MESSAGE_REMOVED)) {
            return Type.MESSAGE_REMOVED;
        } else if (s.equals(TYPE_ROOM_NAME_CHANGED)) {
            return Type.ROOM_NAME_CHANGED;
        } else if (s.equals(TYPE_ROOM_ARCHIVED)) {
            return Type.ROOM_ARCHIVED;
        } else if (s.equals(TYPE_ROOM_UNARCHIVED)) {
            return Type.ROOM_UNARCHIVED;
        } else if (s.equals(TYPE_USER_ADDED)) {
            return Type.USER_ADDED;
        } else if (s.equals(TYPE_USER_REMOVED)) {
            return Type.USER_REMOVED;
        } else if (s.equals(TYPE_USER_JOINED)) {
            return Type.USER_JOINED;
        } else if (s.equals(TYPE_USER_LEFT)) {
            return Type.USER_LEFT;
        } else if (s.equals(TYPE_USER_MUTED)) {
            return Type.USER_MUTED;
        } else if (s.equals(TYPE_USER_UNMUTED)) {
            return Type.USER_UNMUTED;
        } else if (s.equals(TYPE_WELCOME)) {
            return Type.WELCOME;
        } else if (s.equals(TYPE_SUBSCRIPTION_ROLE_ADDED)) {
            return Type.SUBSCRIPTION_ROLE_ADDED;
        } else if (s.equals(TYPE_SUBSCRIPTION_ROLE_REMOVED)) {
            return Type.SUBSCRIPTION_ROLE_REMOVED;
        }
        return Type.OTHER;
    }
}
