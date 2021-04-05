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
	users = lines[0].split(";");
	for(int i = 1; i < lines.length; i++){
		messages.add(new ChatMessage(lines[i]));
	}
    }
    public void initChat(Scanner scan){
        while(scan.hasNext()){
            messages.add(new ChatMessage(scan.nextLine()));
        }
    }
    
    public void addMessage(String line){
        messages.add(new ChatMessage(line));
    }
    public boolean containsMessage(String line){
	for(ChatMessage m : messages){
		if(m.equals(new ChatMessage(line))){
			return true;
		}
	}
	return false;
    }
    public String printMessages(){
        String outMessages = "";

        for(int i = 0; i < messages.size(); i++){
            outMessages = outMessages + messages.get(i).getUser() + ": " + messages.get(i).getDateTime() + "\n" + messages.get(i).getContent() + "\n";
        }

        return outMessages;
    }
    public ChatMessage getMostRecentMessage(){
	if(messages.size() == 0){
		return null; //TODO: Decide on a way to deal with no messages  
	}
        return messages.get(messages.size()-1);
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

    public String getMessagesSince(ChatMessage message){
	String msgs = this.getChatName() + "\n";
	    if(messages.contains(message)){
		boolean flag = false;
		for(ChatMessage m : messages){
			if(flag){
				msgs += m.toString();		
			}
			if(!flag && m.equals(message)){
				flag = true;
			}
		}
	}
	return msgs;
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
