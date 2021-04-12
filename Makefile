JAVAC=/usr/bin/javac
.SUFFIXES: .java .class
SRCDIR=src
BINDIR=bin
$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES=NetworkMessage.class User.class\
	ChatMessage.class Chat.class \
	NetworkRequest.class \
	ClientUpdatorThread.class ClientMessageReceiverThread.class\
	ChatClient.class ChatServerThread.class \
        ChatServer.class
CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

clean:
	rm $(BINDIR)/*.class
runServer:
	java -cp bin ChatServer
runClientLocal:
	java -cp bin ChatClient localhost
runClientRemote:
	java -cp bin ChatClient ec2-13-244-233-216.af-south-1.compute.amazonaws.com
