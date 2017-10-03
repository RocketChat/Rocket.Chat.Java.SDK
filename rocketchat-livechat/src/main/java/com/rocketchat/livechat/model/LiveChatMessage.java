package com.rocketchat.livechat.model;

import com.google.auto.value.AutoValue;
import com.rocketchat.common.data.model.BaseMessage;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import javax.annotation.Nullable;

/**
 * Created by sachin on 9/6/17.
 */
@AutoValue
public abstract class LiveChatMessage extends BaseMessage {

    public static String MESSAGE_TYPE_COMMAND = "command";
    public static String MESSAGE_TYPE_CLOSE = "livechat-close";

    public abstract String token();
    public abstract Boolean newRoom();
    public abstract Boolean showConnecting();
    @Nullable public abstract String sandstormSessionId();

    public static JsonAdapter<LiveChatMessage> jsonAdapter(Moshi moshi) {
        return new AutoValue_LiveChatMessage.MoshiJsonAdapter(moshi);
    }
}
