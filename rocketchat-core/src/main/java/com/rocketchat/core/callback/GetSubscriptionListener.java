package com.rocketchat.core.callback;

import com.rocketchat.common.RocketChatApiException;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.Subscription;

import java.util.List;

/**
 * Created by sachin on 20/7/17.
 */

public interface GetSubscriptionListener extends Listener {
    void onGetSubscriptions(List<Subscription> subscriptions, RocketChatApiException error);
}
