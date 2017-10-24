package com.rocketchat.common.listener;

import com.rocketchat.common.RocketChatException;

public interface Callback {
    /**
     * Called when the request could not be executed due to cancellation, a connectivity problem,
     * timeout, parsing errors, authentication error, etc. Because networks can fail during an
     * exchange, it is possible that the remote server accepted the request before the failure.
     */
    void onError(RocketChatException error);
}
