import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.net.*;
import java.util.*;

public class ChatClient extends JFrame implements ActionListener {
    static DatagramSocket socket;
    static ArrayList<Chat> chatsList = new ArrayList<Chat>();
    static ArrayList<JButton> chatButtons = new ArrayList<JButton>();
    JTextField message = new JTextField();
    JTextArea chatContent = new JTextArea();
    static ChatClient chatApp;
    static JTextField usrNmeIn;
    static JTextField passWdIn;
    static JFrame login;
    static String username;
    static String openChat;
    static InetAddress serverAddress;
    public static void main(String[] args) throws IOException {
        serverAddress = InetAddress.getByName(args[0]);
        socket = new DatagramSocket();
        if (args.length != 1) {
            System.out.println("Usage: java QuoteClient <hostname>");
            return;
        }


        login = new JFrame("login/sign-up");
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setSize(400,200);
        login.setLayout(new BorderLayout());

        JPanel textFeilds = new JPanel();
        textFeilds.setSize(400, 50);
        textFeilds.setLayout(new GridLayout(2,2));
        textFeilds.setBorder(new EmptyBorder(10, 20, 10, 20) );
        JLabel usrNme = new JLabel("Username:");
        JLabel pssWd = new JLabel("Password:");
        usrNmeIn = new JTextField();
        passWdIn = new JTextField();
        usrNmeIn.setSize(300, 50);
        passWdIn.setSize(300, 50);
        textFeilds.add(usrNme);
        textFeilds.add(usrNmeIn);
        textFeilds.add(pssWd);
        textFeilds.add(passWdIn);

        login.add(textFeilds, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(0,1));
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    joinServer(usrNmeIn.getText(), passWdIn.getText(), "login", InetAddress.getByName(args[0]));
                } catch (UnknownHostException unknownHostException) {
                    unknownHostException.printStackTrace();
                }
                boolean success = getLoginConfirmation();
                if(success){
                    username = usrNmeIn.getText();
                    login.setVisible(false);
                    chatApp = new ChatClient();
                    generateChatButtons();
                    chatApp.setVisible(true);
                }else{
                    JOptionPane.showMessageDialog(null, "Login has failed please try again");
                }

            }
        });
        JButton signUpBtn = new JButton("Sign Up");
        signUpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    joinServer(usrNmeIn.getText(), passWdIn.getText(), "register", InetAddress.getByName(args[0]));
                } catch (UnknownHostException unknownHostException) {
                    unknownHostException.printStackTrace();
                }
                boolean success = getLoginConfirmation();
                if(success){
                    username = usrNmeIn.getText();
                    login.setVisible(false);
                    chatApp = new ChatClient();
                    generateChatButtons();
                    chatApp.setVisible(true);
                }else{
                    JOptionPane.showMessageDialog(null, "Signup has failed please try again");
                }
            }
        });
        buttons.add(loginBtn);
        buttons.add(signUpBtn);

        login.add(buttons, BorderLayout.SOUTH);

        login.setVisible(true);





        //chatApp.setVisible(true);
