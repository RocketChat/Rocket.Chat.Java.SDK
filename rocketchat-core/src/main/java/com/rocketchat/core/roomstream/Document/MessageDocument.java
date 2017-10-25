package com.rocketchat.core.roomstream.Document;

import com.rocketchat.core.model.Message;

/**
 * Created by sachin on 16/9/17.
 */

public class MessageDocument {
    Message message;

    public MessageDocument(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message.toString();
    }
}
