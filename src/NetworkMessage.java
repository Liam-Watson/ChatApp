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
    
    public NetworkMessage(int f, String u, String s, String m){
        status =s;
        messageContent = m;
        user = u;
        ID = IPPort+IDcounter++;
        function = f;
        hash = toStringNoHash().hashCode();
    }
    
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
    
    public String toStringNoHash(){
        return (ID+delimiter+function+delimiter+user+delimiter+
            status+delimiter+messageContent);
    }
    
    public String toString(){
        return toStringNoHash()+delimiter+hash;
    }
    
    public boolean compareTo(NetworkMessage other){
        return ID.equals(other.getID());
    }
    
    public boolean validate(){	
        return toStringNoHash().hashCode()==hash;
    }
    
    //Get Methods
    public String getID(){return ID;}
    public int getFunction(){return function;}
    public String getStatus(){return status;}
    public String getUser(){return user;}
    public String getMessage(){return messageContent;}

    public static void setIDCounter(int c){IDcounter = c;}
    public static void setIPPort(String s){IPPort = s;}
}
