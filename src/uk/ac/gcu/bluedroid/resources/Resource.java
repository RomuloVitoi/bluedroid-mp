package uk.ac.gcu.bluedroid.resources;

import uk.ac.gcu.bluedroid.util.Position;


public class Resource {
	//id generator
	protected static int id_generator = 1;
	
	//instance variables
	protected int id;
	private int owner;
	protected Position pos;
	
	public Resource(Position pos){
		this.owner = 0;
		this.pos = pos;
		this.id = id_generator++;
	}
	
	public Resource(int owner, Position pos){
		this.owner = owner;
		this.pos = pos;
		this.id = id_generator++;
	}
	
	public Position getPosition(){
		return pos;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}
	
	

}
