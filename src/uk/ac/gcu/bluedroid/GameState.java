package uk.ac.gcu.bluedroid;

public class GameState {
	public Player[] player;	
	public Map map;
	
	public GameState() {
		player = new Player[2];
		player[0] = new Player();
		player[1] = new Player();
		
		map = new Map();
	}
}
