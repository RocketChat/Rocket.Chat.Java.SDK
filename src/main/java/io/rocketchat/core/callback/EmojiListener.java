package io.rocketchat.core.callback;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.Listener;
import io.rocketchat.core.model.Emoji;
import java.util.List;

/**
 * Created by sachin on 27/7/17.
 */
public interface EmojiListener extends Listener {
    void onListCustomEmoji(List<Emoji> emojis, ErrorObject error);
}
