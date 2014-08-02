package uk.ac.gcu.bluedroid;

public class Player {
	private static int id_generator = 1;
	
	private int id; //id of the player
	private int gold, food, camps;
	
	public Player(){
		this.id = id_generator++;
	}
	
	public void turn(){} 
	
	public int getId() {
		return id;
	}
}
