package uk.ac.gcu.bluedroid;

import android.media.Image;

public class Unit {
	
	protected static int id_generator = 1;
	
	protected int id;
	protected int power;
	protected int life;
	protected int range;
	protected int move;
	protected Image image;
	
	protected Position pos;
	
	public Unit(Position pos){
		this.pos = pos;
		this.id = id_generator++;
	}
	
	public void walk(Position newPos){
		this.pos = newPos;
	}
	
	public void conquer(Player player){
		player.addUnit(this);
	}
	
	public void attack(Unit enemy){
		enemy.takeDemage(this.power);
	}
	
	public void takeDemage(int power){
		life-=power;
		if(life<=0){
			die();
		}
	}
	
	protected void die(){
		//remover imageview da undiade do mapa
		//remover unidade da lista de undiades do jogador
	}
	
}
