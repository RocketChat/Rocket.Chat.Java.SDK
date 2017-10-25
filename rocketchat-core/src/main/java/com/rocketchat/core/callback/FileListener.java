package com.rocketchat.core.callback;

import com.rocketchat.common.RocketChatException;
import com.rocketchat.core.model.FileDescriptor;
import com.rocketchat.core.model.Message;
import java.io.IOException;

/**
 * Created by sachin on 18/8/17.
 */
public interface FileListener {
    void onUploadStarted(String roomId, String fileName, String description);

    void onUploadProgress(int progress, String roomId, String fileName, String description);

    void onUploadComplete(int statusCode, FileDescriptor file, String roomId, String fileName, String description);

    void onUploadError(RocketChatException error, IOException e);

    void onSendFile(Message message, RocketChatException error);
}
