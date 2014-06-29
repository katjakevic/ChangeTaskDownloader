package ctdownloader.connector;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.internal.context.core.LocalContextStore;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.InteractionEvent.Kind;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.ui.TasksUi;

import ctdownloader.exceptions.InvalidURLException;
import ctdownloader.exceptions.TaskContextDownloadException;
import ctdownloader.model.ChangeTaskModel;
import ctdownloader.model.CommentModel;
import ctdownloader.model.InteractionEventModel;
import ctdownloader.model.NoTaskContextAvailableException;
import ctdownloader.model.TaskContextModel;
import ctdownloader.model.InteractionEventModel.EventKind;
import ctdownloader.util.DateParser;
import ctdownloader.util.FileFilterUtil;
import ctdownloader.util.JsonWriter;
import ctdownloader.util.Punctuation;

public class BugzillaTaskDownloader {

	private ArrayList<ChangeTaskModel> tasks = new ArrayList<ChangeTaskModel>();
	private JsonWriter jsonWriter = new JsonWriter();
	private FileFilterUtil ffUtil = new FileFilterUtil();

	/**
	 * Queries the bug tracker Bugzilla with the queryString.
	 * 
	 * @param queryString
	 *            , as example
	 *            "https://bugs.eclipse.org/bugs/buglist.cgi?f1=attachments.
	 *            description&o1=substring&classification=Mylyn&query_format=
	 *            advanced&v1=mylyn%2Fcontext%2Fzip&product=Mylyn%20Context"
	 * 
	 * @return ArrayList<ChangeTaskModel>, the queried change tasks.
	 * @throws InvalidURLException
	 */
	@SuppressWarnings("restriction")
	public ArrayList<ChangeTaskModel> queryTasks(String queryString)
			throws InvalidURLException {

		final BugzillaRepositoryConnector connector = new BugzillaRepositoryConnector();

		IRepositoryManager repositoryManager = TasksUi.getRepositoryManager();
		final List<TaskRepository> repositories = repositoryManager
				.getAllRepositories();
		IRepositoryModel repositoryModel = TasksUi.getRepositoryModel();
		final IRepositoryQuery repositoryQuery = repositoryModel
				.createRepositoryQuery(repositories.get(1));

		// check if the provided url is valid
		String[] schemes = { "http", "https" };
		UrlValidator urlValidator = new UrlValidator(schemes);

		boolean isValid = urlValidator.isValid(queryString);
		if (isValid == false) {
			throw new InvalidURLException();
		}

		try {
			URL obj = new URL(queryString);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			int response = con.getResponseCode();
			if (response != HttpURLConnection.HTTP_OK) {
				throw new InvalidURLException("Provide a valid URL.");
			}
		} catch (MalformedURLException e1) {
			throw new InvalidURLException(e1.getMessage());
		} catch (IOException e) {
			throw new InvalidURLException(e.getMessage());
		}

		repositoryQuery.setUrl(queryString);
		final TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(final TaskData taskData) {
				try {
					TaskData completeTaskData = connector.getTaskData(
							repositories.get(1), taskData.getTaskId(),
							new NullProgressMonitor());
					System.out.println(completeTaskData.getTaskId());

					tasks.add(adaptToModel(completeTaskData));

				} catch (CoreException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (TaskContextDownloadException e) {
					e.printStackTrace();
				}

			}
		};
		connector.performQuery(repositories.get(1), repositoryQuery, collector,
				null, new NullProgressMonitor());

		System.out.println("tasks downloaded: " + tasks.size());

		System.out.println("done");
		return tasks;
	}

