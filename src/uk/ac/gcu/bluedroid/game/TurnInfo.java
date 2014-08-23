package uk.ac.gcu.bluedroid.game;

import uk.ac.gcu.bluedroid.util.Position;
import android.content.Context;
public class TurnInfo {

	public final static int HAS_NOT_RECRUITED = 0;
	public final static int SOLDIER_RECRUITED = 1;
	public final static int ARCHER_RECRUITED = 2;
	public final static int PALADIN_RECRUITED = 3;

	private int recruitedUnit = 0;
	
	private Position endPos = null; 	
	private Position startPos = null;  
	private Position targetPos = null;
	private Position recruitPos = null;
	private boolean hasAttacked = false;
	private boolean hasMoved = false;
	private boolean hasRecruited = false;
	
	public TurnInfo (Position endPos, Position startPos, Position targetPos){
		this.endPos = endPos;
		this.startPos = startPos;
		this.targetPos = targetPos;
	}

	public TurnInfo (){

	}

	public Position getUnitStartPos(){
		return startPos;
	}

	public Position getUnitEndPos(){
		return endPos;
	}

	public Position getUnitTargetPos(){
		return targetPos;
	}
	
	public Position getRecruitPos(){
		return recruitPos;
	}

	public boolean getHasAttacked(){
		return hasAttacked;
	}
	
	public boolean getHasMoved(){
		return hasMoved;
	}
	
	public boolean getHasRecruited(){
		return hasRecruited;
	}
	
	public int getRecruitedUnit(){
		return recruitedUnit;
	}
	
	public void setUnitStartPos(Position pos){
		this.startPos = pos;
	}

	public void setUnitEndPos(Position pos){
		this.endPos = pos;
	}

	public void setUnitTargetPos(Position pos){
		this.targetPos = pos;
	}
	
	public void setRecruitPos(Position pos){
		this.recruitPos = pos;
	}

	public void setHasAttacked (boolean hasAttacked){
		this.hasAttacked = hasAttacked;
	}
	
	public void setHasMoved (boolean hasMoved){
		this.hasMoved = hasMoved;
	}
	
	public void setHasRecruited (boolean hasRecruited){
		this.hasRecruited = hasRecruited;
	}
	
	public void setRecruitedUnit (int type){
		switch (type) {
		case HAS_NOT_RECRUITED:
			this.recruitedUnit = HAS_NOT_RECRUITED;
			break;
		case SOLDIER_RECRUITED:
			this.recruitedUnit = SOLDIER_RECRUITED;
			break;
		case ARCHER_RECRUITED:
			this.recruitedUnit = ARCHER_RECRUITED;
			break;
		case PALADIN_RECRUITED:
			this.recruitedUnit = PALADIN_RECRUITED;
			break;
		}
	}
	
	public void resetValues (){
		this.endPos = null;
		this.startPos = null;
		this.targetPos = null;
		this.recruitPos = null;
		this.hasAttacked = false;
		this.hasMoved = false;
		this.hasRecruited = false;
	}
}