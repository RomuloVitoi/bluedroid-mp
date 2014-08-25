package uk.ac.gcu.bluedroid.util;

public class Position {
	private int x;
	private int y;
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Position) {
			Position p = (Position) o;
			return p.y == this.y && p.x == this.x;
		} else
			return false;
	}
	
}
