package com.rocketchat.common.listener;

import java.util.List;

/**
 * Created by sachin on 26/7/17.
 */
public interface SimpleListCallback<T> extends Callback {
    void onSuccess(List<T> list);
}
