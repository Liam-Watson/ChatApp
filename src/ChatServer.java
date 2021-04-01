import java.io.*;
import java.util.Scanner;

public class ChatServer {
    public static void main(String[] args) throws IOException {
        ChatServerThread chatserver = new ChatServerThread();
        chatserver.start();
        String in = "0";
        while(in.charAt(0) != 'q'){
            Scanner input = new Scanner(System.in);
            in = input.nextLine();
            input.close();
        }
        System.out.println("set var to false");
        //chatserver.keepRunning = false;
        chatserver.end();
    }
}
