import java.util.ArrayList;
import java.util.Scanner;

public class User {
    private String username;//Thanks liam
    private String password;
    private ArrayList<String> chats;
    
    public User(String u, String p, ArrayList<String> c){
        username = u;
        password = p;
        chats = c;
    }
    public User(String u, String p, String c){
        username = u;
        password = p;
        
        Scanner scan = new Scanner(c).useDelimiter(";");
        
        chats = new ArrayList<String>();
        while(scan.hasNext())	chats.add(scan.next());
        scan.close();
    }
    public User(String u, String p){
        username = u;
        password = p;
        chats = new ArrayList<String>();
    }
    
    public boolean authenticate(String u, String p){
        return username.equals(u) && password.equals(p);
    }
    public boolean equals(Object usr){
	User user = (User)usr;
    	System.out.println(this.username +" "+ user.username +" "+ this.password +" " + user.password + " " + (this.username.equals(user.username) && this.password.equals(user.password)));	
	if(user != null && user instanceof User && this.username.equals(user.username)){
	       	return true; 
	}else{
       		return false;
	}
    }
    public boolean addChat(String c){
        if(chats.contains(c)) return false;
        
        chats.add(c);
        return true;
    }
    
    public boolean changePassword(String oldPass, String newPass){
        if (!password.equals(oldPass)) return false;
        
        password = newPass;
        return true;
    }
    public String toString(){
	return username + "*" + password;
    }
}
