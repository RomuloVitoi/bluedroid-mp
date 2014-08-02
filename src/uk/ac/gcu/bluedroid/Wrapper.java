package uk.ac.gcu.bluedroid;

import java.io.Serializable;

public class Wrapper implements Serializable {
	public static final int ACK = 0;
	public static final int MESSAGE = 1;
	
	public int type;
	public Long time;
	public String message;
	public String hash;
	public final String _class;
	
	public Wrapper(String _class, int type) {
		this.time = System.currentTimeMillis();
		this.type = type;
		this._class = _class;
	}
	
	public Wrapper(String _class, int type, String message) {
		this(_class, type);
		this.message = message;
		this.hash = Util.hash(message);
	}
}
