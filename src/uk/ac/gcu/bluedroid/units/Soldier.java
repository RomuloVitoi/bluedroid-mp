package uk.ac.gcu.bluedroid.units;

import uk.ac.gcu.bluedroid.util.Position;

public class Soldier extends Unit {

	public Soldier(int owner, Position pos){
		super(owner, pos);
		this.max_life = 9;
		this.power = 2;
		this.range = 1;
		this.move = 5;	
		this.prefix = "i"; //flag
		life = max_life;
	}

}
