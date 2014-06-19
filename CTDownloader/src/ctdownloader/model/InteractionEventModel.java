package ctdownloader.model;

import java.util.Date;

public class InteractionEventModel {
	
	private String delta;
	private Date startDate;
	private Date endDate;
	private float interest;
	private EventKind eventKind;
	private String navigation;
	private String originId;
	private String structureHandle;
	private String structureKind;
	
	public InteractionEventModel(String delta, Date start, Date end, float interest, 
			EventKind kind, String navi, String origin, String handle, String structureKind){
		this.delta = delta;
		this.startDate = start;
		this.endDate = end;
		this.interest = interest;
		this.eventKind = kind;
		this.navigation = navi;
		this.originId = origin;
		this.structureHandle = handle;
		this.structureKind = structureKind;
	}
	
	
	
	
	
	public String getDelta() {
		return delta;
	}





	public void setDelta(String delta) {
		this.delta = delta;
	}





	public Date getStartDate() {
		return startDate;
	}





	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}





	public Date getEndDate() {
		return endDate;
	}





	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}





	public double getInterest() {
		return interest;
	}





	public void setInterest(float interest) {
		this.interest = interest;
	}

	public EventKind getEventKind() {
		return eventKind;
	}





	public void setEventKind(EventKind eventKind) {
		this.eventKind = eventKind;
	}





	public String getNavigation() {
		return navigation;
	}





	public void setNavigation(String navigation) {
		this.navigation = navigation;
	}





	public String getOriginId() {
		return originId;
	}





	public void setOriginId(String originId) {
		this.originId = originId;
	}





	public String getStructureHandle() {
		return structureHandle;
	}





	public void setStructureHandle(String structureHandle) {
		this.structureHandle = structureHandle;
	}





	public String getStructureKind() {
		return structureKind;
	}





	public void setStructureKind(String structureKind) {
		this.structureKind = structureKind;
	}





	public enum EventKind {
		SELECTION, EDIT, COMMAND, PREFERENCE, PREDICTION, PROPAGATION, MANIPULATION, ATTENTION, UNKOWN;
		}

}
