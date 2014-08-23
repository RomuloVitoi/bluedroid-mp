package uk.ac.gcu.bluedroid.units;

import uk.ac.gcu.bluedroid.util.Position;

public class Archer extends Unit {

	public Archer(int owner, Position pos){
		super(owner, pos);
		this.max_life = 6;
		this.power = 2;
		this.range = 3;
		this.move = 4;
		this.prefix = "a"; //flag
		life = max_life;
	}

}
