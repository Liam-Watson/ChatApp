import java.util.ArrayList;
import java.util.Scanner;

public class Chat{
    private String user1, user2;
    
    private ArrayList<ChatMessage> messages;
    
    public Chat(String u1, String u2){
        user1 = u1;
        user2 = u2;
        messages = new ArrayList<ChatMessage>();
    }
    
    public void initChat(Scanner scan){
        while(scan.hasNext()){
            messages.add(new ChatMessage(scan.nextLine()));
        }
    }
    
    public void addMessage(String line){
        messages.add(new ChatMessage(line));
    }
    
    public String toString(){
        String out = "";
        for(ChatMessage i : messages) out += i +"\n";
        return out;
    }
}	
