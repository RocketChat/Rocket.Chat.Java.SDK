Rocket.Chat.Java.SDK
=====================================
Overview
--------
This SDK is divided into two parts
1. Core SDK
2. LiveChat SDK

Current development approach is focused on **LiveChat SDK**.

License
-------
MIT

Gradle
------
For java 

```Gradle

dependencies {
    compile 'io.rocketchat:rocketchatjavasdk:0.3.2'
}
```
For android 

```Gradle
compile ('io.rocketchat:rocketchatjavasdk:0.3.2'){
        exclude group :'org.json', module: 'json'
}
```

[ ![Download](https://api.bintray.com/packages/sacoo7/Maven/RocketChat-SDK/images/download.svg) ](https://bintray.com/sacoo7/Maven/RocketChat-SDK/_latestVersion)


Documentation
-------------
- LiveChat SDK will consist of RPC for server methods available on hosted or remote rocket.chat server.</br>
- Primary requirement is to have url of hosted server.
Example </br>
     

```



```


