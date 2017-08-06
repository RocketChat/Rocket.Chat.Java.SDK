Rocket.Chat.Java.SDK
=====================================
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)  [![RocketChat](https://img.shields.io/badge/chat-on%20rocketchat-brightgreen.svg)](https://demo.rocket.chat/channel/rocketchatjavasdk) [![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/Rocket-Chat-Java-SDK/Lobby)

- This SDK is used for handling **background communication** with server. 
- Contains **set of remote procedure calls (RPC)** to communicate with server and return appropriate results. 
- It doesn't have any user interface. All API's defined in SDK are **asynchronous/non-blocking.**

Overview
--------
This SDK is divided into two parts
1. Core SDK
2. LiveChat SDK

License
-------
MIT

Gradle
------
For java 

```Gradle

dependencies {
    compile 'io.rocketchat:rocketchatjavasdk:0.6.0'
}
```
For android 

```Gradle
compile ('io.rocketchat:rocketchatjavasdk:0.6.0'){
        exclude group :'org.json', module: 'json'
}
```

[ ![Download](https://api.bintray.com/packages/sacoo7/Maven/RocketChat-SDK/images/download.svg) ](https://bintray.com/sacoo7/Maven/RocketChat-SDK/_latestVersion) <a href="http://www.methodscount.com/?lib=io.rocketchat%3Arocketchatjavasdk%3A0.6.0"><img src="https://img.shields.io/badge/Methods and size-core: 788 | deps: 1256 | 116 KB-e91e63.svg"/></a>


Documentation
-------------

### 1. Core SDK
- This SDK consist of **chat** related API's available on the Rocket.Chat server.
- Currently supports following features.
1. Login/Resume Login
2. Getting Permissions/Getting public settings
3. Getting User Roles
4. Getting rooms
5. Getting chat history
6. Send message to the room
7. Delete message
8. Update message
9. Pin message
10. Unpin message
11. Star message
12. Create public group
13. Create private group
14. Delete group 
15. Archive room
16. Unarchive room
17. Join public group
18. Leave group
19. Open room
20. Hide room
21. Set favourite room
22. Set status (ONLINE, OFFLINE, BUSY, AWAY)
23. Getting room roles
24. Logout

- **User documentation can be found here** => [Core SDK](https://github.com/RocketChat/Rocket.Chat.Java.SDK/blob/develop/docs/ROCKETCHAT.md)
- Core SDK is under development ....

### 2. LiveChat SDK
- This SDK refers to providing helpDesk feature (LiveChat )in any JVM platform.
- This currently supports following features.
1. Getting LiveChat configuration data from server
2. Registration
3. Login
4. Choose departments
5. Getting Chat history
6. Getting Agent data
6. Send message
7. Subscribe room
8. Close conversation

- **User documentation can be found here** => [LiveChat SDK](https://github.com/RocketChat/Rocket.Chat.Java.SDK/blob/develop/docs/LIVECHAT.md)
