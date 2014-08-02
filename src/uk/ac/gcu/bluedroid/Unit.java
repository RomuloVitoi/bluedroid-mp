package uk.ac.gcu.bluedroid;


public class Unit {
	
	protected static int id_generator = 1;
	
	private int owner;
	protected int id;
	protected int power;
	protected int max_life;
	protected int life;
	protected int range;
	protected int move;
	public String prefix;
	
	protected Position pos;
	
	public Unit(int owner, Position pos){
		this.owner = owner;
		this.pos = pos;
		this.id = id_generator++;
	}
	
	public int getOwner() {
		return owner;
	}
	
	public void setPosition(Position pos) {
		this.pos = pos;
	}
	
	public Position getPosition() {
		return pos;
	}
	
	public void walk(Position newPos){
		this.pos = newPos;
	}
	
	public void conquer(Player player){
		// TODO player.addUnit(this);
	}
	
	public void attack(Unit enemy){
		enemy.takeDemage(this.power);
	}
	
	public void takeDemage(int power){
		life-=power;
		if(life<=0)
			life = 0;
	}
		
}
