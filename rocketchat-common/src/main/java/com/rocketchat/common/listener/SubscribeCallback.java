package com.rocketchat.common.listener;

/**
 * Created by sachin on 12/6/17.
 */

public interface SubscribeCallback extends Listener {
    void onSubscribe(Boolean isSubscribed, String subId);
}
