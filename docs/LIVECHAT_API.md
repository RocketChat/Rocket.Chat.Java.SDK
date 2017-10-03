Following methods are provided by LiveChatAPI class

- getInitialData (getting initial configuration data from the server)
- register Guest (registering guest using name, email and department)
- send offline message (sending offline messages to the server, mail will be sent to agent in case not online)
- set connect listener (register connection listener for the callback)
- create room (using userId and authToken)

**Note (Get rid of callback hell):** Best way to write down callbacks is to let given Class implement the interface and passing it's instance to the room method as a callback parameter. Same should be followed for other methods. 
**No Callback:** Pass null in case callback receive is not important.

**1. getInitialData**

- Already described in LiveChat overview. 

**2. registerGuest or login**

- Registration and login is already given in overview.

**3. sendOfflineMessage**

```
        //Name, Email and Message is given, listener will give appropriate callback.
        //Mail will be sent to Agent's official email Id on the server.
        client.sendOfflineMessage("aditya", "aditya123@gmail.com", "This is a test message", new MessageListener.OfflineMessageListener() {
            @Override
            public void onOfflineMesssageSuccess(Boolean success, ErrorObject error) {
                if (success) {
                    System.out.println("Offline message sent to the server");
                }
            }
        });
```

**4. setConnectListener**

- If a given class doesn't contain connect method, then it is essential to have connect related callbacks to get information about internal socket state.
- Make sure given class has implemented _ConnectListener_ interface.

```

client.setConnectListener(this);

```

**5. createRoom**

- Already given in overview.

