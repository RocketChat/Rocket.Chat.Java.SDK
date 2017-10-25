package com.rocketchat.core.adapter;

import com.rocketchat.common.RocketChatException;
import com.rocketchat.core.callback.FileListener;
import com.rocketchat.core.model.FileDescriptor;
import com.rocketchat.core.model.Message;
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
    public void onUploadComplete(int statusCode, FileDescriptor file, String roomId, String fileName, String description) {

    }

    @Override
    public void onUploadError(RocketChatException error, IOException e) {

    }

    @Override
    public void onSendFile(Message message, RocketChatException error) {

    }
}
