import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    JPanel chats = new JPanel();
    JFrame chatApp = new JFrame();
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
    public void setNewChatVars(JPanel inP, JFrame inF){
        chats = inP;
        chatApp = inF;
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
        //wait before sending update requests.
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (keepRunning.get()) {
            //Thread sleeps for 2 seconds, i.e chats will be updated every 2 seconds
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //send most recent messages
            for (int i = 0; i < chatsList.size(); i++) {
                String chatName = chatsList.get(i).getChatName();
                ChatMessage mostRecentMessage = chatsList.get(i).getMostRecentMessage();
                String updateChatRequest = "^";
                if (mostRecentMessage != null) {
                    updateChatRequest = chatName + "\n" + mostRecentMessage.toString();
                }else {
                    updateChatRequest = chatName + "\n" + "empty";
                }

                NetworkMessage request = new NetworkMessage(2, username, "request", updateChatRequest);

                try {
                    byte[] buf = new byte[256];
                    buf = request.toString().getBytes();
                    // send the response to the client at "address" and "port"
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, 4445);
                    if (corrupt()) {
                        socket.send(packet);
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }



            }
            //send all chats
            String currentChats = username + "\n";
            for (int i = 0; i < chatsList.size(); i++) {
                if(!chatsList.get(i).getChatName().equals("")) {
                    currentChats += chatsList.get(i).getChatName() + "\n";
                }
            }
            NetworkMessage chatRequest = new NetworkMessage(7, username, "request", currentChats);

            try {
                byte[] buf = new byte[256];
                buf = chatRequest.toString().getBytes();
                // send the response to the client at "address" and "port"
                DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, 4445);
                if (corrupt()) {
                    socket.send(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();

            }

            //check incomming messages for any new chat messages
            for (int i = 0; i < incomingMessages.size(); i++) {
                if (incomingMessages.get(i).getFunction() == 0) {
                    if (!incomingMessages.get(i).getMessage().equals("^")) {
                        String newMessagesBody = incomingMessages.get(i).getMessage();
                        String[] newMessages = newMessagesBody.split("\n");
                        String ChatName = newMessages[0];
                        for (int j = 0; j < chatsList.size(); j++) {
                            if (chatsList.get(j).getChatName().equals(ChatName)) {
                                for (int k = 1; k < newMessages.length; k++) {
                                    if (!chatsList.get(j).containsMessage(newMessages[k])) {
                                        chatsList.get(j).addMessage(newMessages[k]);
                                    }
                                }
                            }
                        }
                    }
                    incomingMessages.remove(i);


                }
            }
            //show the new chat message on the output text area
            for (int i = 0; i < chatsList.size(); i++) {
                if (chatsList.get(i).getChatName().equals(openChat[0])) {
                    chatContent.setText(chatsList.get(i).printMessages());

                }
            }

            //check for any new chats
            for (int i = 0; i < incomingMessages.size(); i++) {
                if (incomingMessages.get(i).getFunction() == 5){
                    if(!incomingMessages.get(i).getStatus().equals("NoNewChats")){
                        String [] newChats = incomingMessages.get(i).getMessage().split("\n");
                        for(int j = 0; j < newChats.length; j++){
                            boolean alreadyHasChat = false;
                            for(int k = 0; k < chatsList.size(); k++) {
                                if(chatsList.get(k).getChatName().equals(newChats[j])){
                                    alreadyHasChat = true;
                                }
                            }
                            if(!alreadyHasChat) {
                                String newChatName = newChats[j];
                                chatsList.add(new Chat(newChats[j]));
                                JButton newChat = new JButton(newChats[j]);
                                newChat.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        openChat[0] = newChatName;
                                        for (int i = 0; i < chatsList.size(); i++) {
                                            if (chatsList.get(i).getChatName().equals(openChat[0])) {
                                                chatContent.setText(chatsList.get(i).printMessages());

                                            }
                                        }
                                    }
                                });

                                chats.add(newChat);
                                chatApp.remove(chats);
                                chatApp.add(chats, BorderLayout.WEST);
                                chats.revalidate();
                            }
                        }

                    }
                    incomingMessages.remove(i);
                }
            }






        }
    }
    /**
    *Helper method to randomly corrupt a message 10% of the time
    *Used for testing purposes
    */
    public boolean corrupt() {
        double rand = Math.random() * 10;
        if (rand <= 1) {
            return false;
        } else {
            return true;
        }

    }


}
