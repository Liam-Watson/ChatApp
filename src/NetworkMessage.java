import java.util.Scanner;

public class NetworkMessage{
    private String ID;    
    private int function;
    private String user;
    private String status;
    private String messageContent;
    
    private int hash;
    
    private static int IDcounter = 0;
    private static String IPPort = "";
    
    private final String delimiter = "`";
    /**
    *Constructor that takes in the function, user, status and message content as paramaters
    */
    public NetworkMessage(int f, String u, String s, String m){
        status =s;
        messageContent = m;
        user = u;
        ID = IPPort+"%"+IDcounter++;
        function = f;
        hash = toStringNoHash().hashCode();
    }
    /**
    *takes in a string that uses a Scanner to split it into all of its parts for the object
    */
    public NetworkMessage(String n){
        Scanner scan = new Scanner(n).useDelimiter(delimiter);
        try{
            ID = scan.next();
            function = Integer.parseInt(scan.next());
            user = scan.next();
            status = scan.next();
            messageContent = scan.next();
            hash = Integer.parseInt(scan.next());
        }catch (Exception e){
            System.out.println(e);
            System.out.println("Invalid Arguments");
        }
        scan.close();
    }
    /**
    *takes all parts of the network message besides the hash and creates a string with each atribute seperated by the delimiter
    */
    public String toStringNoHash(){
        return (ID+delimiter+function+delimiter+user+delimiter+
            status+delimiter+messageContent);
    }
    /**
    *takes all parts of the network message and creates a string with each atribute seperated by the delimiter
    */
    public String toString(){
        return toStringNoHash()+delimiter+hash;
    }
    /**
    *compares the network messages IDs to each other
    */
    public boolean compareTo(NetworkMessage other){
        return ID.equals(other.getID());
    }
    /**
    *checks for coruption in the message by hashing the toStringNoHash and comaparing it to the hash value sent with it
    *if the message is corupted, either the toStringNoHash will change or the hash. Thereby returning a false for the check
    */
    public boolean validate(){	
        return toStringNoHash().hashCode()==hash;
    }
    
    //Get Methods
    public String getID(){return ID;}
    public int getFunction(){return function;}
    public String getStatus(){return status;}
    public String getUser(){return user;}
    public String getMessage(){return messageContent;}
    /**
    *gets the IPPort atribute from the ID atribute
    */
    public String getIPPort(){
            String[] IDsplit = ID.split("%");
            return IDsplit[0];
    }
    /**
    *gets the message counter from the ID atribute
    */
    public int getCounter(){
            String[] IDsplit = ID.split("%");
            return Integer.parseInt(IDsplit[1]);
    }

    public static void setIDCounter(int c){IDcounter = c;}
    public static void setIPPort(String s){IPPort = s;}
    /**
    *sets the status of the message to duplicate
    */
    public void setStatusDuplicate(){status = "Duplicate";}
    /**
    *checks if the messag is aduplicate
    */
    public boolean checkDuplicate(){return status.equals("Duplicate");}
}
