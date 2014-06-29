package ctdownloader.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

import ctdownloader.model.InteractionEventModel.EventKind;
import ctdownloader.model.codeelements.MethodModel;

public class TaskContextModel {

	private ArrayList<InteractionEventModel> interactions = new ArrayList<>();
	
	private String submitter;

	public TaskContextModel(ArrayList<InteractionEventModel> events) {
		interactions.addAll(events);
	}

	public ArrayList<InteractionEventModel> getInteractionEvents() {
		return interactions;
	}

	public ArrayList<MethodModel> getMethods() {
		ArrayList<MethodModel> methods = new ArrayList<>();
		for (InteractionEventModel event : interactions) {
			if (event.getEventKind() != EventKind.PROPAGATION) {
				if (event.getStructureHandle().contains("~")) {
					MethodModel m = new MethodModel(event.getStructureHandle());
					methods.add(m);

				}
			}

		}

		return methods;
	}
	

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public ArrayList<InteractionEventModel> getSortedInteractions( ArrayList<InteractionEventModel> events) throws NoTaskContextAvailableException {
		Collections.sort(events, new DateComparator());
		return events;
	}
	
	public ArrayList<InteractionEventModel> getAllButPropagations(ArrayList<InteractionEventModel> events){
		ArrayList<InteractionEventModel> noPropagations = new ArrayList<>();
		
		for (InteractionEventModel interactionEventModel : events) {
			if(interactionEventModel.getEventKind() != EventKind.PROPAGATION){
				noPropagations.add(interactionEventModel);
			}
		}
		
		return noPropagations;
	}
	
	public class DateComparator implements Comparator<InteractionEventModel> {
		@Override
		public int compare(InteractionEventModel o1, InteractionEventModel o2) {
			return o1.getStartDate().compareTo(o2.getStartDate());
		}
	}
}
