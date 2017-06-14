Rocket.Chat.Java.SDK
=====================================
Overview
--------
This SDK is divided into two parts
1. Core SDK
2. LiveChat SDK

Current development approach is focused on LiveChat SDK and it allows following functionality

- Getinitialdata (info. about departments and triggers) and register guest  
- Guest login
- Load history
- Send message
- Subscribe to room
- Receive message event
- Get agent info.
- Send and receive typing event

License
-------
Apache License, Version 2.0


Gradle
------
For java 

```Gradle

dependencies {
    compile 'io.rocketchat:rocketchatjavasdk:0.2.1'
}
```
For android 

```Gradle
compile ('io.rocketchat:rocketchatjavasdk:0.2.1'){
        exclude group :'org.json', module: 'json'
}
```

[ ![Download](https://api.bintray.com/packages/sacoo7/Maven/RocketChat-SDK/images/download.svg) ](https://bintray.com/sacoo7/Maven/RocketChat-SDK/_latestVersion)


