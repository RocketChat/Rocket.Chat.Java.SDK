# DEPRECATED
We are working on a new [SDK in Kotlin](https://github.com/RocketChat/Rocket.Chat.Kotlin.SDK/), with compatibility with JAVA (in the future).
 
 
 
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
- ![Core SDK intro](https://github.com/RocketChat/Rocket.Chat.Java.SDK/blob/develop/docs/ROCKETCHAT_INTRO.md)
- ![RocketChat API usage documentation](https://github.com/RocketChat/Rocket.Chat.Java.SDK/blob/develop/docs/ROCKETCHAT_API.md)
- ![RocketChat room API usage documentation](https://github.com/RocketChat/Rocket.Chat.Java.SDK/blob/develop/docs/ROCKETCHAT_ROOM_DOC.md)

[ ![Download](https://client.bintray.com/packages/rocketchat/RocketChat-SDK/RocketChat-Java-SDK-Core/images/download.svg) ](https://bintray.com/rocketchat/RocketChat-SDK/RocketChat-Java-SDK-Core/_latestVersion) <a href="http://www.methodscount.com/?lib=com.rocketchat.core%3Arocketchat-core%3A0.7.1"><img src="https://img.shields.io/badge/Methods and size-core: 548 | deps: 1614 | 80 KB-e91e63.svg"/></a>

2. LiveChat SDK
- ![LiveChat SDK intro](https://github.com/RocketChat/Rocket.Chat.Java.SDK/blob/develop/docs/LIVECHAT_INTRO.md)
- ![LiveChat API usage documentation](https://github.com/RocketChat/Rocket.Chat.Java.SDK/blob/develop/docs/LIVECHAT_API.md)
- ![LiveChat room API usage documentation](https://github.com/RocketChat/Rocket.Chat.Java.SDK/blob/develop/docs/LIVECHAT_ROOM_DOC.md)

[ ![Download](https://client.bintray.com/packages/rocketchat/RocketChat-SDK/RocketChat-Java-SDK-LiveChat/images/download.svg) ](https://bintray.com/rocketchat/RocketChat-SDK/RocketChat-Java-SDK-LiveChat/_latestVersion) <a href="http://www.methodscount.com/?lib=com.rocketchat.livechat%3Arocketchat-livechat%3A0.7.1"><img src="https://img.shields.io/badge/Methods and size-core: 282 | deps: 1614 | 37 KB-e91e63.svg"/></a>


License
-------
MIT

Gradle
------
For java 

**1. Core SDK**

```Gradle
dependencies {
    compile 'com.rocketchat.core:rocketchat-core:0.7.1'
}
```

**2. LiveChat SDK**

```Gradle
dependencies {
    compile 'com.rocketchat.livechat:rocketchat-livechat:0.7.1'
}
```

For android 

**1. Core SDK**

```Gradle
dependencies {
    compile ('com.rocketchat.core:rocketchat-core:0.7.1'){
        exclude group :'org.json', module: 'json'
    }
}
```

**2. LiveChat SDK**

```Gradle
dependencies {
    compile ('com.rocketchat.livechat:rocketchat-livechat:0.7.1'){
            exclude group :'org.json', module: 'json'
    }
}
```



Features
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
24. Upload files
25. Getting status of other users in realtime (Register for user status by userId)
26. Logout

- **User documentation can be found here** => [Core SDK](https://github.com/RocketChat/Rocket.Chat.Java.SDK/blob/develop/docs/ROCKETCHAT_INTRO.md)

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

- **User documentation can be found here** => [LiveChat SDK](https://github.com/RocketChat/Rocket.Chat.Java.SDK/blob/develop/docs/LIVECHAT_INTRO.md)


### Important Note
All docs are available under **docs/** directory on the repo.

#### Demo Android App
- **Android app based on Java SDK can be found here** => [Demo Android App based on SDK](https://github.com/RocketChat/RocketChat-Android-Demo)
