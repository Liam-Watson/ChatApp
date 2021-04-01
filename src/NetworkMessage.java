import java.util.Scanner;

public class NetworkMessage{
    private String ID;    
    private int function;
    private String user;//consider making user static
    private String status;
    private String messageContent;
    
    private static int IDcounter = 0;
    
    private final String delimiter = "`";
    
    public NetworkMessage(int f, String u, String s, String m){
        status =s;
        messageContent = m;
        user = u;
        ID = u+IDcounter++;
        function = f;
    }
    
    public NetworkMessage(String n){
        Scanner scan = new Scanner(n).useDelimiter(delimiter);
        try{
            ID = scan.next();
            function = Integer.parseInt(scan.next());
            user = scan.next();
            status = scan.next();
            messageContent = scan.next();
        }catch (Exception e){
            System.out.println(e);
            System.out.println("Invalid Arguments");
        }
        scan.close();
    }
    
    public String toString(){
        return (ID+delimiter+function+delimiter+user+delimiter+
            status+delimiter+messageContent);
    }
    
    public boolean compareTo(NetworkMessage other){
        return ID.equals(other.getID());
    }
    
    public boolean validate(int hash){	
        return toString().hashCode()==hash;
    }
    
    //Get Methods
    public String getID(){return ID;}
    public int getFunction(){return function;}
    public String getStatus(){return status;}
    public String getUser(){return user;}
    public String getMessage(){return messageContent;}

    public static void setIDCounter(int c){IDcounter = c;}
}
