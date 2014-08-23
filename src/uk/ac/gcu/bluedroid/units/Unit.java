package uk.ac.gcu.bluedroid.units;

import uk.ac.gcu.bluedroid.util.Position;

public class Unit {
	
	//id generator
	private static int id_generator = 1;
	
	//instance variables
	protected int owner;
	protected int id;
	protected int power;
	protected int max_life;
	protected int life;
	protected int range;
	protected int move;
	protected String prefix;
	
	protected Position pos;
	
	public Unit(int owner, Position pos){
		this.owner = owner;
		this.pos = pos;
		this.id = id_generator++;
	}
	
	/**
	 * 
	 * @param newPos
	 */
	public void walk(Position newPos){
		this.pos = newPos;
	}
	
	/**
	 * 
	 * @param enemy
	 */
	public void attack(Unit enemy){
		enemy.takeDemage(this.power);
	}
	
	/**
	 * 
	 * @param power
	 */
	public void takeDemage(int power){
		life-=power;
		if(life<=0)
			life = 0;
	}

	public int getOwner() {
		return owner;
	}

	public int getId() {
		return id;
	}

	public int getPower() {
		return power;
	}

	public int getMax_life() {
		return max_life;
	}

	public int getLife() {
		return life;
	}

	public int getRange() {
		return range;
	}

	public int getMove() {
		return move;
	}

	public String getPrefix() {
		return prefix;
	}

	public Position getPosition() {
		return pos;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public void setMax_life(int max_life) {
		this.max_life = max_life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public void setMove(int move) {
		this.move = move;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setPos(Position pos) {
		this.pos = pos;
	}
	
	
		
}
