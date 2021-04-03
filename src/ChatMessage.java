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
        while(scan.hasNext()) readBy.add(scan.next());
        scan.close();
    }
    
    public ChatMessage(String a){
        Scanner scLine = new Scanner(a).useDelimiter(delimiter);
        ID = Integer.parseInt(scLine.next());
        user = scLine.next();
        dateTime = scLine.next();
        content = scLine.next();
       	if(scLine.hasNext()){ 
        	Scanner scan = new Scanner(scLine.next()).useDelimiter(";");
		while(scan.hasNext()) readBy.add(scan.next());
        	scan.close();
	}
        
        scLine.close();
    }
    
    public boolean equals(Object o){
	    ChatMessage other = (ChatMessage)o;
	    return (ID + user + dateTime + content).equals(other.getID() + other.getUser() + other.getDateTime() + content);
    }
    
    public String toString(){
        String out = "";
        out += ID+delimiter+user+delimiter+dateTime+delimiter+content+delimiter;
       	if(readBy != null){ 
		for(String i: readBy) out+=i+";";
	}
	return out;
    }
    
    public void read(String u){
        if(!(u.equals(user)||readBy.contains(u)))
            readBy.add(u);
    }
    
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
    
    public void editMessage(String dt, String c){
        dateTime = dt;
        content = c;
        readBy = new ArrayList<String>();
    }
}
