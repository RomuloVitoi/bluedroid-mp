package uk.ac.gcu.bluedroid;

import java.util.ArrayList;

public class Player {
	
	private static int id_generator = 1;
	
	private int id; //id of the player
	private int gold, food, camps;
	private ArrayList<Unit> units; //infantry, cavalry, archery;
	private ArrayList<Resource> resources; //mines, crops, camps;
	
	public Player(){
		this.id = id_generator++;
	}
	
	public void turn(){} 
	
	private void updateResources(){
		for(Resource res : resources){
			if(res instanceof Mine){
				gold++;
			}else if(res instanceof Crop){
				food++;
			}else{
				camps++;
			}
		}
	}
	
	public void addUnit(Unit unit){
		units.add(unit);
	}
	
	public void addResource(Resource resource){
		resources.add(resource);
	}
	
	public void removeUnit(Unit unit){
		units.remove(unit);
	}

}
