package com.rocketchat.core.uploader;

import com.rocketchat.common.RocketChatException;
import com.rocketchat.common.utils.MultipartUploader;
import com.rocketchat.common.utils.Utils;
import com.rocketchat.core.RocketChatAPI;
import com.rocketchat.core.callback.FileListener;
import com.rocketchat.core.callback.MessageCallback;
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
public class FileUploader {

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
        api.createUFS(newFileName, (int) file.length(), Utils.getFileTypeUsingName(newFileName),
                room.getRoomData().getRoomId(), description, DEFAULT_STORE, createCallback);
    }

    IFileUpload.UfsCompleteListener completeListener = new IFileUpload.UfsCompleteListener() {
        @Override
        public void onUfsComplete(FileObject file) {
            fileListener.onUploadComplete(statusCode, file, room.getRoomData().getRoomId(), newFileName, description);
            room.sendFileMessage(file, new MessageCallback.MessageAckCallback() {
                @Override
                public void onMessageAck(RocketChatMessage message) {
                    fileListener.onSendFile(message, null);
                }

                @Override
                public void onError(RocketChatException error) {
                    fileListener.onSendFile(null, error);
                }
            });
        }

        @Override
        public void onError(RocketChatException error) {
            fileListener.onUploadError(error, null);
        }
    };

    IFileUpload.UfsCreateCallback createCallback = new IFileUpload.UfsCreateCallback() {

        @Override
        public void onError(RocketChatException error) {
            fileListener.onUploadError(error, null);
        }

        @Override
        public void onUfsCreate(final FileUploadToken token) {
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
                        api.completeUFS(token.getFileId(), DEFAULT_STORE, token.getToken(), completeListener);

                    } catch (IOException e) {
                        fileListener.onUploadError(null, e);
                    }
                }
            }).start();
        }
    };
}
