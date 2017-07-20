package io.rocketchat.core.callback;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.core.model.SubscriptionObject;

import java.util.ArrayList;

/**
 * Created by sachin on 20/7/17.
 */
public class SubscriptionListener {
    public interface GetSubscriptionListener{
        void onGetSubscriptions(ArrayList <SubscriptionObject> subscriptions,ErrorObject error);
    }
}
