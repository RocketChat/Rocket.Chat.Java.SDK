package com.rocketchat.core.adapter;

import com.rocketchat.common.data.model.ApiError;
import com.rocketchat.core.callback.FileListener;
import com.rocketchat.core.model.FileObject;
import com.rocketchat.core.model.RocketChatMessage;
import java.io.IOException;

/**
 * Created by sachin on 20/8/17.
 */
public class FileAdapter implements FileListener {
    @Override
    public void onUploadStarted(String roomId, String fileName, String description) {

    }

    @Override
    public void onUploadProgress(int progress, String roomId, String fileName, String description) {

    }

    @Override
    public void onUploadComplete(int statusCode, FileObject file, String roomId, String fileName, String description) {

    }

    @Override
    public void onUploadError(ApiError error, IOException e) {

    }

    @Override
    public void onSendFile(RocketChatMessage message, ApiError error) {

    }
}
