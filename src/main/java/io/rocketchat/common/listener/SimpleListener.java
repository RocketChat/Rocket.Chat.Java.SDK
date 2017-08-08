package io.rocketchat.common.listener;

import io.rocketchat.common.data.model.ErrorObject;

/**
 * Created by sachin on 26/7/17.
 */
public interface SimpleListener extends Listener {
    void callback(Boolean success, ErrorObject error);
}
