package io.rocketchat.core.uploader;

import io.rocketchat.common.data.model.ErrorObject;
import io.rocketchat.common.utils.MultipartUploader;
import io.rocketchat.common.utils.Utils;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.core.model.FileObject;
import java.io.File;
import java.io.IOException;
import java.util.List;
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

    public FileUploader(RocketChatAPI api, File file, String newFileName, String description, String roomId) {
        this.api = api;
        this.file = file;
        this.newFileName = newFileName;
        this.description = description;
        this.roomId = roomId;
    }

    public void startUpload () {
        api.createUFS(newFileName, (int) file.length(), Utils.getFileTypeUsingName(newFileName),roomId,description,DEFAULT_STORE,this);
    }

    @Override
    public void onUfsCreate(FileUploadToken token, ErrorObject error) {
        if (error == null) {
            System.out.println("Got into fileUploader script");
            System.out.println("Token is "+ token);
            try {
                System.out.println("Token url is "+ token.getUrl());
                multipart = new MultipartUploader(token.getUrl(), charset);
                multipart.addObserver(new Observer() {
                    @Override
                    public void update(Observable o, Object arg) {
                        if (arg != null) {
                            System.out.println("Progress is "+ arg);
                        }
                    }
                });

                multipart.addFilePart("file", file );
                int status = multipart.finish();
                System.out.println("Status is "+ status);
                api.completeUFS(token.getFileId(), DEFAULT_STORE, token.getToken(), this);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("got error in ufscreate "+ error.getMessage());
        }

    }

    @Override
    public void onUfsComplete(FileObject file, ErrorObject error) {
        System.out.println("Got into internal UFSComplete script");
        System.out.println("File is "+ file);
//        RocketChatAPI.ChatRoom room = api.getChatRoomFactory().getChatRoomById(roomId);
//        room.sendFileMessage(file);
    }
}
