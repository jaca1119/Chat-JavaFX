package sample.Message;

import java.io.Serializable;

public class Message implements Serializable
{
    private final MessageType messageType;
    private final String data;
    private final byte[] image;
    public Message(MessageType messageType)
    {
        this.messageType = messageType;
        data = null;
        image = new byte[0];
    }

    public Message(MessageType messageType, String data)
    {
        this.messageType = messageType;
        this.data = data;
        this.image = new byte[0];
    }

    public Message(MessageType messageType, final byte[] image)
    {
        this.messageType = messageType;
        this.image = image;
        this.data = null;
    }

    public MessageType getMessageType()
    {
        return messageType;
    }

    public String getData()
    {
        return data;
    }

    public byte[] getImage()
    {
        return image;
    }
}
