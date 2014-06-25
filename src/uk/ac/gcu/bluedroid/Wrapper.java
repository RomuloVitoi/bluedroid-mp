package uk.ac.gcu.bluedroid;

import java.io.Serializable;

public class Wrapper implements Serializable {
	public static final int ACK = 0;
	public static final int MESSAGE = 1;
	
	public int type;
	public Long time;
	public String message;
	
	public Wrapper(int type) {
		this.time = System.currentTimeMillis();
		this.type = type;
	}
	
	public Wrapper(int type, String message) {
		this(type);
		this.message = message;
	}
}
