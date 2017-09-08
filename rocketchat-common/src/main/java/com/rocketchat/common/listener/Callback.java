package com.rocketchat.common.listener;

import com.rocketchat.common.data.model.Error;

public interface Callback {
    void onError(Error error);
}
