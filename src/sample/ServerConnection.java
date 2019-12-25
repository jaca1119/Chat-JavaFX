package sample;

public class ServerConnection
{
    private static final ServerConnection serverConnection = new ServerConnection();

    private String ip;
    private String userName;

    private ServerConnection()
    {

    }

    public static ServerConnection getInstance()
    {
        return serverConnection;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }
}
