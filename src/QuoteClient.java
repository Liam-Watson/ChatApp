import java.io.*;
import java.net.*;
import java.util.*;

public class QuoteClient {
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Usage: java QuoteClient <hostname>");
            return;
        }

        Scanner in = new Scanner(System.in);
        String inpt = "";

        System.out.println(args[0]);
        while(inpt != "q") {
            inpt = in.next();
            // get a datagram socket
            DatagramSocket socket = new DatagramSocket();
            // send request
            byte[] buf = new byte[256];
            buf = inpt.getBytes();
            InetAddress address = InetAddress.getByName(args[0]);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            socket.send(packet);

            // get response
            buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            // display response
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("From server: " + received);
            socket.close();
        }
    }
}
