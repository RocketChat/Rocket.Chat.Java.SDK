package com.rocketchat.core.uploader;

import com.rocketchat.common.data.model.ApiError;
import com.rocketchat.common.utils.MultipartUploader;
import com.rocketchat.common.utils.Utils;
import com.rocketchat.core.RocketChatAPI;
import com.rocketchat.core.callback.FileListener;
import com.rocketchat.core.callback.MessageListener;
import com.rocketchat.core.model.FileObject;
import com.rocketchat.core.model.RocketChatMessage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by sachin on 18/8/17.
 */

// TODO: 20/8/17 remove new thread after creating running on UIThread and backgroundThread
public class FileUploader implements IFileUpload.UfsCreateListener,
        IFileUpload.UfsCompleteListener,
        MessageListener.MessageAckListener {

    public static final String DEFAULT_STORE = "Uploads";

    RocketChatAPI api;
    File file;
    String newFileName;
    String description;
    RocketChatAPI.ChatRoom room;
    String charset = "UTF-8";
    MultipartUploader multipart;
    FileListener fileListener;
    int statusCode;

    public FileUploader(RocketChatAPI api, File file, String newFileName, String description, RocketChatAPI.ChatRoom room, FileListener fileListener) {
        this.api = api;
        this.file = file;
        this.newFileName = newFileName;
        this.description = description;
        this.room = room;
        this.fileListener = fileListener;
    }

    public void startUpload() {
        api.createUFS(newFileName, (int) file.length(), Utils.getFileTypeUsingName(newFileName), room.getRoomData().getRoomId(), description, DEFAULT_STORE, this);
    }

    @Override
    public void onUfsCreate(final FileUploadToken token, ApiError error) {
        if (error == null) {
            fileListener.onUploadStarted(room.getRoomData().getRoomId(), newFileName, description);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        multipart = new MultipartUploader(token.getUrl(), charset);
                        multipart.addObserver(new Observer() {
                            @Override
                            public void update(Observable o, Object arg) {
                                if (arg != null) {
                                    fileListener.onUploadProgress((Integer) arg, room.getRoomData().getRoomId(), newFileName, description);
                                }
                            }
                        });

                        multipart.addFilePart("file", file);
                        statusCode = multipart.finish();
                        api.completeUFS(token.getFileId(), DEFAULT_STORE, token.getToken(), FileUploader.this);

                    } catch (IOException e) {
                        fileListener.onUploadError(null, e);
                    }
                }
            }).start();

        } else {
            fileListener.onUploadError(error, null);
        }

    }

    @Override
    public void onUfsComplete(FileObject file, ApiError error) {
        if (error == null) {
            fileListener.onUploadComplete(statusCode, file, room.getRoomData().getRoomId(), newFileName, description);
            room.sendFileMessage(file, this);
        } else {
            fileListener.onUploadError(error, null);
        }
    }

    @Override
    public void onMessageAck(RocketChatMessage message, ApiError error) {
        fileListener.onSendFile(message, error);
    }
}
