package ctdownloader.model;

import java.util.ArrayList;

public class TaskContextModel {
	
	private ArrayList<InteractionEventModel> interactions = new ArrayList<>();
	
	public TaskContextModel(ArrayList<InteractionEventModel> events){
		interactions.addAll(events);
	}

	public ArrayList<InteractionEventModel> getInteractionEvents(){
		return interactions;
	}
	
	
}
