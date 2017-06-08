import io.rocketchat.Socket;

import java.io.IOException;

/**
 * Created by sachin on 7/6/17.
 */
public class Main {
    public static void main(String [] args){

//        System.out.println("Hello there");
        Socket socket=new Socket("wss://demo.rocket.chat/websocket");

        //Connect event to server
        try {
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
