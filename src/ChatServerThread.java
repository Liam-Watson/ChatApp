//import sun.nio.ch.Net;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatServerThread extends Thread {
	//AtomicBoolean keepRunning = new AtomicBoolean(true);
	private volatile boolean exit = false;
    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean moreQuotes = true;
    
    private ArrayList<User> users;
    private ArrayList<Chat> chats;
    private ArrayList<NetworkRequest> networkRequests;
    
    public ChatServerThread() throws IOException {
        this("ChatServerThread");
    }

    public ChatServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);
        
        NetworkMessage.setIPPort(InetAddress.getLocalHost().getHostAddress()+" "+socket.getLocalPort());
        NetworkMessage.setIDCounter(0);
        
        initUsers();
        initChats();
        networkRequests = new ArrayList<NetworkRequest>();
    }
    /**
    *helper method to intitlise the user arraylist from a text file
    */
    private void initUsers(){
        users = new ArrayList<User>();
        try{
            Scanner scFile = new Scanner(new File("res/Users.txt"));
            while(scFile.hasNext()){
                Scanner scLine = new Scanner(scFile.nextLine()).useDelimiter("#");
		//This is to ensure we do not try read chats if they do not exist for a user that was just created. 
		String userName = scLine.next();
		String password = scLine.next();
		if(scLine.hasNext()){
			String userChats = scLine.next();
			users.add(new User(userName, password, userChats));
		}else{
			users.add(new User(userName, password));
		}
                scLine.close();
            }
            scFile.close();    
        }catch (IOException e){System.out.println(e);}
    }
    /**
    *helper method to intitlise the chats arraylist from a text file
    */
    private void initChats(){
        chats = new ArrayList<Chat>();
        File folder = new File("res/Chats");
        File[] listOfFiles = folder.listFiles();
        
        for (int i = 0; i < listOfFiles.length; i++) {
            try{
                Scanner scDir = new Scanner(listOfFiles[i]);
		System.out.println("reading chat file: " + listOfFiles[i].getName());
                Chat chat = new Chat(listOfFiles[i].getName().split(";"));
                chat.initChat(scDir);
                chats.add(chat);
                scDir.close();
            }catch(IOException e){e.printStackTrace();}
        }
    
    }
    
    public void run() {
        int i = 0;

        while (!exit) {
			DatagramPacket clientPacket = receiveData();
			if (clientPacket != null) {
				//Get the string from the packet
				String received = new String(clientPacket.getData(), 0, clientPacket.getLength());
				//Parse string message to message object
				NetworkMessage message = new NetworkMessage(received);
				if(!message.validate()){
					System.out.println("Message Recieved was Coruppted");
					sendData(new NetworkMessage(message.getFunction(), "Server", "Corrupted","").toString(),clientPacket);
					continue;	
				}
				if(checkDuplicate(message)){
					System.out.println("Message Recieved was a Duplicate");
					message.setStatusDuplicate();
				}
				switch (message.getFunction()) {
					case 1:
						//recieveMessage
						sendData(recieveMessage(message).toString(), clientPacket);
						break;
					case 2:
						sendData(sendMessage(message).toString(), clientPacket);
						//sendMessage
						break;
					case 3:
						sendData(createUser(message).toString(), clientPacket);
						//createUser
						break;
					case 4:
						sendData(userJoined(message).toString(), clientPacket);
						//UserJoined
						break;
					case 5:
						sendData(sendMessageHistory(message).toString(), clientPacket);
						//Send history
						break;
					case 6:
						sendData(createChat(message).toString(), clientPacket);
						//createChat
						break;
					case 7:
						//get new chats
						sendData(getNewChats(message).toString(), clientPacket);
						break;
					default:
						//sendData(generalError(), clientPacket);
						//TODO: This is the error case, we should handle this by sending back a failed message to client
						break;

				}
				i++;

			}
		}
        //socket.close();
    }
    /**
    *checks if a network message has been proccessed before and is therefore a duplicate
    */
    private boolean checkDuplicate(NetworkMessage n){
    	NetworkRequest nr = new NetworkRequest(n.getIPPort());
    	if(!networkRequests.contains(nr)){
    		networkRequests.add(nr);
    		return false;
    	}
    	nr = networkRequests.get(networkRequests.indexOf(nr));//
    	if(n.getFunction()==3 || n.getFunction()==4){
    		nr.reset();
    		return false;
    	}
    	return !nr.makeRequest(n.getCounter());
    }
    /**
    *Creates a datagramPacket to recieve any network messages from the socket
    *Prints out the networkMessage that was received
    *returns the packet recieved or prints the error encounted
    */
    private DatagramPacket receiveData() {
        try {
            byte[] buf = new byte[256];
            // receive request
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
	    	System.out.println("Server received: \n -------------------------- + \n" + received + "\n -------------------------");
            return packet;
        } catch (SocketException e){
        	System.out.println("socket closed");
        	return null;
		} catch(IOException e){
            System.out.println(e);
            return null;
        }
	}
    /**
    *takes in a string to send and the reciever
    *takes the clients address and port and creates a datagram to send the string to the client
    *sends the datagram packet through the socket
    */
    private String sendData(String data, DatagramPacket clientPacket) {
        try {
            byte[] buf = new byte[256];
            buf = data.getBytes();
            // send the response to the client at "address" and "port"
            InetAddress address = clientPacket.getAddress();
            int port = clientPacket.getPort();
	    System.out.println("Server is sending: \n ____________________\n" + data + "\n_____________________");
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            //if(corrupt()){
	    	socket.send(packet);
	    //}
            	return "sucsess";
        }catch(IOException e){
            System.out.println(e);
            return "failed";
        }
    }
    /*
     * We return a message based on if a user exists or not
     * Currently the status and message are the same I think we should decide on how to seperate them. TODO
     */
    private NetworkMessage createUser(NetworkMessage message){
	if(userExists(message)){
		NetworkMessage response = new NetworkMessage(2, message.getUser(), "Failed", "User " +message.getUser() + " already exists");
		return response;
	}else{	
		if(!message.checkDuplicate()){
			writeToFile(message.getUser() + "#" + message.getMessage() + "#", "res/Users.txt");
	    		User user = new User(message.getUser(), message.getMessage());
			users.add(user);
		}
		NetworkMessage response = new NetworkMessage(2, message.getUser(), "Success", "User succsessfully created");
	      	return response;	
	}	
    }
    /**
    *checks if a user already exits in the user arraylist
    */
    private boolean userExists(NetworkMessage message){
    	User user = new User(message.getUser(), message.getMessage());
	if(users.contains(user)){
	       	return true;
	}else{
	        return false;
	}
    }
    /**
    *helper method to write a string to a specifed file
    */
    private void writeToFile(String data, String fileName){
	try{
		FileWriter writer = new FileWriter(fileName, true);
		if(data != null){
			writer.append(data + "\n");
		}
		writer.close();
	}catch(IOException e){
		e.printStackTrace();
	}
    }
    /**
    *takes username from network message and passwrod from the netowrkmessage message 
    *checks if a user has entered a correct username and password combination
    */
    private boolean checkUserPw(NetworkMessage message){
    	User user = new User(message.getUser(), message.getMessage());
	return users.get(users.indexOf(user)).authenticate(message.getUser(), message.getMessage());		
    }
   /*
    *This method is to be run when we get a user login request
    *
    */ 
    private NetworkMessage userJoined(NetworkMessage message){
	if(userExists(message) && checkUserPw(message)){
		NetworkMessage response = new NetworkMessage(2, message.getUser(), "Success", "User " +message.getUser() + " logged in");
		return response;
	}else{
		//TODO: we need to decide on a number for response messages, I have used -1 NBNBNB. Maybe we should match the number with the type of request?
		NetworkMessage response = new NetworkMessage(2, message.getUser(), "Failed", "User" + message.getUser() + " does not exist. Try check your spelling or create a new user. ");
	      	return response;	
	}	
		

    }
    /**
    *This method is to be run when a user tries to create a new chat
    *the users to be added to the chat are checked to see if a user tried to include themself twice. If so they are removed
    *All users to be added are checked to be valid. If not an error message is returned
    *Checks if the chat already exists. If so, an error message is returned
    *If there are no errors, a new chat is created
    */
    private NetworkMessage createChat(NetworkMessage message){//Provide a string of all other users seperated by ";" including sender
	String[] chatUsersUnsafe = (message.getUser()+";"+message.getMessage()).split(";");
	String[] chatUsers;
	int includedUser=0;
	for(int i=1;i<chatUsersUnsafe.length;i++){
		if(chatUsersUnsafe[i].equals(message.getUser())){
			includedUser = i;
		}		
	}
	if(includedUser!=0){
		chatUsers = new String[chatUsersUnsafe.length-1];
		int found=0;
		for(int i=0;i<chatUsersUnsafe.length;i++){
			if(i!=includedUser){
				chatUsers[i-found] = chatUsersUnsafe[i];
			}else{
				found++;
			}		
		}
	}else{
		chatUsers = chatUsersUnsafe;
	}
	Chat newChat = new Chat(chatUsers); //Now takes in array 
	if(chats.contains(newChat)){
		NetworkMessage response = new NetworkMessage(3, message.getUser(), "Failed", "Chat " + String.join(";",chatUsers) + " already exists.");
		//Error chat already exists
		return response;
	}else{
		for(int i=0;i<chatUsers.length;i++){
			if(!users.contains(new User(chatUsers[i],""))){
				return new NetworkMessage(3,message.getUser(),"Failed: Chat not created. User: "+chatUsers[i]+" not found","");	
			}
		}
		if(!message.checkDuplicate()){
			writeToFile(null, "res/Chats/" + String.join(";",chatUsers));		
			chats.add(newChat);
		}
		NetworkMessage response = new NetworkMessage(3, message.getUser(), "Success", "Chat " + String.join(";",chatUsers) + " created.");
		return response;
	}
    }
    /*
     * Here we find all the chats a user is part of and return a network message contianing them. 
     * This method is to be used to intially populate a users chats on login. 
     */
    private NetworkMessage sendMessageHistory(NetworkMessage message){
    	//TODO: Validate that userNames and messages do not contain our delimiters :(
	String userName = message.getUser();
	ArrayList<Chat> userChats = new ArrayList<Chat>();
	String history = "";
	for(Chat c : chats){
		if(c.userPartOfChat(userName)){
			history += c.toString() + "~";
		}
	}
	if(history.equals("")){
		return new NetworkMessage(1, message.getUser(), "Failed", history);
	}else{
		return new NetworkMessage(1, message.getUser(), "Success", history);
	}
   	 
    }
    /**
    *This method is called to get new chats that a user does not have 
    */
    private NetworkMessage getNewChats(NetworkMessage message){
    	String[] messageData = message.getMessage().split("\n");
    	String username = messageData[0];
    	String newChats = "";
    	boolean hasChat = false;
    	String currentChat =  "";
    	for(Chat c : chats){
    		if(c.getChatName().contains(username)){
    			currentChat = c.getChatName();
    			for(int j = 1; j < messageData.length; j++){

    				if(messageData[j].trim().equals(currentChat.trim())){
    					hasChat = true;
					}

				}
				if(!hasChat) {
					newChats += currentChat + "\n";
				}
				hasChat = false;
			}
		}
    	if(newChats.equals("")) {
			return new NetworkMessage(5, message.getUser(), "NoNewChats", "");
		}else{
			return new NetworkMessage(5, message.getUser(), "NewChats", newChats);
		}
	}
	/**
        *This message is called to recieve a message from the client
        *It checks if the message has a valid chat to be added to
        *Then it is checked to see if the message is already in the chat
        *It then adds the message to the chat and writes it to the file
        */
	private NetworkMessage recieveMessage(NetworkMessage message) {
		String[] parts = message.getMessage().split("\n"); //TODO: Choose delimiter
		String status = "Could not find chat";
		//Message format: chatMessage.toString() / Recipents(seperated by ";")
		ChatMessage m = new ChatMessage(parts[0]);
		Chat temp = new Chat(parts[1].split(";"));
		//System.out.println(">>>>>>>>>>>>>>" +temp.toString());
		for (Chat c : chats) {
			if (c.equals(temp)) {
				//if(temp.getChatName().equals(c.getChatName())){
				System.out.println("added");
				if(!message.checkDuplicate()){
					if (!c.containsMessage(m.toString())) {
						c.addMessage(m.toString());
					}
					status = "Message Received";
					writeToFile(parts[0], "res/Chats/" + c.getChatName());
				}
			}
		}
		return new NetworkMessage(12, message.getUser(), status, "Function 12");
	}
    
    public void end(){
    	exit = true;
    	socket.close();
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
    /**
    *This mesthod is called to send a new message to the client
    */
    public NetworkMessage sendMessage(NetworkMessage message){
		//Note Here we are using an & to delimit chat name and chat message. The same is used client side.
		//There is also a problem where there have been no message changes and for the Networkmessage contructor we need a character to send. I have used ^
		String[] messageData = message.getMessage().split("\n");
		if (messageData[1].equals("empty")) {
			Chat tmpChat = new Chat(messageData[0]);
			int index = -1;
			for(int i = 0; i < chats.size(); i++){
				if(chats.get(i).getChatName().equals(tmpChat.getChatName())){
					index = i;
				}
			}

			if (index != -1) {
				Chat actualChat = chats.get(index);
				return new NetworkMessage(0, message.getUser(), "succsess", actualChat.toString()); //TODO: Handle case where chat is empty
			} else {
				return new NetworkMessage(0, message.getUser(), "failed1", "^");

			}
		} else {
			Chat tmpChat = new Chat(message.getMessage());
			if (chats.contains(tmpChat)) {
				Chat actualChat = chats.get(chats.indexOf(tmpChat));
				ChatMessage msg = new ChatMessage(message.getMessage().split("\n")[1]);
				String messages = actualChat.getMessagesSince(msg);
				if (messages.equals("")) {
					return new NetworkMessage(0, message.getUser(), "failed2", "^");
				}

				return new NetworkMessage(0, message.getUser(), "succsess", messages);
			} else {
				return new NetworkMessage(0, message.getUser(), "failed3", "^");
			}
		}
	}
   /* public boolean corrupt(){
        double rand = Math.random()*10;
        if(rand <= 1){
                return false;
        }else{
		return true;
        }

    }*/

}
