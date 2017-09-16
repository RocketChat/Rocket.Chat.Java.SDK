package com.rocketchat.core.db;

import com.rocketchat.common.data.lightdb.collection.Collection;
import com.rocketchat.core.db.Document.FileDocument;
import com.rocketchat.core.db.Document.MessageDocument;

/**
 * Created by sachin on 16/9/17.
 */
public class RoomDbManager {
    private Collection<String, FileDocument> roomFilesCollection;
    private Collection <String, MessageDocument> mentionedMessages;


}
