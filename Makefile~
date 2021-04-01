JAVAC=/usr/bin/javac
.SUFFIXES: .java .class
SRCDIR=src
BINDIR=bin
$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES=NetworkMessage.class User.class\
	ChatMessage.class Chat.class \
	ClientUpdatorThread.class ClientMessageReceiverThread.class\
	ChatClient.class ChatServerThread.class \
        ChatServer.class
CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

clean:
	rm $(BINDIR)/*.class
runServer:
	java -cp bin ChatServer
runClient:
	java -cp bin ChatClient localhost

		
