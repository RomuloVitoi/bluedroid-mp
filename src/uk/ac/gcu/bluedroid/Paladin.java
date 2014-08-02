package uk.ac.gcu.bluedroid;

public class Paladin extends Unit {

	public Paladin(int owner, Position pos){
		super(owner, pos);
		this.id = 0; //todo
		this.max_life = 10; //todo
		this.power = 2; // todo
		this.range = 1; //todo
		this.move = 5;
		
		this.prefix = "c";
		
		life = max_life;
	}

}
