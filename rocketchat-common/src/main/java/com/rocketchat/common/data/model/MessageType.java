package com.rocketchat.common.data.model;

import com.squareup.moshi.Json;

public enum MessageType {
    @Json(name = "connected")
    CONNECTED,
    @Json(name = "result")
    RESULT,
    @Json(name = "ready")
    READY,
    @Json(name = "nosub")
    UNSUBSCRIBED,
    @Json(name = "updated")
    UPDATED,
    @Json(name = "added")
    ADDED,
    @Json(name = "changed")
    CHANGED,
    @Json(name = "removed")
    REMOVED,
    @Json(name = "ping")
    PING,
    @Json(name = "pong")
    PONG
}