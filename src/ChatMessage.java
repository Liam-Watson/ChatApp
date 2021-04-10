import java.util.ArrayList;
import java.util.Scanner;

public class ChatMessage{
    private int ID;
    private String user;
    private String dateTime;
    private String content;
    private ArrayList<String> readBy;
    private final String delimiter = "#";
    
    public ChatMessage(int i,String u, String dt, String c, String r){
        ID = i;
        user = u;
        dateTime = dt;
        content = c;
        
        Scanner scan = new Scanner(r).useDelimiter(";");
        readBy = new ArrayList<String>();
        while(scan.hasNext()) readBy.add(scan.next());
        scan.close();
    }
    /**
    *Constructor that takes in a string and uses a Scanner to split the mesage into is seperate parts to create a new ChatMessage
    */
    public ChatMessage(String a) {
        Scanner scLine = new Scanner(a).useDelimiter(delimiter);
        ID = Integer.parseInt(scLine.next());
        user = scLine.next();
        dateTime = scLine.next();
        content = scLine.next();
        if (scLine.hasNext()) {
            Scanner scan = new Scanner(scLine.next()).useDelimiter(";");
            readBy = new ArrayList<String>();
            while (scan.hasNext()) readBy.add(scan.next());
            scan.close();
        }

        scLine.close();
    }
    /**
    *compares if another object is the same as the current message
    */
    public boolean equals(Object o){
	    ChatMessage other = (ChatMessage)o;
	    return (ID + user + dateTime + content).equals(other.getID() + other.getUser() + other.getDateTime() + content);
    }
    /**
    *returns a formated string of the message object
    */
    public String toString(){
        String out = "";
        out += ID+delimiter+user+delimiter+dateTime+delimiter+content+delimiter;
       	if(readBy != null){ 
		for(String i: readBy) out+=i+";";
	}
	return out;
    }
    /**
    * marks a user as having read a message
    */
    public void read(String u){
        if(!(u.equals(user)||readBy.contains(u)))
            readBy.add(u);
    }
    /**
    *checks is a user has read a particular message
    */
    public boolean readByUser(String u){
        return readBy.contains(u);
    }
    
    //Get Methods
    public int getID(){return ID;}
    public String getUser(){return user;}
    public String getDateTime(){return dateTime;}
    public String getContent(){return content;}
    public ArrayList<String> getReadBy(){return readBy;}
    public String getDelimiter(){return delimiter;}
    /**
    *edits the details of a message
    */
    public void editMessage(String dt, String c){
        dateTime = dt;
        content = c;
        readBy = new ArrayList<String>();
    }
}
