package uk.ac.gcu.bluedroid.units;

import uk.ac.gcu.bluedroid.util.Position;

public class Paladin extends Unit {

	public Paladin(int owner, Position pos){
		super(owner, pos);
		this.max_life = 12;
		this.power = 3;
		this.range = 1;
		this.move = 7;
		this.prefix = "c"; //flag
		life = max_life;
	}

}
