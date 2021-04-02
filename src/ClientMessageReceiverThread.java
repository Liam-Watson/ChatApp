import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientMessageReceiverThread extends Thread{
    AtomicBoolean keepRunning = new AtomicBoolean(true);
    DatagramSocket socket = new DatagramSocket();
    List<NetworkMessage> incomingMessages = Collections.synchronizedList(new ArrayList<NetworkMessage>());
    public ClientMessageReceiverThread() throws IOException {
        super("ClientMessageReceiverThread");

    }
    public ClientMessageReceiverThread(String name) throws IOException {
        super(name);

    }
    public void setIncommingMessages(List<NetworkMessage> in){
        incomingMessages = in;
    }
    public void setSocket(DatagramSocket in){
        socket = in;
    }

    public void run(){
        while(keepRunning.get()){
            try {
                byte[] buf = new byte[16384]; //TODO: This limits the size of messages we recieve. I byte is 1 character in a string. I have set it to 2^14 for now. 
                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                incomingMessages.add(new NetworkMessage(new String(packet.getData(), 0, packet.getLength())));
            } catch (SocketException e){
                System.out.println("socket closed");
            }catch(IOException e){
                e.printStackTrace();

            }
        }

    }
    public void end(){
        keepRunning.set(false);
        socket.close();
    }
}
