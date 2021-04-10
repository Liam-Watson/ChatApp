import java.util.ArrayList;
import java.util.Scanner;

public class User {
    private String username;
    private String password;
    private ArrayList<String> chats;//TODO: Relic code?
    
    
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
    /**
    *Creates a user with a specified username and password as strings
    */
    public User(String u, String p){
        username = u;
        password = p;
        chats = new ArrayList<String>();
    }
    /**
    *checks if both the username and password are the same as the ones in the user object
    */
    public boolean authenticate(String u, String p){
        return username.equals(u) && password.equals(p);
    }
    /**
    *checks if the username(which is the unique identifier) is equal to the username of anotehr user
    */
    public boolean equals(Object usr){
	User user = (User)usr;
    	//System.out.println(this.username +" "+ user.username +" "+ this.password +" " + user.password + " " + (this.username.equals(user.username) && this.password.equals(user.password)));	
	if(user != null && user instanceof User && this.username.equals(user.username)){
	       	return true; 
	}else{
       		return false;
	}
    }
    /**
    *includes a chat as part of the user objects chat arraylist
    */
    public boolean addChat(String c){
        if(chats.contains(c)) return false;
        
        chats.add(c);
        return true;
    }
    /**
    *changes a users password if the old password given is correct
    *returns true if the change was made or false if the oldpassword was incorrect
    */
    public boolean changePassword(String oldPass, String newPass){
        if (!password.equals(oldPass)) return false;
        
        password = newPass;
        return true;
    }
    /**
    *returns a formatted string of the user object
    */
    public String toString(){
	return username + "*" + password;
    }
}
