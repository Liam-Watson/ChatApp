import java.io.IOException;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientUpdatorThread extends Thread{
    AtomicBoolean keepRunning = new AtomicBoolean(true);
    ArrayList<NetworkMessage> incomingMessages = new ArrayList<NetworkMessage>();
    ArrayList<Chat> chatsList = new ArrayList<Chat>();
    String username = "";


    public ClientUpdatorThread() throws IOException {
        super("ClientUpdatorThread");

    }
    public ClientUpdatorThread(String name) throws IOException {
        super(name);

    }
    public void setIncommingMessages(ArrayList<NetworkMessage> in){
        incomingMessages = in;
    }
    public void setChatsList(ArrayList<Chat> in){
        chatsList = in;
    }
    public void setUsername(String in){
        username = in;
    }


    public void run(){
        while(keepRunning.get()) {
            for (int i = 0; i < chatsList.size(); i++) {
                String chatName = chatsList.get(i).getChatName();
                ChatMessage mostRecentMessage = chatsList.get(i).getMostRecentMessage();
                String updateChatRequest = chatName + mostRecentMessage.toString();
                //based on the doc I assume that a the sendMessage function (2) is what we will use for this
                NetworkMessage request = new NetworkMessage(2, username, "request", username);

                //TODO Send This network message to the server, so that the server can send back any new messages
                //TODO NOTE The incoming network message object must contain the string <ChatName>\n<ChatMessage.toString()>\n<ChatMessage... i.e chat name and messages with ~ as a delimiter
            }

            for (int i = 0; i < incomingMessages.size(); i++) {
                if (incomingMessages.get(i).getFunction() == 0) {
                    String newMessagesBody = incomingMessages.get(i).getMessage();
                    String[] newMessages = newMessagesBody.split("\n");
                    String ChatName = newMessages[0];
                    for (int j = 0; j < chatsList.size(); j++) {
                        if (chatsList.get(i).getChatName().equals(ChatName)) {
                            for (int k = 1; k < newMessages.length; k++) {
                                chatsList.get(i).addMessage(newMessages[k]);
                            }
                        }
                    }
                    incomingMessages.remove(i);


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
}
