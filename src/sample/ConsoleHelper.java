package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper
{
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message)
    {
        System.out.println(message);
    }

    public static String readString()
    {
        String readedStr = null;

        while (readedStr == null)
        {
            try
            {
                readedStr = reader.readLine();
            } catch (IOException e)
            {
                System.out.println("An error occurred while trying to enter text. Try again.");
            }
        }

        return readedStr;
    }

    public static int readInt()
    {
        Integer number = null;

        while (number == null)
        {
            try
            {
                number = Integer.parseInt(readString());
            } catch (NumberFormatException e)
            {
                System.out.println("An error occurred while trying to enter a number. Try again.");
            }
        }

        return number;
    }

}
