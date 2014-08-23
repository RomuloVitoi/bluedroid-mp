package uk.ac.gcu.bluedroid.resources;

import uk.ac.gcu.bluedroid.util.Position;

public class Mine extends Resource {
	
	public Mine(int owner, Position pos){
		super(owner, pos);
	}
	
	public Mine(Position pos){
		super(0, pos);
	}

}
