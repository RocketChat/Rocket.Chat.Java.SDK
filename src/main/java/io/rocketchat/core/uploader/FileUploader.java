package io.rocketchat.core.uploader;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.utils.MultipartUploader;
import io.rocketchat.common.utils.Utils;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.core.callback.UploadListener;
import io.rocketchat.core.model.FileObject;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by sachin on 18/8/17.
 */

public class FileUploader implements IFileUpload.UfsCreateListener,
        IFileUpload.UfsCompleteListener {

    public static final String DEFAULT_STORE = "Uploads";

    RocketChatAPI api;
    File file;
    String newFileName;
    String description;
    String roomId;
    String charset = "UTF-8";
    MultipartUploader multipart;
    UploadListener uploadListener;
    int statusCode;

    public FileUploader(RocketChatAPI api, File file, String newFileName, String description, String roomId, UploadListener uploadListener) {
        this.api = api;
        this.file = file;
        this.newFileName = newFileName;
        this.description = description;
        this.roomId = roomId;
        this.uploadListener = uploadListener;
    }

    public void startUpload() {
        api.createUFS(newFileName, (int) file.length(), Utils.getFileTypeUsingName(newFileName), roomId, description, DEFAULT_STORE, this);
    }

    @Override
    public void onUfsCreate(FileUploadToken token, ErrorObject error) {
        if (error == null) {
            uploadListener.onUploadStarted(roomId, newFileName, description);
            try {
                multipart = new MultipartUploader(token.getUrl(), charset);
                multipart.addObserver(new Observer() {
                    @Override
                    public void update(Observable o, Object arg) {
                        if (arg != null) {
                            uploadListener.onUploadProgress((Integer) arg, roomId, newFileName, description);
                        }
                    }
                });

                multipart.addFilePart("file", file);
                statusCode = multipart.finish();
                api.completeUFS(token.getFileId(), DEFAULT_STORE, token.getToken(), this);

            } catch (IOException e) {
                uploadListener.onUploadError(null, e);
            }
        } else {
            uploadListener.onUploadError(error, null);
        }

    }

    @Override
    public void onUfsComplete(FileObject file, ErrorObject error) {
        if (error == null) {
            uploadListener.onUploadComplete(statusCode, file, roomId, newFileName, description);
        } else {
            uploadListener.onUploadError(error, null);
        }
    }
}
