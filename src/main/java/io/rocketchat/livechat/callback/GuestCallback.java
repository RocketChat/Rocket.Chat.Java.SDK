package io.rocketchat.livechat.callback;
import io.rocketchat.livechat.middleware.LiveChatMiddleware;
import io.rocketchat.livechat.model.GuestObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * Gets called during user registration or login. Returns agent info. having visitorToken and userId
 */

public interface GuestCallback extends Callback{
    void call(LiveChatMiddleware.CallbackType guestCallbackType,GuestObject object);
}
