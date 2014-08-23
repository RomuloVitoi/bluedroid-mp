package uk.ac.gcu.bluedroid.game;

import uk.ac.gcu.bluedroid.resources.Camp;
import uk.ac.gcu.bluedroid.resources.Crop;
import uk.ac.gcu.bluedroid.resources.Mine;
import uk.ac.gcu.bluedroid.resources.Resource;
import uk.ac.gcu.bluedroid.units.Archer;
import uk.ac.gcu.bluedroid.units.Paladin;
import uk.ac.gcu.bluedroid.units.Soldier;
import uk.ac.gcu.bluedroid.units.Unit;
import uk.ac.gcu.bluedroid.util.Position;
import android.content.Context;
import android.widget.Toast;

public class Map {
	private int[][] selectedMap;

	private Context context;

	private Unit[][] units;
	private Resource[][] resources;

	public Map(Context context) {
		this.context = context;

		selectedMap = Maps.map1;

		units = new Unit[selectedMap.length][selectedMap[0].length];
		resources = new Resource[selectedMap.length][selectedMap[0].length];

		int player = 1;
		for (int i = 0; i < getX(); i++)
			for (int j = 0; j < getY(); j++) {
				int selected = selectedMap[j][i];
				switch (selected) {
				case 5: // units startup
					addUnit(new Paladin(player, new Position(i, j + 1)));
					addUnit(new Archer(player, new Position(i - 1, j)));
					addUnit(new Soldier(player, new Position(i + 1, j)));
					player++;
					break;
				case Player.CAMPS: // camps startup
					addResource(new Camp(new Position(i, j)));
					break;
				case Player.MINES: // mines startup
					addResource(new Mine(new Position(i, j)));
					break;
				case Player.CROPS: // crops startup
					addResource(new Crop(new Position(i, j)));
					break;
				default:
					break;
				}
			}
	}

	/**
	 * Add units in the map
	 * 
	 * @param u
	 *            unit to be added
	 */
	public void addUnit(Unit u) {
		units[u.getPosition().getY()][u.getPosition().getX()
		                              ] = u;
	}

	/**
	 * 
	 * @param unit
	 */
	public void removeUnit(Unit unit) {
		units[unit.getPosition().getY()][unit.getPosition().getX()] = null;
	}

	/**
	 * Add resources in the map
	 * 
	 * @param r
	 *            resource to be added
	 */
	public void addResource(Resource r) {
		resources[r.getPosition().getY()][r.getPosition().getX()] = r;
	}

	/**
	 * 
	 * @param r
	 * @param owner
	 * @param type
	 */
	public void conquerResource(Resource r, int owner, String type) {
		r.setOwner(owner);
		resources[r.getPosition().getY()][r.getPosition().getX()] = r; // update resource
		Toast.makeText(context, "Player " + owner + " conquered a " + type,
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * 
	 * @param unit
	 * @param x
	 * @param y
	 */
	public void moveUnit(Unit unit, int x, int y) {
		units[y][x] = unit;
		units[unit.getPosition().getY()][unit.getPosition().getX()] = null;
		units[y][x].walk(new Position(x, y));

		// conquer functions
		int type = getSquareType(x, y);
		if (type == Player.MINES || type == Player.CROPS
				|| type == Player.CAMPS) { // if type is a mine, camp or crop,
											// conquer it
			switch (type) {
			case Player.CAMPS: // if a camp
				conquerResource(new Camp(unit.getOwner(), new Position(x, y)),
						unit.getOwner(), "camp");
				break;
			case Player.MINES: // if a mine
				conquerResource(new Mine(unit.getOwner(), new Position(x, y)),
						unit.getOwner(), "mine");
				break;
			case Player.CROPS: // if a crop
				conquerResource(new Crop(unit.getOwner(), new Position(x, y)),
						unit.getOwner(), "crop");
				break;
			}
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int walkable(int x, int y) {
		if (units[y][x] != null)
			return 0;

		switch (getSquareType(x, y)) {
		case 2:
			return 2;
		case 3:
			return 3;
		case 4:
			return 4;
		case 0:
		case 5:
			return 1;
		default:
			return 0;
		}
	}

	/**
	 * THIS FUNCTION IS NOT WORKING
	 * 
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 * @param range
	 * @return
	 */
	public boolean canWalkTo(int x0, int y0, int x1, int y1, int range) {
		boolean able = true;
		int disX = Math.abs(x0 - x1);
		int disY = Math.abs(y0 - y1);

		if ((disX + disY) > range)
			return false;
		if (disX > 0 && disY > 0)
			return false;
		if (disX > 0)
			for (int i = 1; i <= disX; i++) {
				if (walkable((x0 + i), y0) == 0) {
					able = false;
					break;
				}
			}
		else {
			for (int i = 1; i <= disY; i++) {
				if (walkable(x0, (y0 + i)) == 0) {
					able = false;
					break;
				}
			}
		}
		return able;
	}

	/**
	 * Update selected player resources (gold and food)
	 * 
	 * @param player
	 *            player to have the resources updated
	 */
	public void updateResources(Player player) {
		for (int i = 0; i < getX(); i++) {
			for (int j = 0; j < getY(); j++) {
				int selected = selectedMap[j][i]; // selects a map unit
				if (selected == Player.CROPS || selected == Player.MINES) {
					Resource res = getResource(i, j);
					if (getResource(i, j) instanceof Mine
							&& getResource(i, j).getOwner() == player.getId()) {
						player.updateResource(0, 1);
					} else if (res instanceof Crop
							&& res.getOwner() == player.getId()) {
						player.updateResource(1, 1);
					}
				}
			}
		}

	}

	public int getSquareType(int x, int y) {
		return selectedMap[y][x];
	}

	public int getX() {
		return selectedMap[0].length;
	}

	public int getY() {
		return selectedMap.length;
	}

	public Unit getUnit(int x, int y) {
		return units[y][x];
	}

	public Unit getUnit(Position pos) {
		return units[pos.getY()][pos.getX()];
	}
	
	public Resource getResource(int x, int y) {
		return resources[y][x];
	}

}
