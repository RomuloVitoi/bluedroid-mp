package uk.ac.gcu.bluedroid;

public class Map {
    int[][] selectedMap;
    
	private Unit[][] units;
	
	public Map() {
		selectedMap = Maps.map1;
		
		units = new Unit[selectedMap.length][selectedMap[0].length];
		
		int player = 1;
		for(int i = 0; i < getX(); i++)
			for(int j = 0; j < getY(); j++)
				if(selectedMap[j][i] == 5) {
					addUnit(new Paladin(player, new Position(i, j+1)));
					addUnit(new Archer(player, new Position(i-1, j)));
					addUnit(new Soldier(player, new Position(i+1, j)));
					player++;
				}
	}
	
	public int getX() {
		return selectedMap[0].length;
	}
	
	public int getY() {
		return selectedMap.length;
	}
	
	public void addUnit(Unit u) {
		units[u.getPosition().y][u.getPosition().x] = u;
	}
	
	public Unit getUnit(int x, int y) {
		return units[y][x];
	}
	
	public void moveUnit(Unit unit, int x, int y) {
		units[y][x] = unit;
		units[unit.getPosition().y][unit.getPosition().x] = null;
		units[y][x].walk(new Position(x, y));
	}
	
	public void removeUnit(Unit unit) {
		units[unit.getPosition().y][unit.getPosition().x] = null;
	}
	
	public boolean walkable(int x, int y) {
		if(units[y][x] != null)
			return false;
		
		switch(getSquareType(x, y)) {
		case 0:
		case 2:
		case 3:
		case 4:
		case 5:
			return true;
		default:
			return false;
		}
	}
	
	public boolean canWalkTo(int x0, int y0, int x1, int y1, int range) {
		boolean able = true;
		int disX = Math.abs(x0 - x1);
		int disY = Math.abs(y0 - y1);

		if ((disX + disY) > range) return false;
		if (disX > 0 && disY > 0) return false;
		if (disX > 0)
			for (int i = 1; i <= disX; i++) {
				if (!walkable((x0 + i), y0)) {
					able = false;
					break;
				}
			}
		else {
			for (int i = 1; i <= disY; i++) {
				if (!walkable(x0, (y0 + i))) {
					able = false;
					break;
				}
			}
		}
		return able;
	}
	
	public int getSquareType (int x, int y){
		return selectedMap[y][x];
	}
}
