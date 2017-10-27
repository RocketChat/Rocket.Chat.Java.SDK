package com.rocketchat.common.listener;

/**
 * Created by sachin on 26/7/17.
 */
public interface SimpleValueCallback<T> extends Callback {
    void onSuccess(T data);
}