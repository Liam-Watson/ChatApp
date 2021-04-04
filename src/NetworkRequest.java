import java.util.ArrayList;

public class NetworkRequest{
    private String IPPort;
    private ArrayList<Integer> requestNumbers;
    
    public NetworkRequest(String i){
        IPPort = i;
        reset();
    }
    
    public boolean equals(Object o){
        if(o instanceof NetworkRequest){
            NetworkRequest n = (NetworkRequest) o;
            if(IPPort.equals(n.getIPPort())){
                return true;
            }
        } 
        return false;
    }
    
    public void reset(){requestNumbers=new ArrayList<Integer>();}
    
    public boolean makeRequest(int i){
        if(requestNumbers.contains(i)){
            return false;
        }
        requestNumbers.add(i);
        return true;
    }
    
    
    public String getIPPort(){return IPPort;}
    public ArrayList<Integer> getRequestNumbers(){return requestNumbers;}
}