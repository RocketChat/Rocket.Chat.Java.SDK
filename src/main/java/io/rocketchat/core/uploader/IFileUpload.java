package io.rocketchat.core.uploader;

import io.rocketchat.core.model.FileObject;

/**
 * Created by sachin on 17/8/17.
 */
public interface IFileUpload {

    String STORAGE_TYPE_S3 = "AmazonS3";
    String STORAGE_TYPE_GRID_FS = "GridFS";
    String STORAGE_TYPE_FILE_SYSTEM = "FileSystem";
    String STORAGE_TYPE_GOOGLE = "GoogleCloudStorage";

    void onUfsCreate(FileUploadToken token);

    void onUfsComplete(FileObject file);
}
