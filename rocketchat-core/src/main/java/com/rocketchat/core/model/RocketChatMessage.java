package com.rocketchat.core.model;

import com.rocketchat.common.data.model.Message;
import com.rocketchat.core.model.attachment.Attachment;
import com.rocketchat.core.model.attachment.TAttachment;
import java.util.ArrayList;
import java.util.List;
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
    private List<MessageUrl> urls; //A collection of URLs metadata. Available when the message contains at least one URL
    private List<TAttachment> attachments; //A collection of attachment objects, available only when the message has at least one attachment
    private String avatar; //A url to an image, that is accessible to anyone, to display as the avatar instead of the message user’s account avatar
    private Boolean parseUrls; //Whether Rocket.Chat should try and parse the urls or not
    private JSONObject translations;
    private List<String> starred_by;
    private JSONObject reactions; // Need to dump to get data

    //This is required for message pin and unpin
    private JSONObject rawMessage;

    //File
    FileObject file;

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

        if (object.opt("file") != null) {
            file = new FileObject(object.optJSONObject("file"));
        }

        if (object.opt("urls") != null) {
            urls = new ArrayList<>();
            JSONArray array = object.optJSONArray("urls");
            for (int i = 0; i < array.length(); i++) {
                urls.add(new MessageUrl(array.optJSONObject(i)));
            }
        }

        if (object.opt("attachments") != null) {
            attachments = new ArrayList<>();
            JSONArray array = object.optJSONArray("attachments");
            for (int i = 0; i < array.length(); i++) {
                if (file == null) {
                    attachments.add(new Attachment.TextAttachment(array.optJSONObject(i)));
                } else {
                    String type = file.getFileType();
                    if (type.contains("image")) {
                        attachments.add(new Attachment.ImageAttachment(array.optJSONObject(i)));
                    } else if (type.contains("video")) {
                        attachments.add(new Attachment.VideoAttachment(array.optJSONObject(i)));
                    } else if (type.contains("audio")) {
                        attachments.add(new Attachment.AudioAttachment(array.optJSONObject(i)));
                    }
                }
            }
        }

        avatar = object.optString("avatar");
        parseUrls = object.optBoolean("parseUrls");
        translations = object.optJSONObject("translations");

        if (object.opt("starred") != null) {
            starred_by = new ArrayList<>();
            JSONArray array = object.optJSONArray("starred");
            for (int i = 0; i < array.length(); i++) {
                starred_by.add(array.optJSONObject(i).optString("_id"));
            }
        }

        reactions = object.optJSONObject("reactions");

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

    public List<MessageUrl> getUrls() {
        return urls;
    }

    public List<TAttachment> getAttachments() {
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


    // URLS can be present in any type of message

    public enum Type {
        TEXT,
        ATTACHMENT,
        MESSAGE_EDITED,
        MESSAGE_STARRED,
        MESSAGE_REACTION,
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

    // TODO: 22/8/17 Try sending each type of message and test it accordingly
    public Type getMsgType() {
        if (getMessagetype() != null && !getMessagetype().equals("")) {
            return getType(getMessagetype());
        } else {
            if (getEditedBy() != null) {
                return Type.MESSAGE_EDITED;
            } else if (starred_by != null) {
                return Type.MESSAGE_STARRED;
            } else if (reactions != null) {
                return Type.MESSAGE_REACTION;
            } else if (attachments != null) {
                return Type.ATTACHMENT;
            } else {
                return Type.TEXT;
            }
        }
    }

    private static Type getType(String s) {
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
