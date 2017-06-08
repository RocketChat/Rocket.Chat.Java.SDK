package io.rocketchat.livechat.middleware;

/**
 * Created by sachin on 8/6/17.
 */

public class LiveChatMiddleware {

    //It will contain ConcurrentArrayList of all callbacks
    //Each new response will trigger each of the callback

    private static LiveChatMiddleware middleware= new LiveChatMiddleware();

    private LiveChatMiddleware(){

    }

    public static LiveChatMiddleware getInstance(){
        return middleware;
    }

    public void createcallback(){

    }

    public void processcallback(){

    }
}
