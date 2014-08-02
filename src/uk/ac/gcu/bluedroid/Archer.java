package uk.ac.gcu.bluedroid;

public class Archer extends Unit {

	public Archer(int owner, Position pos){
		super(owner, pos);
		this.id = 0; //todo
		this.max_life = 3; //todo
		this.power = 1; // todo
		this.range = 4;
		this.move = 2;
		
		this.prefix = "a";
		
		life = max_life;
	}

}
