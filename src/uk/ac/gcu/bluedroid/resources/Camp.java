package uk.ac.gcu.bluedroid.resources;

import uk.ac.gcu.bluedroid.game.Player;
import uk.ac.gcu.bluedroid.util.Position;

public class Camp extends Resource {

	// constants
	public static final int PALADIN_COST_GOLD = 6;
	public static final int ARCHER_COST_GOLD = 1;
	public static final int SOLDIER_COST_GOLD = 2;
	public static final int PALADIN_COST_FOOD = 12;
	public static final int ARCHER_COST_FOOD = 8;
	public static final int SOLDIER_COST_FOOD = 6;
	public static final int ARCHER = 1;
	public static final int SOLDIER = 2;
	public static final int PALADIN = 3;

	private boolean working;

	public Camp(int owner, Position pos) {
		super(owner, pos);
		working = true;
	}

	public Camp(Position pos) {
		super(0, pos);
		working = true;
	}
	
	public boolean hasEnoughResources(int type, Player player){
		switch (type) {
		case Camp.ARCHER:
			return player.getGold() >= Camp.ARCHER_COST_GOLD && player.getFood() >= Camp.ARCHER_COST_FOOD;
		case Camp.SOLDIER:
			return player.getGold() >= Camp.SOLDIER_COST_GOLD && player.getFood() >= Camp.SOLDIER_COST_FOOD;
		case Camp.PALADIN:
			return player.getGold() >= Camp.PALADIN_COST_GOLD && player.getFood() >= Camp.PALADIN_COST_FOOD;
		default:
			return false;
		}
		
	}

	public boolean isWorking() {
		return working;
	}

	public void setWorking(boolean bol) {
		this.working = bol;
	}

}
