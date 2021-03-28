import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.net.*;
import java.util.*;

public class ChatClient extends JFrame implements ActionListener {
    ArrayList<String[]> chatsList = new ArrayList<String[]>();
    static ChatClient chatApp;
    static JTextField usrNmeIn;
    static JTextField passWdIn;
    static JFrame login;

    public static void main(String[] args) throws IOException {

        chatApp = new ChatClient();

        login = new JFrame("login/sign-up");
        login.setSize(400,200);
        login.setLayout(new BorderLayout());

        JPanel textFeilds = new JPanel();
        textFeilds.setLayout(new GridLayout(1,1));
        JLabel usrNme = new JLabel("Username:");
        JLabel pssWd = new JLabel("Password:");
        usrNmeIn = new JTextField();
        passWdIn = new JTextField();
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
            }
        });
        JButton signUpBtn = new JButton("Sign Up");
        signUpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login.setVisible(false);
                chatApp.setVisible(true);
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
}


