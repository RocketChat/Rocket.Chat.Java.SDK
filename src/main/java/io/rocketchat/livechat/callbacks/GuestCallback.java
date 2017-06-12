package io.rocketchat.livechat.callbacks;
import io.rocketchat.livechat.models.GuestObject;

/**
 * Created by sachin on 9/6/17.
 */

/**
 * Gets called during user registration or login. Returns agent info. having token and userId
 */

public interface GuestCallback extends Callback{
    void call(GuestObject object);
}
