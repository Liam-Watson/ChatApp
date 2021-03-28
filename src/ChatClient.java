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
    ArrayList<String[]> chatsList = new ArrayList<String[]>();
    static ChatClient chatApp;
    static JTextField usrNmeIn;
    static JTextField passWdIn;
    static JFrame login;

    public static void main(String[] args) throws IOException {

        socket = new DatagramSocket();
        if (args.length != 1) {
            System.out.println("Usage: java QuoteClient <hostname>");
            return;
        }
        chatApp = new ChatClient();

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
                login.setVisible(false);
                chatApp.setVisible(true);
                try {
                    joinServer(usrNmeIn.getText(), passWdIn.getText(), "login", InetAddress.getByName(args[0]));
                } catch (UnknownHostException unknownHostException) {
                    unknownHostException.printStackTrace();
                }
            }
        });
        JButton signUpBtn = new JButton("Sign Up");
        signUpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login.setVisible(false);
                chatApp.setVisible(true);
                try {
                    joinServer(usrNmeIn.getText(), passWdIn.getText(), "register", InetAddress.getByName(args[0]));
                } catch (UnknownHostException unknownHostException) {
                    unknownHostException.printStackTrace();
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



        ArrayList<JButton> chatButtons = new ArrayList<JButton>();
        chatButtons.add(new JButton("test"));
        chatButtons.add(new JButton("+"));

        for(int i = 0; i < chatsList.size(); i++){
            String[] currentChat = chatsList.get(i);
            chatButtons.add(new JButton(currentChat[0]));
        }
        JPanel chats = new JPanel();
        chats.setLayout(new GridLayout(chatButtons.size(),0));
        chats.setBackground(Color.GRAY);

        for(int i = 0; i < chatButtons.size(); i++){
            chatButtons.get(i).addActionListener(this);
            chatButtons.get(i).setSize(200,700);
            chats.add(chatButtons.get(i));
        }

        add(chats, BorderLayout.WEST);

        JPanel chat = new JPanel();
        chat.setLayout(new BorderLayout());
        chat.setSize(500, 900);
        chat.setBackground(Color.lightGray);
        chat.setBorder(new EmptyBorder(30, 10, 0, 10) );

        JTextArea chatContent = new JTextArea();
        chatContent.setSize(40, 50);
        chatContent.setBorder(new EmptyBorder(30, 30, 30, 30) );
        chatContent.setEditable(false);

        chat.add(chatContent, BorderLayout.CENTER);

        JPanel input = new JPanel();
        input.setLayout(new BorderLayout());
        input.setBorder(new EmptyBorder(10, 10, 10, 10) );

        JTextField message = new JTextField();
        message.setSize(400, 500);
        input.add(message, BorderLayout.CENTER);

        JButton send = new JButton("Send");
        send.addActionListener(this);
        send.setSize(100,500);
        input.add(send, BorderLayout.EAST);

        chat.add(input, BorderLayout.SOUTH);
        add(chat, BorderLayout.CENTER);



    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        switch (action){
        }
    }
	/*
	 * Regster a user with the server by sending a packet containing a name and password
	 * @params register is a variable to tell the server if a user wishes to sign in or create a new user
	 * On the server side we get name from userName and the intent from status and password from message
	 */
	private static void joinServer(String name, String password, String register, InetAddress address){
		NetworkMessage message = new NetworkMessage(3, name, register, password);
		byte[] buf = message.toString().getBytes();
		try{
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
			socket.send(packet);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}


