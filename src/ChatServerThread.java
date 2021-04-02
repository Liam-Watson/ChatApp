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
    
    public ChatServerThread() throws IOException {
        this("ChatServerThread");
    }

    public ChatServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);

        
        initUsers();
        initChats();
    }

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
				switch (message.getFunction()) {
					case 1:
						//recieveMessage
						sendData(recieveMessage(message).toString(), clientPacket);
						break;
					case 2:
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
					default:
						//sendData(generalError(), clientPacket);
						//TODO: This is the error case, we should handle this by sending back a failed message to client
						break;

				}
				System.out.println(received);
				i++;

			}
		}
        //socket.close();
    }

    private DatagramPacket receiveData() {
        try {
            byte[] buf = new byte[256];
            // receive request
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
	    	System.out.println("Server received: \n --------------------------" + received + "\n -------------------------");
            return packet;
        } catch (SocketException e){
        	System.out.println("socket closed");
        	return null;
		} catch(IOException e){
            System.out.println(e);
            return null;
        }
	}

    private String sendData(String data, DatagramPacket clientPacket) {
        try {
            byte[] buf = new byte[256];
            buf = data.getBytes();
            // send the response to the client at "address" and "port"
            InetAddress address = clientPacket.getAddress();
            int port = clientPacket.getPort();
	    System.out.println("Server is sending: \n ____________________\n" + data + "\n_____________________");
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
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
		System.out.println(response.toString());	
		return response;
	}else{
		writeToFile(message.getUser() + "#" + message.getMessage() + "#", "res/Users.txt");
    		User user = new User(message.getUser(), message.getMessage());
		users.add(user);
		//TODO: we need to decide on a number for response messages, I have used -1
		NetworkMessage response = new NetworkMessage(2, message.getUser(), "Success", "User succsessfully created");
		System.out.println(response.toString());	
	      	return response;	
	}	
    }
    private boolean userExists(NetworkMessage message){
	System.out.println(message.getUser() + "\t" + message.getMessage());
    	User user = new User(message.getUser(), message.getMessage());
	if(users.contains(user)){
	       	return true;
	}else{
	        return false;
	}
    }

    private void writeToFile(String data, String fileName){
	try{
		FileWriter writer = new FileWriter(fileName, true);
		writer.append(data + "\n");
		writer.close();
	}catch(IOException e){
		e.printStackTrace();
	}
    }

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
    private NetworkMessage createChat(NetworkMessage message){//Provide a string of all other users seperated by ";" including sender
	String[] chatUsers = (message.getUser()+";"+message.getMessage()).split(";");
	Chat newChat = new Chat(chatUsers); //Now takes in array 
	if(chats.contains(newChat)){
		NetworkMessage response = new NetworkMessage(3, message.getUser(), "Failed", "Chat " + String.join(";",chatUsers) + " already exists.");
		System.out.println(response.toString());
		//Error chat already exists
		return response;
	}else{
		for(int i=0;i<chatUsers.length;i++){
			if(!users.contains(new User(chatUsers[i],""))){
				System.out.println("Chat not created. User: "+chatUsers[i]+" not found");
				return new NetworkMessage(3,message.getUser(),"Failed: Chat not created. User: "+chatUsers[i]+" not found","");	
			}
		}
		System.out.println(message.getUser() + "\t" + chatUsers);
		writeToFile("", "res/Chats/" + String.join(";",chatUsers));		
		chats.add(newChat);
		NetworkMessage response = new NetworkMessage(3, message.getUser(), "Succsess", "Chat " + String.join(";",chatUsers) + " created.");
		System.out.println(response.toString());
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
		System.out.println("History:"+ history);
		return new NetworkMessage(1, message.getUser(), "Success", history);
	}
   	 
    }
    
    private NetworkMessage recieveMessage(NetworkMessage message){
    	String[] parts = message.getMessage().split("\n"); //TODO: Choose delimiter
    	String status = "Could not find chat";
    	//Message format: chatMessage.toString() / Recipents(seperated by ";")
    	ChatMessage m = new ChatMessage(parts[1]);
	Chat temp = new Chat((message.getUser()+";"+parts[1]).split(";"));
    	for(Chat c : chats){
    		if(temp.getChatName().equals(c.getChatName())){
    			c.addMessage(m.toString());
    			status = "Message Received";
			writeToFile(m.toString(), temp.getChatName());
			
    		}
    	}
    	return new NetworkMessage(1, message.getUser(), status,""+ message.toString().hashCode()); 
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

}
