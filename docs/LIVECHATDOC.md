Following methods are provided by LiveChatAPI.ChatRoom API

- Login (from second time onwards)
- getChatHistory (getting history from the server)
- getAgentData (getting agent data after agent is assigned to room)
- sendMessage (sending message to the room)
- sendIsTyping (sending typing event to the server)
- subscribeLiveChatRoom (subscribe for agent connection to the user)
- subscribeRoom (subscribing to the room for receiving messages from sender)
- subscribeTyping (Listening to typing events)
- closeConversation (Closing conversation with the server)

**1. login**

```java
    room.login(new AuthListener.LoginListener() {
                    @Override
                    public void onLogin(GuestObject object, ErrorObject error) {
                        
            }
    });
    
```

**Note (Get rid of callback hell):** Best way to write down callbacks is to let Main Class implement the interface and passing it's instance to the room method as a callback parameter. Same should be followed for other methods. 
    
- Example 

        public class Main implements AuthListener.LoginListener{
           
           public void login(){
                room.login(this);
           }
           
           @Override
           public void onLogin(GuestObject object, ErrorObject error) {
            if (error==null) {
                    System.out.println("login is successful");
            }
           }
        }
        
   
**2. getChatHistory**

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

**3. getAgentData**

```java
    room.getAgentData(new AgentListener.AgentDataListener() {
                    @Override
                    public void onAgentData(AgentObject agentObject, ErrorObject error) {
                        
                    }
    });
```

**4. sendMessage**

```java
    room.sendMessage("This is some random message");
```

**5. sendIsTyping**

```java
    room.sendIsTyping(true); //for sending typing event to true
    room.sendIsTyping(false); //for sending typing event to false
```


**6. subscribeLiveChatRoom**

```java
   room.subscribeLiveChatRoom(new SubscribeListener() {
            @Override
            public void onSubscribe(Boolean isSubscribed, String subId) {

            }
        }, new AgentListener.AgentConnectListener() {
            @Override
            public void onAgentConnect(AgentObject agentObject) {
                
            }
    });
```

**7. subscribeRoom**

```java

    room.subscribeRoom(new SubscribeListener() {
        @Override
        public void onSubscribe(Boolean isSubscribed, String subId) {

        }
    }, new MessageListener.SubscriptionListener() {
        @Override
        public void onMessage(String roomId, LiveChatMessage object) {
            
        }

        @Override
        public void onAgentDisconnect(String roomId, LiveChatMessage object) {

        }
    });

```

**8. subscribeTyping**

```java

        room.subscribeTyping(new SubscribeListener() {
            @Override
            public void onSubscribe(Boolean isSubscribed, String subId) {

            }
        }, new TypingListener() {
            @Override
            public void onTyping(String roomId, String user, Boolean istyping) {
                
            }
        });
```

**9. closeConversation**

```java
    room.closeConversation();
```

#### Priority of calling methods    
- Some methods are required to be called after receiving callbacks from other methods.
Example : login or register can only be called after connect callback.
- Priority decides which must methods should be called before other methods.
- For room object, it can be given as follow. </br>
**I.** Login </br>
**II.** For the first login use subscribeLiveChatRoom (agent assignment will be returned in callback, it will remain constant afterwards) else getAgentData (after receiving callback from login) </br>
**III.** Any other Method (after receiving callback from step II)</br>
