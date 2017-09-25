package com.rocketchat.core.model;

import com.google.auto.value.AutoValue;
import com.rocketchat.common.data.model.BaseMessage;
import com.rocketchat.common.data.model.User;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by sachin on 21/7/17.
 */

/**
 * // TODO: 21/7/17 Convert members to strict data
 */

@AutoValue
public abstract class Message extends BaseMessage {

    @Nullable public abstract Boolean groupable();
    @Nullable public abstract List<Url> urls();
    @Nullable public abstract String avatar();
    @Nullable public abstract List<User> mentions();
    @Nullable public abstract Boolean parseUrls();
    // TODO -> channels
    // TODO -> attachments
    // TODO -> translations
    // TODO -> reactions
    @Json(name = "starred") @Nullable public abstract List<User> starredBy();

    public static JsonAdapter<Message> jsonAdapter(Moshi moshi) {
        return new AutoValue_Message.MoshiJsonAdapter(moshi);
    }

    /*private JSONArray mentions;
    private JSONArray channels;
    private Boolean groupable;  //Boolean that states whether or not this message should be grouped together with other messages from the same userBoolean that states whether or not this message should be grouped together with other messages from the same user
    private List<Url> urls; //A collection of URLs metadata. Available when the message contains at least one URL
    private List<TAttachment> attachments; //A collection of attachment objects, available only when the message has at least one attachment
    private String avatar; //A url to an image, that is accessible to anyone, to display as the avatar instead of the message user’s account avatar
    private Boolean parseUrls; //Whether Rocket.Chat should try and parse the urls or not
    private JSONObject translations;
    private List<String> starred_by;
    private JSONObject reactions; // Need to dump to get data

    //This is required for message pin and unpin
    private JSONObject rawMessage;

    //File
    FileObject file;*/

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


    /*public RocketChatMessage(JSONObject object) {
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
                urls.add(new Url(array.optJSONObject(i)));
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

    public JSONArray mentions() {
        return mentions;
    }

    public JSONArray channels() {
        return channels;
    }

    public Boolean groupable() {
        return groupable;
    }

    public List<Url> urls() {
        return urls;
    }

    public List<TAttachment> attachments() {
        return attachments;
    }

    public String avatar() {
        return avatar;
    }

    public Boolean parseUrls() {
        return parseUrls;
    }

    public JSONObject translations() {
        return translations;
    }

    public JSONObject getRawJsonObject() {
        return rawMessage;
    }*/


    // URLS can be present in any type of message

    public enum MessageType {
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
    public MessageType getMsgType() {
        if (type() != null && !type().equals("")) {
            return getType(type());
        } else {
            /*if (editedBy() != null) {
                return Type.MESSAGE_EDITED;
            } else if (starred_by != null) {
                return Type.MESSAGE_STARRED;
            } else if (reactions != null) {
                return Type.MESSAGE_REACTION;
            } else if (attachments != null) {
                return Type.ATTACHMENT;
            } else {*/
                return MessageType.TEXT;
            //}
        }
    }

    private static MessageType getType(String s) {
        switch (s) {
            case TYPE_MESSAGE_REMOVED:
                return MessageType.MESSAGE_REMOVED;
            case TYPE_ROOM_NAME_CHANGED:
                return MessageType.ROOM_NAME_CHANGED;
            case TYPE_ROOM_ARCHIVED:
                return MessageType.ROOM_ARCHIVED;
            case TYPE_ROOM_UNARCHIVED:
                return MessageType.ROOM_UNARCHIVED;
            case TYPE_USER_ADDED:
                return MessageType.USER_ADDED;
            case TYPE_USER_REMOVED:
                return MessageType.USER_REMOVED;
            case TYPE_USER_JOINED:
                return MessageType.USER_JOINED;
            case TYPE_USER_LEFT:
                return MessageType.USER_LEFT;
            case TYPE_USER_MUTED:
                return MessageType.USER_MUTED;
            case TYPE_USER_UNMUTED:
                return MessageType.USER_UNMUTED;
            case TYPE_WELCOME:
                return MessageType.WELCOME;
            case TYPE_SUBSCRIPTION_ROLE_ADDED:
                return MessageType.SUBSCRIPTION_ROLE_ADDED;
            case TYPE_SUBSCRIPTION_ROLE_REMOVED:
                return MessageType.SUBSCRIPTION_ROLE_REMOVED;
        }
        return MessageType.OTHER;
    }
}
