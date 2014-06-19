package ctdownloader.connector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.ui.TasksUi;

import ctdownloader.model.ChangeTaskModel;
import ctdownloader.model.CommentModel;
import ctdownloader.util.Punctuation;

public class Bugzilla {
	
	private ArrayList<ChangeTaskModel> tasks = new ArrayList<ChangeTaskModel>();
	public static final String DATABASE_MYLYNCONTEXT = "org.eclipse.mylyn.context.tasks.db4o";

	/**
	 * Queries the bug tracker Bugzilla with the queryString.
	 * 
	 * @param queryString, as example "https://bugs.eclipse.org/bugs/buglist.cgi?f1=attachments.
	 * description&o1=substring&classification=Mylyn&query_format=
	 * advanced&v1=mylyn%2Fcontext%2Fzip&product=Mylyn%20Context"
	 * 
	 * @return ArrayList<ChangeTaskModel>, the queried change tasks.
	 */
	@SuppressWarnings("restriction")
	public ArrayList<ChangeTaskModel> queryTasks(String queryString) {

		final BugzillaRepositoryConnector connector = new BugzillaRepositoryConnector();

		IRepositoryManager repositoryManager = TasksUi.getRepositoryManager();
		final List<TaskRepository> repositories = repositoryManager
				.getAllRepositories();
		IRepositoryModel repositoryModel = TasksUi.getRepositoryModel();
		final IRepositoryQuery repositoryQuery = repositoryModel
				.createRepositoryQuery(repositories.get(1));
		repositoryQuery.setUrl(repositories.get(1).getUrl() + queryString);
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
				}catch(NullPointerException e){
					e.printStackTrace();
				}
			}
		};
		connector.performQuery(repositories.get(1),
				repositoryQuery, collector, null, new NullProgressMonitor());
		

		
		System.out.println("tasks downloaded: "+ tasks.size());
		
		System.out.println("done");
		return tasks;
	}

	private ChangeTaskModel adaptToModel(TaskData completeTaskData) {

		TaskAttribute attribute = completeTaskData.getRoot();

		String summary = attribute.getMappedAttribute(TaskAttribute.SUMMARY)
				.getValue();
		String desc = attribute.getMappedAttribute(TaskAttribute.DESCRIPTION)
				.getValue();
		int id = Integer.valueOf(attribute.getTaskData().getTaskId());
		
		String contextFileName = getContext(attribute);
		File f = new File(contextFileName);
		boolean hasContext = f.exists();
		
		String changed = attribute.getMappedAttribute(TaskAttribute.DATE_MODIFICATION)
				.getValue();
		Punctuation punct = new Punctuation();
		changed = punct.removeAllPunctuationButNumbers(changed);
		changed = changed.trim();
		
		String severity = attribute.getMappedAttribute(TaskAttribute.SEVERITY)
				.getValue();
		String priority = attribute.getMappedAttribute(TaskAttribute.PRIORITY)
				.getValue();
		String product = attribute.getMappedAttribute(TaskAttribute.PRODUCT)
				.getValue();

		ChangeTaskModel taskModel = new ChangeTaskModel(summary, desc,
				getComments(attribute), id, contextFileName, product, severity, priority, hasContext);
		taskModel.setChanged(changed);
		
		return taskModel;
	}
	

	private String getContext(TaskAttribute attribute) {
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
