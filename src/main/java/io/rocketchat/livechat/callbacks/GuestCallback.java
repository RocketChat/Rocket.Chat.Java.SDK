package io.rocketchat.livechat.callbacks;
import io.rocketchat.livechat.models.GuestObject;

/**
 * Created by sachin on 9/6/17.
 */

public interface GuestCallback extends Callback{
    void call(GuestObject object);
}
