package io.rocketchat.core.middleware;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sachin on 21/7/17.
 */
public class CoreStreamMiddleware {


    public enum SubType {

    }

    public static CoreStreamMiddleware middleware=new CoreStreamMiddleware();

    ConcurrentHashMap<String,Object[]> subcallbacks;

    private CoreStreamMiddleware(){
        subcallbacks=new ConcurrentHashMap<>();
    }

    public static CoreStreamMiddleware getInstance(){
        return middleware;
    }
}
