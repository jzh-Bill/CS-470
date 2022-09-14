package network;

import java.io.Serializable;
import java.time.LocalDateTime;

public class IPEntry implements Serializable
{
	final int TTL = 30;
	Boolean isAlive;
	LocalDateTime timeStamp;
	int timeToLive;
	

	public int getTimeToLive()
	{
		return timeToLive;
	}

	public void setTimeToLive(int timeToLive)
	{
		this.timeToLive = timeToLive;
	}

	public Boolean getIsAlive() 
	{
		return this.isAlive;
	}

	public void setIsAlive(Boolean isAlive) 
	{
		this.isAlive = isAlive;
	}

	public LocalDateTime getTimeStamp() 
	{
		return this.timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) 
	{
		this.timeStamp = timeStamp;
	}
	
	public void setTimeStampNow()
	{
		this.timeStamp = LocalDateTime.now();
	}

	public IPEntry(Boolean status)
	{
		this.isAlive = status;
		this.timeStamp = LocalDateTime.now();
		this.timeToLive = TTL;
	}
	
	public String getStatusString()
	{
		if(isAlive)
		{
			return " is alive!";
		}
		return " is dead.";
	}
	
	
}