//        if (args.length != 1) {
//            System.out.println("Usage: java QuoteClient <hostname>");
//            return;
//        }
//
//        Scanner in = new Scanner(System.in);
//        String inpt = "";
//
//        System.out.println(args[0]);
//        while(inpt != "q") {
//            inpt = in.next();
//            // get a datagram socket
//            DatagramSocket socket = new DatagramSocket();
//            // send request
//            byte[] buf = new byte[256];
//            buf = inpt.getBytes();
//            InetAddress address = InetAddress.getByName(args[0]);
//            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
//            socket.send(packet);
//
//            // get response
//            buf = new byte[256];
//            packet = new DatagramPacket(buf, buf.length);
//            socket.receive(packet);
//            // display response
//            String received = new String(packet.getData(), 0, packet.getLength());
//            System.out.println("From server: " + received);
//            socket.close();
//        }
    Scanner in = new Scanner(System.in);
    String inpt = "";
    //Note here "register" is a place holder that will be determined by user button click @Luc
    joinServer(in.next(), in.next(), "register", InetAddress.getByName(args[0]));


    }

    public ChatClient() {
        super("Chat App");
        setSize(700,900);
        setLocation(50, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());





        JPanel chat = new JPanel();
        chat.setLayout(new BorderLayout());
        chat.setSize(500, 900);
        chat.setBackground(Color.lightGray);
        chat.setBorder(new EmptyBorder(30, 10, 0, 10) );


        chatContent.setSize(40, 50);
        chatContent.setBorder(new EmptyBorder(30, 30, 30, 30) );
        chatContent.setEditable(false);

        chat.add(chatContent, BorderLayout.CENTER);

        JPanel input = new JPanel();
        input.setLayout(new BorderLayout());
        input.setBorder(new EmptyBorder(10, 10, 10, 10) );


        message.setSize(400, 500);
        input.add(message, BorderLayout.CENTER);

        JButton send = new JButton("Send");
        send.addActionListener(this);
        send.setSize(100,500);
        input.add(send, BorderLayout.EAST);

        chat.add(input, BorderLayout.SOUTH);
        add(chat, BorderLayout.CENTER);



    }
    public static void populateChatButton(){
        chatButtons.clear();
        for(int i = 0; i < chatsList.size(); i++){
            Chat currentChat = chatsList.get(i);
            chatButtons.add(new JButton(currentChat.getUser1() + ", " + currentChat.getUser2()));
        }
    }
    public static void generateChatButtons(){
        getChatHistory();
        populateChatButton();
        if(chatApp != null){
            Component[] componentList = chatApp.getComponents();

            for(Component c : componentList){
                if(c instanceof JPanel){
                    if(c.getName().equals("chats")){
                        chatApp.remove(c);
                    }

                }
            }
        }
        chatButtons.add(new JButton("test"));
        openChat = "test";
        chatButtons.add(new JButton("+"));

        JPanel chats = new JPanel();
        chats.setLayout(new GridLayout(chatButtons.size(),0));
        chats.setBackground(Color.GRAY);

        for(int i = 0; i < chatButtons.size(); i++){
            chatButtons.get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String action = e.getActionCommand();
                    switch (action){
                        case "+":
                            String otherUser = "";

                            otherUser = JOptionPane.showInputDialog("Enter the name of the user you want to chat with");
                            createChat(username, otherUser, serverAddress);
                            boolean success = getNewChatConfirmation();
                            if(success){
                                generateChatButtons();
                            }else{
                                JOptionPane.showMessageDialog(null, "Chat creation has failed, other user not found");
                            }
                            break;
                        default:
                            openChat = action;
                            break;
                    }
                }
            });
            chatButtons.get(i).setSize(200,700);
            chats.add(chatButtons.get(i));
        }

        chatApp.add(chats, BorderLayout.WEST);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        switch (action){
            case "send":
                sendMessage(username, openChat ,message.getText());

                break;

        }
    }
	/*
	 * Regster a user with the server by sending a packet containing a name and password
	 * @params register is a variable to tell the server if a user wishes to sign in or create a new user
	 * On the server side we get name from userName and the intent from status and password from message
	 */
	private static void joinServer(String name, String password, String register, InetAddress address){
		if(register.equals("login")){
			NetworkMessage message = new NetworkMessage(4, name, register, password);
			sendData(message.toString());
			//byte[] buf = message.toString().getBytes();
			//DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
			//socket.send(packet);
		}else{
			NetworkMessage message = new NetworkMessage(3, name, register, password);
			sendData(message.toString());
			//byte[] buf = message.toString().getBytes();
			//DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
			//socket.send(packet);
		}
	}

	public static void createChat(String currentName, String userNames, InetAddress address){
	    System.out.println(currentName);
		NetworkMessage message = new NetworkMessage(6, currentName, "request", userNames);
		sendData(message.toString());	
		//byte[] buf = message.toString().getBytes();
		//DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
		//socket.send(packet);
	}
	public static void getChatHistory() {
	//send packet to request chats
	NetworkMessage request = new NetworkMessage(5, username, "request", username);
	sendData(request.toString());
        chatsList.clear();

        byte[] buf = new byte[256];
	
        DatagramPacket packet= new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        NetworkMessage in = new NetworkMessage(new String(packet.getData(), 0, packet.getLength()));
        String chatsReceived = in.getMessage();
        if(chatsReceived.length() > 100) {
            String[] breakChats = chatsReceived.split("~");
            for (int i = 0; i < breakChats.length; i++) {
                String currentChat = breakChats[i];
                Chat newChat = new Chat(currentChat);
                chatsList.add(newChat);
            }
        }

    }

    public static boolean getLoginConfirmation(){
	    boolean confirmed = true;
	    //get response from server if the login succeeded.
	    return confirmed;
    }
    public static boolean getNewChatConfirmation(){
        boolean confirmed = true;
        //get response from server if the chat creation succeeded.
        return confirmed;
    }

    public void sendMessage(String user, String chat, String message){
        //send message to server to update chat
    }
    public void updateChat(String ChatName, ChatMessage mostRecent){
	    //send request to update chat and receive the messages after the most recent message stored

        //destination variable for most recent chat object
        ArrayList<ChatMessage> newMessages = new ArrayList<ChatMessage>();


        for(int i = 0; i < chatsList.size(); i++){
            if(ChatName.equals(chatsList.get(i).getChatName())){
                for(int j = 0; j < newMessages.size(); j++){
                    chatsList.get(i).addMessage(newMessages.get(i).toString());
                }
            }
        }


    }
    //Try to use this method as a pattern to recieve packets
    private static NetworkMessage receiveData() {
        try {
            byte[] buf = new byte[256];
            // receive request
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            return new NetworkMessage(new String(packet.getData(), 0, packet.getLength()));
        }catch(IOException e){
            System.out.println(e);
            return null;
        }
    }
    //Try to use this method as a pattern to send packets
    private static String sendData(String data) {
        try {
            byte[] buf = new byte[256];
            buf = data.getBytes();
            // send the response to the client at "address" and "port"
            DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, 4445);
            socket.send(packet);
            return "sucsess";
        }catch(IOException e){
            System.out.println(e);
            return "failed";
        }
    }

	/*
 *    public NetworkMessage(int f, String u, String s, String m){
 *       status =s;
 *       messageContent = m;
 *       user = u;
 *       ID = u+IDcounter++;
 *       function = f;
 *   }
 */

}


