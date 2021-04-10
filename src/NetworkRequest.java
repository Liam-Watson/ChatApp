import java.util.ArrayList;

public class NetworkRequest{
    private String IPPort;
    private ArrayList<Integer> requestNumbers;
    /**
    *takes in a string of the IPPort of the sender and intilaises the request numbers as an empty arraylist
    */
    public NetworkRequest(String i){
        IPPort = i;
        reset();
    }
    /**
    *checks if anotehr network request is equal to the current one
    */
    public boolean equals(Object o){
        if(o instanceof NetworkRequest){
            NetworkRequest n = (NetworkRequest) o;
            if(IPPort.equals(n.getIPPort())){
                return true;
            }
        } 
        return false;
    }
    /**
    *re initilises the restur numbers arraylist as an empty arraylist
    */
    public void reset(){requestNumbers=new ArrayList<Integer>();}
    /**
    *checks if a request with a specified integer has been made. 
    *if it has it returns false
    *if it has not, it adds the integer to requests that have been made and retunrs true
    */
    public boolean makeRequest(int i){
        if(requestNumbers.contains(i)){
            return false;
        }
        requestNumbers.add(i);
        return true;
    }
    
    //Accessor methods
    public String getIPPort(){return IPPort;}
    public ArrayList<Integer> getRequestNumbers(){return requestNumbers;}
}
