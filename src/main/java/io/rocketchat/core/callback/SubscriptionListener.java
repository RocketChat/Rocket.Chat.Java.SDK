package io.rocketchat.core.callback;

import java.util.List;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.listener.Listener;
import io.rocketchat.core.model.SubscriptionObject;

/**
 * Created by sachin on 20/7/17.
 */
public class SubscriptionListener {
    public interface GetSubscriptionListener extends Listener {
        void onGetSubscriptions(List<SubscriptionObject> subscriptions, ErrorObject error);
    }
}
