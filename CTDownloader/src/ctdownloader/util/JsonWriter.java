package ctdownloader.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ctdownloader.model.ChangeTaskModel;
import ctdownloader.model.CommentModel;
import ctdownloader.model.InteractionEventModel;
import ctdownloader.model.TaskContextModel;

public class JsonWriter {
	
	
	public void writeContextToJsonFile(TaskContextModel context, String path){
		
		ArrayList<InteractionEventModel> events = context.getInteractionEvents();
		JSONArray jsonArray = new JSONArray();
		for (InteractionEventModel event : events) {
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.put("delta", event.getDelta());
			jsonObject.put("startDate", event.getStartDate().toString());
			jsonObject.put("endDate", event.getEndDate().toString());
			jsonObject.put("interest", event.getInterest());
			jsonObject.put("eventKind", event.getEventKind().name());
			jsonObject.put("navigation", event.getNavigation());
			jsonObject.put("originId", event.getOriginId());
			jsonObject.put("structureHandle", event.getStructureHandle());
			jsonObject.put("structureKind", event.getStructureKind());

			jsonArray.add(jsonObject);
			
		}
		

		JSONObject jsonTaskContextObject = new JSONObject();
		jsonTaskContextObject.put("submitter", context.getSubmitter());
		jsonTaskContextObject.put("interactionhistory", jsonArray);
		
		try {

			FileWriter jsonFileWriter = new FileWriter(path);
			jsonFileWriter.write(jsonTaskContextObject.toJSONString());
			jsonFileWriter.flush();
			jsonFileWriter.close();

			System.out.print(jsonTaskContextObject);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeChangeTaskModelToJsonFile(ChangeTaskModel task){
		
		JSONObject jsonTaskObject = new JSONObject();
		jsonTaskObject.put("summary", task.getSummary());
		jsonTaskObject.put("description", task.getDescription());
		jsonTaskObject.put("id", task.getId());
		jsonTaskObject.put("product", task.getProduct());
		jsonTaskObject.put("severity", task.getSeverity());
		jsonTaskObject.put("priority", task.getPriority());
		jsonTaskObject.put("changed", task.getChanged().toString());
		jsonTaskObject.put("hasContext", task.hasTaskContext());
		jsonTaskObject.put("folderPath", task.getFolderPath());
		
		
		
		JSONArray jsonCommentArray = new JSONArray();
		for(CommentModel comment : task.getComments()){
			JSONObject jsonCommentObject = new JSONObject();
			jsonCommentObject.put("content", comment.getContent());
			jsonCommentObject.put("author", comment.getAuthor());
			jsonCommentObject.put("date", comment.getDate());
			
			jsonCommentArray.add(jsonCommentObject);
		}
		jsonTaskObject.put("comments", jsonCommentArray);
		
		//path, submitter
		JSONArray jsonContextMapArray = new JSONArray();
		for(Entry<String,String> contextEntry : task.getContextMap().entrySet()){
			JSONObject jsonContextObject = new JSONObject();
			jsonContextObject.put("path", contextEntry.getKey());
			jsonContextObject.put("submitter", contextEntry.getValue());
			
			jsonContextMapArray.add(jsonContextObject);
		}
		
		jsonTaskObject.put("contextMap", jsonContextMapArray);
		
		try {

			String taskFileName = task.getFolderPath()+"\\task.json";
			FileWriter jsonFileWriter = new FileWriter(taskFileName);
			jsonFileWriter.write(jsonTaskObject.toJSONString());
			jsonFileWriter.flush();
			jsonFileWriter.close();

			System.out.print(jsonTaskObject);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}
