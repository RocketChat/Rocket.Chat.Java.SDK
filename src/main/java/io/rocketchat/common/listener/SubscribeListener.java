package io.rocketchat.common.listener;

/**
 * Created by sachin on 12/6/17.
 */

public interface SubscribeListener extends Listener{

    void onSubscribe(Boolean isSubscribed, String subId);
}
