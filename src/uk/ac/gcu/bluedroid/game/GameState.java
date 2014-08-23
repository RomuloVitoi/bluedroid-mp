package uk.ac.gcu.bluedroid.game;

import android.content.Context;

public class GameState {
	private Player[] players;	
	private Map map;
	private int turn;
	
	public GameState(Context context) {
		turn = 0;
		players = new Player[2];
		players[0] = new Player();
		players[1] = new Player();
		
		map = new Map(context);
	}
	
	public void updateTurn(){
		turn++;
	}

	public Player[] getPlayers() {
		return players;
	}

	public Map getMap() {
		return map;
	}

	public int getTurn() {
		return turn;
	}

	public void setPlayers(Player[] players) {
		this.players = players;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}
	
	
}
