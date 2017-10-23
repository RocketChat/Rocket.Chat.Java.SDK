package com.rocketchat.common.listener;

import org.json.JSONObject;

public interface StreamCollectionListener<T> {
    void onAdded(String documentKey, T document);

    void onChanged(String documentKey, T values);

    void onRemoved(String documentKey);
}
