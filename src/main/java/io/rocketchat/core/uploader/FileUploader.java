package io.rocketchat.core.uploader;

import io.rocketchat.common.utils.MultipartUploader;
import io.rocketchat.common.utils.Utils;
import io.rocketchat.core.RocketChatAPI;
import io.rocketchat.core.model.FileObject;
import java.io.File;
import java.io.IOException;

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
    public void onUfsCreate(FileUploadToken token) {
        try {
            multipart = new MultipartUploader(token.getUrl(), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUfsComplete(FileObject file) {

    }
}
