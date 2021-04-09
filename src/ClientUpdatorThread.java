import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientUpdatorThread extends Thread{
    AtomicBoolean keepRunning = new AtomicBoolean(true);
    List<NetworkMessage> incomingMessages = Collections.synchronizedList(new ArrayList<NetworkMessage>());
    List<Chat> chatsList = Collections.synchronizedList(new ArrayList<Chat>());
    String username = "";
    String [] openChat = new String[1];
    JTextArea chatContent = new JTextArea();
    DatagramSocket socket = new DatagramSocket();
    InetAddress serverAddress;


    public ClientUpdatorThread() throws IOException {
        super("ClientUpdatorThread");

    }
    public ClientUpdatorThread(String name) throws IOException {
        super(name);

    }
    public void setIncommingMessages(List<NetworkMessage> in){
        incomingMessages = in;
    }
    public void setChatsList(List<Chat> in){
        chatsList = in;
    }
    public void setOutputArea(JTextArea in){
        chatContent = in;
    }
    public void setUsername(String in){
        username = in;
    }
    public void setCurrentChat(String [] in) {
        openChat = in;
    }
    public void setSocket(DatagramSocket in){
        socket = in;
    }
    public void setServerAddress(InetAddress in){
        serverAddress = in;
    }


    public void run(){
        while(keepRunning.get()) {
            for (int i = 0; i < chatsList.size(); i++) {
                String chatName = chatsList.get(i).getChatName();
                ChatMessage mostRecentMessage = chatsList.get(i).getMostRecentMessage();
                String updateChatRequest = "^";
		if(mostRecentMessage != null){
			updateChatRequest = chatName + "\n" + mostRecentMessage.toString();
		}
		//based on the doc I assume that a the sendMessage function (2) is what we will use for this
                //message body will be chatname <newline> most recent chat message (in String form)
                NetworkMessage request = new NetworkMessage(2, username, "request", updateChatRequest);

                try {
                    byte[] buf = new byte[256];
                    buf = request.toString().getBytes();
                    // send the response to the client at "address" and "port"
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, 4445);
		    if(corrupt()){
                    	socket.send(packet);
		    }
                }catch(IOException e){
                    e.printStackTrace();

                }

                //TODO NOTE Incoming network message must have function value of 0
                //TODO NOTE The incoming network message object must contain the string <ChatName>\n<ChatMessage.toString()>\n<ChatMessage... i.e chat name and messages with \n as a delimiter


            }

            for (int i = 0; i < incomingMessages.size(); i++) {
                if (incomingMessages.get(i).getFunction() == 0) {
		    if(!incomingMessages.get(i).getMessage().equals("^")){
                    	String newMessagesBody = incomingMessages.get(i).getMessage();
                    	String[] newMessages = newMessagesBody.split("\n");
                    	String ChatName = newMessages[0];
                    	for (int j = 0; j < chatsList.size(); j++) {
                            if (chatsList.get(j).getChatName().equals(ChatName)) {
                            	for (int k = 1; k < newMessages.length; k++) {
					if(!chatsList.get(j).containsMessage(newMessages[k])){
                                		chatsList.get(j).addMessage(newMessages[k]);
					}
                            	}
                            }
                    	}
		    }
                    incomingMessages.remove(i);


                }
            }

            for (int i = 0; i < chatsList.size(); i++) {
                if(chatsList.get(i).getChatName().equals(openChat[0])){
                    chatContent.setText(chatsList.get(i).printMessages());

                }
            }

            //ChatClient.showMessages();
            //Thread sleeps for 2 seconds, i.e chats will be updated every 2 seconds
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    public boolean corrupt(){
        double rand = Math.random()*10;
        if(rand <= 1){
                return false;
        }else{
		return true;
        }

    }


}
