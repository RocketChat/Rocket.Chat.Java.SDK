package com.rocketchat.core.callback;

import com.rocketchat.common.data.model.ErrorObject;
import com.rocketchat.common.listener.Listener;
import com.rocketchat.core.model.SubscriptionObject;

import java.util.List;

/**
 * Created by sachin on 20/7/17.
 */

public interface GetSubscriptionListener extends Listener {
    void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error);
}
