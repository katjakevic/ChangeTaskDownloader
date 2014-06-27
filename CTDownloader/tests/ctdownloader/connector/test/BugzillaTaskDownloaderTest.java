package ctdownloader.connector.test;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import ctdownloader.connector.BugzillaTaskDownloader;
import ctdownloader.exceptions.InvalidURLException;
import ctdownloader.model.ChangeTaskModel;
import junit.framework.TestCase;

public class BugzillaTaskDownloaderTest extends TestCase {
	
	private BugzillaTaskDownloader downloader;
	
	@Before
	public void setUp(){
		downloader = new BugzillaTaskDownloader();
	}
	
	
	@Test
	public void testQueryTasksWithValidURL() throws InvalidURLException{
		ArrayList<ChangeTaskModel> tasks =  downloader.queryTasks("https://bugs.eclipse.org/bugs/buglist.cgi?classification=Mylyn&f1=attachments.description&o1=substring&product=Mylyn%20Context&query_format=advanced&v1=mylyn%2Fcontext%2Fzip");

		System.out.println(tasks.size());
	}
	
	@Test
	public void testQueryTasksWithInValidURL() {
		Throwable ex = null;
		try {
			downloader.queryTasks("buglist.cgi?classification=Mylyn&f1=attachments.description&o1=substring&product=Mylyn%20Context&query_format=advanced&v1=mylyn%2Fcontext%2Fzip");
		} catch (InvalidURLException e) {
			ex = e;
		}
		assertTrue(ex instanceof InvalidURLException);
	}
	

}
