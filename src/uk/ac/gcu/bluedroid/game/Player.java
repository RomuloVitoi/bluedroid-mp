package uk.ac.gcu.bluedroid.game;

public class Player {
	
	//constants
	public static final int GOLD = 0;
	public static final int FOOD = 1;
	public static final int CAMPS = 2;
	public static final int MINES = 3;
	public static final int CROPS = 4;
	
	
	//id generator
	private static int id_generator = 1;
	
	//instance variables
	private int id; //id of the player
	private int gold, food, camps; //0 - gold, 1 - food, 2 - camps
	private int crops, mines; //3 - crops, 4 - mines;
	
	public Player(){
		this.id = id_generator++;
	}
	
	public void updateResource(int resource, int quant){
		switch (resource) {
		case GOLD:
			gold+=quant;
			break;
		case FOOD:
			food+=quant;
			break;
		case CAMPS:
			camps+=quant;
			break;
		case CROPS:
			crops+=quant;
			break;
		case MINES:
			mines+=quant;
			break;
		default:
			break;
		}
	}
	
	public int getId() {
		return id;
	}

	public int getGold() {
		return gold;
	}

	public int getFood() {
		return food;
	}
	
	public int getCamps() {
		return camps;
	}

	public int getCrops() {
		return crops;
	}

	public int getMines() {
		return mines;
	}

	
}
