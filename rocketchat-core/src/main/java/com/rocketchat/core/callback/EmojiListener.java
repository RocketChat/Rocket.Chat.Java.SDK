package com.rocketchat.core.callback;

import com.rocketchat.common.RocketChatApiException;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.Emoji;
import java.util.List;

/**
 * Created by sachin on 27/7/17.
 */
public interface EmojiListener extends Listener {
    void onListCustomEmoji(List<Emoji> emojis, RocketChatApiException error);
}
