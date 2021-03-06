package sample.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClientGuiModel
{
    private final Set<String> allUserNames = new HashSet<>();

    private String newMessage;
    private byte[] newImage;

    public void addUser(String userName)
    {
        allUserNames.add(userName);
    }

    public void deleteUser(String userName)
    {
        allUserNames.remove(userName);
    }

    public Set<String> getAllUserNames()
    {
        return Collections.unmodifiableSet(allUserNames);
    }

    public String getNewMessage()
    {
        return newMessage;
    }

    public void setNewMessage(String newMessage)
    {
        this.newMessage = newMessage;
    }

    public byte[] getNewImage()
    {
        return newImage;
    }

    public void setNewImage(byte[] newImage)
    {
        this.newImage = newImage;
    }
}
