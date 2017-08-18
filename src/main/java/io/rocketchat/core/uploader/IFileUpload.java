package io.rocketchat.core.uploader;

/**
 * Created by sachin on 17/8/17.
 */
public interface IFileUpload {

    String STORAGE_TYPE_S3 = "AmazonS3";
    String STORAGE_TYPE_GRID_FS = "GridFS";
    String STORAGE_TYPE_FILE_SYSTEM = "FileSystem";
    String STORAGE_TYPE_GOOGLE = "GoogleCloudStorage";


}
