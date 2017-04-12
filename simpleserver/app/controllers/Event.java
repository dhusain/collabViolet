package controllers;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Event {

    /*
    The event object represents any addition to a UML diagram.
    This version has the potential to be expanded for use for multiple diagram types
    but is currently only implemented for Class diagrams
    The current design is for the handling class to read input from a scanner
    and use setters to assign values to fields as different diagrams have different
    fields that are used
     */

    //URL is used to create a representation of an Event class that can be passed as String object
	//to be used in a URL argument with all instance variables being recreated
	private String URL;

	//ID is used for identifying the event in an array
	private int ID;
	
	// Diagram types
	private final int CLASS = 1, OBJECT = 2, USECASE = 3, STATE = 4, ACTIVITY = 5, SEQUENCE = 6;
	private int diagramType;

	// Event instance variables
	private int x, y, xTo, yTo;
	private String name, attributes, methods, content;
	private String startLabel, middleLabel, endLabel;
	private final int AUTO = 1, STRAIGHT = 2, FREE = 3, HV = 4, VH = 5, HVH = 6, VHV = 7;
	private int bendStyle;

	public Event() {
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setYTo(int xTo){this.xTo = xTo;}

    public void setXTo(int yTo){this.yTo = yTo;}

    public void setAttributes(String attributes){this.attributes = attributes;}

    public void setMethods(String methods){this.methods = methods;}

    public void setContent(String content){this.content = content;}

    public void setStartLabel(String startLabel){this.startLabel = startLabel;}

    public void setMiddleLabel(String middleLabel){this.middleLabel = middleLabel;}

    public void setEndLabel(String endLabel){this.endLabel = endLabel;}

    public void setBendStyle(int bendStyle){this.bendStyle = bendStyle;}

    public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public String getName()
	{
		return name;
	}

    public int getXTo(){return xTo;}

    public int getYTo(){return yTo;}

    public String getAttributes() {return attributes;}

    public String getMethods(){return methods;}

    public String getContent(){return content;}

    public String getStartLabel(){return startLabel;}

    public String getMiddleLabel(){return middleLabel;}

    public String getEndLabel(){return endLabel;}

    public int getBendStyle(){return bendStyle;}

	public String generateEventURL(){
		URL = x+"__"+y+"__"+xTo+"__"+yTo+"__"+name+"__"+attributes+"__"+methods+"__"+content+"__"+startLabel+"__"+middleLabel+"__"+endLabel+"__"+bendStyle;
		return URL;
	}

	public void readFromID(String urlID)
	{
		Scanner s = new Scanner(urlID).useDelimiter("\\s*__\\s*");
		x = Integer.parseInt(s.next());
		y = Integer.parseInt(s.next());
		xTo = Integer.parseInt(s.next());
		yTo = Integer.parseInt(s.next());
		name = s.next();
		attributes = s.next();
		methods = s.next();
		content = s.next();
		startLabel = s.next();
		middleLabel = s.next();
		endLabel = s.next();
		bendStyle = Integer.parseInt(s.next());
	}


	public void generateID()
	{
		ID =ThreadLocalRandom.current().nextInt(00000000, 99999999);
	}

	public int getID()
	{
		return ID;
	}


}
