import java.io.*;
import java.util.Scanner;

public class ChatServer {
    /**
    *Main method intilised the chat server thread and starts it in a sperate thread
    *If "q" is pressed the program will shut down
    */
    public static void main(String[] args) throws IOException {
        ChatServerThread chatserver = new ChatServerThread();
        chatserver.start();
        String in = "0";
        while(in.charAt(0) != 'q'){
            Scanner input = new Scanner(System.in);
            in = input.nextLine();
            input.close();
        }

        chatserver.end();
    }
}
