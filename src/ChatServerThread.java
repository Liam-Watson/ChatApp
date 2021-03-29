/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServerThread extends Thread {

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean moreQuotes = true;
    
    private ArrayList<User> users;
    private ArrayList<Chat> chats;
    
    public ChatServerThread() throws IOException {
        this("QuoteServerThread");
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
                Scanner scFileName = new Scanner(listOfFiles[i].getName()).useDelimiter("#");
                Chat chat = new Chat(scFileName.next(),scFileName.next());
                chat.initChat(scDir);
                chats.add(chat);
                scDir.close();
            }catch(IOException e){e.printStackTrace();}
        }
    
    }
    
    public void run() {
        int i = 0;
        while (i < 200) {
            DatagramPacket clientPacket = receiveData();
            //Get the string from the packet
            String received = new String(clientPacket.getData(), 0, clientPacket.getLength());
	    //Parse string message to message object
	    NetworkMessage message = new NetworkMessage(received);
	    switch(message.getFunction()){
		case 1:
			//recieveMessage
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
			//Send history
			break;
		case 6:
			//createChat
			break;
		default:
			//sendData(generalError(), clientPacket);
			//TODO: This is the error case, we should handle this by sending back a failed message to client
			break;

	    }
            System.out.println(received);
            sendData(received, clientPacket);
            i++;
        }
        socket.close();
    }

    private DatagramPacket receiveData() {
        try {
            byte[] buf = new byte[256];
            // receive request
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            return packet;
        }catch(IOException e){
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
		NetworkMessage response = new NetworkMessage(-1, message.getUser(), "Failed", "User " +message.getUser() + " already exists");
		System.out.println(response.toString());	
		return response;
	}else{
		writeToFile(message.getUser() + "#" + message.getMessage() + "#", "res/Users.txt");
    		User user = new User(message.getUser(), message.getMessage());
		users.add(user);
		//TODO: we need to decide on a number for response messages, I have used -1
		NetworkMessage response = new NetworkMessage(-1, message.getUser(), "User succsessfully created", "User succsessfully created");
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
   /*
    *This method is to be run when we get a user login request
    *
    */ 
    private NetworkMessage userJoined(NetworkMessage message){
	if(userExists(message)){
		NetworkMessage response = new NetworkMessage(-1, message.getUser(), "Succsess", "User " +message.getUser() + " logged in");
		System.out.println(response.toString());	
		return response;
	}else{
		//TODO: we need to decide on a number for response messages, I have used -1 NBNBNB. Maybe we should match the number with the type of request?
		NetworkMessage response = new NetworkMessage(-1, message.getUser(), "Failed", "User" + message.getUser() + " does not exist. Try check your spelling or create a new user. ");
		System.out.println(response.toString());	
	      	return response;	
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
