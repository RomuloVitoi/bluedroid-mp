package uk.ac.gcu.bluedroid;

public class Soldier extends Unit {

	public Soldier(int owner, Position pos){
		super(owner, pos);
		this.id = 0; //todo
		this.max_life = 6; //todo
		this.power = 1; // todo
		this.range = 1;
		this.move = 3;
		
		this.prefix = "i";
		
		life = max_life;
	}

}
