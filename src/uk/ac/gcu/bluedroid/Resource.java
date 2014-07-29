package uk.ac.gcu.bluedroid;

import android.media.Image;

public class Resource {
	
	protected static int id_generator = 1;
	
	protected int id;
	protected int status;
	
	protected Position pos;
	protected Image image;
	
	public Resource(Position pos){
		this.pos = pos;
		this.id = id_generator++;
	}

	public void conquer(Player player){
		player.addResource(this);
	}

}
