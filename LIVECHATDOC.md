LiveChat SDK can be effectively used using two classes
- Till now we have used `LiveChatAPI` class, in order to use `LiveChatAPI.ChatRoom` class .

1. LiveChatAPI
- Provides functionality that can hold instance required to maintain connection with server.
- API allows basic functionality like connect, login, register, reconnection and disconnection.

2. LiveChatAPI.ChatRoom
- It is created using LiveChatAPI and is extended version of basic API. 
- Provides abstraction as a **room**.
- All advanced API's like sending messages, loading history, getting agent data etc. can be used via ChatRoom.