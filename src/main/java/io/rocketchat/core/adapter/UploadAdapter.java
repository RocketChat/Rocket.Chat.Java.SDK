package io.rocketchat.core.adapter;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.core.callback.UploadListener;
import io.rocketchat.core.model.FileObject;
import java.io.IOException;

/**
 * Created by sachin on 20/8/17.
 */
public class UploadAdapter implements UploadListener{
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
}