	private ChangeTaskModel adaptToModel(TaskData completeTaskData)
			throws TaskContextDownloadException {

		TaskAttribute attribute = completeTaskData.getRoot();

		String summary = attribute.getMappedAttribute(TaskAttribute.SUMMARY)
				.getValue();
		String desc = attribute.getMappedAttribute(TaskAttribute.DESCRIPTION)
				.getValue();
		int id = Integer.valueOf(attribute.getTaskData().getTaskId());

		Map<String, String> contextMap = getContext(attribute);

		boolean hasContext = false;
		if (!contextMap.isEmpty()) {
			hasContext = true;
		}

		String changed = attribute.getMappedAttribute(
				TaskAttribute.DATE_MODIFICATION).getValue();
		Date changeDated = DateParser.getDateFromString2(changed);

		String severity = attribute.getMappedAttribute(TaskAttribute.SEVERITY)
				.getValue();
		String priority = attribute.getMappedAttribute(TaskAttribute.PRIORITY)
				.getValue();
		String product = attribute.getMappedAttribute(TaskAttribute.PRODUCT)
				.getValue();

		ChangeTaskModel taskModel = new ChangeTaskModel(summary, desc,
				getComments(attribute), id, contextMap, product, severity,
				priority, hasContext);
		taskModel.setChanged(changeDated);
		
		//write the task to a json file
		jsonWriter.writeChangeTaskModelToJsonFile(taskModel);
		if (taskModel.hasTaskContext()) {
			LocalContextStore store = new LocalContextStore(
					new InteractionContextScaling());
			for (Entry<String, String> context : contextMap.entrySet()) {
				File file = new File(context.getKey());

				IInteractionContext interactionContext = store.loadContext(
						String.valueOf(id), file,
						new InteractionContextScaling());

				List<InteractionEvent> events = interactionContext
						.getInteractionHistory();

				ArrayList<InteractionEventModel> interactions = new ArrayList<>();
				for (InteractionEvent interactionEvent : events) {
					Kind k = interactionEvent.getKind();

					InteractionEventModel inter = new InteractionEventModel(
							interactionEvent.getDelta(),
							interactionEvent.getDate(),
							interactionEvent.getEndDate(),
							interactionEvent.getInterestContribution(),
							getEventKind(k), interactionEvent.getNavigation(),
							interactionEvent.getOriginId(),
							interactionEvent.getStructureHandle(),
							interactionEvent.getStructureKind());
					interactions.add(inter);
				}

				TaskContextModel tcModel = new TaskContextModel(interactions);
				tcModel.setSubmitter(context.getValue());
				
				String name = file.getName();
				name = name.replace(".zip", "");
				String path = file.getParentFile().getAbsolutePath();
				String jsonfileName =  path+"\\"+name+ ".json";
				
				jsonWriter.writeContextToJsonFile(tcModel,
						jsonfileName);

			}
		}
		return taskModel;
	}
	
	private EventKind getEventKind(Kind k) {

		switch (k) {

		case ATTENTION:
			return EventKind.ATTENTION;
		case SELECTION:
			return EventKind.SELECTION;
		case EDIT:
			return EventKind.EDIT;
		case COMMAND:
			return EventKind.COMMAND;
		case PREFERENCE:
			return EventKind.PREFERENCE;
		case PREDICTION:
			return EventKind.PREDICTION;
		case PROPAGATION:
			return EventKind.PROPAGATION;
		case MANIPULATION:
			return EventKind.MANIPULATION;
		default:
			return EventKind.UNKOWN;
		}

	}

	private Map<String, String> getContext(TaskAttribute attribute)
			throws TaskContextDownloadException {
		TaskContextDownloader contextParser = new TaskContextDownloader();
		return contextParser.downloadTaskContext(attribute);
	}

	private ArrayList<CommentModel> getComments(TaskAttribute attribute) {
		ArrayList<CommentModel> comments = new ArrayList<CommentModel>();
		for (Map.Entry<String, TaskAttribute> entry : attribute.getAttributes()
				.entrySet()) {
			if (entry.getKey().contains("task.common.comment")) {
				TaskAttribute commentAttr = entry.getValue();
				Map<String, TaskAttribute> commentAttributes = commentAttr
						.getAttributes();
				String author = commentAttributes.get("who").getValue();
				String content = commentAttributes.get("thetext").getValue();
				String when = commentAttributes.get("bug_when").getValue();
				comments.add(new CommentModel(content, author, when));
			}
		}
		return comments;
	}

}
