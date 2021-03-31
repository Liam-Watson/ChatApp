import java.util.ArrayList;
import java.util.Scanner;

public class Chat{
    private String[] users;
    
    private ArrayList<ChatMessage> messages;
    
    public Chat(String[] u){
        users = u;
        messages = new ArrayList<ChatMessage>();
    }
    public Chat(String chat){
        messages = new ArrayList<ChatMessage>();
	String [] lines = chat.split("\n");
	String [] users = lines[0].split(";");
	
	for(int i = 1; i < lines.length; i++){
		messages.add(new ChatMessage(lines[i]));
	}
    }
    public void initChat(Scanner scan){
        scan.nextLine();
        while(scan.hasNext()){
	//	System.out.println(scan.nextLine());
            messages.add(new ChatMessage(scan.nextLine()));
        }
    }
    
    public void addMessage(String line){
        messages.add(new ChatMessage(line));
    }
    public String printMessages(){
        String outMessages = "";

        for(int i = 0; i < messages.size(); i++){
            outMessages = outMessages + messages.get(i).getUser() + ": " + messages.get(i).getDateTime() + "\n" + messages.get(i).getContent() + "\n";
        }

        return outMessages;
    }
    public boolean equals(Object other){
		Chat otherChat = (Chat)other;
		if(otherChat != null && otherChat instanceof Chat){
		    Chat temp = (Chat) other;
		    for(int i=0;i<users.length;i++) {
                        if(!temp.userPartOfChat(users[i])) return false;
                    }
                    return true;
		}else{
			return false;
		}
    }
    public String toString(){
        String out = ""; //TODO: Multiple users > 2 
	out+= String.join(";",users);
	
	out+="\n";
        for(ChatMessage i : messages) out += i +"\n";
        return out;
    }
    public String getChatName(){
        return String.join(";",users);
    }
    public String[] getUsers(){
	return users;
    }
    
    public boolean userPartOfChat(String userName){
        for(int i=0;i<users.length;i++){
            if(userName.equals(users[i])){
	    	return true;
            }
        }
        return false;
    }
}	
