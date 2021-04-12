import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.PixelGrabber;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.net.*;
import java.util.*;
import java.util.List;
import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class
import java.util.Timer;

public class ChatClient extends JFrame implements ActionListener {
    //declare public and static variables that need to be used in multiple methods and classes

    static DatagramSocket socket; //This is for sending
    static List<Chat> chatsList = Collections.synchronizedList(new ArrayList<Chat>());
    static ArrayList<JButton> chatButtons = new ArrayList<JButton>();
    public static List<NetworkMessage> incomingMessages = Collections.synchronizedList(new ArrayList<NetworkMessage>());
    static JTextField message = new JTextField();
    static JTextArea chatContent = new JTextArea();
    static JScrollPane scrollPane = new JScrollPane(chatContent);
    static ChatClient chatApp;
    static JPanel chats = new JPanel();
    static JTextField usrNmeIn;
    static JPasswordField passWdIn;
    static JFrame login;
    static String username;
    static String[] openChat = new String[1];
    static InetAddress serverAddress;
    static String LoginOrSignUp;
    static String otherUser;


    static ClientUpdatorThread clUpdator;
    static ClientMessageReceiverThread clReceiver;
    static NetworkMessage response = new NetworkMessage(-1, "failed", "failed", "failed");
    /**
    *Main method to create and open the login screen GUI
    *Initilises the Socket and sets the servers adress
    *Sets the static varables in the Network Message Class 
    */
    public static void main(String[] args) throws IOException {
        serverAddress = InetAddress.getByName(args[0]);
        socket = new DatagramSocket();

        NetworkMessage.setIPPort(InetAddress.getLocalHost().getHostAddress() + " " + socket.getLocalPort());
        NetworkMessage.setIDCounter(0);

        clUpdator = new ClientUpdatorThread();
        clUpdator.setIncommingMessages(incomingMessages);
        clUpdator.setChatsList(chatsList);
        clUpdator.setCurrentChat(openChat);
        clUpdator.setOutputArea(chatContent);
        clUpdator.setSocket(socket);
        clUpdator.setServerAddress(serverAddress);



        clReceiver = new ClientMessageReceiverThread();
        clReceiver.setSocket(socket);
        clReceiver.setIncommingMessages(incomingMessages);
        clReceiver.start();

        if (args.length != 1) {
            System.out.println("Usage: java QuoteClient <hostname>");
            return;
        }


        login = new JFrame("login/sign-up");
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setSize(400, 200);
        login.setLayout(new BorderLayout());
        login.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                clUpdator.keepRunning.set(false);
                clReceiver.end();
                //System.out.println("window closed");
                e.getWindow().dispose();

            }
        });


        JPanel textFeilds = new JPanel();
        textFeilds.setSize(400, 50);
        textFeilds.setLayout(new GridLayout(2, 2));
        textFeilds.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel usrNme = new JLabel("Username:");
        JLabel pssWd = new JLabel("Password:");
        usrNmeIn = new JTextField();
        passWdIn = new JPasswordField();
        usrNmeIn.setSize(300, 50);
        passWdIn.setSize(300, 50);
        textFeilds.add(usrNme);
        textFeilds.add(usrNmeIn);
        textFeilds.add(pssWd);
        textFeilds.add(passWdIn);

        login.add(textFeilds, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(0, 1));
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginOrSignUp = "login";
                try {
                    joinServer(usrNmeIn.getText(), String.valueOf(passWdIn.getPassword()), "login", InetAddress.getByName(args[0]));
                } catch (UnknownHostException unknownHostException) {
                    unknownHostException.printStackTrace();
                }
                boolean success = false;
                try {
                    success = getLoginConfirmation();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                if (success) {
                    NetworkMessage.setIDCounter(0);
                    username = usrNmeIn.getText();
                    clUpdator.setUsername(username);
                    login.setVisible(false);
                    chatApp = new ChatClient();
                    clUpdator.setNewChatVars(chats, chatApp);
                    try {
                        getChatHistory();
                        generateChatButtons();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    chatApp.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Login has failed please try again");
                }

            }
        });
        JButton signUpBtn = new JButton("Sign Up");
        signUpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginOrSignUp = "register";
                try {
                    joinServer(usrNmeIn.getText(), String.valueOf(passWdIn.getPassword()), "register", InetAddress.getByName(args[0]));
                } catch (UnknownHostException unknownHostException) {
                    unknownHostException.printStackTrace();
                }
                boolean success = false;
                try {
                    success = getLoginConfirmation();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                if (success) {
                    NetworkMessage.setIDCounter(0);
                    username = usrNmeIn.getText();
                    clUpdator.setUsername(username);
                    login.setVisible(false);
                    chatApp = new ChatClient();
                    clUpdator.setNewChatVars(chats, chatApp);
                    try {
                        getChatHistory();
                        generateChatButtons();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    chatApp.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Signup has failed please try again");
                }
            }
        });
        buttons.add(loginBtn);
        buttons.add(signUpBtn);

        login.add(buttons, BorderLayout.SOUTH);

        login.setVisible(true);

    }
    /**
    *Creates the ChatClient GUI for creating chat as well as sending and reciveing messages 
    */
    public ChatClient() {
        super("Chat App");
        setSize(700, 900);
        setLocation(50, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        JPanel chat = new JPanel();
        chat.setLayout(new BorderLayout());
        chat.setSize(500, 900);
        chat.setBackground(Color.lightGray);
        chat.setBorder(new EmptyBorder(30, 10, 0, 10));


        chatContent.setSize(40, 50);
        chatContent.setBorder(new EmptyBorder(30, 30, 30, 30));
        chatContent.setEditable(false);
        chatContent.setText("Welcome to Chat App - Our submission for CSC3002F Assignment 1\nBy\n"+
                " - Liam Watson : WTSLIA001\n"+
                " - Richard Patterson : PTRRIC011\n"+
                " - Luc van den Handel : VHNLUC001\n\n\n\n\n\n\n\n" +
                "<-----Open a chat to start messaging or use the \"+\" button to create a new chat\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" +
                "Once a chat is open, type a message and hit send.\n" +
                "\t |\n\t |\n\t |\n\t |\n\t |\n\tV\n");

        chat.add(scrollPane, BorderLayout.CENTER);

        JPanel input = new JPanel();
        input.setLayout(new BorderLayout());
        input.setBorder(new EmptyBorder(10, 10, 10, 10));


        message.setSize(400, 500);
        input.add(message, BorderLayout.CENTER);

        JButton send = new JButton("Send");
        send.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        LocalDateTime dateTime = LocalDateTime.now();
                        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");
                        String chatMessage = message.getText();
                        message.setText("");

                        try {
                            sendMessage(username, openChat[0], "1" + "#" + username + "#" + dateTime.format(dateTimeFormat) + "#" + chatMessage + "#");
                        } catch (InterruptedException f) {
                            f.printStackTrace();
                        }
                        String[] defaultChats = new String[]{"default1", "default2"}; //TODO: Explain what this is, will this not cause weird behavior?
                        Chat currentChat = new Chat(defaultChats);
                        for (int i = 0; i < chatsList.size(); i++) {
                            if (chatsList.get(i).getChatName().equals(openChat[0])) {
                                currentChat = chatsList.get(i);
                                currentChat.addMessage("1" + "#" + username + "#" + dateTime.format(dateTimeFormat) + "#" + chatMessage + "#");
                            }
                        }
                        String oldMessages = chatContent.getText();
                        chatContent.setText(oldMessages + username + ": " + dateTime.format(dateTimeFormat) + "\n" + chatMessage + "✓\n");


                    }
                });
        send.setSize(100, 500);
        input.add(send, BorderLayout.EAST);

        chat.add(input, BorderLayout.SOUTH);
        add(chat, BorderLayout.CENTER);

        clUpdator.start();

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                clUpdator.keepRunning.set(false);
                clReceiver.end();
                e.getWindow().dispose();

            }
        });


    }

    public static void populateChatButton() {
        chatButtons.clear();

        for (int i = 0; i < chatsList.size(); i++) {
            if (!chatsList.get(i).getChatName().equals("")) {
                Chat currentChat = chatsList.get(i);
                JButton chatButton = new JButton(currentChat.getChatName());
                chatButton.setVisible(true);
                chatButton.setText(currentChat.getChatName());
                chatButtons.add(chatButton);
            }
        }
    }
    /**
    *Helper method to generate the chat buttons
    */
    public static void generateChatButtons() throws InterruptedException {
        //getChatHistory();

        if (chatApp != null) {
            Component[] componentList = chatApp.getComponents();

            for (Component c : componentList) {
                if (c instanceof JPanel) {
                    if (c.getName().equals("chats")) {
                        System.out.println("frame removed");
                        chatApp.remove(c);
                    }

                }
            }
        }
        populateChatButton();
        chatButtons.add(new JButton("+"));

        chats = new JPanel();
        clUpdator.setNewChatVars(chats, chatApp);
        chats.setName("chats");
        chats.setLayout(new GridLayout(chatButtons.size(), 0));
        chats.setBackground(Color.GRAY);
        //chatApp.remove(chats);

        for (int i = 0; i < chatButtons.size(); i++) {

            chatButtons.get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String action = e.getActionCommand();
                    switch (action) {
                        case "+":
                            otherUser = "";

                            otherUser = JOptionPane.showInputDialog("Enter the name of the user(s) you want to chat with.\nTo make a group chat, enter the usernames with a semi-colon between them\n(e.g. John;Jane will create a chat with you, John and Jane)");
                            int index = otherUser.indexOf(username);
                            while(index>=0){
                            	if(index+username.length()<otherUser.length()){
                            		otherUser = otherUser.substring(0,index) + otherUser.substring(index+username.length()+1);
                            	}else{
                            		otherUser = otherUser.substring(0,index-1) + otherUser.substring(index+username.length());
                            	}
                            	index = otherUser.indexOf(username);
                            }
                            createChat(username, otherUser, serverAddress);
                            String chatName = username + ";" + otherUser;
                            boolean success = false;
                            try {
                                success = getNewChatConfirmation();
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                            if (success) {
                                try {
                                    String[] newUsers = new String[2];
                                    newUsers[0] = username;
                                    newUsers[1] = otherUser;
                                    Chat newChat = new Chat(newUsers);
                                    chatsList.add(newChat);
                                    chatApp.remove(chats);

                                    generateChatButtons();
                                } catch (InterruptedException interruptedException) {
                                    interruptedException.printStackTrace();
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Chat creation has failed, other user not found");
                            }
                            otherUser = "";
                            break;
                        default:
                            openChat[0] = action;
                            break;
                    }
                    showMessages();

                }
            });

            chats.add(chatButtons.get(i));
        }

        chatApp.add(chats, BorderLayout.WEST);
        chats.revalidate();
    }
    /**
    *Displays the messages for the currently selected chosen chat
    */
    public static void showMessages() {
        String[] defaultChats = new String[]{"default1", "default2"}; //TODO: Explain what this is, will this not cause weird behavior?
        Chat currentChat = new Chat(defaultChats);
        for (int i = 0; i < chatsList.size(); i++) {
            if (chatsList.get(i).getChatName().equals(openChat[0])) {
                currentChat = chatsList.get(i);
            }
        }
        chatContent.setText(currentChat.printMessages());
    }
    /**
    *Sends a message to the Server with the text from the text field, and the currently selected chat
    */
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    /*
     * Regster a user with the server by sending a packet containing a name and password
     * @params register is a variable to tell the server if a user wishes to sign in or create a new user
     * On the server side we get name from userName and the intent from status and password from message
     */
    private static void joinServer(String name, String password, String register, InetAddress address) {
        if (register.equals("login")) {
            NetworkMessage message = new NetworkMessage(4, name, register, password);
            sendData(message.toString());
            //byte[] buf = message.toString().getBytes();
            //DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            //socket.send(packet);
        } else {
            NetworkMessage message = new NetworkMessage(3, name, register, password);
            sendData(message.toString());
            //byte[] buf = message.toString().getBytes();
            //DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            //socket.send(packet);
        }
    }
    /**
    *Sends a request to the server to create a chat
    */
    public static void createChat(String currentName, String userNames, InetAddress address) {
        NetworkMessage message = new NetworkMessage(6, currentName, "request", userNames);
        sendData(message.toString());
        //byte[] buf = message.toString().getBytes();
        //DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        //socket.send(packet);
    }
    /**
    *Sends a request to the server for all of the current chats 
    */
    public static void getChatHistory() throws InterruptedException {
        //send packet to request chats
        NetworkMessage request = new NetworkMessage(5, username, "request", username);
        sendData(request.toString());
        chatsList.clear();
        Thread.sleep(400);
        response = new NetworkMessage(-1, "failed", "failed", "failed");
        while (response.getFunction() != 1) {

            //check for incoming message until timeout
            Timer check = new Timer();
            final int[] count = {0};
            check.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (count[0] < 20 && response.getFunction() != 1) {
                        for (int l = 0; l < incomingMessages.size(); l++) {
                            if (incomingMessages.get(l).getFunction() == 1) {
                                response = incomingMessages.get(l);
                                incomingMessages.remove(l);
                                break;
                            }
                        }
                        count[0]++;
                    } else {
                        check.cancel();
                    }
                }
            }, 0, 1000);


            if (response.getFunction() != 1) {
                //resend packet after delay
                Thread.sleep(500);
                request = new NetworkMessage(5, username, "request", username);
                sendData(request.toString());
            }
        }


        String chatsReceived = response.getMessage();
        if (chatsReceived != null) {
            String[] breakChats = chatsReceived.split("~");
            for (int i = 0; i < breakChats.length; i++) {
                String currentChat = breakChats[i];
                Chat newChat = new Chat(currentChat);
                chatsList.add(newChat);
            }
        }

    }
    /**
    *Checks if the login was a success or failure and reports it to the user
    */
    public static boolean getLoginConfirmation() throws InterruptedException {
        //get response from server if the login succeeded.
        //sorry about these default objects, Java just wont let me use a variable that has not been initialised.
        Thread.sleep(400);
        response = new NetworkMessage(-1, "failed", "failed", "failed");
        while (response.getFunction() != 2) {

            //check for incoming message until timeout
            Timer check = new Timer();
            final int[] count = {0};
            check.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (count[0] < 20 && response.getFunction() != 2) {
                        for (int l = 0; l < incomingMessages.size(); l++) {
                            if (incomingMessages.get(l).getFunction() == 2) {
                                response = incomingMessages.get(l);
                                incomingMessages.remove(l);
                                break;
                            }
                        }
                        count[0]++;
                    } else {
                        check.cancel();
                    }
                }
            }, 0, 1000);


            if (response.getFunction() != 2) {
                //resend packet
                Thread.sleep(500);
                joinServer(usrNmeIn.getText(), String.valueOf(passWdIn.getPassword()), LoginOrSignUp, serverAddress);
            }
        }
        if (response.getStatus().equals("Success")) {
            return true;
        } else {
            return false;
        }
    }
    /**
    *Confirms that the server created a new chat
    */
    public static boolean getNewChatConfirmation() throws InterruptedException {
        //get response from server if the chat creation succeeded.
        Thread.sleep(400);
        response = new NetworkMessage(-1, "failed", "failed", "failed");
        while (response.getFunction() != 3) {

            //check for incoming message until timeout
            Timer check = new Timer();
            final int[] count = {0};
            check.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (count[0] < 20 && response.getFunction() != 3) {
                        for (int l = 0; l < incomingMessages.size(); l++) {
                            if (incomingMessages.get(l).getFunction() == 3) {
                                response = incomingMessages.get(l);
                                incomingMessages.remove(l);
                                break;
                            }
                        }
                        count[0]++;
                    } else {
                        check.cancel();
                    }
                }
            }, 0, 1000);


            if (response.getFunction() != 3) {
                //resend packet
                Thread.sleep(500);
                createChat(username, otherUser, serverAddress);
            }
        }
        if (response.getStatus().equals("Success")) {
            return true;
        } else {
            return false;
        }
    }

    /**
    *Sends a message to the sever to send to the recipent(s)
    */
    public void sendMessage(String user, String chat, String message) throws InterruptedException {
        NetworkMessage packet = new NetworkMessage(1, user, "request", message + "\n" + chat);
        System.out.println("Chat  :" + chat);
        sendData(packet.toString());
        Thread.sleep(400);
        response = new NetworkMessage(-1, "failed", "failed", "failed");
        while (response.getFunction() != 4) {

            //check for incoming message until timeout
            Timer check = new Timer();
            final int[] count = {0};
            check.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (count[0] < 20 && response.getFunction() != 4) {
                        for (int l = 0; l < incomingMessages.size(); l++) {
                            if (incomingMessages.get(l).getFunction() == 4) {
                                response = incomingMessages.get(l);
                                incomingMessages.remove(l);
                                break;
                            }
                        }
                        count[0]++;
                    } else {
                        check.cancel();
                    }
                }
            }, 0, 1000);


            if (response.getFunction() != 4) {
                //resend packet
                Thread.sleep(500);
                packet = new NetworkMessage(1, user, "request", message + "\n" + chat);
                sendData(packet.toString());
            }
        }
        if (response.getFunction() == 4) {
            if (response.getStatus().equals("Message Received")) {
                System.out.println("Message: " + message + " received on chat " + chat);
            } else if (response.getStatus().equals("Message Received")) {
                System.out.println("Could not find chat: " + chat);
                sendMessage(user, chat, message);
            }
        }
    }

    //Try to use this method as a pattern to recieve packets
//    private static NetworkMessage receiveData() {
//        try {
//            byte[] buf = new byte[256];
//            // receive request
//            DatagramPacket packet = new DatagramPacket(buf, buf.length);
//            socket.receive(packet);
//            return new NetworkMessage(new String(packet.getData(), 0, packet.getLength()));
//        }catch(IOException e){
//            e.printStackTrace();
//	    return null;
//        }
//    }
    //Try to use this method as a pattern to send packets
    /**
    *helper method to send data through the socket to the server
    */
    private static String sendData(String data) {
        try {
            byte[] buf = new byte[256];
            buf = data.getBytes();
            // send the response to the client at "address" and "port"
            DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, 4445);
            if (corrupt()) {
                socket.send(packet);
                return "sucsess";
            } else {
                return "failed";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "failed";
        }
    }
    /**
    *Helper Method to randomly corrupt a message with a 10% chance
    *Used for error checking
    */
    public static boolean corrupt() {
        double rand = Math.random() * 10;
        if (rand <= 1) {
            return false;
        } else {
            return true;
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


