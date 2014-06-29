package ctdownloader.model.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import ctdownloader.connector.BugzillaTaskDownloader;
import ctdownloader.exceptions.InvalidURLException;
import ctdownloader.model.ChangeTaskModel;
import ctdownloader.model.CommentModel;
import ctdownloader.model.NoTaskContextAvailableException;

public class ChangeTaskModelTest extends TestCase {

	private ChangeTaskModel changeTask;

	@Before
	public void setUp() {

		CommentModel comment1 = new CommentModel("Thanks Remy.",
				"Steffen Pingel", "2011-06-27 15:16:19 EDT");
		ArrayList<CommentModel> comments = new ArrayList<>();
		comments.add(comment1);

		changeTask = new ChangeTaskModel(
				"SuchMethodError thrown when de/activating a task",
				"I tried toggling the activation of a task and got some errors in my log.",
				comments, 350482, null, "Mylyn Context",
				"P1", "major", false);
	}

	@Test
	public void testGetTaskContextModelWithNoTaskContext(){
		assertFalse(changeTask.hasTaskContext());
		Throwable ex = null;
		try {
			changeTask.getAllTaskContextModels();
		} catch (NoTaskContextAvailableException e) {
			ex = e;
		}
		assertTrue(ex instanceof NoTaskContextAvailableException);
		
		Throwable ex1 = null;
		try {
			changeTask.getTaskContexts();
		} catch (NoTaskContextAvailableException e) {
			ex1 = e;
		}
		assertTrue(ex1 instanceof NoTaskContextAvailableException);
		
		
		
	}

	@Test
	public void testGetTaskContextModelWithTaskContext() throws InvalidURLException{
		BugzillaTaskDownloader downloader = new BugzillaTaskDownloader();
		ArrayList<ChangeTaskModel> tasks = downloader.queryTasks("https://bugs.eclipse.org/bugs/buglist.cgi?bug_id=359547&bug_id_type=anyexact&query_format=advanced");
		
		assertTrue(tasks.get(0).hasTaskContext());
		
	}
}
