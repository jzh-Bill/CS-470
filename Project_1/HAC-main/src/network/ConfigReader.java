package network;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConfigReader
{
	public String[] getIPListFromFile(String fileName)
    {
        // list that holds strings of a file
        List<String> listOfStrings = new ArrayList<String>();
       
        // load data from file
        File file = new File(fileName);
        Scanner scanner = null;
		try
		{
			scanner = new Scanner(file);
		} catch (FileNotFoundException e)
		{
			System.out.println("Cannot find " + fileName);
			e.printStackTrace();
		}
		while (scanner.hasNextLine()) 
		{
		    listOfStrings.add(scanner.nextLine());
		}
		scanner.close();
       
        // storing the data in arraylist to array
        return listOfStrings.toArray(new String[0]);
    }
	
	public String getSingleIP(String fileName)
	{
		String myIP = null;
		File file = new File(fileName);
        Scanner scanner = null;
		try
		{
			scanner = new Scanner(file);
		} catch (FileNotFoundException e)
		{
			System.out.println("Cannot find " + fileName);
			e.printStackTrace();
		}
		if (scanner.hasNextLine())
		{
			myIP = scanner.nextLine();
		}
		scanner.close();
		return myIP;
	}
	
}