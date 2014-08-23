package uk.ac.gcu.bluedroid.resources;

import uk.ac.gcu.bluedroid.util.Position;

public class Crop extends Resource {
	
	public Crop(int owner, Position pos){
		super(owner, pos);
	}
	
	public Crop(Position pos){
		super(0, pos);
	}

}
