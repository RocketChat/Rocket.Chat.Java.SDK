Following RPC are provided by LiveChatAPI.ChatRoom API

- Login (from second time onwards)
- getChatHistory (getting history from the server)
- getAgentData (getting agent data after agent is assigned to room)
- sendMessage (sending message to the room)
- sendIsTyping (sending typing event to the server)
- subscribeRoom (subscribing to the room for receiving messages from sender)
- subscribeLiveChatRoom (subscribe for agent connection to the user)
- subscribeTyping (Listening to typing events)
- closeConversation (Closing conversation with the server)

1. login

```java
    room.login(new AuthListener.LoginListener() {
                    @Override
                    public void onLogin(GuestObject object, ErrorObject error) {
                        
            }
    });
```

2. getChatHistory

```java
    /****
     * @param oldestMessageTimestamp Used to do pagination (null means latest timestamp)
     * @param count The message quantity, messages are loaded having timestamp older than @param oldestMessageTimestamp
     * @param lastTimestamp Date of the last time when client got data (Used to calculate unread)[unread count suggests number of unread messages having timestamp above @param lastTimestamp]
     **/
    room.getChatHistory(count,oldestMessageTimestamp,lastTimestamp, new LoadHistoryListener() {
                    @Override
                    public void onLoadHistory(ArrayList<MessageObject> list, int unreadNotLoaded, ErrorObject error) {
                        
                    }
    });
    
    Hint: pass count=20, oldestMessageTimestamp=new Date(),lastTimestamp=null for getting latest 20 messages
```