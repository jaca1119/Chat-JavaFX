package sample;

import sample.Message.Message;
import sample.Message.MessageType;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Server
{
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException
    {
        ConsoleHelper.writeMessage("Enter server port(1024–49151 is fo user or registered ports): ");
//        int port = ConsoleHelper.readInt();
        int port = 1025;

        ServerSocket serverSocket = new ServerSocket(port, 10, InetAddress.getLocalHost());
//        ServerSocket serverSocket = new ServerSocket(port);

        //debug info
        ConsoleHelper.writeMessage("Server run info:");
        ConsoleHelper.writeMessage(String.format("%s \n%s\n%s\n", serverSocket.toString(), serverSocket.getInetAddress(), serverSocket.getLocalSocketAddress()));
        ConsoleHelper.writeMessage("Server host info:");
        ConsoleHelper.writeMessage(InetAddress.getLocalHost().toString() + " " + InetAddress.getLoopbackAddress());


        ConsoleHelper.writeMessage("Server is running.");

        while (true)
        {
            Socket socket = null;

            try
            {
                socket = serverSocket.accept();

                Handler handler = new Handler(socket);
                handler.start();
            } catch (Exception e)
            {
                ConsoleHelper.writeMessage(e.getMessage());
                serverSocket.close();

                if (socket != null)
                {
                    socket.close();
                }
                break;
            }
        }
    }



    public static void sendBroadcastMessage(Message message)
    {
        connectionMap.forEach((name, connection) -> {
            try
            {
                connection.send(message);
            } catch (IOException e)
            {
                try
                {
                    connection.send(new Message(MessageType.TEXT, "Message couldn't be send."));
                } catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        });
    }


    private static class Handler extends Thread
    {
        private Socket socket;

        public Handler(Socket socket)
        {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            ConsoleHelper.writeMessage("New connection established with " + socket.getRemoteSocketAddress());

            Connection connection = null;
            String userName = null;

            try
            {
                connection = new Connection(socket);
                userName = serverHandshake(connection);

                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);

                serverMainLoop(connection, userName);
            } catch (IOException | ClassNotFoundException e)
            {
                ConsoleHelper.writeMessage("Error occurred with connecting with the remote address.");

                if (connection != null)
                {
                    try
                    {
                        connection.close();
                    } catch (IOException ex)
                    {
                        //empty
                    }
                }
            }

            //Probably should be in catch above
            if (userName != null)
            {
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            }

            ConsoleHelper.writeMessage("Connection with remote address is closed");
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException
        {
            while (true)
            {
                Message message = connection.receive();

                if (message.getMessageType() == MessageType.TEXT)
                {
                    StringBuilder builder = new StringBuilder(userName);
                    builder.append(": ").append(message.getData());

                    Message textMessage = new Message(MessageType.TEXT, builder.toString());

                    sendBroadcastMessage(textMessage);
                }
                else if (message.getMessageType() == MessageType.IMAGE)
                {
                    Message imageMessage = new Message(MessageType.IMAGE, message.getImage());

                    sendBroadcastMessage(imageMessage);
                }
                else
                {
                    ConsoleHelper.writeMessage("Error with message type");
                }
            }
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException
        {
            connection.send(new Message(MessageType.NAME_REQUEST));
            Message message = connection.receive();

            while (!message.getMessageType().equals(MessageType.USER_NAME) || message.getData() == null || connectionMap.containsKey(message.getData()) || message.getData().equals(""))
            {
                connection.send(new Message(MessageType.NAME_REQUEST));
                message = connection.receive();
            }

            connectionMap.put(message.getData(), connection);
            connection.send(new Message(MessageType.NAME_ACCEPTED));

            return message.getData();
        }

        private void notifyUsers(Connection connection, String userName) throws IOException
        {
            for (Map.Entry<String, Connection> entry : connectionMap.entrySet())
            {
                String name = entry.getKey();
                Connection user = entry.getValue();
                if (!Objects.equals(name, userName))
                {
                    connection.send(new Message(MessageType.USER_ADDED, name));
                }
            }
        }
    }
}
