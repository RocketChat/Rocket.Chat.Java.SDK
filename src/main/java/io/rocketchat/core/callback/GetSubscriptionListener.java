package io.rocketchat.core.callback;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.Listener;
import io.rocketchat.core.model.SubscriptionObject;
import java.util.List;

/**
 * Created by sachin on 20/7/17.
 */

public interface GetSubscriptionListener extends Listener {
    void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error);
}
