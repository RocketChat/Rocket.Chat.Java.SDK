package com.rocketchat.core.callback;

import com.rocketchat.common.RocketChatException;
import com.rocketchat.core.model.FileObject;
import com.rocketchat.core.model.RocketChatMessage;

import java.io.IOException;

/**
 * Created by sachin on 18/8/17.
 */
public interface FileListener {
    void onUploadStarted(String roomId, String fileName, String description);

    void onUploadProgress(int progress, String roomId, String fileName, String description);

    void onUploadComplete(int statusCode, FileObject file, String roomId, String fileName, String description);

    void onUploadError(RocketChatException error, IOException e);

    void onSendFile(RocketChatMessage message, RocketChatException error);
}
