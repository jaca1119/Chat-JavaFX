package sample.client;

import sample.ConsoleHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class BotClient extends Client
{

    public static void main(String[] args)
    {
        BotClient botClient = new BotClient();
        botClient.run();
    }

    @Override
    protected SocketThread getSocketThread()
    {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole()
    {
        return false;
    }

    @Override
    protected String getUserName()
    {
        return "BotClient_" + (int) (Math.random() * 100);
    }

    public class BotSocketThread extends SocketThread
    {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException
        {
            sendTextMessage("Hello, there. I am a bot. I understand following commands: date, time.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message)
        {
            ConsoleHelper.writeMessage(message);

            String[] senderNameAndMessage = message.split(":");
            String senderName = senderNameAndMessage[0].trim();
            String senderMessage = senderNameAndMessage[1].trim();

            //debug
            ConsoleHelper.writeMessage(senderMessage);

            if (message.contains(":"))
            {
                switch (senderMessage)
                {
                    case "date":
                        sendTextMessage("Message to `" + senderName + "`: " + LocalDate.now().toString());
                        break;
                    case "time":
                        sendTextMessage("Message to `" + senderName + "`: " + LocalTime.now().toString());
                }
            }
        }
    }
}
