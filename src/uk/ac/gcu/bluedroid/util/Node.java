package uk.ac.gcu.bluedroid.util;

public class Node {
	int x, y;
	
	public Node(int i, int j) {
		x = i;
		y = j;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof Node) {
			Node n = (Node) obj;
			return (x == n.x && y == n.y);
		}
		else return false;
	}
}