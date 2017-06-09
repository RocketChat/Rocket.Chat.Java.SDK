package io.rocketchat.livechat.callbacks;

import io.rocketchat.livechat.models.MessageObject;

import java.util.ArrayList;

/**
 * Created by sachin on 9/6/17.
 */
public interface MessagesCallback extends Callback{
    void call(ArrayList <MessageObject> list,int unreadNotLoaded);
}
