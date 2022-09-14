// Authors: Zhihao Jin and Donnie Beck
// version: 1.0
// Date modified: 02/27/2022

package network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;


public class Message implements Serializable 
{
	// The serialVersionUID is a identifier that is used to 
	// serialize/deserialize an object of a Serializable class.
	// value could be 1,2,3,4L
	private static final long serialVersionUID = 1L;
	
	// Message properties.
	private double version = 1.0;
	private boolean isP2P;
    private boolean isSimple;
    private String text;
    private String senderIP;
    private Hashtable <String, IPEntry> nodeList;
    private static Set<String> setOfNodeIPs; 
    
    // The Message constructor.
    public Message(boolean isP2P, boolean isSimple, String text, String senderIP, Hashtable<String, IPEntry> nodeList) 
    {
    	
    	this.isP2P = isP2P;  //True: P2P, False: Client-Server
    	this.isSimple = isSimple; 
        this.text = text;  // The information this variable contains
        this.senderIP = senderIP;
        this.nodeList = nodeList; // The IP or IP lists the message may contain.
        if(nodeList != null)
        {
        	this.setOfNodeIPs = nodeList.keySet();
        }
    }
    
    /**
     * get of text
     * @return the text of the message stored in the class
     */
    public String getText()
    {
    	return this.text;
    }
    
    /**
     * get of version
     * @return the version of the message class
     */
    public double getVersion() 
    {
      return this.version;
    }

    /**
     * get of IP list
     * @return the IPlist in the message
     */
    public Hashtable<String, IPEntry> getnodeList() 
    {
      return this.nodeList;
    }

    /**
     * get of functioning mode of a message
     * @return the mode of the message
     */
    public boolean getP2P() 
    {
      return this.isP2P;
    }

    /**
     * get the usage of a this message
     * @return the boolean value of Heartbeat of the message
     */
    public boolean getisSimple() 
    {
      return this.isSimple;
    }

    /**
     * set text message contained in a message
     * @param textt the text desired to be stored in the text field
     */
    public void setText(String text) 
    {
      this.text = text;
    }

    /**
     * set the meaning of the message
     * @param meaning the meaning of the message
     */
    public void setIsSimple(boolean isSimple) 
    {
      this.isSimple = isSimple;
    }
    
    
    /**
     * Serializes all data of message into bytes in order to be packed.
	 * No parameter, use all data inside of message object.
     * @return the bytes number of this object.
     */
    private byte[] serializedMessage() 
    {
		// Create a new message object to be serialized.
	    Message serialMessageObj = new Message(this.isP2P, this.isSimple, this.text, this.senderIP, this.nodeList);
        
        // Serialize the message to be fitted into a buffer
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ObjectOutput outBuffer;
        try 
        {
	        // write the message objects in the buffer
	    	outBuffer = new ObjectOutputStream(outStream);
	    	outBuffer.writeObject(serialMessageObj);
	    	outBuffer.close();
        } catch (IOException io) {
            io.printStackTrace();
            System.exit(-1);
        }
        byte[] requestbuffer = outStream.toByteArray();
        return requestbuffer;
      }
    
    /**
     * Deserializes the incoming packet from the outside.
     * @param incomingPacket the packet to extract data from
     * @return to the taget message with the deserialized information.
     */
    public Message deserializer(DatagramPacket incomingPacket) 
    {
        ObjectInputStream inputStream;
        Message responsemessage = null;
    	try 
    	{
	        byte[] buffer = incomingPacket.getData();
	        inputStream = new ObjectInputStream(new ByteArrayInputStream(buffer));
	        responsemessage = (Message) inputStream.readObject();
	        inputStream.close();
        } 
    	catch (Exception e) 
    	{
    		e.printStackTrace();
        }
        return responsemessage;
     }
    
    
    /**
     * The function is used to packet the whole message with additional information.
     * @param hostaddress the IP address of the receiver
     * @param port the port number used to transmit the message
     * @return the completed network package originated from the input message
     */
    public DatagramPacket createPacket(InetAddress hostIP, int port) 
    {
      // we first serialize with all the information we have in this object into bytes.
      byte[] requestbuffer = serializedMessage();
      // And we start to pack them with host IP address and port number
      DatagramPacket outPacket = new DatagramPacket(requestbuffer, requestbuffer.length, hostIP, port);
      return outPacket;
    }
    
    public void updateTimestamp()
    {
    	for (String ip : setOfNodeIPs)
		{
    		nodeList.get(ip).setTimeStampNow();
		}
    }

	public String getSenderIP()
	{
		return senderIP;
	}

	public void setSenderIP(String senderIP)
	{
		this.senderIP = senderIP;
	}
}
