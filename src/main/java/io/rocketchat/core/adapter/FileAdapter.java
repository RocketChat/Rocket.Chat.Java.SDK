package io.rocketchat.core.adapter;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.core.callback.FileListener;
import io.rocketchat.core.model.FileObject;
import io.rocketchat.core.model.RocketChatMessage;
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
    public void onUploadError(ErrorObject error, IOException e) {

    }

    @Override
    public void onSendFile(RocketChatMessage message, ErrorObject error) {

    }
}
