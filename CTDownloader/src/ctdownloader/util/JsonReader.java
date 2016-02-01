package ctdownloader.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ctdownloader.model.ChangeTaskModel;
import ctdownloader.model.CommentModel;
import ctdownloader.model.InteractionEventModel;
import ctdownloader.model.InteractionEventModel.EventKind;
import ctdownloader.model.TaskContextModel;

public class JsonReader {

	FileFilter dirFilter = new FileFilter() {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			return false;
		}
	};

	private JSONParser jsonParser = new JSONParser();

	// folderPath = MylynContexts
	public ArrayList<ChangeTaskModel> readTasks(String folderPath) {

		ArrayList<ChangeTaskModel> changeTasks = new ArrayList<>();

		FileFilter taskFilter = new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.getName().equals("task.json")) {
					return true;
				}
				return false;
			}
		};

		File folder = new File(folderPath);

		for (File taskFile : folder.listFiles(dirFilter)) {
			// taskFile = folder 123456
			for (File entry : taskFile.listFiles(taskFilter)) {

				try {
					FileReader fileReader = new FileReader(
							entry.getAbsolutePath());
					JSONObject jsonObject = (JSONObject) jsonParser
							.parse(fileReader);

					String summary = (String) jsonObject.get("summary");
					String description = (String) jsonObject.get("description");
					long idRetrieved = (long) jsonObject.get("id");
					int id = (int) idRetrieved;
					String product = (String) jsonObject.get("product");
					String severity = (String) jsonObject.get("severity");
					String priority = (String) jsonObject.get("priority");
					String changed = (String) jsonObject.get("changed");
					boolean hasContext = (boolean) jsonObject.get("hasContext");

					ArrayList<CommentModel> commentModels = new ArrayList<CommentModel>();

					JSONArray comments = (JSONArray) jsonObject.get("comments");
					Iterator i = comments.iterator();
					while (i.hasNext()) {
						JSONObject jsonCommentObject = (JSONObject) i.next();
						String content = (String) jsonCommentObject
								.get("content");
						String author = (String) jsonCommentObject
								.get("author");
						String date = (String) jsonCommentObject.get("date");

						CommentModel commentModel = new CommentModel(content,
								author, date);
						commentModels.add(commentModel);
					}

					Map<String, String> contextMap = new HashMap<String, String>();

					JSONArray jsonContextMapArray = (JSONArray) jsonObject
							.get("contextMap");
					Iterator i1 = jsonContextMapArray.iterator();
					while (i1.hasNext()) {
						JSONObject jsonContextObject = (JSONObject) i1.next();
						String path = (String) jsonContextObject.get("path");
						String submitter = (String) jsonContextObject
								.get("submitter");

						contextMap.put(path, submitter);
					}

					ChangeTaskModel changeTask = new ChangeTaskModel(summary,
							description, commentModels, id, contextMap,
							product, severity, priority, hasContext);
					
					changeTask.setChanged(DateParser.getDateFromString(changed));

					changeTasks.add(changeTask);

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		return changeTasks;
	}

	public ArrayList<TaskContextModel> readTaskContexts(String path) {

		ArrayList<TaskContextModel> contexts = new ArrayList<>();
		FileFilter taskContextFilter = new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.getName().endsWith(".json")
						&& !f.getName().equals("task.json")) {
					return true;
				}
				return false;
			}
		};

		File folder = new File(path);

		// taskFile = folder 123456
		for (File entry : folder.listFiles(taskContextFilter)) {
			try {
				//System.out.println(entry.getAbsolutePath());
				FileReader fileReader = new FileReader(entry.getAbsolutePath());
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser
						.parse(fileReader);

				String submitter = (String) jsonObject.get("submitter");

				ArrayList<InteractionEventModel> interactions = new ArrayList<>();
				JSONArray interactionHistory = (JSONArray) jsonObject
						.get("interactionhistory");
				Iterator i = interactionHistory.iterator();
				while (i.hasNext()) {
					JSONObject jsonInteractionObject = (JSONObject) i.next();
					String delta = (String) jsonInteractionObject.get("delta");
					
					String sDate = (String) jsonInteractionObject
							.get("startDate");
					Date startDate = DateParser.getDateFromString(sDate);
					
					String eDate = (String) jsonInteractionObject.get("endDate");
					Date endDate = DateParser.getDateFromString(eDate);
					double interestDouble = (double) jsonInteractionObject
							.get("interest");
					float interest = (float) interestDouble;
					String eKind = (String) jsonInteractionObject
							.get("eventKind");
					
					EventKind eventKind = EventKind.valueOf(eKind);
					String navigation = (String) jsonInteractionObject
							.get("navigation");
					String originId = (String) jsonInteractionObject
							.get("originId");
					String structureHandle = (String) jsonInteractionObject
							.get("structureHandle");
					String structureKind = (String) jsonInteractionObject
							.get("structureKind");

					InteractionEventModel interactionEvent = new InteractionEventModel(
							delta, startDate, endDate, interest, eventKind,
							navigation, originId, structureHandle,
							structureKind);
					interactions.add(interactionEvent);
				}

				TaskContextModel taskContext = new TaskContextModel(
						interactions);
				taskContext.setSubmitter(submitter);

				contexts.add(taskContext);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return contexts;
	}

}
