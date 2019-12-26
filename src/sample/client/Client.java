package sample.client;


import sample.Connection;
import sample.ConsoleHelper;
import sample.Message;
import sample.MessageType;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class Client
{
    protected Connection connection;

    private volatile boolean clientConnected = false;

    public static void main(String[] args)
    {
        Client client = new Client();

        client.run();
    }

    public void run()
    {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();

        try
        {
            synchronized (this)
            {
                wait();

                if (clientConnected)
                {
                    ConsoleHelper.writeMessage("Connection established. To exit, enter 'exit'");

                    while (clientConnected)
                    {
                        String message = ConsoleHelper.readString();

                        if (Objects.equals(message, "exit"))
                        {
                            break;
                        }

                        if (shouldSendTextFromConsole())
                        {
                            sendTextMessage(message);
                        }
                    }
                }
                else
                    {
                    ConsoleHelper.writeMessage("An error occurred while working with the client.");
                }

                notify();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    protected String getServerAddress()
    {
        ConsoleHelper.writeMessage("Enter server address(localhost if on same machine or IP if on different): ");
        String serverAddress = ConsoleHelper.readString();

        return serverAddress;
    }

    protected int getServerPort()
    {
        ConsoleHelper.writeMessage("Enter server port:");
        int port = ConsoleHelper.readInt();

        return port;
    }

    protected String getUserName()
    {
        ConsoleHelper.writeMessage("Enter your username:");
        String userName = ConsoleHelper.readString();

        return userName;

    }

    protected boolean shouldSendTextFromConsole()
    {
        return true;
    }

    protected SocketThread getSocketThread()
    {
        return new SocketThread();
    }

    protected void sendTextMessage(String text)
    {
        try
        {
            connection.send(new Message(MessageType.TEXT, text));
        }
        catch (IOException e)
        {
            clientConnected = false;

            e.printStackTrace();
        }
    }

    public class SocketThread extends Thread
    {

        @Override
        public void run()
        {
            System.out.println("Client thread run");
            try
            {
                Socket socket = new Socket(getServerAddress(), getServerPort());

                if (socket.isConnected())
                {
                    System.out.println("Connected");
                }
                else
                {
                    System.out.println("Not connected");
                }

                connection = new Connection(socket);
                clientHandshake();

                clientMainLoop();
            } catch (IOException | ClassNotFoundException e)
            {
                System.out.println("Client run exception");
                notifyConnectionStatusChanged(false);
            }

        }

        protected void clientHandshake() throws IOException, ClassNotFoundException
        {
            while (true)
            {
                Message message = connection.receive();

                if (message.getMessageType() == MessageType.NAME_REQUEST)
                {
                    connection.send(new Message(MessageType.USER_NAME, getUserName()));
                }
                else if (message.getMessageType() == MessageType.NAME_ACCEPTED)
                {
                    notifyConnectionStatusChanged(true);
                    break;
                }
                else
                {
                    throw new IOException("Unexpected message type.");
                }
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException
        {

            while (!shouldStop(false))
            {
                System.out.println("Client main loop");
                Message message = connection.receive();

                switch (message.getMessageType())
                {
                    case TEXT:
                        processIncomingMessage(message.getData());
                        break;
                    case USER_ADDED:
                        informAboutAddingNewUser(message.getData());
                        break;
                    case USER_REMOVED:
                        informAboutDeletingNewUser(message.getData());
                        break;

                    default:
                        throw new IOException("Unexpected message type");

                }
            }
        }

        protected void processIncomingMessage(String message)
        {
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName)
        {
            ConsoleHelper.writeMessage(String.format("'%s' joined to chat", userName));
        }

        protected void informAboutDeletingNewUser(String userName)
        {
            ConsoleHelper.writeMessage(String.format("'%s' has left the chat", userName));
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected)
        {
            Client.this.clientConnected = clientConnected;

            synchronized (Client.this)
            {
                Client.this.notify();
            }
        }
    }

    protected boolean shouldStop(boolean stop)
    {
        return stop;
    }
}
