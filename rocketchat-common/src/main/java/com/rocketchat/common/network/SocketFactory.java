package com.rocketchat.common.network;

import com.rocketchat.common.SocketListener;
import com.rocketchat.common.utils.Logger;

import okhttp3.OkHttpClient;

public interface SocketFactory {
    Socket create(OkHttpClient client, String url, Logger logger, SocketListener socketListener);
}
